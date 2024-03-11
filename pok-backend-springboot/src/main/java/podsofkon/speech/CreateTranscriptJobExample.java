package podsofkon.speech;

/** This is an automatically generated code sample.
 To make this code sample work in your Oracle Cloud tenancy,
 please replace the values for any parameters whose current values do not fit
 your use case (such as resource IDs, strings containing ‘EXAMPLE’ or ‘unique_id’, and
 boolean, number, and enum parameters with values not fitting your use case).
 */

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.aispeech.AIServiceSpeechClient;
import com.oracle.bmc.aispeech.model.*;
import com.oracle.bmc.aispeech.requests.*;
import com.oracle.bmc.aispeech.responses.*;
import podsofkon.XRApplication;
import podsofkon.oci.AuthDetailsProviderFactory;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.UUID;
import java.util.Arrays;


public class CreateTranscriptJobExample {

    public String process(String fileName) throws Exception {

        /**
         * Create a default authentication provider that uses the DEFAULT
         * profile in the configuration file.
         * Refer to <see href="https://docs.cloud.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm#SDK_and_CLI_Configuration_File>the public documentation</see> on how to prepare a configuration file.
         */
//        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
//        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        /* Create a service client */
        AIServiceSpeechClient client = AIServiceSpeechClient.builder().build(AuthDetailsProviderFactory.getAuthenticationDetailsProvider());

        /* Create a request and dependent object(s). */
        CreateTranscriptionJobDetails createTranscriptionJobDetails = CreateTranscriptionJobDetails.builder()
                .displayName("EXAMPLE-displayName-Value")
                .compartmentId(XRApplication.COMPARTMENT_ID)
                .description("EXAMPLE-description-Value")
                .additionalTranscriptionFormats(new ArrayList<>(Arrays.asList(CreateTranscriptionJobDetails.AdditionalTranscriptionFormats.Srt)))
                .modelDetails(TranscriptionModelDetails.builder()
                        .domain(TranscriptionModelDetails.Domain.Generic)
                        .languageCode(TranscriptionModelDetails.LanguageCode.FrFr).build())
                .normalization(TranscriptionNormalization.builder()
                        .isPunctuationEnabled(false)
                        .filters(new ArrayList<>(Arrays.asList(ProfanityTranscriptionFilter.builder()
                                .mode(ProfanityTranscriptionFilter.Mode.Mask).build()))).build())
                .inputLocation(ObjectListFileInputLocation.builder()
                        .objectLocation(ObjectLocation.builder()
                                .namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                                .bucketName(XRApplication.BUCKET_NAME)
                                .objectNames(new ArrayList<>(Arrays.asList(fileName))).build()).build())
                .outputLocation(OutputLocation.builder()
                        .namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                        .bucketName(XRApplication.BUCKET_NAME)
                        .prefix("EXAMPLE-prefix-Value").build())
//                .freeformTags(new HashMap<java.lang.String, java.lang.String>() {
//                    {
//                        put("EXAMPLE_KEY_Wk3ku","EXAMPLE_VALUE_raCwfdzF8zhmFi8zazKH");
//                    }
//                })
//                .definedTags(new HashMap<java.lang.String, java.util.Map<java.lang.String, java.lang.Object>>() {
//                    {
//                        put("EXAMPLE_KEY_Mys6P",new HashMap<java.lang.String, java.lang.Object>() {
//                            {
//                                put("EXAMPLE_KEY_rPY4E","EXAMPLE--Value");
//                            }
//                        });
//                    }
//                })
                .build();

        CreateTranscriptionJobRequest createTranscriptionJobRequest = CreateTranscriptionJobRequest.builder()
                .createTranscriptionJobDetails(createTranscriptionJobDetails)
//                .opcRetryToken("EXAMPLE-opcRetryToken-Value")
//                .opcRequestId("MCKDV06HLY6TXLA82ILD<unique_ID>")
                .build();

        /* Send request to the Client */
        CreateTranscriptionJobResponse response = client.createTranscriptionJob(createTranscriptionJobRequest);
        return "" + response;
    }


}

