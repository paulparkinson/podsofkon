package podsofkon.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeleteNamespace {

    static ApiClient client;
    private static final Logger log = LoggerFactory.getLogger(DeleteNamespace.class);
    public String deleteNamespace(String appName) throws Exception {
        CoreV1Api api = new CoreV1Api(getApiClient());
        log.debug("deleting application/namespace..." + appName);
        api.deleteNamespace(appName, null , null, null, null, null, null);
        log.debug("deleted application/namespace..." + appName);
        return "application/namespace deleted successfully";
    }

    @NotNull
    private static ApiClient getApiClient() throws IOException {
        if (client != null) return client;
        client = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
                .newBuilder()
                .addInterceptor(interceptor)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        client.setHttpClient(newClient);
        return client;
    }
}
