#include <SPI.h>
#include <TFT_eSPI.h>
#include <DHT.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <HardwareSerial.h>
#include <TinyGPS++.h>
#include <SPIFFS.h>
#include "logo.h"

#include <SPIFFS.h>
using fs::File;

const char* ssid     = "LAURA";
const char* password = "l@ur@2016";

const char* broker = "6dad182b442b4e5faedf835d3ec12587.s1.eu.hivemq.cloud";
const int   port   = 8883;

#define DHTPIN  4
#define DHTTYPE DHT22

#define GPS_RX_PIN 16
#define GPS_TX_PIN 17
#define GPS_BAUD   9600

#define OFFLINE_FILE   "/pendentes.txt"
#define MAX_PENDING    500 

#define LED_GREEN  25
#define LED_RED    26
#define LED_WHITE  33        

TFT_eSPI       tft = TFT_eSPI();
DHT            dht(DHTPIN, DHTTYPE);
HardwareSerial gpsSerial(2);
TinyGPSPlus    gps;

WiFiClientSecure wifiClient;
PubSubClient     mqtt(wifiClient);

uint16_t calData[5] = { 275, 3620, 264, 3532, 1 };

double  gpsLat   = 0.0;
double  gpsLng   = 0.0;
double  gpsSpeed = 0.0;
int     gpsSats  = 0;
bool    gpsFixed = false;

bool    wifiOk   = false;
bool    mqttOk   = false;
int     pendingCount = 0;

bool lampOn = true;

bool tryConnectWiFi() {
  WiFi.begin(ssid, password);

  Serial.print("WiFi...");

  int attempts = 0;

  while (WiFi.status() != WL_CONNECTED && attempts < 30) {
    delay(500);
    Serial.print(".");
    attempts++;
  }

  wifiOk = (WiFi.status() == WL_CONNECTED);

  Serial.println(wifiOk ? " OK!" : " FALHOU (modo offline)");

  return wifiOk;
}

void checkWiFi() {
  if (WiFi.status() != WL_CONNECTED) {
    wifiOk = false;
    mqttOk = false;
    WiFi.reconnect();
  } else {
    wifiOk = true;
  }
}

bool tryConnectMQTT() {
  if (!wifiOk) return false;

  wifiClient.setInsecure();

  mqtt.setServer(broker, port);
  mqtt.setBufferSize(512);

  if (mqtt.connect("BeefTrackerESP32", "admin", "31xm30JsNbKCn6KoKNnE")) {
    mqttOk = true;
    Serial.println("MQTT OK!");
    return true;
  }

  mqttOk = false;
  return false;
}

void checkMQTT() {
  if (!wifiOk) {
    mqttOk = false;
    return;
  }

  if (!mqtt.connected()) {
    mqttOk = tryConnectMQTT();
  } else {
    mqttOk = true;
  }
}

void saveOffline(const char* json) {
  if (pendingCount >= MAX_PENDING) return;

  File f = SPIFFS.open(OFFLINE_FILE, FILE_APPEND);

  if (!f) {
    Serial.println("Erro ao abrir arquivo offline!");
    return;
  }

  f.println(json);
  f.close();

  pendingCount++;

  Serial.printf("Salvo offline (%d pendentes)\n", pendingCount);
}

int countPending() {
  if (!SPIFFS.exists(OFFLINE_FILE)) return 0;

  File f = SPIFFS.open(OFFLINE_FILE, FILE_READ);

  if (!f) return 0;

  int count = 0;

  while (f.available()) {
    f.readStringUntil('\n');
    count++;
  }

  f.close();

  return count;
}

void flushOffline() {
  if (!mqttOk) return;

  if (!SPIFFS.exists(OFFLINE_FILE)) return;

  File f = SPIFFS.open(OFFLINE_FILE, FILE_READ);

  if (!f) return;

  int sent = 0;

  Serial.println("Enviando registros offline...");

  while (f.available()) {
    String line = f.readStringUntil('\n');

    line.trim();

    if (line.length() == 0) continue;

    if (mqtt.publish("", line.c_str())) {
      sent++;
    } else {
      Serial.printf("MQTT caiu durante flush (enviados: %d)\n", sent);
      f.close();
      return;
    }

    mqtt.loop();

    delay(50);
  }

  f.close();

  SPIFFS.remove(OFFLINE_FILE);

  pendingCount = 0;

  Serial.printf("Flush OK: %d registros enviados!\n", sent);
}

