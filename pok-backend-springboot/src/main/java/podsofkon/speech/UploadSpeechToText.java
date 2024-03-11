package podsofkon.speech;
//
//import com.oracle.oci.sdk.speech.SpeechClient;
//import com.oracle.oci.sdk.speech.model.CreateTranscriptionRequest;
//import com.oracle.oci.sdk.speech.model.CreateTranscriptionResult;
//import com.oracle.oci.sdk.speech.model.LanguageCode;
//import com.oracle.oci.sdk.speech.model.Transcription;
//import com.oracle.oci.sdk.speech.model.TranscriptionJob;
//import com.oracle.oci.sdk.speech.model.TranscriptionJobLifecycleState;
//import com.oracle.oci.sdk.speech.model.TranscriptionJobSummary;

public class UploadSpeechToText {
    public static void process() {
//        SpeechClient speechClient = new SpeechClient(config);
///**
// * Create a CreateTranscriptionRequest object to specify the parameters for the transcript job. You will need to specify the following:
// * The language of the audio
// * The audio file to transcribe (either a URL or a file stored in an Oracle Cloud Infrastructure Object Storage bucket)
// * The ID of the compartment where the transcript job will be created
// */
//        CreateTranscriptionRequest request = CreateTranscriptionRequest.builder()
//                .languageCode(LanguageCode.EnUs)
//                .audioUrl("https://example.com/audio.mp3")
//                .compartmentId("ocid1.compartment.oc1..xxxxx")
//                .build();
////Use the speechClient.createTranscription method to create the transcript job:
//        CreateTranscriptionResult result = speechClient.createTranscription(request);
///**
// * The result object will contain the Transcription object, which has the ID of the transcript job. You can use this ID to retrieve the transcript job and check its status.
// *
// * To retrieve the transcript job, you can use the speechClient.getTranscription method and pass in the ID of the transcript job:
// */
//        Transcription transcription = speechClient.getTranscription(GetTranscriptionRequest.builder()
//                .transcriptionId(result.getTranscription().getId())
//                .build()).getTranscription();
///**
// * To check the status of the transcript job, you can use the transcription.getLifecycleState method to get the current state of the transcript job. You can use the TranscriptionJobLifecycleState enum to check if the job is still in progress, or if it has completed or failed.
// */
//        if (transcription.getLifecycleState() == TranscriptionJobLifecycleState.Completed) {
//            //
//
//        }
    }
}