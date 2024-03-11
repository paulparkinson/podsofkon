package podsofkon;


import com.oracle.bmc.ailanguage.model.DetectLanguageSentimentsResult;
import com.oracle.bmc.ailanguage.model.SentimentAspect;
import com.oracle.bmc.ailanguage.responses.DetectLanguageSentimentsResponse;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.ailanguage.requests.DetectLanguageSentimentsRequest;
import com.oracle.bmc.ailanguage.AIServiceLanguageClient;
import com.oracle.bmc.ailanguage.model.DetectLanguageSentimentsDetails;
import com.oracle.bmc.model.BmcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import podsofkon.oci.AuthDetailsProviderFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/languageai")
public class LanguageController {

    private static Logger log = LoggerFactory.getLogger(LanguageController.class);
    private static final String REGION = "us-phoenix-1";

    @PostMapping("/sentiments")
    public String sentiments(
            HttpServletRequest request, @RequestParam("textcontent") String textcontent,
            @RequestParam(value = "token", required = false) String token) // , Model model)
            throws Exception {
        String emailForAuthKey = AuthAndUsageTracking.instance().auth(request, AuthAndUsageTracking.AILANGUAGE_SENTIMENTDETECTION, token);
        log.info("analyze text for sentiment:" + textcontent);
        AuthenticationDetailsProvider provider  = AuthDetailsProviderFactory.getAuthenticationDetailsProvider();
        AIServiceLanguageClient languageClient =
                AIServiceLanguageClient.builder().build(provider);
        languageClient.setRegion(REGION);
        DetectLanguageSentimentsDetails details =
                DetectLanguageSentimentsDetails.builder()
                        .text(textcontent)
                        .build();
        DetectLanguageSentimentsRequest detectLanguageSentimentsRequest =
                DetectLanguageSentimentsRequest.builder()
                        .detectLanguageSentimentsDetails(details)
                        .build();
        DetectLanguageSentimentsResponse response = null;
        try {
            response = languageClient.detectLanguageSentiments(detectLanguageSentimentsRequest);
        } catch (BmcException e) {
            System.err.println("Failed to detect language and sentiments: " + e.getMessage());
        }
        DetectLanguageSentimentsResult detectLanguageSentimentsResult = response.getDetectLanguageSentimentsResult();
        String sentimentReturn = "";
        for (SentimentAspect aspect : detectLanguageSentimentsResult.getAspects()) {
            System.out.println("aspect.getText(): ");
            sentimentReturn += "sentiment:" + aspect.getSentiment();
            sentimentReturn += " text:" + aspect.getText();
//            sentimentReturn += " scores:" + aspect.getScores();
            sentimentReturn += "\n";
        }
        AuthAndUsageTracking.instance().addUsage(emailForAuthKey, AuthAndUsageTracking.AILANGUAGE_SENTIMENTDETECTION);
        log.info(sentimentReturn);
        return sentimentReturn;
    }
    
}