void readGPS() {
  unsigned long start = millis();

  while (millis() - start < 200) {
    while (gpsSerial.available()) {
      gps.encode(gpsSerial.read());
    }
  }

  if (gps.location.isValid() && gps.location.isUpdated()) {
    gpsLat   = gps.location.lat();
    gpsLng   = gps.location.lng();
    gpsFixed = true;
  } else {
    gpsFixed = false;
  }

  if (gps.speed.isValid()) {
    gpsSpeed = gps.speed.kmph();
  }

  if (gps.satellites.isValid()) {
    gpsSats = gps.satellites.value();
  }
}

void handleData(float temp, float hum) {
  char json[300];

snprintf(
  json,
  sizeof(json),
  "{\"sensorToken\":\"d16bb857-c478-4e5c-b11f-df8f3fca180a\","
  "\"timestamp\":%lu,"
  "\"lat\":%.6f,"
  "\"lon\":%.6f,"
  "\"temp\":%.1f,"
  "\"umidade\":%.1f}",
  (unsigned long)time(nullptr) * 1000UL,
  gpsLat,
  gpsLng,
  temp,
  hum
);

  if (mqttOk) {

    bool ok =
      mqtt.publish("beeftracker/temperatura", String(temp, 1).c_str()) &&
      mqtt.publish("beeftracker/umidade", String(hum, 1).c_str()) &&
      mqtt.publish("sensor/leitura", json);

    if (gpsFixed) {
      String gpsPayload = String(gpsLat, 6) + "," + String(gpsLng, 6);

      mqtt.publish("beeftracker/gps", gpsPayload.c_str());
      mqtt.publish("beeftracker/velocidade", String(gpsSpeed, 1).c_str());
    }

    if (!ok) {
      Serial.println("Publish falhou, salvando offline...");
      saveOffline(json);
    }

  } else {
    saveOffline(json);
  }

  Serial.printf(
    "[%s] Temp:%.1f | Hum:%.1f | GPS:%s (%.6f,%.6f) %.1fkm/h %dsats\n",
    mqttOk ? "ONLINE" : "OFFLINE",
    temp,
    hum,
    gpsFixed ? "FIX" : "NO-FIX",
    gpsLat,
    gpsLng,
    gpsSpeed,
    gpsSats
  );
}

