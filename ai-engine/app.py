from flask import Flask, request, jsonify
from sklearn.ensemble import IsolationForest
import numpy as np

app = Flask(__name__)

model = IsolationForest(contamination=0.1, random_state=42)
X_train = np.array([[1], [1.1], [0.9], [1], [1.2], [0.8], [100]])
model.fit(X_train)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    level = data.get('level', 'INFO')
    feature_value = 100 if level == 'ERROR' else 1

    X_test = np.array([[feature_value]])
    prediction = model.predict(X_test)

    is_anomaly = bool(prediction[0] == -1)
    return jsonify({"anomaly": is_anomaly})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
