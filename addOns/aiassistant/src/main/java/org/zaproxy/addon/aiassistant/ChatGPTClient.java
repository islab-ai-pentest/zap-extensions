package org.zaproxy.addon.aiassistant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;

import net.sf.json.JSONObject;

public class ChatGPTClient {
  private static final String CHATGPT_ENDPOINT = "https://api.openai.com/v1/chat/completions";
  private final String apiKey;
  private ArrayList<Map.Entry<String, String>> conversations = new ArrayList<Map.Entry<String, String>>();
  private ArrayList<String> alertsId = new ArrayList<String>();
  private static final Logger LOGGER = LogManager.getLogger(ExtensionSimpleExample.class);

  public ChatGPTClient(String apiKey) {
    this.apiKey = apiKey;
  }

  public void addAlertsToConversation(List<Alert> alerts) {
    for (Alert alert : alerts) {
      String unhashed = alert.getUri() + alert.getRisk() + alert.getConfidence() + alert.getCweId() + alert.getWascId();
      String hashed = Integer.toString(unhashed.hashCode());

      if (alertsId.contains(hashed)) {
        continue;
      }
      alertsId.add(hashed);

      String message = "Id: " + hashed;
      message += "\nUri: " + alert.getUri();
      message += "\nRisk: " + alert.getRisk();
      message += "\nConfidence: " + alert.getConfidence();
      message += "\nCWE ID: " + alert.getCweId();
      message += "\nWASC ID: " + alert.getWascId();
      message += "\nDescription: " + alert.getDescription();

      conversations.add(new AbstractMap.SimpleEntry<>("alert", message));
    }
  }

  public String sendMessage(String message) throws Exception {
    conversations.add(new AbstractMap.SimpleEntry<>("user", message));

    // Create a JSON payload with the message
    JSONObject payload = new JSONObject();
    payload.put("model", "gpt-3.5-turbo");

    // array of JSONObject
    List<JSONObject> messages = new ArrayList<JSONObject>();

    for (Map.Entry<String, String> entry : conversations) {
      if (entry.getKey().equals("alert")) {
        continue;
      }

      JSONObject messageJson = new JSONObject();
      messageJson.put("role", entry.getKey());
      messageJson.put("content", entry.getValue());

      messages.add(messageJson);
    }

    payload.put("messages", messages);

    // Create an HTTP POST request with the payloads
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(CHATGPT_ENDPOINT))
        .header("Authorization", "Bearer " + apiKey)
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(payload.toString()))
        .build();

    // Send the request and retrieve the response
    HttpResponse<String> response = httpClient.send(request,
        BodyHandlers.ofString());

    // Parse the response body as a JSON object
    JSONObject responseBody = new JSONObject();
    responseBody = JSONObject.fromObject(response.body());

    // Retrieve the completed message from the response
    String completedMessage;
    try {
      completedMessage = responseBody.getJSONArray("choices")
          .getJSONObject(0)
          .getJSONObject("message")
          .getString("content");
    } catch (Exception e) {
      LOGGER.error(payload.toString());
      LOGGER.error(responseBody, e);
      throw e;
    }

    conversations.add(new AbstractMap.SimpleEntry<>("assistant", completedMessage));

    return completedMessage;
  }
}