void setup() {
  Serial.begin(115200);

  gpsSerial.begin(GPS_BAUD, SERIAL_8N1, GPS_RX_PIN, GPS_TX_PIN);

  if (!SPIFFS.begin(true)) {
    Serial.println("ERRO: SPIFFS nao iniciou!");
  } else {
    pendingCount = countPending();
    Serial.printf("SPIFFS OK. Pendentes: %d\n", pendingCount);
  }

  tft.init();
  tft.setRotation(1);
  tft.invertDisplay(true);
  tft.setSwapBytes(true);
  tft.setTouch(calData);
  tft.fillScreen(TFT_BLACK);

  tft.drawBitmap(60, 20, epd_bitmap_logo_beeftracker_emblema, 200, 142, 0xF800);

  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(MC_DATUM);

  tft.drawString("Iniciando BeefTracker...", 160, 180, 2);

  int barraX = 60;
  int barraY = 208;

  tft.drawRect(barraX, barraY, 200, 12, TFT_WHITE);

  for (int i = 0; i < 196; i++) {
    tft.fillRect(barraX + 2, barraY + 2, i, 8, TFT_RED);
    delay(10);
  }

  delay(500);

  tft.fillScreen(TFT_BLACK);

  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextDatum(MC_DATUM);

  tft.drawString("Conectando WiFi...", 160, 90, 2);

  dht.begin();

  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_WHITE, OUTPUT);

  digitalWrite(LED_WHITE, HIGH);
  digitalWrite(LED_GREEN, LOW);
  digitalWrite(LED_RED, LOW);

  tryConnectWiFi();

  tft.fillRect(0, 110, 320, 30, TFT_BLACK);

  if (wifiOk) {

    tft.setTextColor(TFT_GREEN, TFT_BLACK);
    tft.drawString("WiFi OK!", 160, 120, 2);

    delay(1000);

    tft.fillRect(0, 70, 320, 80, TFT_BLACK);

    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.drawString("Conectando MQTT...", 160, 90, 2);

    tryConnectMQTT();

  } else {

    tft.setTextColor(TFT_RED, TFT_BLACK);
    tft.drawString("Falha no WiFi!", 160, 120, 2);

    delay(1500);
  }

  tft.fillRect(0, 110, 320, 30, TFT_BLACK);

  if (mqttOk) {

    tft.setTextColor(TFT_GREEN, TFT_BLACK);
    tft.drawString("MQTT OK!", 160, 120, 2);

  } else {

    tft.setTextColor(TFT_RED, TFT_BLACK);
    tft.drawString("Falha MQTT!", 160, 120, 2);
  }

  delay(1500);

  tft.fillScreen(TFT_BLACK);

  if (mqttOk && pendingCount > 0) {
    flushOffline();
  }
}

void loop() {

  checkWiFi();

  checkMQTT();

  if (mqttOk) {
    mqtt.loop();
  }

  if (mqttOk && pendingCount > 0) {
    flushOffline();
  }

  readGPS();

  float temp = dht.readTemperature();
  float hum  = dht.readHumidity();

  if (isnan(temp) || isnan(hum)) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.setTextDatum(MC_DATUM);
    tft.drawString("Erro no Sensor DHT!", 120, 160, 2);

    delay(2000);

    return;
  }

  uint16_t tx = 0;
  uint16_t ty = 0;

  if (tft.getTouch(&tx, &ty)) {

    if (tx > 4 && tx < 154 && ty > 208 && ty < 236) {

      lampOn = !lampOn;

      digitalWrite(LED_WHITE, lampOn);

      delay(300);
    }
  }

  handleData(temp, hum);

  digitalWrite(LED_GREEN, mqttOk);

  digitalWrite(LED_RED, temp > 30.0);

  if (temp > 30.0) {
    drawAlert(temp);
  } else {
    drawDashboard(temp, hum);
  }

  delay(1800);
}

