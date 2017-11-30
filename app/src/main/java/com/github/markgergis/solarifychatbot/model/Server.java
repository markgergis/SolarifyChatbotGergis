package com.github.markgergis.solarifychatbot.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by markragaee on 11/30/17.
 */

public class Server {
    OkHttpClient client;
    public static MediaType JSON;
    public Gson gson;
    public String uuid;
    public String message;
    public static final String URL = "https://frozen-cove-10952.herokuapp.com/";

    public Server(){
        client  = new OkHttpClient();

        JSON = MediaType.parse("application/json; charset=utf-8");
        gson = new Gson();


    }
    public String get() {
        Request request = new Request.Builder()
                .url("https://frozen-cove-10952.herokuapp.com/welcome")
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String s = response.body().string();
            fromJson(s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String post(String bodyJson) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyJson);
        Request request = new Request.Builder()
                .addHeader("Authorization", uuid)
                .url(URL + "chat")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    void fromJson(String json){
        JsonObject jobj = gson.fromJson(json, JsonObject.class);
        uuid = jobj.get("uuid").toString();
        message = jobj.get("message").getAsString();
    }
}
