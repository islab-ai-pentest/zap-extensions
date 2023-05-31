package org.zaproxy.addon.aiassistant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;

import java.net.URI;

import net.sf.json.JSONObject;

public class CipherClient {
  private static String CIPHER_ENDPOINT = "https://6acf-164-125-252-223.ngrok-free.app/generate_chain?model_idx=1";

  private static final Logger LOGGER = LogManager.getLogger(ExtensionSimpleExample.class);

  public String CONVERSATION_ID = UUID.randomUUID().toString();

  public String sendMessage(String message) {
    JSONObject payload = new JSONObject();

    payload.put("message", message);
    payload.put("conv_id", CONVERSATION_ID);

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(CIPHER_ENDPOINT))
      .header("accept", "application/json")
      .header("Content-Type", "application/json")
      .POST(BodyPublishers.ofString(payload.toString()))
      .build();

    HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
      return "Error:" + e.getMessage();
    }

    return response.body();
  }

  public String sendAlert(Alert alert) {
    String debugMessage = "Uri: " + alert.getUri();
    debugMessage += "\nRisk: " + alert.getRisk();
    debugMessage += "\nConfidence: " + alert.getConfidence();
    debugMessage += "\nCWE ID: " + alert.getCweId();
    debugMessage += "\nWASC ID: " + alert.getWascId();
    debugMessage += "\nDescription: " + alert.getDescription();
    LOGGER.debug(debugMessage);

    String message = "I have an alert with the following details:\n" + alert.getDescription();
    return sendMessage(message);
  }
}
