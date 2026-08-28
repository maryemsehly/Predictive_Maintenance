# ESP32_Configuration

Firmware handling the ESP32's network configuration and the establishment of secure connections (HTTPS / MQTTS) with the IoT platform.

## Folder Contents

| File | Description |
|---|---|
| `ESP32_Configuration.ino` | Firmware for network configuration and platform connection |

## How It Works

### 1. Network Configuration (Wi-Fi)

Wi-Fi configuration is handled through the **WiFiManager** library, which allows the ESP32 to automatically create a temporary access point when no network is configured. The user connects to this access point and accesses a web interface (via the ESP32's IP address) to enter the target network's SSID and password — without needing to reflash the board.

Once the credentials are entered, the ESP32 automatically connects to the specified Wi-Fi network in station mode.

### 2. HTTPS Connection Configuration

A web interface allows the user to enter:
- `Client ID`
- `Asset ID`
- `Client Secret`

These parameters are saved to a local JSON file and later used to securely send failure prediction results to the remote platform.

### 3. MQTTS Connection Configuration

A similar interface allows the user to enter:
- `Client ID`
- MQTT server address
- Username / password
- `Asset ID`

These parameters are also saved as JSON. The ESP32 retrieves them via secure HTTPS requests and then uses them to continuously publish monitoring data to the platform over MQTT.

## Security

- Encrypted exchanges via **HTTPS** and **MQTTS**
- Authentication via `Client ID` / `Client Secret`
- No hard-coded credentials in the firmware — everything is configured dynamically through the web interface and stored locally in JSON

## Requirements

- Arduino IDE with ESP32 board support
- `WiFiManager` library
- HTTPS / MQTT client libraries (e.g. `WiFiClientSecure`, `PubSubClient` or equivalent)
