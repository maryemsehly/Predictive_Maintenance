# Data_Collection

Scripts and dataset used to collect raw sensor data (temperature, vibration, current) and build the dataset used to train the failure prediction model.

## Folder Contents

| File | Description |
|---|---|
| `main.py` | Server-side Python script (receiving / storing data sent by the ESP32) |
| `donnees_machine.csv` | Collected dataset (raw sensor measurements) |

## How It Works

1. **Sensor reading** — The ESP32 continuously polls the temperature (DS18B20), vibration (ADXL345), and current (SCT-013) sensors (see [`ESP32_Configuration/`](../ESP32_Configuration)).
2. **Transmission** — Measurements are sent from the board to a web server over **HTTPS**, ensuring the confidentiality of the exchange.
3. **Storage** — `main.py` receives the HTTPS requests and stores the data in `donnees_machine.csv`, which serves as the basis for the cleaning and labeling step.

## Data Preparation for Machine Learning

Measurements were collected under a variety of operating conditions to ensure a representative dataset. Before training the model (see [`Model_ML/`](../Model_ML)), the data goes through:

- **Cleaning**: detection and removal of outliers, handling of missing values.
- **Normalization / standardization** of measurement scales where needed.
- **Labeling**: each record is classified according to the machine's state:
  - `normal` — normal operation
  - `mechanical failure` — abnormal vibration detected
  - `thermal failure` — abnormal temperature increase
  - `mixed failure` — abnormal vibration and temperature simultaneously

## Requirements

- Python 3 with the dependencies required by `main.py` (receiving server)
- ESP32 board configured and connected to Wi-Fi (see [`ESP32_Configuration/`](../ESP32_Configuration))
