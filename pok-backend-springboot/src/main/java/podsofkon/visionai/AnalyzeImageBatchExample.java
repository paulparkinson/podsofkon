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
import com.oracle.bmc.aivision.model.CreateImageJobDetails;
import com.oracle.bmc.aivision.model.ImageClassificationFeature;
import com.oracle.bmc.aivision.model.ImageFeature;
import com.oracle.bmc.aivision.model.ImageJob;
import com.oracle.bmc.aivision.model.ImageObjectDetectionFeature;
import com.oracle.bmc.aivision.model.ImageTextDetectionFeature;
import com.oracle.bmc.aivision.model.ObjectListInlineInputLocation;
import com.oracle.bmc.aivision.model.ObjectLocation;
import com.oracle.bmc.aivision.model.OutputLocation;
import com.oracle.bmc.aivision.requests.CreateImageJobRequest;
import com.oracle.bmc.aivision.requests.GetImageJobRequest;
import com.oracle.bmc.aivision.responses.CreateImageJobResponse;
import com.oracle.bmc.aivision.responses.GetImageJobResponse;

import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an example of how to use OCI Vision Service to batch analyze images in an async way.
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
public class AnalyzeImageBatchExample {

    private static final String ENDPOINT = "https://vision.aiservice.us-ashburn-1.oci.oraclecloud.com";
    private static final String REGION = "us-ashburn-1";

    // You can run this demo directly if your DEFAULT_PROFILE is using ocas-test tenancy
    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "DEFAULT";
    private static final String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaasdfh4p66frooq";

    private static final String NAMESPACE_NAME = "asdf";
    private static final String BUCKET_NAME = "async-demo";
    private static final String PREFIX = "async_job_result";
    // Generate a zip file for all analyzed results in user-specified output location in object storage
    private final static boolean ENABLE_ZIP = false; // set true to enable zip output feature

    /**
     * The entry point for the example.
     *
     * @param args Arguments to provide to the example. This example expects no arguments.
     */
     void main(String[] args) throws Exception {

        System.out.println("Start Running AnalyzeImageBatch Example ...");

        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI config file
        // "~/.oci/config", and a profile in that config with the name defined in CONFIG_PROFILE variable.
        final ConfigFileReader.ConfigFile configFile =  ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        // Set up AI Service Vision client with credentials and endpoint
        final AIServiceVisionClient aiServiceVisionClient = new AIServiceVisionClient(provider);
        //aiServiceVisionClient.setEndpoint(ENDPOINT);
        aiServiceVisionClient.setRegion(REGION);

        // Build a list of features to indicate what we want to do with this image
        List<ImageFeature> imageFeatures = new ArrayList<>();
        // Now let's add ImageFeature(s) into a list, you can add multiple features if you want
        ImageFeature classifyFeature = ImageClassificationFeature.builder()
                .maxResults(10)
                .build();
        ImageFeature detectImageFeature = ImageObjectDetectionFeature.builder()
                .maxResults(10)
                .build();
        ImageFeature textDetectImageFeature = ImageTextDetectionFeature.builder().build();

        imageFeatures.add(classifyFeature);
        imageFeatures.add(detectImageFeature);
        imageFeatures.add(textDetectImageFeature);

        // Choose input file(s) from object storage, make sure you have created the bucket and files exist
        ObjectLocation objectLocation1 = ObjectLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .objectName("000046611bd6b353.jpg")
                .build();

        ObjectLocation objectLocation2 = ObjectLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .objectName("0ead8e640fa5e475.jpg")
                .build();

        ObjectLocation objectLocation3 = ObjectLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .objectName("book-page.png")
                .build();

        List<ObjectLocation> objectLocations = Lists.newArrayList(objectLocation1, objectLocation2, objectLocation3);

        ObjectListInlineInputLocation objectListInlineInputLocation = ObjectListInlineInputLocation.builder()
                .objectLocations(objectLocations).build();


        // Choose output location in object storage, make sure you have created the bucket
        OutputLocation outputLocation = OutputLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .prefix(PREFIX)
                .build();

        // Build the body and request with all information above
        CreateImageJobDetails createImageJobDetails = CreateImageJobDetails.builder()
                .compartmentId(COMPARTMENT_ID)
                .features(imageFeatures)
                .inputLocation(objectListInlineInputLocation)
                .outputLocation(outputLocation)
                .isZipOutputEnabled(ENABLE_ZIP)
                .build();

        CreateImageJobRequest request = CreateImageJobRequest.builder()
                .createImageJobDetails(createImageJobDetails)
                .build();

        // Send request and parse the response
        CreateImageJobResponse response = aiServiceVisionClient.createImageJob(request);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

        String json = mapper.writeValueAsString(response.getImageJob());
        System.out.println("AnalyzeImageBatch Job Info.:");
        System.out.println(json);

        // Start get job result
        System.out.println("Calling getImageJob API ...");
        String jobId = response.getImageJob().getId();
        GetImageJobRequest getJobRequest = GetImageJobRequest.builder().imageJobId(jobId).build();
        GetImageJobResponse getJobResponse = null;
        boolean jobInProgress = true;
        int waitSeconds = 0;
        // Poll the job until it is ready
        while(jobInProgress) {
            System.out.print("Job " + jobId + " is IN_PROGRESS ..." + StringUtils.repeat(".", waitSeconds) + "\r");

            getJobResponse = aiServiceVisionClient.getImageJob(getJobRequest);
            if (!ImageJob.LifecycleState.Accepted.equals(getJobResponse.getImageJob().getLifecycleState()) && ! ImageJob.LifecycleState.InProgress.equals(getJobResponse.getImageJob().getLifecycleState())) {
                jobInProgress = false;
            }

            waitSeconds++;
            Thread.sleep(1000L);
        }

        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        String getJobJson = mapper.writeValueAsString(getJobResponse.getImageJob());
        System.out.println("GetImageJob Result Info.:" + getJobJson);
    }
}
