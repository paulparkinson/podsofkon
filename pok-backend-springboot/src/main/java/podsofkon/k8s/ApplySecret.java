package podsofkon.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class ApplySecret {
    private static final Logger log = LoggerFactory.getLogger(ApplySecret.class);

    @Autowired
    private Environment environment;
    private static String registryAuth = System.getenv("registry.auth");
    public String createRegAuthSecret(String appName) throws Exception {
        log.info("createSecret...");
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
        log.info("registryAuth:" + registryAuth);
        registryAuth = registryAuth.replace("'", "\"");
        log.info("after  replace registryAuth:" + registryAuth);
        String encodedAuthString = Base64.getEncoder().encodeToString(registryAuth.getBytes());
        log.info("before encodedAuthString:" + encodedAuthString);
        Yaml yaml = new Yaml();
        String secretyaml = "apiVersion: v1\n" +
                "kind: Secret\n" +
                "metadata:\n" +
                "  name: registry-auth\n" +
//                "  namespace: " + appName +"\n" +
                "data:\n" +
                "  .dockerconfigjson: " + encodedAuthString + "\n" +
                "type: kubernetes.io/dockerconfigjson\n";
        log.debug("secret yaml..." + secretyaml);
        V1Secret v1Secret = yaml.loadAs(secretyaml, V1Secret.class);
        String namespace = appName;
        log.debug("secret creating..." + v1Secret);
        V1Secret createResult = api.createNamespacedSecret(
                namespace, v1Secret, null, null, null, null);
        log.debug("secret created");
        return "image pull secret (registry-auth) created successfully";
    }
    public String createDataSourceSecret(String appName, String serviceName, String servicePassword)
            throws Exception {
        log.info("createDataSourceSecret...");
        ApiClient client = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
                .newBuilder()
                .addInterceptor(interceptor)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        client.setHttpClient(newClient);
        log.debug("client:" + client);
        CoreV1Api api = new CoreV1Api(client);
        Yaml yaml = new Yaml();
        String secretName = serviceName + "-db-secrets";
        String secretyaml = "apiVersion: v1\n" +
                "kind: Secret\n" +
                "metadata:\n" +
                "  name: " + secretName + "\n" +
                "data:\n" +
                "  db.username: " + Base64.getEncoder().encodeToString(serviceName.getBytes()) + "\n" +
//                "  db.name: " + Base64.getEncoder().encodeToString(DBOperations.DB_NAME.getBytes())  + "\n" +
                "  db.password: " + Base64.getEncoder().encodeToString(environment.getProperty("spring.datasource.password").getBytes())  + "\n" +
//                "  db.service: " + Base64.getEncoder().encodeToString(DBOperations.DB_SERVICE.getBytes())  + "\n" +
                "type: Opaque\n";
        log.debug("secret yaml..." + secretyaml);
        V1Secret v1Secret = yaml.loadAs(secretyaml, V1Secret.class);
        String namespace = appName;
        log.debug("secret creating..." + v1Secret);
        V1Secret createResult = api.createNamespacedSecret(
                namespace, v1Secret, null, null, null, null);
        log.debug("secret created");
        return "database secret (" + secretName + ") created successfully";
    }
    public String updateOracleSpringAdminSecret(String adminPassword)
            throws Exception {
        log.info("updateOracleSpringAdminSecret...");
        ApiClient client = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
                .newBuilder()
                .addInterceptor(interceptor)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        client.setHttpClient(newClient);
        log.debug("client:" + client);
        CoreV1Api api = new CoreV1Api(client);
        Yaml yaml = new Yaml();
        String secretyaml = "apiVersion: v1\n" +
                "kind: Secret\n" +
                "metadata:\n" +
                "  name: oracle-spring-admin-admin-secret\n" +
                "  namespace: oracle-spring-admin\n" +
                "type: Opaque\n" +
                "data:\n" +
                "  password: " + Base64.getEncoder().encodeToString(adminPassword.getBytes())  + "\n";
        log.debug("secret yaml..." + secretyaml);
        V1Secret v1Secret = yaml.loadAs(secretyaml, V1Secret.class);
        log.debug("secret deleting..." + v1Secret);
        V1Status deleteResult = api.deleteNamespacedSecret("oracle-spring-admin-admin-secret",
                "oracle-spring-admin",  null, null, null, null, null, null);

        log.debug("secret deleted, now updating/creating..." + v1Secret);
        V1Secret createResult = api.createNamespacedSecret(
                "oracle-spring-admin", v1Secret, null, null, null, null);
        log.debug("updateOracleSpringAdminSecret successful");
        return "Oracle Spring Admin password updated successfully";
    }
}
