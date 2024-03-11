package podsofkon;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.time.Duration;

@RestController
@RequestMapping("/speechai")
public class SpeechController {

    public static final String openAIToken = "sk-qFI0kradsfwwSoq9Ng";
    // https://platform.openai.com/docs/api-reference/audio/create

    @PostMapping("/transcribe")
    public String transcribe(
            HttpServletRequest request, @RequestParam("file") MultipartFile file,
            @RequestParam(value = "token", required = false) String token) throws Exception {
        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.SPEECHAI_TRANSCRIBE, token);
        OpenAiService service =
                new OpenAiService(openAIToken, Duration.ofSeconds(60));
        String audioTranscription = transcribeFile(file, service);
        service.shutdownExecutor();
        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.SPEECHAI_TRANSCRIBE);
        return audioTranscription;
    }

    @PostMapping("/translate")
    public String translate(
            HttpServletRequest request, @RequestParam("file") MultipartFile file,
            @RequestParam(value = "token", required = false) String token) throws Exception {
        String email = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.SPEECHAI_TRANSLATE, token);
        OpenAiService service =
                new OpenAiService(openAIToken, Duration.ofSeconds(60));
        String audioTranslation = translateFile(file, service);
        service.shutdownExecutor();
        AuthAndUsageTracking.instance().addUsage(email, AuthAndUsageTracking.SPEECHAI_TRANSCRIBE);
        return audioTranslation;
    }

    private String translateFile(MultipartFile file, OpenAiService service) throws Exception
        {
            String endpoint = "https://api.openai.com/v1/audio/translations";
            MultipartFile audioFile = file; // get MultipartFile object from request
            String modelName = "whisper-1";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openAIToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            });
            body.add("model", modelName);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);

            System.out.println("Response status code: " + response.getStatusCodeValue());
            System.out.println("Response body: " + response.getBody());
            return response.getBody();
        }
    private String transcribeFile(MultipartFile file, OpenAiService service) throws Exception
        {
            String endpoint = "https://api.openai.com/v1/audio/transcriptions";
            MultipartFile audioFile = file; // get MultipartFile object from request
            String modelName = "whisper-1";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openAIToken);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            });
            body.add("model", modelName);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
            System.out.println("Response status code: " + response.getStatusCodeValue());
            System.out.println("Response body: " + response.getBody());
            return response.getBody();
        }





}
