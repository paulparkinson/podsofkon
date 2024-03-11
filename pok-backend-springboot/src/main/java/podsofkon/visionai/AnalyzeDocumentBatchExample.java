/**
 * Copyright (c) 2016, 2021, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package podsofkon.visionai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.aivision.AIServiceVisionClient;
import com.oracle.bmc.aivision.model.CreateDocumentJobDetails;
import com.oracle.bmc.aivision.model.DocumentClassificationFeature;
import com.oracle.bmc.aivision.model.DocumentFeature;
import com.oracle.bmc.aivision.model.DocumentJob;
import com.oracle.bmc.aivision.model.DocumentKeyValueDetectionFeature;
import com.oracle.bmc.aivision.model.DocumentLanguage;
import com.oracle.bmc.aivision.model.DocumentLanguageClassificationFeature;
import com.oracle.bmc.aivision.model.DocumentTextDetectionFeature;
import com.oracle.bmc.aivision.model.ObjectListInlineInputLocation;
import com.oracle.bmc.aivision.model.ObjectLocation;
import com.oracle.bmc.aivision.model.OutputLocation;
import com.oracle.bmc.aivision.requests.CreateDocumentJobRequest;
import com.oracle.bmc.aivision.requests.GetDocumentJobRequest;
import com.oracle.bmc.aivision.responses.CreateDocumentJobResponse;
import com.oracle.bmc.aivision.responses.GetDocumentJobResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * This class provides an example of how to use OCI Vision Service to batch analyze documents in an async way.
 * <p>
 * The Vision Service queried by this example will be assigned:
 * <ul>
 * <li>an endpoint url defined by constant ENDPOINT</li>
 * <li>an object storage namespace defined by constant NAMESPACE_NAME</li>
 * <li>
 * The configuration file used by service clients will be sourced from the default
 * location (~/.oci/config) and the CONFIG_PROFILE profile will be used.
 * </li>
 * </ul>
 * </p>
 */
public class AnalyzeDocumentBatchExample {
    private static final String ENDPOINT = "https://vision.aiservice.us-ashburn-1.oci.oraclecloud.com";
    private static final String REGION = "us-ashburn-1";

    // You can run this demo directly if your DEFAULT_PROFILE is using ocas-test tenancy
    private final static String CONFIG_LOCATION = "~/.oci/config";
    private final static String CONFIG_PROFILE = "DEFAULT";
    private final static String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaaaa6xo4q4r2l2nvcr3sl657pwla5k3xtbk2s6vgyrvxfuh4p66frooq";

    private static final String NAMESPACE_NAME = "axhheqi2ofpb";
    private final static String BUCKET_NAME = "async-demo";
    private final static String PREFIX = "async_job_result";
    // Generate a zip file for all analyzed results in user-specified output location in object storage
    private final static boolean ENABLE_ZIP = false; // set true to enable zip output feature


    /**
     * The entry point for the example.
     *
     * @param args Arguments to provide to the example. This example expects no arguments.
     */
    void main(String[] args) throws Exception {
        if (args.length > 0) {
            throw new IllegalArgumentException(
                    "This example expects no argument");
        }
        System.out.println("Start Running AnalyzeDocumentBatch Example ...");

        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI config file
        // "~/.oci/config", and a profile in that config with the name defined in CONFIG_PROFILE variable.
        final ConfigFileReader.ConfigFile configFile =  ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        // Set up AI Service Vision client with credentials and endpoint
        final AIServiceVisionClient aiServiceVisionClient = new AIServiceVisionClient(provider);
        //aiServiceVisionClient.setEndpoint(ENDPOINT);
        aiServiceVisionClient.setRegion(REGION);

        // Build a list of features to indicate what we want to do with this document
        List<DocumentFeature> documentFeatures = new ArrayList<>();

        // Now let's add DocumentTextDetectionFeature features into a list, you can add multiple features if you want
        DocumentFeature textDetectionFeature = DocumentTextDetectionFeature.builder()
                .generateSearchablePdf(false)
                .build();

        DocumentFeature documentClassificationFeature = DocumentClassificationFeature.builder()
                .build();

        DocumentFeature documentKeyValueDetectionFeature = DocumentKeyValueDetectionFeature.builder()
                .build();
        DocumentFeature documentLanguageClassificationFeature = DocumentLanguageClassificationFeature.builder()
                .build();
        documentFeatures.add(textDetectionFeature);
        documentFeatures.add(documentClassificationFeature);
        documentFeatures.add(documentKeyValueDetectionFeature);
        documentFeatures.add(documentLanguageClassificationFeature);

        // Choose input file(s) from object storage, make sure you have created the bucket and files exist
        ObjectLocation objectLocation1 = ObjectLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .objectName("book(2pages).pdf")
                .build();
        ObjectLocation objectLocation2 = ObjectLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .objectName("3pages.tiff")
                .build();
        List<ObjectLocation> objectLocations = Lists.newArrayList(objectLocation1, objectLocation2);
        ObjectListInlineInputLocation objectListInlineInputLocation = ObjectListInlineInputLocation.builder()
                .objectLocations(objectLocations).build();

        // Choose output location in object storage, make sure you have created the bucket
        OutputLocation outputLocation = OutputLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .prefix(PREFIX)
                .build();

        // Build the body and request with all information above
        CreateDocumentJobDetails createDocumentJobDetails = CreateDocumentJobDetails.builder()
                .compartmentId(COMPARTMENT_ID)
                .features(documentFeatures)
                .inputLocation(objectListInlineInputLocation)
                .language(DocumentLanguage.Eng)
                .outputLocation(outputLocation)
                .isZipOutputEnabled(ENABLE_ZIP)
                .build();
        CreateDocumentJobRequest request = CreateDocumentJobRequest.builder()
                .createDocumentJobDetails(createDocumentJobDetails)
                .build();

        // Send request and parse the response
        CreateDocumentJobResponse response = aiServiceVisionClient.createDocumentJob(request);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        String json = mapper.writeValueAsString(response.getDocumentJob());
        System.out.println("AnalyzeDocumentBatch Job Info.:");
        System.out.println(json);

        // Start get job result
        System.out.println("Calling getDocumentJob API ...");
        String jobId = response.getDocumentJob().getId();
        GetDocumentJobRequest getJobRequest = GetDocumentJobRequest.builder().documentJobId(jobId).build();
        GetDocumentJobResponse getJobResponse = null;
        boolean jobInProgress = true;
        int waitSeconds = 0;

        // Poll the job until it is ready
        while(jobInProgress) {
            System.out.print("Job " + jobId + " is IN_PROGRESS ..." + StringUtils.repeat(".", waitSeconds) + "\r");
            getJobResponse = aiServiceVisionClient.getDocumentJob(getJobRequest);
            if (!DocumentJob.LifecycleState.Accepted.equals(getJobResponse.getDocumentJob().getLifecycleState()) && ! DocumentJob.LifecycleState.InProgress.equals(getJobResponse.getDocumentJob().getLifecycleState())) {
                jobInProgress = false;
            }
            waitSeconds++;
            Thread.sleep(1000L);
        }
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        String getJobJson = mapper.writeValueAsString(getJobResponse.getDocumentJob());
        System.out.println("GetDocumentJob Result Info.:" + getJobJson);
    }
}
