package podsofkon.objectstorage;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.springframework.stereotype.Service;
import podsofkon.oci.AuthDetailsProviderFactory;

@Service
public class StorageStreamService {

    private ObjectStorage getClient() throws Exception {
//        AuthenticationDetailsProvider adp = OCIConfig.getAuthenticationDetailsProvider();
        ObjectStorage client = new ObjectStorageClient(AuthDetailsProviderFactory.getAuthenticationDetailsProvider());
        return client;
    }

//    private String getDefaultNamespace() throws Exception {
//        GetNamespaceResponse namespaceResponse = getClient().getNamespace(GetNamespaceRequest.builder().build());
//        return namespaceResponse.getValue();
//    }
//
//    public byte[] getBytesFromStorage(String bucket, String object) throws Exception {
//        return getBytesFromStorage(bucket, object, Region.US_ASHBURN_1.toString());
//    }

//    public byte[] getBytesFromStorage(String bucket, String object, String region) throws Exception {
//        return getBytesFromStorage(bucket, object, region, getDefaultNamespace());
//    }

    public byte[] getBytesFromStorage(String bucket, String object, String region, String namespace) throws Exception {
        ObjectStorage client = getClient();
        client.setRegion(Region.valueOf(region));

        GetObjectResponse getResponse = client.getObject(
                GetObjectRequest.builder().namespaceName(namespace).bucketName(bucket).objectName(object).build());

        return getResponse.getInputStream().readAllBytes();

    }
}
