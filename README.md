# IoT System for Industrial Predictive Maintenance

## Context

In many industrial environments, machine monitoring still relies on manual data collection: periodic meter readings, visual inspections, paper logging. This approach is slow, prone to human error, and cannot detect a drift in machine behavior before it turns into an actual failure.

This project implements a complete monitoring and predictive maintenance pipeline, from physical data acquisition on the machine to failure-type prediction (mechanical, thermal, or mixed) using a Machine Learning model, with data reported to a centralized IoT platform.

## Objectives

- **Automate data collection** on machine operating parameters (temperature, vibration, electrical current) to eliminate manual logging.
- **Monitor the machine in real time** through a centralized platform, accessible from any device.
- **Predict failures** before they occur, using a supervised classification model trained on sensor data.
- **Secure all data exchanges** end to end (SSL/TLS encryption, authentication, HTTPS/MQTTS communications).

## Overall Architecture

The system is organized into five layers:

| Layer | Role |
|---|---|
| **Perception** | Sensors (temperature, vibration, current) + ESP32 microcontroller acting as an Edge Computing node; direct reading from the machine's PLC is also possible |
| **Transport** | Wi-Fi communication, data exchange via MQTT, HTTP and TCP/IP |
| **Software** | Centralized platform that receives, stores, and exposes collected data |
| **Application** | Web configuration interface (network parameters, connection credentials, device management) |
| **Security** | Digital certificates (mkcert), authentication (Keycloak), SSL/TLS encryption |

**Simplified data flow:**

```
Sensors (temperature / vibration / current)
        │
        ▼
   ESP32 (Edge Computing)
        │  Wi-Fi — HTTPS / MQTTS
        ▼
 Centralized IoT platform
        │
        ├── Historical data storage
        ├── Modbus TCP agent ↔ machine PLC
        └── Random Forest model → failure prediction
        │
        ▼
   Alert sent to the maintenance manager
```

A **Modbus TCP** agent can also be configured on the platform to query the machine's PLC directly (IP address, port, polling interval), complementing the data reported by the ESP32.

## Repository Structure

| Folder | Content |
|---|---|
| [`Circuit_Creation/`](./Circuit_Creation) | Electronic schematic, PCB design and photos of the acquisition circuit |
| [`Data_Collection/`](./Data_Collection) | ESP32 firmware and scripts for collecting sensor data |
| [`ESP32_Configuration/`](./ESP32_Configuration) | Firmware for network setup and platform connection |
| [`Model_ML/`](./Model_ML) | Training and inference of the failure prediction model |
| [`Platform_OpenRemote/`](./Platform_OpenRemote) | IoT platform (based on OpenRemote) adapted for this project |

Each folder contains its own README detailing how it works.

## Tech Stack

- **Hardware**: ESP32 Dev Kit V1 (ESP-WROOM-32), SCT-013 current sensor, DS18B20 temperature sensor, ADXL345 vibration sensor
- **Firmware**: Arduino/C++ (`.ino`)
- **Data processing / ML**: Python, scikit-learn (Random Forest)
- **IoT platform**: OpenRemote (Modbus TCP, MQTT, HTTP agents), Docker
- **Security**: Keycloak, mkcert, SSL/TLS
- **Electronic design**: EasyEDA
- **Communication**: Wi-Fi 802.11 b/g/n, HTTPS, MQTTS, Modbus TCP

## Results

The trained Random Forest model reaches an overall accuracy of **96%**, with the following per-class performance:

| Class | F1-score |
|---|---|
| Normal | 1.00 |
| Mechanical failure | 0.98 |
| Mixed failure | 0.90 |
| Thermal failure | 0.87 |

## --Pipeline

1. Sensors continuously measure temperature, vibration, and current on the machine.
2. The ESP32 collects, structures, and transmits this data via HTTPS/MQTTS to the platform.
3. The platform centralizes the data and makes it available for model training and inference.
4. The Random Forest model analyzes incoming data and predicts a state: normal, mechanical, thermal, or mixed failure.
5. When an anomaly is detected, an alert is generated and sent to the maintenance manager.
