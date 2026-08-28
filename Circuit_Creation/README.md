# Circuit_Creation

Electronic design of the acquisition circuit connected to the ESP32: schematic, PCB layout, and physical integration of the sensors.

## Folder Contents

| File | Description |
|---|---|
| `Circuit.png` | Overview of the monitoring circuit connecting the sensors to the microcontroller |
| `Circuit_Development.png` | Physically assembled circuit (board + wired sensors) |
| `schematic.png` | Full electronic schematic designed with EasyEDA |
| `sensors_integration.png` | Sensor integration on the monitored machine |

## Components Used

**Microcontroller**
- ESP32 Dev Kit V1 (ESP-WROOM-32) — 40 digital pins, 16 analog inputs, Wi-Fi 802.11 b/g/n, Bluetooth 4.2/BLE, 240 MHz clock speed, 4 MB flash. Role: local data processing (Edge Computing) and transmission to the platform.

**Sensors**
- **SCT-013** (current) — 5A/1V ratio, measures the electrical current and power drawn by the machine.
- **DS18B20** (temperature) — range -55°C to 125°C, accuracy ±0.5°C between -10°C and 85°C.
- **ADXL345** (vibration/acceleration) — 3 axes (X, Y, Z), range ±16g, used to detect abnormal movement and vibration.

**Protection and wiring components**
- 1 kΩ resistors (ESP32 pin protection)
- 10 kΩ resistors (voltage divider to adapt the SCT-013 signal to the ESP32's supported voltage)
- 4.7 kΩ resistor (additional pin protection)
- Capacitors (signal filtering)
- Screw terminals (secure connection of the temperature and vibration sensors)
- Female JACK connectors (SCT-013 sensor connection)

## Schematic Design

The schematic (`schematic.png`) was designed with **EasyEDA**, an online electronic circuit design tool for creating schematics, producing PCBs, and simulating circuits.

Component labeling on the schematic:
- `J1, J2, J3`: JACK connectors for adapting the SCT-013 current sensor
- `R15, R14, R16`: ESP32 pin protection resistors (1 kΩ)
- `C1, C2, C3`: filtering capacitors
- `R17–R22`: voltage divider (10 kΩ) for the SCT-013 signal
- `U5, U6`: screw terminals for the temperature and vibration sensors
- `R23`: protection resistor (4.7 kΩ)
- `U1`: ESP32, the core of the processing and communication system

The routing was also verified through a 3D visualization of the PCB before manufacturing, to confirm the circuit was correctly designed.
