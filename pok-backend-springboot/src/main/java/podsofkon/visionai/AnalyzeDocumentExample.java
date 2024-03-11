/**
 * Copyright (c) 2016, 2021, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package podsofkon.visionai;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.aivision.AIServiceVisionClient;
import com.oracle.bmc.aivision.model.AnalyzeDocumentDetails;
import com.oracle.bmc.aivision.model.DocumentClassificationFeature;
import com.oracle.bmc.aivision.model.DocumentFeature;
import com.oracle.bmc.aivision.model.DocumentKeyValueDetectionFeature;
import com.oracle.bmc.aivision.model.DocumentLanguage;
import com.oracle.bmc.aivision.model.DocumentLanguageClassificationFeature;
import com.oracle.bmc.aivision.model.DocumentTableDetectionFeature;
import com.oracle.bmc.aivision.model.DocumentTextDetectionFeature;
import com.oracle.bmc.aivision.model.InlineDocumentDetails;
import com.oracle.bmc.aivision.model.OutputLocation;
import com.oracle.bmc.aivision.requests.AnalyzeDocumentRequest;
import com.oracle.bmc.aivision.responses.AnalyzeDocumentResponse;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an example of how to use OCI Vision Service to analyze document.
 * <p>
 * The Vision Service queried by this example will be assigned:
 * <ul>
 * <li>an endpoint url defined by constant ENDPOINT</li>
 * <li>
 * The configuration file used by service clients will be sourced from the default
 * location (~/.oci/config) and the CONFIG_PROFILE profile will be used.
 * </li>
 * </ul>
 * </p>
 */
public class AnalyzeDocumentExample {
    private static final String ENDPOINT = "https://vision.aiservice.us-ashburn-1.oci.oraclecloud.com";
    private static final String REGION = "us-ashburn-1";

    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "DEFAULT";
    // this compartmentId will only be used for boat user
    private final static String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaasdfvgyrvxfuh4p66frooq";

    private static final String NAMESPACE_NAME = "asdf";
    private static final String BUCKET_NAME = "sync-demo";
    private static final String PREFIX = "result";

    /**
     * The entry point for the example.
     *
     * @param args Arguments to provide to the example. This example expects no arguments.
     */
    String main(String[] args) throws Exception {
        if (args.length > 0) {
            throw new IllegalArgumentException(
                    "This example expects no argument");
        }

        System.out.println("Start Running AnalyzeDocument Example ...");

        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI config file
        // "~/.oci/config", and a profile in that config with the name defined in CONFIG_PROFILE variable.
        final ConfigFileReader.ConfigFile configFile =  ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        // Set up AI Service Vision client with credentials and endpoint
        final AIServiceVisionClient aiServiceVisionClient = new AIServiceVisionClient(provider);
        //aiServiceVisionClient.setEndpoint(ENDPOINT);
        aiServiceVisionClient.setRegion(REGION);

        // Read document file from resources folder
        byte[] bytes = Files.readAllBytes(Paths.get("src/resources/receipt.jpg"));

        // Now let's add DocumentTextDetection features into a list, you can add multiple features if you want
        List<DocumentFeature> features = new ArrayList<>();
        DocumentFeature textDetectionFeature = DocumentTextDetectionFeature.builder()
                .generateSearchablePdf(false)
                .build();

        DocumentFeature textDetectionFeatureSearchablePdf = DocumentTextDetectionFeature.builder()
                .generateSearchablePdf(false)
                .build();

        DocumentFeature documentClassificationFeature = DocumentClassificationFeature.builder()
                .build();

        DocumentFeature documentKeyValueDetectionFeature = DocumentKeyValueDetectionFeature.builder()
                .build();

        DocumentFeature documentLanguageClassificationFeature = DocumentLanguageClassificationFeature.builder()
                .build();

        DocumentFeature documentTableDetectionFeature = DocumentTableDetectionFeature.builder()
                .build();

        features.add(textDetectionFeature);
        features.add(textDetectionFeatureSearchablePdf);
        features.add(documentClassificationFeature);
        features.add(documentKeyValueDetectionFeature);
        features.add(documentLanguageClassificationFeature);
        features.add(documentTableDetectionFeature);

        // Let's wrap document bytes into InlineDocumentDetails
        InlineDocumentDetails inlineDocumentDetails = InlineDocumentDetails.builder()
                .data(bytes)
                .build();

        // This is optional, fill in object storage location for searchable PDF. Make sure you have created the bucket.
        // The result file will be named as searchable_document.pdf and saved in the bucket.
        // If outputLocation is not specified, it will include pdf bytes in response.
        OutputLocation outputLocation = OutputLocation.builder()
                .namespaceName(NAMESPACE_NAME)
                .bucketName(BUCKET_NAME)
                .prefix(PREFIX)
                .build();

        // Now include everything in AnalyzeDocumentDetails body
        AnalyzeDocumentDetails analyzeDocumentDetails = AnalyzeDocumentDetails.builder()
                .features(features)
                .document(inlineDocumentDetails)
                .language(DocumentLanguage.Eng)
                .outputLocation(outputLocation)
//                .compartmentId(COMPARTMENT_ID) //uncomment this line if using boat user
                .build();

        // Build request, send, and get response
        AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                .analyzeDocumentDetails(analyzeDocumentDetails)
                .build();

        AnalyzeDocumentResponse response = aiServiceVisionClient.analyzeDocument(request);

        // Parse response
        ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

        String json = mapper.writeValueAsString(response.getAnalyzeDocumentResult());

        System.out.println("AnalyzeDocument Result");
        System.out.println(json);
        return json;
    }
}
