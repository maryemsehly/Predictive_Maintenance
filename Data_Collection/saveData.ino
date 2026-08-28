#include <WiFi.h>
#include <HTTPClient.h>
#include <Preferences.h>
#include <WebServer.h>

// --- CONFIG --- chbik dh 
const char* serverURL = "http://127.0.0.1:5000/data"; // <- Remplace par IP de ton serveur Flask
const int HTTP_SEND_INTERVAL = 10000; // en ms

// AP Config (mode point d'accès pour config Wi-Fi)
const char* ap_ssid = "ESP32_Config";
const char* ap_pass = "12345678";

Preferences preferences;
WebServer server(80);

unsigned long lastSendTime = 0;
bool wifiConnected = false;

// --- Fonctions Wi-Fi Config ---
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
  server.send(200, "text/html", getHTMLForm());
}

void handleConnect() {
  if (server.hasArg("ssid") && server.hasArg("password")) {
    String ssid = server.arg("ssid");
    String pass = server.arg("password");

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
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ap_ssid, ap_pass);
  Serial.print("AP IP: ");
  Serial.println(WiFi.softAPIP());

  server.on("/", handleRoot);
  server.on("/connect", HTTP_POST, handleConnect);
  server.begin();
}

// --- Connexion Wi-Fi ---
bool connectToSavedWiFi() {
  preferences.begin("wifi", true);
  String ssid = preferences.getString("ssid", "");
  String pass = preferences.getString("password", "");
  preferences.end();

  if (ssid == "") return false;

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid.c_str(), pass.c_str());

  Serial.print("Connexion à ");
  Serial.println(ssid);

  unsigned long startAttemptTime = millis();

  while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < 10000) {
    Serial.print(".");
    delay(500);
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nConnecté au Wi-Fi !");
    Serial.print("IP locale : ");
    Serial.println(WiFi.localIP());
    return true;
  } else {
    Serial.println("\nÉchec de connexion Wi-Fi.");
    return false;
  }
}

// --- Lecture capteurs simulée ---
float readTemperature() {
  // Ici tu intègres ta lecture réelle
  return 25.3 + random(-20, 20) * 0.1; // simule entre 23.3 et 27.3
}

float readVibrationAxis() {
  // simule vibration axe x/y/z entre -1.5 et 1.5
  return random(-150, 150) / 100.0;
}

// --- Envoi HTTP POST ---
void sendDataHTTP() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverURL);
    http.addHeader("Content-Type", "application/json");

    float temp = readTemperature();
    float vib_x = readVibrationAxis();
    float vib_y = readVibrationAxis();
    float vib_z = readVibrationAxis();

    String jsonData = "{";
    jsonData += "\"temperature\":";
    jsonData += String(temp, 2);
    jsonData += ",\"vibration\":{";
    jsonData += "\"x\":" + String(vib_x, 3) + ",";
    jsonData += "\"y\":" + String(vib_y, 3) + ",";
    jsonData += "\"z\":" + String(vib_z, 3);
    jsonData += "}}";

    Serial.println("Envoi JSON:");
    Serial.println(jsonData);

    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
      Serial.printf("[HTTP] Code réponse : %d\n", httpResponseCode);
      String response = http.getString();
      Serial.println("[HTTP] Réponse serveur : " + response);
    } else {
      Serial.printf("[HTTP] Erreur lors de l'envoi : %s\n", http.errorToString(httpResponseCode).c_str());
    }

    http.end();
  } else {
    Serial.println("[HTTP] WiFi non connecté.");
  }
}

void setup() {
  Serial.begin(115200);
  delay(1000);

  if (!connectToSavedWiFi()) {
    Serial.println("Démarrage mode AP pour config Wi-Fi...");
    startAPMode();
  } else {
    wifiConnected = true;
  }
}

void loop() {
  if (!wifiConnected) {
    server.handleClient();
  }

  if (wifiConnected && (millis() - lastSendTime > HTTP_SEND_INTERVAL)) {
    sendDataHTTP();
    lastSendTime = millis();
  }
}
