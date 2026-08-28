#include "secret.h"
//#include <ESP_config.ino>
#include <WiFi.h>
#include <PubSubClient.h>
#include <WebServer.h>
#include <Preferences.h>


const char* ap_ssid = "ESP32_Config";
const char* ap_pass = "12345678";
WebServer server(80);
Preferences preferences;

String getHTMLForm() {
  return R"rawliteral(
    <html><head><title>Wi-Fi Config</title></head><body>
    <h2>Configure Wi-Fi</h2>
    <form method="POST" action="/connect">
      SSID: <input name="ssid"><br>
      Password: <input name="password" type="password"><br>
      <input type="submit" value="Connect">
    </form>
    </body></html>
  )rawliteral";
}

void handleRoot() {
  Serial.println("[INFO] Accessed '/'");
  server.send(200, "text/html", getHTMLForm());
}

void handleConnect() {
  if (server.hasArg("ssid") && server.hasArg("password")) {
    String ssid = server.arg("ssid");
    String pass = server.arg("password");

    Serial.println("[INFO] Received Wi-Fi credentials");
    Serial.println("SSID: " + ssid);
    Serial.println("PASS: " + pass);

    preferences.begin("wifi", false);
    preferences.putString("ssid", ssid);
    preferences.putString("password", pass);
    preferences.end();

    server.send(200, "text/html", "<h3>Credentials saved. Rebooting...</h3>");

    delay(2000);
    ESP.restart();
  } else {
    server.send(400, "text/plain", "Missing SSID or password");
  }
}

void startAPMode() {
  Serial.println("[INFO] Starting AP Mode...");
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ap_ssid, ap_pass);
  Serial.print("AP IP: ");
  Serial.println(WiFi.softAPIP());

  server.on("/", handleRoot);
  server.on("/connect", HTTP_POST, handleConnect);
  server.begin();
}

bool connectToSavedWiFi() {
  preferences.begin("wifi", true);
  String ssid = preferences.getString("ssid", "");
  String pass = preferences.getString("password", "");
  preferences.end();

  if (ssid == "") {
    Serial.println("[INFO] No saved credentials.");
    return false;
  }

  Serial.println("[INFO] Trying saved credentials:");
  Serial.println("SSID: " + ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid.c_str(), pass.c_str());

  unsigned long startAttempt = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - startAttempt < 10000) {
    Serial.print(".");
    delay(500);
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[SUCCESS] Connected!");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    return true;
  } else {
    Serial.println("\n[ERROR] Connection failed.");
    return false;
  }
}

// Global state tracking
bool wasConnected = false;       // Tracks previous Wi-Fi state
bool apModeStarted = false;      // Prevents restarting AP multiple times

// Check Wi-Fi status
void checkWiFi() {
  if (WiFi.status() != WL_CONNECTED && wasConnected) {
    Serial.println("[WARN] Lost Wi-Fi connection.");
    wasConnected = false;

    // Start AP if not already started
    if (!apModeStarted) {
      Serial.println("[INFO] Switching to AP mode for reconfiguration...");
      startAPMode();
      apModeStarted = true;
    }
  }

  if (WiFi.status() == WL_CONNECTED && !wasConnected) {
    Serial.println("[INFO] Wi-Fi reconnected!");
    Serial.print("[INFO] New IP: ");
    Serial.println(WiFi.localIP());
    wasConnected = true;
  }
}

// Non-SSL WiFi client
//WiFiClient askClient;
WiFiClientSecure askClient;
PubSubClient client(askClient);



void setup() {

 Serial.begin(115200);
  delay(500);
  Serial.println("=== ESP32 Wi-Fi Boot ===");

  if (connectToSavedWiFi()) {
    wasConnected = true;
  } else {
    startAPMode();
    apModeStarted = true;
  }

  Serial.println(WiFi.localIP());
  
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

void loop() {

  server.handleClient();
  checkWiFi(); // Continuously monitor connection status

  //Publish Boolean format:
  client.publish("master/esp32-F5A2D4/writeattributevalue/Temperature/4z3Knm5eryxzzz8H9TfxzT", "1");
  //To publish Strings:
  client.publish("master/esp32-F5A2D4/writeattributevalue/Temperature/4z3Knm5eryxzzz8H9TfxzT", String("Hello").c_str());
}

//MQTT callback
void callback(char* topic, byte * payload, unsigned int length) {

  for (int i = 0; i < length; i++) {
    Serial.println(topic);
    Serial.print(" has send ");
    Serial.print((char)payload[i]);
  }

}

//MQTT reconnect
void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("********** Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect(ClientID, username, mqttpass, lastwill, 1, 1, lastwillmsg)) {
      Serial.println("-> MQTT client connected");
      client.subscribe(topic);
      Serial.print("Subscribed to: ");
      Serial.println(topic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println("-> try again in 5 seconds");
      // Wait 5 seconds before retrying
    }
  }
}

