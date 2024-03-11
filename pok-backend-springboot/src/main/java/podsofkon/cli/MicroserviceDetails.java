package podsofkon.cli;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Getter
@Setter
public class MicroserviceDetails {

    private MultipartFile microserviceJarFile;
    private Path microserviceFilePath;

    public MicroserviceDetails( Path path, MultipartFile file) {
        microserviceFilePath = path;
        microserviceJarFile = file;
    }
}
