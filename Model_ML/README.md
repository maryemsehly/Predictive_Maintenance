# Model_ML

Training and inference of the Machine Learning model used to predict failures from sensor data (temperature, vibration, current).

## Folder Contents

| File | Description |
|---|---|
| `Model.py` | Data preparation, model training, and evaluation |
| `predict.py` | Loads the trained model and predicts on new incoming data |

## Approach

### Model Choice: Random Forest

**Random Forest** was selected for several reasons:
- Robustness against overfitting (ensemble approach based on multiple decision trees)
- Good handling of multivariate, noisy data
- Ability to process both categorical and numerical variables
- Strong generalization capability, suited to an industrial context

### Predicted Classes

| Class | Description |
|---|---|
| `normal` | Machine operating normally |
| `mechanical failure` | Abnormal vibration detected |
| `thermal failure` | Abnormal temperature increase |
| `mixed failure` | Abnormal vibration and temperature simultaneously |

### Training (`Model.py`)

1. Load the cleaned and labeled dataset (see [`Data_Collection/`](../Data_Collection)).
2. Split into training (80%) and test (20%) sets.
3. Train the Random Forest classifier on the training set.
4. Evaluate on the test set using appropriate metrics: precision, recall, F1-score, AUC-ROC.

**Results obtained:**

| Class | F1-score |
|---|---|
| Normal | 1.00 |
| Mechanical failure | 0.98 |
| Mixed failure | 0.90 |
| Thermal failure | 0.87 |

Overall model accuracy: **96%**.

### Inference (`predict.py`)

Loads the trained model and produces a prediction from new incoming temperature and vibration values (e.g. received from the ESP32 via HTTPS). The model returns the predicted class, allowing a preventive alert to be triggered when an anomaly is detected.

## Deployment

The model is deployed server-side: the ESP32 continuously transmits monitoring data via HTTPS requests, and the server runs `predict.py` to assess the machine's state in near real time and generate an alert if needed.

## Requirements

```
python >= 3.x
scikit-learn
pandas
numpy
```
