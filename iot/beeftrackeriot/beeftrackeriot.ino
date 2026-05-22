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


const char* ssid     = "Thiago 2.4GHz";
const char* password = "17283940";

const char* broker = "83e2212cc0ea43788f0907e75586790c.s1.eu.hivemq.cloud";
const int   port   = 8883;


#define DHTPIN  4
#define DHTTYPE DHT22

#define GPS_RX_PIN 12
#define GPS_TX_PIN 13
#define GPS_BAUD   9600

#define OFFLINE_FILE   "/pendentes.txt"
#define MAX_PENDING    500         


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

  if (mqtt.connect("BeefTrackerESP32", "Thiago", "Thiagoteste123")) {
    mqttOk = true;
    Serial.println("MQTT OK!");
    return true;
  }
  mqttOk = false;
  return false;
}

void checkMQTT() {
  if (!wifiOk) { mqttOk = false; return; }
  if (!mqtt.connected()) {
    mqttOk = tryConnectMQTT();
  } else {
    mqttOk = true;
  }
}


void saveOffline(const char* json) {
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

    if (mqtt.publish("beeftracker/dados", line.c_str())) {
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

  if (gps.speed.isValid())      gpsSpeed = gps.speed.kmph();
  if (gps.satellites.isValid()) gpsSats  = gps.satellites.value();
}

void handleData(float temp, float hum) {
  char json[300];
  snprintf(json, sizeof(json),
    "{\"temp\":%.1f,\"hum\":%.1f,\"lat\":%.6f,\"lng\":%.6f,"
    "\"speed\":%.1f,\"sats\":%d,\"fix\":%s}",
    temp, hum,
    gpsLat, gpsLng,
    gpsSpeed, gpsSats,
    gpsFixed ? "true" : "false"
  );

  if (mqttOk) {
    
    bool ok = mqtt.publish("beeftracker/temperatura", String(temp, 1).c_str())
           && mqtt.publish("beeftracker/umidade",     String(hum,  1).c_str())
           && mqtt.publish("beeftracker/dados",       json);

    if (gpsFixed) {
      String gpsPayload = String(gpsLat, 6) + "," + String(gpsLng, 6);
      mqtt.publish("beeftracker/gps",        gpsPayload.c_str());
      mqtt.publish("beeftracker/velocidade", String(gpsSpeed, 1).c_str());
    }

    if (!ok) {
      
      Serial.println("Publish falhou, salvando offline...");
      saveOffline(json);
    }
  } else {
    
    saveOffline(json);
  }

  Serial.printf("[%s] Temp:%.1f | Hum:%.1f | GPS:%s (%.6f,%.6f) %.1fkm/h %dsats\n",
    mqttOk ? "ONLINE" : "OFFLINE",
    temp, hum,
    gpsFixed ? "FIX" : "NO-FIX",
    gpsLat, gpsLng, gpsSpeed, gpsSats);
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
  tft.setRotation(0);
  tft.invertDisplay(true);
  tft.setSwapBytes(true);
  tft.setTouch(calData);
  tft.fillScreen(TFT_BLACK);

  tft.drawBitmap(20, 30, epd_bitmap_logo_beeftracker_emblema, 200, 142, 0xF800);
  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("Iniciando BeefTracker...", 120, 190, 2);

  int barraX = 20, barraY = 215;
  tft.drawRect(barraX, barraY, 200, 10, TFT_WHITE);
  for (int i = 0; i < 196; i++) {
    tft.fillRect(barraX + 2, barraY + 2, i, 6, TFT_RED);
    delay(10);
  }

  dht.begin();
  delay(500);
  tft.fillScreen(TFT_BLACK);

  tryConnectWiFi();
  if (wifiOk) tryConnectMQTT();

  if (mqttOk && pendingCount > 0) flushOffline();
}

void loop() {
 
  checkWiFi();
  checkMQTT();
  if (mqttOk) mqtt.loop();

 
  if (mqttOk && pendingCount > 0) flushOffline();

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

  handleData(temp, hum);

  if (temp > 30.0) {
    drawAlert(temp);
  } else {
    drawDashboard(temp, hum);
  }

  delay(1800);
}

void drawDashboard(float t, float h) {
  
  uint16_t headerColor = mqttOk
    ? tft.color565(150, 0, 0)
    : tft.color565(100, 100, 0); 

  tft.fillRect(0, 0, 240, 35, headerColor);
  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(MC_DATUM);
  tft.drawString(mqttOk ? "BEEF TRACKER" : "BEEF TRACKER [OFFLINE]", 120, 17, 1);

  
  tft.drawRoundRect(10, 40, 220, 80, 5, TFT_RED);
  tft.setTextColor(TFT_RED, TFT_BLACK);
  tft.drawString("TEMP", 120, 50, 1);
  tft.drawFloat(t, 1, 120, 78, 6);

  tft.drawRoundRect(10, 130, 220, 45, 5, TFT_WHITE);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.drawString("UMIDADE: " + String(h, 0) + "%", 120, 152, 2);

  tft.drawRoundRect(10, 183, 220, 75, 5, tft.color565(0, 180, 255));
  tft.setTextColor(tft.color565(0, 200, 255), TFT_BLACK);
  tft.drawString("GPS", 120, 193, 1);
  if (gpsFixed) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.drawString("Lat: " + String(gpsLat, 4), 120, 210, 1);
    tft.drawString("Lng: " + String(gpsLng, 4), 120, 224, 1);
    tft.drawString(String(gpsSpeed, 1) + " km/h  " + String(gpsSats) + " sats", 120, 242, 1);
  } else {
    tft.setTextColor(TFT_RED, TFT_BLACK);
    tft.drawString("Aguardando sinal GPS...", 120, 225, 1);
  }

  String statusMsg = mqttOk
    ? "SISTEMA OK"
    : ("OFFLINE | " + String(pendingCount) + " pend.");

  tft.fillRoundRect(10, 265, 220, 45, 5, headerColor);
  tft.setTextColor(TFT_WHITE);
  tft.drawString(statusMsg, 120, 287, 2);
}


void drawAlert(float temp) {
  tft.fillScreen(TFT_BLACK);
  tft.drawRect(0, 0, 240, 320, TFT_RED);
  tft.setTextColor(TFT_RED);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("ALERTA CRITICO", 120, 50, 4);

  tft.fillRect(0, 110, 240, 80, TFT_RED);
  tft.setTextColor(TFT_WHITE);
  tft.drawFloat(temp, 1, 120, 150, 7);
  tft.drawString("TEMPERATURA ALTA!", 120, 210, 2);

  tft.setTextColor(tft.color565(0, 200, 255), TFT_BLACK);
  if (gpsFixed) {
    tft.drawString(String(gpsLat, 4) + ", " + String(gpsLng, 4), 120, 260, 1);
  } else {
    tft.setTextColor(TFT_YELLOW, TFT_BLACK);
    tft.drawString("GPS: SEM SINAL", 120, 260, 1);
  }

  String statusMsg = mqttOk ? "ONLINE" : ("OFFLINE | " + String(pendingCount) + " pend.");
  tft.setTextColor(mqttOk ? TFT_GREEN : TFT_YELLOW, TFT_BLACK);
  tft.drawString(statusMsg, 120, 285, 1);
}
