package com.example.ecocity.ai;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiClient {

    private static final String TAG  = "GeminiClient";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public GeminiClient(String apiKey){
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public String enviarPregunta(String pregunta){
        try{
            //Construimos el JSON

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", pregunta);

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject contentObject = new JsonObject();
            contentObject.add("parts", partsArray);

            JsonArray contentsArray  = new JsonArray();
            contentsArray.add(contentObject);

            JsonObject bodyJson = new JsonObject();
            bodyJson.add("contents", contentsArray);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

            //Construimos la request
            Request request = new Request.Builder()
                    .url(BASE_URL + "?key=" + apiKey)
                    .post(body)
                    .build();

            //Ejecutamos
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Log.d(TAG, "Respuesta completa Gemini: " + responseBody);

            if(!response.isSuccessful()){
                Log.e(TAG, "Error API: " + response.code());
                return "Error API: " + response.code();
            }

            //Parseamos la respuesta
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            //Extreamos el contenido esperado
            if(json.has("candidates")){
                JsonObject candidate = json
                        .getAsJsonArray("candidates")
                        .get(0).getAsJsonObject();

                JsonObject content = candidate.getAsJsonObject("content");

                JsonArray parts = content.getAsJsonArray("parts");

                return parts.get(0)
                        .getAsJsonObject()
                        .get("text")
                        .getAsString();
            }
            return "No se pudo generar respuesta";

        }catch (IOException e){
            e.printStackTrace();
            return "Error al comunicarse con la IA";
        }
    }
}
