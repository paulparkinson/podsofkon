package podsofkon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.logging.Logger;

@Component
public class StartupInfo {

    private static final Logger LOG = Logger.getLogger(StartupInfo.class.toString());

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        LOG.info(Arrays.asList(environment.getDefaultProfiles()).toString());
        LOG.info("StartupInfo datasource.url:" + System.getenv("datasource.url"));
    }
}