package podsofkon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oracle.bmc.aivision.AIServiceVisionClient;
import com.oracle.bmc.aivision.model.*;
import com.oracle.bmc.aivision.requests.AnalyzeImageRequest;
import com.oracle.bmc.aivision.responses.AnalyzeImageResponse;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.Data;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONArray;
import org.json.JSONObject;
import podsofkon.oci.AuthDetailsProviderFactory;

import static podsofkon.OpenAIController.openAIToken;
import static podsofkon.XRApplication.REGION;

@RestController
@RequestMapping("/health")
public class ExplainAndAdviseOnHealthTestResults {

    private static Logger log = LoggerFactory.getLogger(ExplainAndAdviseOnHealthTestResults.class);

    @GetMapping("/form")
    public String form(){
        return "                <html><form method=\"post\" action=\"/health/analyzedoc\" enctype=\"multipart/form-data\">\n" +
                "                    Select an image file to conduct object detection upon...\n" +
                "                    <input type=\"file\" name=\"file\" accept=\"image/*\">\n" +
                "                    <br>\n" +
                "                    <br>Hit submit and a raw JSON return of objects detected and other info will be returned...\n" +
                "                    <br><input type=\"submit\" value=\"Send Request to Vision AI\">\n" +
                "                </form></html>";
    }


