package podsofkon.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ApplyNamespace {
    private static final Logger log = LoggerFactory.getLogger(ApplyNamespace.class);

    public String createNamespace(String appName) throws Exception {
        ApiClient client = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
                .newBuilder()
                .addInterceptor(interceptor)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        client.setHttpClient(newClient);
        CoreV1Api api = new CoreV1Api(client);
        Yaml yaml = new Yaml();
        String serviceyaml = "apiVersion: v1\n" +
                "kind: Namespace\n" +
                "metadata:\n" +
                "  name: " + appName;
        V1Namespace v1Namespace = yaml.loadAs(serviceyaml, V1Namespace.class);
        log.debug("creating application/namespace..." + appName);
        api.createNamespace(v1Namespace, null , null, null, null);
        //todo get the detail if exception (eg AlreadyExists) as the exception message does not seem to have it
        log.debug("created application/namespace..." + appName);
        return "application/namespace created successfully";
    }
}
