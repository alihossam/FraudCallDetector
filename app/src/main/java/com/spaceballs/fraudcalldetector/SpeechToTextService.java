package com.spaceballs.fraudcalldetector;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.File;
import java.io.IOException;

public class SpeechToTextService {

    private RecognizeOptions options;
    private final SpeechToText speechService;

    public SpeechToTextService(){
        Authenticator authenticator = new IamAuthenticator("");
        speechService = new SpeechToText(authenticator);
        options = new RecognizeOptions.Builder()
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    public void getTranscripts(String filePath){
        File audioFile = new File(filePath);
        try {

            this.options = options.newBuilder()
                    .audio(audioFile)
                    .build();

            speechService.recognizeUsingWebSocket(this.options, new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechRecognitionResults speechResults) {
                    String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    System.out.println(text);
                    ///////////////////////////////////////////////
                    //Call ML API here, If Scam send a notification
                    //This can be done with a callback from an MLService class
                }

                @Override
                public void onConnected() {

                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onDisconnected() {

                }
            });

        } catch (IOException e) {
            System.out.println("Failed to get call transcripts. Err: " + e.getMessage());
        }
    }
}
