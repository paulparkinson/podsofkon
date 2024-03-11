package podsofkon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oracle.bmc.aivision.AIServiceVisionClient;
import com.oracle.bmc.aivision.model.*;
import com.oracle.bmc.aivision.requests.AnalyzeImageRequest;
import com.oracle.bmc.aivision.responses.AnalyzeImageResponse;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import podsofkon.model.Account;
import podsofkon.model.Summary;
import podsofkon.oci.AuthDetailsProviderFactory;
import podsofkon.repository.SummaryRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/visionai")
public class VisionController {

    private static Logger log = LoggerFactory.getLogger(VisionController.class);


    SummaryRepository summaryRepository;

    public VisionController(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }


    private static final String REGION = "us-phoenix-1";

    @GetMapping("/geojson")
    public String geojson() {
        return "{" +
                "  \"type\": \"FeatureCollection\"," +
                "  \"features\": [" +
                "    {" +
                "      \"type\": \"Feature\"," +
                "      \"geometry\": {" +
                "        \"type\": \"Point\"," +
                "        \"coordinates\": [-81.5812, 28.4187]" +
                "      }," +
                "      \"properties\": {" +
                "        \"name\": \"Space Mountain\"," +
                "        \"park\": \"Walt Disney World\"" +
                "      }" +
                "    }," +
                "    {" +
                "      \"type\": \"Feature\"," +
                "      \"geometry\": {" +
                "        \"type\": \"Point\"," +
                "        \"coordinates\": [-81.5793, 28.4193]" +
                "      }," +
                "      \"properties\": {" +
                "        \"name\": \"Cinderella Castle\"," +
                "        \"park\": \"Walt Disney World\"" +
                "      }" +
                "    }" +
                "  ]" +
                "}";
    }
    @GetMapping("/geojson2")
    public String geojson2() {
        return "{ \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    { \"type\": \"Feature\",\n" +
                "      \"geometry\": {\"type\": \"Point\", \"coordinates\": [102.0, 0.5]},\n" +
                "      \"properties\": {\"prop0\": \"value0\"}\n" +
                "      },\n" +
                "    { \"type\": \"Feature\",\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"LineString\",\n" +
                "        \"coordinates\": [\n" +
                "          [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]\n" +
                "          ]\n" +
                "        },\n" +
                "      \"properties\": {\n" +
                "        \"prop0\": \"value0\",\n" +
                "        \"prop1\": 0.0\n" +
                "        }\n" +
                "      },\n" +
                "    { \"type\": \"Feature\",\n" +
                "       \"geometry\": {\n" +
                "         \"type\": \"Polygon\",\n" +
                "         \"coordinates\": [\n" +
                "           [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],\n" +
                "             [100.0, 1.0], [100.0, 0.0] ]\n" +
                "           ]\n" +
                "\n" +
                "       },\n" +
                "       \"properties\": {\n" +
                "         \"prop0\": \"value0\",\n" +
                "         \"prop1\": {\"this\": \"that\"}\n" +
                "         }\n" +
                "       }\n" +
                "    ]\n" +
                "  }";
    }

    @GetMapping("/albumjson")
    public String albumjson() {
        return "{" +
                "  \"userId\": 1," +
                "  \"id\": 1," +
                "  \"title\": \"quidem molestiae enim\"" +
                "}";
    }
    @GetMapping("/form")
    public String form() {
        return "                <html><form method=\"post\" action=\"/visionai/summarize\" enctype=\"multipart/form-data\">\n" +
                "                    Select an image file to conduct summarizewithcohere upon...\n" +
                "                    <input type=\"file\" name=\"file\" accept=\"image/*\">\n" +
                "                    <br>\n" +
                "                    <br>Hit submit and a raw JSON summary will be returned...\n" +
                "                    <br><input type=\"submit\" value=\"Send Request to Vision AI and Cohere\">\n" +
                "                </form></html>";
    }

