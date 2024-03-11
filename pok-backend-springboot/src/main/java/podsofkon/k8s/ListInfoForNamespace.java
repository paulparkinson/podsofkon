/**
 * 
 */
package podsofkon.k8s;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ListInfoForNamespace {
    /**
    private static final Logger log = LoggerFactory.getLogger(ListInfoForNamespace.class);
    public String getPodsInfoForNamespace(String namespace) throws Exception {
        V1PodList items = getPodsForNamespace(namespace);
        String returnString = "";
        for (V1Pod v1Pod : items.getItems()) {
            V1ObjectMeta metadata = v1Pod.getMetadata();
            ;
            returnString += "name:" + metadata.getName() +
                    " " + " status:" + v1Pod.getStatus().getContainerStatuses().iterator().next() //todo only gives one
            ;
            Map<String, String> annotations = metadata.getAnnotations();
            if (annotations != null)  for (Map.Entry<String, String> entry : annotations.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                returnString += "\n" + entry.getKey() + ":" + entry.getValue();
            }
        }
        V1ServiceList serviceitems = getServicesForNamespace(namespace);
        for (V1Service v1Service : serviceitems.getItems()) {
            V1ObjectMeta metadata = v1Service.getMetadata();
            ;
            returnString += "name:" + metadata.getName() +
                    " " + " kind:" + v1Service.getKind()
            ;
            Map<String, String> annotations = metadata.getAnnotations();
            if (annotations != null)  for (Map.Entry<String, String> entry : annotations.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                returnString += "\n" + entry.getKey() + ":" + entry.getValue();
            }
        }

        return returnString;
    }

     static V1PodList getPodsForNamespace(String namespace) throws IOException, ApiException {
        ApiClient client  = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
          .newBuilder()
          .addInterceptor(interceptor)
          .readTimeout(0, TimeUnit.SECONDS)
          .build();
        client.setHttpClient(newClient);
        CoreV1Api api = new CoreV1Api(client);
        V1PodList items = api.listNamespacedPod(namespace,null, null, null, null, null, null, null, null, 10, false);
        return items;
    }
     static V1ServiceList getServicesForNamespace(String namespace) throws IOException, ApiException {
        ApiClient client  = Config.defaultClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> log.info(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient newClient = client.getHttpClient()
          .newBuilder()
          .addInterceptor(interceptor)
          .readTimeout(0, TimeUnit.SECONDS)
          .build();
        client.setHttpClient(newClient);
        CoreV1Api api = new CoreV1Api(client);
        V1ServiceList items = api.listNamespacedService(namespace,null, null, null, null, null, null, null, null, 10, false);
        return items;
    }
     */
}
