package podsofkon;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/openai")
public class OpenAIController {

    public static final String openAIToken = "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";

    @PostMapping("/imagegeneration")
    public String imagegeneration(
            HttpServletRequest request, @RequestParam("imagedescription") String imagedescription,
            @RequestParam(value = "token", required = false) String token) throws Exception {
        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT, token);
        OpenAiService service =
                new OpenAiService(openAIToken, Duration.ofSeconds(60));
        CreateImageRequest openairequest = CreateImageRequest.builder()
                .prompt(imagedescription)
                .build();

        System.out.println("\nImage is located at:");
        String imageLocation = service.createImage(openairequest).getData().get(0).getUrl();
        service.shutdownExecutor();
        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT);
     return imageLocation;
    }

// https://platform.openai.com/docs/models/model-endpoint-compatibility
    // https://openai.com/pricing

    @GetMapping("/models")
    public static List<Model> models(
            HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token) throws Exception {
//        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT, token);
        OpenAiService service =  new OpenAiService(openAIToken, Duration.ofSeconds(60));
        List<Model> models = service.listModels();
        service.shutdownExecutor();
//        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT);
        return models;
    }
    @PostMapping("/chat")
    public String chat(
            HttpServletRequest request, @RequestParam("textcontent") String textcontent, @RequestParam("model") String model,
            @RequestParam(value = "token", required = false) String token) throws Exception {
        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT, token);
        OpenAiService service =
                new OpenAiService(openAIToken, Duration.ofSeconds(60));
        System.out.println("Streaming chat completion... textcontent:" + textcontent);
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), textcontent);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(model) // "gpt-3.5-turbo"
                .messages(messages)
                .n(1)
                .maxTokens(300) //was 50
                .logitBias(new HashMap<>())
                .build();
        String replyString = "";
        String content;
        for (ChatCompletionChoice choice : service.createChatCompletion(chatCompletionRequest).getChoices()) {
            content = choice.getMessage().getContent();
            replyString += (content == null?" ": content);
        }
        service.shutdownExecutor();
        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.OPENAI_IMAGEFORTEXT);
        return replyString;
    }

    private static void completion(OpenAiService service) {
        System.out.println("\nCreating completion...");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("ada")
                .prompt("Somebody once told me ")
                .echo(true)
                .user("testing")
                .n(3)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
    }
}
