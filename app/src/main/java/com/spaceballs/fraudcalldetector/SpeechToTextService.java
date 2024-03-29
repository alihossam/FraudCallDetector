package com.spaceballs.fraudcalldetector;

import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeechToTextService {
    interface CallbackWithTranscript {
        void run(String text);
    }

    public void speechToTextUsingGoogle(File file, final CallbackWithTranscript callback) {
        JSONObject audioRequest = new JSONObject();
        JSONObject configRequest = new JSONObject();

        JSONObject contentJsonObject = new JSONObject();

        try {

            configRequest.put("enableAutomaticPunctuation", true);
            configRequest.put("encoding", CallRecordingService.ENCODING);
            configRequest.put("sampleRateHertz", CallRecordingService.SAMPLING_RATE);
            configRequest.put("languageCode", "en-US");
            //configRequest.put("model", "phone_call");
            configRequest.put("audioChannelCount", "1");

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
                .url("https://speech.googleapis.com/v1/speech:recognize?key=")
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
                        StringBuilder totalStringBuilder = new StringBuilder();
                        try {
                            JSONArray results = tempRes.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                // There is a different results object between every pause during a conversation,
                                // so we concatenate all of them before sending everything to the spam filter.
                                JSONObject alternative = results.getJSONObject(i).getJSONArray("alternatives").getJSONObject(0);
                                totalStringBuilder.append(alternative.getString("transcript") + "\n");
                            }
                        } catch (JSONException e) {
                            Log.e("Results Error", "Cannot deserialize JSON: " + tempRes.toString());
                        }
                        callback.run(totalStringBuilder.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
