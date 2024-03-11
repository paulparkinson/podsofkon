 /**
 * Copyright (c) 2016, 2021, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package podsofkon.speech;


 import com.oracle.bmc.aispeech.AIServiceSpeechClient;
 import com.oracle.bmc.aispeech.model.*;
 import com.oracle.bmc.aispeech.requests.*;
 import com.oracle.bmc.auth.AuthenticationDetailsProvider;
 import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
 import org.jetbrains.annotations.NotNull;
 import org.springframework.web.multipart.MultipartFile;
 import podsofkon.XRApplication;
 import podsofkon.oci.AuthDetailsProviderFactory;

 import java.io.File;
 import java.util.*;

 import static com.oracle.bmc.aispeech.model.TranscriptionModelDetails.builder;

 public class SpeechAI {
     private static final String ENDPOINT = "https://vision.aiservice.us-ashburn-1.oci.oraclecloud.com";
 //    private static final String ENDPOINT = "https://vision.aiservice.us-ashburn-1.oci.oraclecloud.com";
     // https://vision.aiservice.us-phoenix-1.oci.oraclecloud.com/20220125/actions/analyzeImage

     private final static String CONFIG_LOCATION = "src/resources/config";
 //    private final static String CONFIG_LOCATION = "~/.oci/config";
     private final static String CONFIG_PROFILE = "DEFAULT";
     // this compartmentId will only be used for boat user
 //    private final static String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaasdfvxfuh4p66frooq";


     File configFile;
     public String process(String fileName) throws Exception {
         System.out.println("speech detection ");
         AIServiceSpeechClient aiServiceSpeechClient =
                 new AIServiceSpeechClient(AuthDetailsProviderFactory.getAuthenticationDetailsProvider());
//                 AIServiceSpeechClient.builder().build(AuthDetailsProviderFactory.getAuthenticationDetailsProvider());

         //aiServiceVisionClient.setEndpoint(ENDPOINT);
         aiServiceSpeechClient.setRegion(XRApplication.REGION);
         /**
          display_name=SAMPLE_DISPLAY_NAME,
          compartment_id=SAMPLE_COMPARTMENT_ID,
          description=SAMPLE_DESCRIPTION,
          model_details=SAMPLE_MODE_DETAILS,
          input_location=SAMPLE_INPUT_LOCATION,
          output_location=SAMPLE_OUTPUT_LOCATION)
          */
         List<String> objectNamesList = new ArrayList<String>() ;
         objectNamesList.add(fileName);
         ObjectLocation objectLocationForInput = ObjectLocation.builder().namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                 .bucketName(XRApplication.BUCKET_NAME).objectNames(new ArrayList<>(Arrays.asList("leia.m4a"))).build();
         CreateTranscriptionJobDetails createTranscriptionJobDetails = CreateTranscriptionJobDetails.builder()
                 .displayName("xrtestdisplayname")
                 .compartmentId(XRApplication.COMPARTMENT_ID)
                 .description("xr speech ai test descr")
                 .modelDetails(builder().build())
                 //ObjectListInlineInputLocation ObjectListFileInputLocation
                 .inputLocation(ObjectListInlineInputLocation.builder()
                         .objectLocations(   new ArrayList<>(Arrays.asList(  objectLocationForInput ) )).build()
                         )
                 .outputLocation(
                         OutputLocation.builder().namespaceName(XRApplication.OBJECTSTORE_NAMESPACENAME)
                                 .bucketName(XRApplication.BUCKET_NAME)
                                 .prefix("speechai-output")
                                 .build())
                 .build();
         CreateTranscriptionJobRequest createTranscriptionJobRequest =
                 CreateTranscriptionJobRequest.builder().createTranscriptionJobDetails(createTranscriptionJobDetails).build();
         aiServiceSpeechClient.createTranscriptionJob(createTranscriptionJobRequest);
         return "processing of audio file complete";
     }

     /**

      * python example from livelabs workshop...

      import oci
      from oci.config import from_file

      ai_client = oci.ai_speech.AIServiceSpeechClient(oci.config.from_file())

      # Give your job related details in these fields
      SAMPLE_DISPLAY_NAME = "<job_name>"
      SAMPLE_COMPARTMENT_ID = "<compartment_id>"
      SAMPLE_DESCRIPTION = "<job_description>"
      SAMPLE_NAMESPACE = "<sample_namespace>"
      SAMPLE_BUCKET = "<bucket_name>"
      JOB_PREFIX = "Python_SDK_DEMO"
      LANGUAGE_CODE = "en-US"
      FILE_NAMES = ["<file1>", "<file2>"]
      NEW_COMPARTMENT_ID = "<new_compartment>"
      NEW_DISPLAY_NAME = "<new_name>"
      NEW_DESCRIPTION = "<new_description>"
      SAMPLE_MODE_DETAILS = oci.ai_speech.models.TranscriptionModelDetails(domain="GENERIC", language_code=LANGUAGE_CODE)
      SAMPLE_OBJECT_LOCATION = oci.ai_speech.models.ObjectLocation(namespace_name=SAMPLE_NAMESPACE, bucket_name=SAMPLE_BUCKET,
      object_names=FILE_NAMES)
      SAMPLE_INPUT_LOCATION = oci.ai_speech.models.ObjectListInlineInputLocation(
      location_type="OBJECT_LIST_INLINE_INPUT_LOCATION", object_locations=[SAMPLE_OBJECT_LOCATION])
      SAMPLE_OUTPUT_LOCATION = oci.ai_speech.models.OutputLocation(namespace_name=SAMPLE_NAMESPACE, bucket_name=SAMPLE_BUCKET,
      prefix=JOB_PREFIX)
      COMPARTMENT_DETAILS = oci.ai_speech.models.ChangeTranscriptionJobCompartmentDetails(compartment_id=NEW_COMPARTMENT_ID)
      UPDATE_JOB_DETAILS = oci.ai_speech.models.UpdateTranscriptionJobDetails(display_name=NEW_DISPLAY_NAME, description=NEW_DESCRIPTION)

      # Create Transcription Job with details provided
      transcription_job_details = oci.ai_speech.models.CreateTranscriptionJobDetails(display_name=SAMPLE_DISPLAY_NAME,
      compartment_id=SAMPLE_COMPARTMENT_ID,
      description=SAMPLE_DESCRIPTION,
      model_details=SAMPLE_MODE_DETAILS,
      input_location=SAMPLE_INPUT_LOCATION,
      output_location=SAMPLE_OUTPUT_LOCATION)

      transcription_job = None
      print("***CREATING TRANSCRIPTION JOB***")
      try:
      transcription_job = ai_client.create_transcription_job(create_transcription_job_details=transcription_job_details)
      except Exception as e:
      print(e)
      else:
      print(transcription_job.data)


      print("***CANCELLING TRANSCRIPTION JOB***")
      # Cancel transcription job and all tasks under it
      try:
      ai_client.cancel_transcription_job(transcription_job.data.id)
      except Exception as e:
      print(e)


      print("***UPDATING TRANSCRIPTION JOB DETAILS")
      try:
      ai_client.update_transcription_job(transcription_job.data.id, UPDATE_JOB_DETAILS)
      except Exception as e:
      print(e)

      print("***MOVE TRANSCRIPTION JOB TO NEW COMPARTMENT***")
      try:
      ai_client.change_transcription_job_compartment(transcription_job.data.id, COMPARTMENT_DETAILS)
      except Exception as e:
      print(e)


      print("***GET TRANSCRIPTION JOB WITH ID***")
      # Gets Transcription Job with given Transcription job id
      try:
      if transcription_job.data:
      transcription_job = ai_client.get_transcription_job(transcription_job.data.id)
      except Exception as e:
      print(e)
      else:
      print(transcription_job.data)


      print("***GET ALL TRANSCRIPTION JOBS IN COMPARTMENT***")
      # Gets All Transcription Jobs from a particular compartment
      try:
      transcription_jobs = ai_client.list_transcription_jobs(compartment_id=SAMPLE_COMPARTMENT_ID)
      except Exception as e:
      print(e)
      else:
      print(transcription_jobs.data)



      print("***GET ALL TaskS FROM TRANSCIRPTION JOB ID***")
      #Gets Transcription tasks under given transcription Job Id
      transcription_tasks = None
      try:
      transcription_tasks = ai_client.list_transcription_tasks(transcription_job.data.id)
      except Exception as e:
      print(e)
      else:
      print(transcription_tasks.data)


      print("***GET PRATICULAR TRANSCRIPTION Task USING JOB AND Task ID***")
      # Gets a Transcription Task with given Transcription task id under Transcription Job id
      transcription_task = None
      try:
      if transcription_tasks.data:

      transcription_task = ai_client.get_transcription_task(transcription_job.data.id, transcription_tasks.data.items[0].id)
      except Exception as e:
      print(e)
      else:
      print(transcription_task.data)


      print("***CANCEL PARTICULAR TRANSCRIPTION Task***")
      try:
      if transcription_task:
      ai_client.cancel_transcription_task(transcription_job.data.id, transcription_task.data.id)
      except Exception as e:
      print(e)
      */
 }