    String openAIGPT(String textcontent) {
        try {
            OpenAiService service =
                    new OpenAiService(openAIToken, Duration.ofSeconds(60));
            System.out.println("Streaming chat completion... textcontent:" + textcontent);
            final List<ChatMessage> messages = new ArrayList<>();
            final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), textcontent);
            messages.add(systemMessage);
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .n(1)
                    .maxTokens(300) //was 50
                    .logitBias(new HashMap<>())
                    .build();
            String replyString = "";
            String content;
            for (ChatCompletionChoice choice : service.createChatCompletion(chatCompletionRequest).getChoices()) {
                content = choice.getMessage().getContent();
                replyString += (content == null ? " " : content);
            }
            service.shutdownExecutor();
            return replyString;
        } catch (Exception exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

    public String cohere( String textcontent) throws Exception {
        AtomicReference<String> resons = new AtomicReference<>("");
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        client.prepare("POST", "https://api.cohere.ai/v1/summarize")
                .setHeader("accept", "application/json")
                .setHeader("content-type", "application/json")
                .setHeader("authorization", "Bearer oJfPT7nsadfJi1VRz7")
                .setBody("{\"length\":\"medium\",\"format\":\"paragraph\",\"model\":\"summarize-xlarge\",\"extractiveness\":\"low\",\"temperature\":0.3," +
                        "\"text\":\"" + textcontent + "\"}")
//                .setBody("{\"length\":\"medium\",\"format\":\"paragraph\",\"model\":\"summarize-xlarge\",\"extractiveness\":\"low\",\"temperature\":0.3,\"text\":\"Ice cream is a sweetened frozen food typically eaten as a snack or dessert. It may be made from milk or cream and is flavoured with a sweetener, either sugar or an alternative, and a spice, such as cocoa or vanilla, or with fruit such as strawberries or peaches. It can also be made by whisking a flavored cream base and liquid nitrogen together. Food coloring is sometimes added, in addition to stabilizers. The mixture is cooled below the freezing point of water and stirred to incorporate air spaces and to prevent detectable ice crystals from forming. The result is a smooth, semi-solid foam that is solid at very low temperatures (below 2 °C or 35 °F). It becomes more malleable as its temperature increases.\\n\\nThe meaning of the name \\\"ice cream\\\" varies from one country to another. In some countries, such as the United States, \\\"ice cream\\\" applies only to a specific variety, and most governments regulate the commercial use of the various terms according to the relative quantities of the main ingredients, notably the amount of cream. Products that do not meet the criteria to be called ice cream are sometimes labelled \\\"frozen dairy dessert\\\" instead. In other countries, such as Italy and Argentina, one word is used fo\\r all variants. Analogues made from dairy alternatives, such as goat's or sheep's milk, or milk substitutes (e.g., soy, cashew, coconut, almond milk or tofu), are available for those who are lactose intolerant, allergic to dairy protein or vegan.\"}")
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    try {
                        String responseBody = response.getResponseBody();
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(responseBody);
                        String summary = jsonNode.get("summary").asText();
                        System.out.println(summary);
                        System.out.println(responseBody);
                        resons.set(summary);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .join();
        client.close();
        return resons.get();
    }

    String processImage(byte[] bytes, boolean isConfigFileAuth) throws Exception {
        AIServiceVisionClient aiServiceVisionClient;
        AuthenticationDetailsProvider provider;
        if (isConfigFileAuth) {
            provider = AuthDetailsProviderFactory.getAuthenticationDetailsProvider();
            aiServiceVisionClient = AIServiceVisionClient.builder().build(provider);
        } else {
            aiServiceVisionClient = new AIServiceVisionClient(InstancePrincipalsAuthenticationDetailsProvider.builder().build());
        }
        aiServiceVisionClient.setRegion(REGION);
        List<ImageFeature> features = new ArrayList<>();
        ImageFeature classifyFeature = ImageClassificationFeature.builder()
                .maxResults(10)
                .build();
        ImageFeature detectImageFeature = ImageObjectDetectionFeature.builder()
                .maxResults(10)
                .build();
        ImageFeature textDetectImageFeature = ImageTextDetectionFeature.builder().build();
        features.add(classifyFeature);
        features.add(detectImageFeature);
        features.add(textDetectImageFeature);
        InlineImageDetails inlineImageDetails = InlineImageDetails.builder()
                .data(bytes)
                .build();
        AnalyzeImageDetails analyzeImageDetails = AnalyzeImageDetails.builder()
                .image(inlineImageDetails)
                .features(features)
                .build();
        AnalyzeImageRequest request = AnalyzeImageRequest.builder()
                .analyzeImageDetails(analyzeImageDetails)
                .build();
        AnalyzeImageResponse response = aiServiceVisionClient.analyzeImage(request);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

        String json = mapper.writeValueAsString(response.getAnalyzeImageResult());
        System.out.println("AnalyzeImage Result");
        System.out.println(json);
        return json;
    }

    @Data
    class ImageObject {
        private String name;
        private double confidence;
        private BoundingPolygon boundingPolygon;
    }

    @Data
    class BoundingPolygon {
        private List<Point> normalizedVertices;
    }

    @Data
    class Point {
        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    @Data
    class Label {
        private String name;
        private double confidence;
    }

    @Data
    class OntologyClass {
        private String name;
        private List<String> parentNames;
        private List<String> synonymNames;
    }

    @Data
    class ImageText {
        private List<Word> words;
        private List<Line> lines;
    }

    @Data
    class Word {
        private String text;
        private double confidence;
        private BoundingPolygon boundingPolygon;
    }

    @Data
    class Line {
        private String text;
        private double confidence;
        private BoundingPolygon boundingPolygon;
        private List<Integer> wordIndexes;
    }

    @Data
    class ImageAnalysis {
        private List<ImageObject> imageObjects;
        private List<Label> labels;
        private List<OntologyClass> ontologyClasses;
        private ImageText imageText;
        private String imageClassificationModelVersion;
        private String objectDetectionModelVersion;
        private String textDetectionModelVersion;
        private List<String> errors;
    }


    ImageAnalysis parseJsonToImageAnalysis(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        List<ImageObject> imageObjects = new ArrayList<>();
        if (json.has("imageObjects") && json.get("imageObjects") instanceof JSONArray) {
            JSONArray imageObjectsArray = json.getJSONArray("imageObjects");
            for (int i = 0; i < imageObjectsArray.length(); i++) {
                JSONObject imageObjectJson = imageObjectsArray.getJSONObject(i);
                ImageObject imageObject = new ImageObject();
                imageObject.setName(imageObjectJson.getString("name"));
                imageObject.setConfidence(imageObjectJson.getDouble("confidence"));

                JSONObject boundingPolygonJson = imageObjectJson.getJSONObject("boundingPolygon");
                JSONArray normalizedVerticesArray = boundingPolygonJson.getJSONArray("normalizedVertices");
                List<Point> normalizedVertices = new ArrayList<>();
                for (int j = 0; j < normalizedVerticesArray.length(); j++) {
                    JSONObject vertexJson = normalizedVerticesArray.getJSONObject(j);
                    Point vertex = new Point(vertexJson.getDouble("x"), vertexJson.getDouble("y"));
                    normalizedVertices.add(vertex);
                }
                BoundingPolygon boundingPolygon = new BoundingPolygon();
                boundingPolygon.setNormalizedVertices(normalizedVertices);
                imageObject.setBoundingPolygon(boundingPolygon);

                imageObjects.add(imageObject);
            }
        }
        JSONArray labelsArray = json.getJSONArray("labels");
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < labelsArray.length(); i++) {
            JSONObject labelJson = labelsArray.getJSONObject(i);
            Label label = new Label();
            label.setName(labelJson.getString("name"));
            label.setConfidence(labelJson.getDouble("confidence"));
            labels.add(label);
        }
        JSONArray ontologyClassesArray = json.getJSONArray("ontologyClasses");
        List<OntologyClass> ontologyClasses = new ArrayList<>();
        for (int i = 0; i < ontologyClassesArray.length(); i++) {
            JSONObject ontologyClassJson = ontologyClassesArray.getJSONObject(i);
            OntologyClass ontologyClass = new OntologyClass();
            ontologyClass.setName(ontologyClassJson.getString("name"));
            JSONArray parentNamesArray = ontologyClassJson.getJSONArray("parentNames");
            List<String> parentNames = new ArrayList<>();
            for (int j = 0; j < parentNamesArray.length(); j++) {
                parentNames.add(parentNamesArray.getString(j));
            }
            ontologyClass.setParentNames(parentNames);
            ontologyClasses.add(ontologyClass);
        }
        JSONObject imageTextJson = json.getJSONObject("imageText");
        JSONArray wordsArray = imageTextJson.getJSONArray("words");
        List<Word> words = new ArrayList<>();
        for (int i = 0; i < wordsArray.length(); i++) {
            JSONObject wordJson = wordsArray.getJSONObject(i);
            Word word = new Word();
            word.setText(wordJson.getString("text"));
            word.setConfidence(wordJson.getDouble("confidence"));
            JSONObject boundingPolygonJson = wordJson.getJSONObject("boundingPolygon");
            JSONArray normalizedVerticesArray = boundingPolygonJson.getJSONArray("normalizedVertices");
            List<Point> normalizedVertices = new ArrayList<>();
            for (int j = 0; j < normalizedVerticesArray.length(); j++) {
                JSONObject vertexJson = normalizedVerticesArray.getJSONObject(j);
                Point vertex = new Point(vertexJson.getDouble("x"), vertexJson.getDouble("y"));
                normalizedVertices.add(vertex);
            }
            BoundingPolygon boundingPolygon = new BoundingPolygon();
            boundingPolygon.setNormalizedVertices(normalizedVertices);
            word.setBoundingPolygon(boundingPolygon);
            words.add(word);
        }
        JSONArray linesArray = imageTextJson.getJSONArray("lines");
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < linesArray.length(); i++) {
            JSONObject lineJson = linesArray.getJSONObject(i);
            Line line = new Line();
            line.setText(lineJson.getString("text"));
            line.setConfidence(lineJson.getDouble("confidence"));
            JSONObject boundingPolygonJson = lineJson.getJSONObject("boundingPolygon");
            JSONArray normalizedVerticesArray = boundingPolygonJson.getJSONArray("normalizedVertices");
            List<Point> normalizedVertices = new ArrayList<>();
            for (int j = 0; j < normalizedVerticesArray.length(); j++) {
                JSONObject vertexJson = normalizedVerticesArray.getJSONObject(j);
                Point vertex = new Point(vertexJson.getDouble("x"), vertexJson.getDouble("y"));
                normalizedVertices.add(vertex);
            }
            BoundingPolygon boundingPolygon = new BoundingPolygon();
            boundingPolygon.setNormalizedVertices(normalizedVertices);
            line.setBoundingPolygon(boundingPolygon);
            JSONArray wordIndexesArray = lineJson.getJSONArray("wordIndexes");
            List<Integer> wordIndexes = new ArrayList<>();
            for (int j = 0; j < wordIndexesArray.length(); j++) {
                wordIndexes.add(wordIndexesArray.getInt(j));
            }
            line.setWordIndexes(wordIndexes);
            lines.add(line);
        }

        String imageClassificationModelVersion = json.getString("imageClassificationModelVersion");
        String objectDetectionModelVersion = json.getString("objectDetectionModelVersion");
        String textDetectionModelVersion = json.getString("textDetectionModelVersion");
        List<String> errors = new ArrayList<>();
        JSONArray errorsArray = json.getJSONArray("errors");
        for (int i = 0; i < errorsArray.length(); i++) {
            errors.add(errorsArray.getString(i));
        }

        ImageText imageText = new ImageText();
        imageText.setWords(words);
        imageText.setLines(lines);

        ImageAnalysis imageAnalysis = new ImageAnalysis();
        imageAnalysis.setImageObjects(imageObjects);
        imageAnalysis.setLabels(labels);
        imageAnalysis.setOntologyClasses(ontologyClasses);
        imageAnalysis.setImageText(imageText);
        imageAnalysis.setImageClassificationModelVersion(imageClassificationModelVersion);
        imageAnalysis.setObjectDetectionModelVersion(objectDetectionModelVersion);
        imageAnalysis.setTextDetectionModelVersion(textDetectionModelVersion);
        imageAnalysis.setErrors(errors);

        return imageAnalysis;
    }
}



