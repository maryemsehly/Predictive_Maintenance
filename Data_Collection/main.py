from flask import Flask, request, jsonify
from datetime import datetime
import os

app = Flask(__name__)

# Dossier de sauvegarde des données (optionnel)
SAVE_DIR = "received_data"
os.makedirs(SAVE_DIR, exist_ok=True)

@app.route('/data', methods=['POST'])
def receive_data():
    try:
        data = request.get_json(force=True)
        print(f"[{datetime.now()}] Données reçues : {data}")

        # (Optionnel) Sauvegarder les données dans un fichier
        timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
        with open(os.path.join(SAVE_DIR, f"data_{timestamp}.json"), "w") as f:
            f.write(str(data))

        return jsonify({"status": "ok", "message": "Données reçues avec succès"}), 200
    except Exception as e:
        print("Erreur lors de la réception des données :", e)
        return jsonify({"status": "error", "message": str(e)}), 400

if __name__ == '_main_':
    app.run(host='0.0.0.0', port=5000)