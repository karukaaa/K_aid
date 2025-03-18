const express = require("express");
const admin = require("firebase-admin");
const dotenv = require("dotenv");

dotenv.config(); // Загружаем переменные окружения

// Загружаем Firebase ключ
const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);

// Исправляем ключ
serviceAccount.private_key = serviceAccount.private_key.replace(/\\n/g, '\n');

// Инициализируем Firebase
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

const app = express();
app.use(express.json());

// Тестовый роут
app.get("/api/test", (req, res) => {
  res.json({ message: "API работает! 🚀" });
});

// Запуск сервера
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`🔥 Сервер запущен на порту ${PORT}`));
