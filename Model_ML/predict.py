import pandas as pd
import joblib

# 1. Charger le modèle et le scaler
model = joblib.load('randomForest_model.pkl')
scaler = joblib.load('scalerRandom.pkl')

# 2. Saisie manuelle des données utilisateur
try:
    temperature = float(input("Entrez la température (°C) : "))
    vibration_x = float(input("Entrez vibration_x : "))
    vibration_y = float(input("Entrez vibration_y : "))
    vibration_z = float(input("Entrez vibration_z : "))
except ValueError:
    print("Erreur : vous devez entrer des valeurs numériques.")
    exit()

# 3. Calcul de la vibration combinée
vibration = (vibration_x**2 + vibration_y**2 + vibration_z**2) ** 0.5

# 4. Préparation du DataFrame
data = pd.DataFrame([{
    "temperature": temperature,
    "vibration": vibration
}])

# 5. Application du scaler
data_scaled = scaler.transform(data)

# 6. Prédiction
prediction = model.predict(data_scaled)[0]
confidence = model.predict_proba(data_scaled).max()

# 7. Affichage du résultat
print(f"\n➡️  Prédiction : {prediction}")
print(f"📊 Confiance : {confidence:.3f}")