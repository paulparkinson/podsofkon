package podsofkon.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class ApplyService {
    private static final Logger log = LoggerFactory.getLogger(ApplyService.class);

    public String createService(String appName, String serviceName) throws Exception {
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
        String app = serviceName;
        String serviceyaml = "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: " + serviceName + "\n" +
                "  labels:\n" +
                "    app: " + app + "\n" +
                "spec:\n" +
                "  type: NodePort\n" +
                "  ports:\n" +
                "    - port: 8080\n" +
                "      targetPort: 8080\n" +
                "  selector:\n" +
                "    app: " + app + "\n";
        V1Service v1Service = yaml.loadAs(serviceyaml, V1Service.class);
        String namespace = appName;
        log.debug("service creating..." + v1Service);
        V1Service createResult = api.createNamespacedService(
                namespace, v1Service, null, null, null, null);
        log.debug("service created");
        return "service created successfully";
    }
}
