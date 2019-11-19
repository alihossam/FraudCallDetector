package com.spaceballs.fraudcalldetector;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeechToTextService {

    private RecognizeOptions options;
    private final SpeechToText speechService;

    public SpeechToTextService() {
        Authenticator authenticator = new IamAuthenticator("Alb8CQY8aG63hxhVf6o5bEgU0Zde2hXW-EQZf32v8y4Z");
        speechService = new SpeechToText(authenticator);
    }

    public void getTranscripts(String filePath) {
        File audioFile = new File(filePath);
        try {

            this.options = new RecognizeOptions.Builder()
                    .model(RecognizeOptions.Model.EN_US_NARROWBANDMODEL)
                    .interimResults(true)
                    .inactivityTimeout(2000)
                    .audio(audioFile)
                    .build();

            speechService.recognizeUsingWebSocket(this.options, new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechRecognitionResults speechResults) {
                    if (speechResults.getResults().size() != 0) {
                        String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                        System.out.println(text);
                    }
                    ///////////////////////////////////////////////
                    //Call ML API here, If Scam send a notification
                    //This can be done with a callback from an MLService class
                }

                @Override
                public void onConnected() {
                    System.out.println("Connected");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("ERROR:" + e.getMessage());
                }

                @Override
                public void onDisconnected() {
                    System.out.println("Disconnected");
                }
            });

        } catch (IOException e) {
            System.out.println("Failed to get call transcripts. Err: " + e.getMessage());
        }
    }


    public void speechToTextUsingGoogle(File file) {
        JSONObject audioRequest = new JSONObject();
        JSONObject configRequest = new JSONObject();

        JSONObject contentJsonObject = new JSONObject();

        try {

            configRequest.put("enableAutomaticPunctuation", true);
            //configRequest.put("encoding", "");
            //configRequest.put("sampleRateHertz", "16000");
            configRequest.put("languageCode", "en-US");
//            configRequest.put("model", "phone_call");
//          configRequest.put("audioChannelCount", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String byteEncodedString;
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            byteEncodedString = Base64.encodeToString(bytes, Base64.NO_WRAP);

            audioRequest.put("content", byteEncodedString);
            contentJsonObject.put("config", configRequest);
            contentJsonObject.put("audio", audioRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("JSON Content", "--> " + contentJsonObject.toString());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(contentJsonObject.toString(), JSON);

        final Request request = new Request.Builder()
                .url("https://speech.googleapis.com/v1/speech:recognize?key=AIzaSyCsD9mPrtmRQFlozBG4aBQcNaob-tsJlXU")
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.e("Response", "--> " + response.toString());
                    JSONObject tempRes = new JSONObject(response.body().string());
                    Log.e("Message", "--> " + tempRes.toString());

                    if (tempRes.has("results")) {

                        JSONArray alternatives;
                        alternatives = tempRes.getJSONArray("results").getJSONObject(0).getJSONArray("alternatives");

                        final JSONObject transcript;
                        transcript = alternatives.getJSONObject(0);

                        Log.e("Check", "-->" + transcript.getString("transcript"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void convertToFlac(String filePath, Context context) {
        File mpegFile = new File(filePath);
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                speechToTextUsingGoogle(convertedFile);
                // TODO send in file instead

                //getTranscripts(convertedFile.getAbsolutePath());
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                System.out.println("Error: "+error.getMessage());
            }
        };
        AndroidAudioConverter.with(context)
                // Your current audio file
                .setFile(mpegFile)

                // Your desired audio format
                .setFormat(AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
}
