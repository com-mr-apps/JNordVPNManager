package com.mr.apps.JNordVpnManager.utils.Json;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import com.mr.apps.JNordVpnManager.Starter;

public class JsonReader
{
   public static JSONArray readJsonFromUrl(String url) throws IOException, InterruptedException, JSONException, ConnectException
   {
      Starter._m_logError.getCurElapsedTime("readJsonFromUrl start");
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      Starter._m_logError.getCurElapsedTime("readJsonFromUrl end  ");
      return new JSONArray(response.body());
   }
}