void drawDashboard(float t, float h) {

  tft.fillScreen(TFT_BLACK);

  uint16_t headerColor =
    mqttOk
      ? tft.color565(150, 0, 0)
      : tft.color565(120, 100, 0);

  tft.fillRect(0, 0, 320, 28, headerColor);

  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(ML_DATUM);

  tft.drawString("BEEF TRACKER", 8, 14, 1);

  tft.setTextDatum(MR_DATUM);

  tft.setTextColor(mqttOk ? TFT_GREEN : TFT_YELLOW);

  tft.drawString(mqttOk ? "ONLINE" : "OFFLINE", 312, 14, 1);

  tft.drawRoundRect(4, 32, 100, 170, 4, TFT_RED);

  tft.setTextColor(TFT_RED, TFT_BLACK);
  tft.setTextDatum(MC_DATUM);

  tft.drawString("TEMP", 54, 45, 1);

  tft.setTextColor(tft.color565(255, 80, 80), TFT_BLACK);

  tft.drawFloat(t, 1, 54, 105, 6);

  tft.setTextColor(TFT_RED, TFT_BLACK);

  tft.drawString("o C", 54, 160, 2);

  tft.drawRoundRect(112, 32, 98, 80, 4, TFT_WHITE);

  tft.setTextColor(TFT_WHITE, TFT_BLACK);

  tft.drawString("UMIDADE", 161, 45, 1);

  tft.setTextColor(tft.color565(100, 255, 180), TFT_BLACK);

  tft.drawString(String(h, 0) + "%", 161, 82, 4);

  tft.drawRoundRect(112, 120, 98, 82, 4, tft.color565(60, 60, 60));

  tft.setTextColor(tft.color565(150, 150, 150), TFT_BLACK);

  tft.drawString("LEDS", 161, 133, 1);

  tft.fillCircle(
    140,
    165,
    8,
    mqttOk ? TFT_GREEN : tft.color565(30, 60, 30)
  );

  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextDatum(ML_DATUM);

  tft.drawString("OK", 152, 165, 1);

  tft.fillCircle(
    140,
    187,
    8,
    (t > 30.0) ? TFT_RED : tft.color565(60, 20, 20)
  );

  tft.drawString("ALR", 152, 187, 1);

  tft.drawRoundRect(218, 32, 98, 170, 4, tft.color565(0, 150, 220));

  tft.setTextColor(tft.color565(0, 200, 255), TFT_BLACK);
  tft.setTextDatum(MC_DATUM);

  tft.drawString("GPS", 267, 45, 1);

  if (gpsFixed) {

    tft.setTextColor(TFT_WHITE, TFT_BLACK);

    tft.drawString("FIX", 267, 65, 1);

    tft.drawString(String(gpsLat, 4), 267, 90, 1);

    tft.drawString(String(gpsLng, 4), 267, 110, 1);

    tft.setTextColor(tft.color565(150, 150, 150), TFT_BLACK);

    tft.drawString(String(gpsSats) + " sats", 267, 135, 1);

    tft.drawString(String(gpsSpeed, 1) + " km/h", 267, 155, 1);

  } else {

    tft.setTextColor(TFT_RED, TFT_BLACK);

    tft.drawString("NO FIX", 267, 80, 1);

    tft.setTextColor(tft.color565(100, 100, 100), TFT_BLACK);

    tft.drawString("Aguardando", 267, 110, 1);

    tft.drawString("satelites...", 267, 130, 1);
  }

  uint16_t lampColor =
    lampOn
      ? tft.color565(180, 140, 0)
      : tft.color565(50, 50, 50);

  tft.fillRoundRect(4, 208, 150, 28, 4, lampColor);

  tft.setTextColor(lampOn ? TFT_BLACK : TFT_WHITE);

  tft.drawString(lampOn ? "LAMP ON" : "LAMP OFF", 79, 222, 2);

  tft.fillRoundRect(162, 208, 154, 28, 4, headerColor);

  tft.setTextColor(TFT_WHITE);

  String statusMsg =
    mqttOk
      ? "SISTEMA OK"
      : ("OFFLINE | " + String(pendingCount) + " pend.");

  tft.drawString(statusMsg, 239, 222, 1);
}

void drawAlert(float temp) {

  tft.fillScreen(TFT_BLACK);

  tft.drawRect(0, 0, 320, 240, TFT_RED);

  tft.setTextColor(TFT_RED);
  tft.setTextDatum(MC_DATUM);

  tft.drawString("ALERTA CRITICO", 160, 40, 4);

  tft.fillRect(40, 80, 240, 80, TFT_RED);

  tft.setTextColor(TFT_WHITE);

  tft.drawFloat(temp, 1, 160, 120, 7);

  tft.drawString("TEMPERATURA ALTA!", 160, 180, 2);

  tft.setTextColor(tft.color565(0, 200, 255), TFT_BLACK);

  if (gpsFixed) {
    tft.drawString(
      String(gpsLat, 4) + ", " + String(gpsLng, 4),
      160,
      205,
      1
    );
  } else {
    tft.setTextColor(TFT_YELLOW, TFT_BLACK);
    tft.drawString("GPS: SEM SINAL", 160, 205, 1);
  }

  String statusMsg =
    mqttOk
      ? "ONLINE"
      : ("OFFLINE | " + String(pendingCount) + " pend.");

  tft.setTextColor(mqttOk ? TFT_GREEN : TFT_YELLOW, TFT_BLACK);

  tft.drawString(statusMsg, 160, 225, 1);
}