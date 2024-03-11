package podsofkon.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.util.ClientBuilder;

import java.util.concurrent.TimeUnit;

public class DeleteDeployment {
        private static final Logger log = LoggerFactory.getLogger(DeleteDeployment.class);


    public String deleteDeployment(String namespace, String deploymentName) throws Exception {
            // Set the namespace and name of the deployment you want to delete.
//            String namespace = "default";
//            String deploymentName = "example-deployment";

            try {
//                ApiClient client = ClientBuilder.cluster().build();
//                Configuration.setDefaultApiClient(client);



                ApiClient client = Config.defaultClient();
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient newClient = client.getHttpClient()
                        .newBuilder()
//                        .addInterceptor(interceptor)
                        .readTimeout(0, TimeUnit.SECONDS)
                        .build();
                client.setHttpClient(newClient);

                AppsV1Api appsV1Api = new AppsV1Api(client);
                V1DeleteOptions deleteOptions = new V1DeleteOptions();
                appsV1Api.deleteNamespacedDeployment(
                        deploymentName,
                        namespace,
                        null, // pretty print output
                        null, // dry run option
                        null, // grace period for deletion
                        false, // orphan dependents
                        "Background", // propagation policy: Foreground, Background or Orphan
                        deleteOptions // body options
                );

                System.out.println("Deployment deleted: " + deploymentName);

            } catch (ApiException e) {
                System.err.println("Exception when calling AppsV1Api#deleteNamespacedDeployment e:" + e);
          //      e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                System.err.println("Exception when setting up the client");
        //        e.printStackTrace();
                return e.getMessage();
            }
            return "deployment deleted successfully";
        }

    public String deleteDeployment0(String appName, String deploymentName) throws Exception {
        try {
            ApiClient client = Config.defaultClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient newClient = client.getHttpClient()
                    .newBuilder()
                    .addInterceptor(interceptor)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();
            client.setHttpClient(newClient);
            AppsV1Api apiInstance = new AppsV1Api();
            V1DeleteOptions deleteOptions = new V1DeleteOptions();
            apiInstance.deleteNamespacedDeployment(
                    deploymentName, appName, null, null, null, null, null, null);

            System.out.println("Deployment deleted successfully!");
        } catch (ApiException e) {
            e.printStackTrace();
            System.err.println("Exception when deleting the deployment: " + e);
            System.err.println("Exception when deleting the deployment: " + e.getResponseBody());
        }catch (Exception e) {
            System.err.println("Exception when deleting the deployment: " + e);
        }
            return "deleted deployment";
    }

}
