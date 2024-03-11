package podsofkon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/cohere")
public class CohereCall {


    @GetMapping("/generate")
    public String generate() throws Exception {

        // https://github.com/AsyncHttpClient/async-http-client
        AtomicReference<String> resons = new AtomicReference<>("");
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        client.prepare("POST", "https://api.cohere.ai/v1/generate")
                .setHeader("accept", "application/json")
                .setHeader("content-type", "application/json")
                .setHeader("authorization", "Bearer oJasdf9Ji1VRz7")
                .setBody("{\"max_tokens\":20,\"return_likelihoods\":\"NONE\",\"truncate\":\"END\",\"prompt\":\"Please explain to me how LLMs work\"}")
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    try {
                        String responseBody = response.getResponseBody();
                        System.out.println("responseBody:" + responseBody);
                        resons.set(responseBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .join();
        client.close();
        return resons.get();
    }


    @GetMapping("/form")
    public String form(){
        return "                <html><form method=\"post\" action=\"/health/analyzedoc\" enctype=\"multipart/form-data\">\n" +
                "                    Select an image file to conduct object detection upon...\n" +
                "                    <input type=\"file\" name=\"file\" accept=\"image/*\">\n" +
                "                    <br>\n" +
                "                    <br>Hit submit to get a summary...\n" +
                "                    <br><input type=\"submit\" value=\"Send Request to Cohere AI\">\n" +
                "                </form></html>";
    }


    @GetMapping("/summarize")
    public String summarize( @RequestParam("textcontent") String textcontent) throws Exception {
        AtomicReference<String> resons = new AtomicReference<>("");
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        client.prepare("POST", "https://api.cohere.ai/v1/summarize")
                .setHeader("accept", "application/json")
                .setHeader("content-type", "application/json")
                .setHeader("authorization", "Bearer oJfPT7nhQasdf9Ji1VRz7")
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
}
