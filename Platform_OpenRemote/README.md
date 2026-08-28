# Platform_OpenRemote

Centralized IoT platform, based on **OpenRemote**, used to receive, store, visualize, and act on the monitoring data sent by the ESP32, as well as to query the machine's PLC directly through a Modbus TCP agent.

## Role in the Architecture

Connected devices (ESP32, PLC) send their data through secure protocols (**MQTTS**, **HTTPS**, **TCP/IP**, **WSS**). This data is received by the OpenRemote **Manager**, which processes it through several services: mapping, notifications, automation rules, optimization.

The Manager relies on **agents** to interact with the different protocols used by connected devices:

| Agent | Role |
|---|---|
| **Modbus TCP** | Direct reading of data from the machine's PLC (configurable IP address, port, polling interval) |
| **MQTT** | Receiving monitoring data published by the ESP32 |
| **HTTP** | Receiving failure prediction results sent by the Machine Learning server |

Users view and manage the system through the **Manager Web UI** and **Insights** interfaces.

## Implemented Features

- **Modbus TCP agent management**: create, modify, delete, and connect an agent to a machine's PLC, with data retrieval into dedicated attributes.
- **Real-time reception and visualization** of monitoring data (temperature, vibration, current).
- **Reception of failure prediction results** from the Machine Learning model, triggering alerts to the maintenance manager.
- **User and access management**, platform customization.

## Security

- Encrypted exchanges via **SSL/TLS**, with certificates generated using **mkcert**
- Authentication via **Keycloak**
- Secure communications: **HTTPS**, **MQTTS**

## Deployment

The platform is containerized with **Docker**, simplifying deployment and configuration.

> This folder contains a version of OpenRemote adapted to the specific needs of this project (agent configuration, integration of data flows, and predictive maintenance alerts).
