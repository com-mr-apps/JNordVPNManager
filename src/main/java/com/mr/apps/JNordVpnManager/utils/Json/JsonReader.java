/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils.Json;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONException;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

public class JsonReader
{
   private static final int    COMMAND_TIMEOUT    = UtilPrefs.getCommandTimeout();

   public static JSONArray readJsonFromUrl(String url) throws IOException, InterruptedException, JSONException, ConnectException
   {
      Starter._m_logError.getCurElapsedTime("readJsonFromUrl start");
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(COMMAND_TIMEOUT)).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      Starter._m_logError.getCurElapsedTime("readJsonFromUrl end  ");
      return new JSONArray(response.body());
   }
}