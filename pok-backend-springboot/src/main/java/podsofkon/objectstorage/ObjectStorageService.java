package podsofkon.objectstorage;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;
import com.oracle.bmc.objectstorage.transfer.ProgressReporter;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.springframework.web.multipart.MultipartFile;
import podsofkon.XRApplication;
import podsofkon.oci.AuthDetailsProviderFactory;

import java.io.*;

import java.util.Map;

import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;

public class ObjectStorageService {


    public String main(MultipartFile file, byte[] bytes, boolean isConfigFileAuth) throws Exception {

        AuthenticationDetailsProvider provider = AuthDetailsProviderFactory.getAuthenticationDetailsProvider();
        ObjectStorageClient client = new ObjectStorageClient(provider);
        client.setRegion(Region.US_PHOENIX_1);

        GetNamespaceResponse namespaceResponse =
                client.getNamespace(GetNamespaceRequest.builder().build());
        String namespaceName = namespaceResponse.getValue();
        System.out.println("Using namespace: " + namespaceName);

        Builder listBucketsBuilder =
                ListBucketsRequest.builder()
                        .namespaceName(namespaceName)
                        .compartmentId(provider.getTenantId());

        String nextToken = null;
        do {
            listBucketsBuilder.page(nextToken);
            ListBucketsResponse listBucketsResponse =
                    client.listBuckets(listBucketsBuilder.build());
            for (BucketSummary bucket : listBucketsResponse.getItems()) {
                System.out.println("Found bucket: " + bucket.getName());
            }
            nextToken = listBucketsResponse.getOpcNextPage();
        } while (nextToken != null);

        // fetch the file from the object storage
        String bucketName = "xrbucket";
        String objectName = file.getName();
        GetObjectResponse getResponse =
                client.getObject(
                        GetObjectRequest.builder()
                                .namespaceName(namespaceName)
                                .bucketName(bucketName)
                                .objectName(objectName)
                                .build());

        // stream contents should match the file uploaded
        try (final InputStream fileStream = getResponse.getInputStream()) {
            // use fileStream
        } // try-with-resources automatically closes fileStream

        client.close();
        return "success from objectstore";
    }


    public String upload(MultipartFile file) throws Exception {

        String objectName = file.getOriginalFilename();
//        Map<String, String> metadata = null;
//        String contentType = null;
//        String contentEncoding = null;
//        String contentLanguage = null;

        File body = new File("whateverfile");
        OutputStream outStream = new FileOutputStream(body);
        outStream.write(file.getBytes());


        ObjectStorageClient client = new ObjectStorageClient(AuthDetailsProviderFactory.getAuthenticationDetailsProvider());
        client.setRegion(Region.US_PHOENIX_1);

        // configure upload settings as desired
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(XRApplication.BUCKET_NAME)
                        .namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                        .objectName(objectName)
//                        .contentType(file.getContentType())
//                        .contentLanguage(contentLanguage)
//                        .contentEncoding(contentEncoding)
//                        .opcMeta(metadata)
                        .build();

        UploadRequest uploadDetails =
                UploadRequest.builder(body).allowOverwrite(true)
                        .progressReporter(new ProgressReporter() {
                            @Override
                            public void onProgress(long l, long l1) {
                                System.out.println("ObjectStorage.onProgress l = " + l + ", l1 = " + l1);
                            }
                        })
                        .build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw
        // BmcException
        UploadManager.UploadResponse response = uploadManager.upload(uploadDetails);
        System.out.println(response);

        // fetch the object just uploaded
        GetObjectResponse getResponse =
                client.getObject(
                        GetObjectRequest.builder()
                                .namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                                .bucketName(XRApplication.BUCKET_NAME)
                                .objectName(objectName)
                                .build());

        // use the response's function to print the fetched object's metadata
        System.out.println(getResponse.getOpcMeta());

        // stream contents should match the file uploaded
        try (final InputStream fileStream = getResponse.getInputStream()) {
            // use fileStream
        } // try-with-resources automatically closes fileStream
        return "successful upload";
    }
}