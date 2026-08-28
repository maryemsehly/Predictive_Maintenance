import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from sklearn.preprocessing import StandardScaler
import joblib


data = pd.read_csv("dataset.csv")  
data["vibration"] = (data["vibration_x"]**2 + data["vibration_y"]**2 + data["vibration_z"]**2)**0.5

features = ["temperature", "vibration"]
X = data[features]
y = data["panne"]

scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.2, stratify=y)

model = RandomForestClassifier(
    n_estimators=100,
     class_weight={
        'mecanique': 1.0,
        'mixed': 1.0,      
        'normal': 1.0,
        'thermique': 1.1 
    },
    random_state=42
)
model.fit(X_train, y_train)

y_pred = model.predict(X_test)
print(classification_report(y_test, y_pred))

joblib.dump(model, 'randomForest_model.pkl')

joblib.dump(scaler, 'scalerRandom.pkl')