    @PostMapping("/explain")
    public String explain(@RequestParam("file") MultipartFile file)
            throws Exception {
        log.info("analyzing image file:" + file);
        ExplainAndAdviseOnHealthTestResults explainAndAdviseOnHealthTestResults = new ExplainAndAdviseOnHealthTestResults();
        try {
            String objectDetectionResults = processImage(file.getBytes(), true);
            ExplainAndAdviseOnHealthTestResults.ImageAnalysis imageAnalysis =
                    explainAndAdviseOnHealthTestResults.parseJsonToImageAnalysis(objectDetectionResults);
            List<ExplainAndAdviseOnHealthTestResults.Line> lines = imageAnalysis.getImageText().getLines();
            String fullText = "";
            for (ExplainAndAdviseOnHealthTestResults.Line line : lines) fullText += line.getText();
            log.info("fullText = " + fullText);
            String explanationOfResults = explainAndAdviseOnHealthTestResults.openAIGPT("explain these test results in simple terms " +
                    "and tell me what should I do to get better results: \"" + fullText + "\"");
            return explanationOfResults;
        } catch (com.oracle.bmc.model.BmcException exception) {
            System.out.println("VisionController.processImage exception:" + exception);
            exception.printStackTrace();
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.equals("image.data size must be between 0 and 11534336"))
                return "image.data size must be between 0 and 11534336";
            else return exceptionMessage;
        }
    }

    @PostMapping("/summarize")
    public String summarize(@RequestParam("file") MultipartFile file)
            throws Exception {
        log.info("analyzing image file:" + file);
        ExplainAndAdviseOnHealthTestResults explainAndAdviseOnHealthTestResults = new ExplainAndAdviseOnHealthTestResults();
        try {
            String objectDetectionResults = processImage(file.getBytes(), true);
            ExplainAndAdviseOnHealthTestResults.ImageAnalysis imageAnalysis =
                    explainAndAdviseOnHealthTestResults.parseJsonToImageAnalysis(objectDetectionResults);
            List<ExplainAndAdviseOnHealthTestResults.Line> lines = imageAnalysis.getImageText().getLines();
            String fullText = "";
            for (ExplainAndAdviseOnHealthTestResults.Line line : lines) fullText += line.getText();
            log.info("fullText = " + fullText);
            String explanationOfResults = explainAndAdviseOnHealthTestResults.openAIGPT("summarize this in less than forty words : \"" +
                    fullText + "\"");
            return persistResults(explanationOfResults, file.getBytes());
        } catch (com.oracle.bmc.model.BmcException exception) {
            System.out.println("VisionController.processImage exception:" + exception);
            exception.printStackTrace();
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.equals("image.data size must be between 0 and 11534336"))
                return "image.data size must be between 0 and 11534336";
            else return exceptionMessage;
        }
    }
    @PostMapping("/summarizewithcohere")
    public String summarizewithcohere(@RequestParam("file") MultipartFile file)
            throws Exception {
        log.info("summarizewithcohere analyzing image file:" + file);
        ExplainAndAdviseOnHealthTestResults explainAndAdviseOnHealthTestResults = new ExplainAndAdviseOnHealthTestResults();
        try {
            String objectDetectionResults = processImage(file.getBytes(), true);
            ExplainAndAdviseOnHealthTestResults.ImageAnalysis imageAnalysis =
                    explainAndAdviseOnHealthTestResults.parseJsonToImageAnalysis(objectDetectionResults);
            List<ExplainAndAdviseOnHealthTestResults.Line> lines = imageAnalysis.getImageText().getLines();
            String fullText = "";
            for (ExplainAndAdviseOnHealthTestResults.Line line : lines) fullText += line.getText();
            log.info("summarizewithcohere fullText = " + fullText);
            String explanationOfResults = explainAndAdviseOnHealthTestResults.cohere(fullText );
            return explanationOfResults;
        } catch (com.oracle.bmc.model.BmcException exception) {
            log.warn("summarizewithcohere exception:" + exception);
            exception.printStackTrace();
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.equals("image.data size must be between 0 and 11534336"))
                return "image.data size must be between 0 and 11534336";
            else return exceptionMessage;
        }
    }

    @PostMapping("/objectdetection")
    public String objectdetection(
            HttpServletRequest request, @RequestParam("file") MultipartFile file,
            @RequestParam(value = "token", required = false) String token)
            throws Exception {
        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.AIVISION_IMAGEDETECTION, token);
        log.info("got image file, now analyze, file = " + file);
        String objectDetectionResults = processImage(file.getBytes(), true);
        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.AIVISION_IMAGEDETECTION);
        return objectDetectionResults;
    }


    public String processImage(byte[] bytes, boolean isConfigFileAuth) throws Exception {
        return doProcessImage(bytes, isConfigFileAuth);
    }

    public String doProcessImage(byte[] bytes, boolean isConfigFileAuth) throws Exception {
        AIServiceVisionClient aiServiceVisionClient;
        AuthenticationDetailsProvider provider;
        if (isConfigFileAuth) {
            provider = AuthDetailsProviderFactory.getAuthenticationDetailsProvider();
            aiServiceVisionClient = new AIServiceVisionClient(provider);
        } else {
            aiServiceVisionClient = new AIServiceVisionClient(InstancePrincipalsAuthenticationDetailsProvider.builder().build());
        }
        aiServiceVisionClient.setRegion(REGION);
        // Read image file from resources folder
        //    if (bytes==null) bytes = Files.readAllBytes(Paths.get("src/resources/cat.jpg"));
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
//                .compartmentId(COMPARTMENT_ID) //uncomment this line if using boat user
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
    /**
     byte[] bytes = Files.readAllBytes(Paths.get("src/resources/IMG_2068.jpg"));

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
     */


    String persistResults(String explanationOfResults, byte[] bytes) {
        summaryRepository.saveAndFlush(new Summary(explanationOfResults, bytes));
        return "resultspersisted";
    }

    @GetMapping("/getsummaries")
    public List<Summary> getsummaries()
            throws Exception {
        String objectDetectionResults = "";
        List<Summary> summaries = summaryRepository.findAll();
        for (Summary summary : summaries) {
            System.out.println(summary);
            objectDetectionResults+=summary;
        }
        System.out.println(summaries);
        return summaries;
    }
}
