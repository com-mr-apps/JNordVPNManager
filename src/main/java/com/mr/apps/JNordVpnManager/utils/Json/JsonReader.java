package com.mr.apps.JNordVpnManager.utils.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonReader
{
   public static JSONArray readJsonFromUrl(String url) throws IOException, InterruptedException, JSONException
   {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return new JSONArray(response.body());
   }
}