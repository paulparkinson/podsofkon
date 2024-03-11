package podsofkon.speech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import podsofkon.objectstorage.ObjectStorageService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
public class XRAISpeechController {
    private static Logger log = LoggerFactory.getLogger(XRAISpeechController.class);

    @PostMapping("/aispeechexisting")
    public String aispeech(
            HttpServletRequest request, @RequestParam("fileName") String fileName)
            throws Exception {
//        return new CreateTranscriptJobExample().process(fileName);
        return new SpeechAI().process(fileName);

    }



    @PostMapping("/aispeech")
    public String aispeech(
            HttpServletRequest request, @RequestParam("id") String id, @RequestParam("file") MultipartFile file)
            throws Exception {
        log.info("got objectstore file, now analyze " + "id = " + id + ", filename = " + file.getOriginalFilename());
        new ObjectStorageService().upload(file);
        return new SpeechAI().process(file.getOriginalFilename());
    }
}
