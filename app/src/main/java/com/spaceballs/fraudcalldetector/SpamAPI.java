package com.spaceballs.fraudcalldetector;

import java.io.IOException;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

// Source: https://www.mkyong.com/java/okhttp-how-to-send-http-requests/
public class SpamAPI {
    private String _endpoint;
    private String _apiKey;
    private final OkHttpClient httpClient = new OkHttpClient();

    // JSON type to send HTTP POST request using OkHttp3
    // https://stackoverflow.com/a/34180100/6288413
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public SpamAPI(String apiEnpoint, String apiKey) {
        _endpoint = apiEnpoint;
        _apiKey = apiKey;
    }

    private Response sendOopSpamRequest(String text) throws Exception {
        /*
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", text)
                .build();
        */
        /*
        RequestBody requestBody = new FormBody.Builder()
                .add("content", text)
                .build();
        */

        // TODO change deprecation
        JSONObject jsonText = new JSONObject();
        jsonText.put("content", text);

        RequestBody requestBody = RequestBody.create(jsonText.toString(), JSON);

        System.out.println(requestBody);
        Response response = null;
        try {

            Request request = new Request.Builder()
                    .url(_endpoint)
                    // add request headers
                    .addHeader("content-type", "application/json")
                    .addHeader("accept", "application/json")
                    .addHeader("User-Agent", "OkHttp")
                    .addHeader("x-rapidapi-host", "oopspam.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", _apiKey)
                    .post(requestBody)
                    .build();
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful())
            {
                throw new Exception("Something went wrong");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject sendOopSpamRequestJson(String text) throws Exception {
        JSONObject jsonResponse = null;
        Response response = null;
        try {
            response = sendOopSpamRequest(text);
            String responseBody = response.body().string();
            jsonResponse = new JSONObject(responseBody);
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (response != null)
                System.out.print(response.code() + " " + response.message());
            throw new IOException("Response body cannot be converted to string.");
        }
        return jsonResponse;
    }

    public int getTextSpamScore(JSONObject responseBody)
    {
        try {
            return Integer.parseInt(responseBody.get("Score").toString());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("responseBody is null");
        } catch (JSONException e) {
            throw new IllegalArgumentException("ResponseBody does not contain Score field");
        }
    }

    public boolean isTextSpam(JSONObject responseBody)
    {
        try {
            return ((JSONObject) responseBody.get("Details")).get("isContentSpam").equals("spam");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("responseBody is null");
        } catch (JSONException e) {
            throw new IllegalArgumentException("ResponseBody does not contain isContentSpam field. responseBody: " + responseBody.toString());
        }
    }
}
