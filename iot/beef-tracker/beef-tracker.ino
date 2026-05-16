#include <SPI.h>
#include <TFT_eSPI.h>
#include <DHT.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include "logo.h"

const char *ssid = "Enrique's Galaxy S20 FE 5G";
const char *password = "mklv4028";

const char *broker = "83e2212cc0ea43788f0907e75586790c.s1.eu.hivemq.cloud";
const int port = 8883;

#define DHTPIN 4
#define DHTTYPE DHT22

TFT_eSPI tft = TFT_eSPI();
DHT dht(DHTPIN, DHTTYPE);

WiFiClientSecure wifiClient;
PubSubClient mqtt(wifiClient);

uint16_t calData[5] = {275, 3620, 264, 3532, 1};

void connectWiFi()
{
  WiFi.begin(ssid, password);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("Conectando WiFi...", 120, 160, 2);
  Serial.print("Conectando WiFi");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi OK!");
  tft.fillScreen(TFT_BLACK);
}

void connectMQTT()
{
  wifiClient.setInsecure();
  mqtt.setServer(broker, port);

  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("Conectando MQTT...", 120, 160, 2);
  Serial.print("Conectando MQTT");

  while (!mqtt.connected())
  {
    if (mqtt.connect("BeefTrackerESP32", "Thiago", "Thiagoteste123"))
    {
      Serial.println("\nMQTT OK!");
    }
    else
    {
      Serial.print(".");
      delay(1000);
    }
  }
  tft.fillScreen(TFT_BLACK);
}

void setup()
{
  Serial.begin(115200);

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

  int barraLarguraMax = 200;
  int barraX = 20;
  int barraY = 215;
  tft.drawRect(barraX, barraY, barraLarguraMax, 10, TFT_WHITE);
  for (int i = 0; i < barraLarguraMax - 4; i++)
  {
    tft.fillRect(barraX + 2, barraY + 2, i, 6, TFT_RED);
    delay(10);
  }

  dht.begin();
  delay(500);
  tft.fillScreen(TFT_BLACK);

  connectWiFi();
  connectMQTT();
}

void loop()
{

  if (!mqtt.connected())
    connectMQTT();
  mqtt.loop();

  float temp = dht.readTemperature();
  float hum = dht.readHumidity();

  if (isnan(temp))
  {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.drawString("Erro no Sensor DHT!", 120, 160, 2);
    return;
  }

  mqtt.publish("beeftracker/temperatura", String(temp, 1).c_str());
  mqtt.publish("beeftracker/umidade", String(hum, 1).c_str());

  Serial.print("Publicado: Temp: ");
  Serial.print(temp);
  Serial.print("C | Umidade: ");
  Serial.print(hum);
  Serial.println("%");

  if (temp > 30.0)
  {
    drawAlert(temp);
  }
  else
  {
    drawDashboard(temp, hum);
  }

  delay(2000);
}

void drawDashboard(float t, float h)
{
  tft.fillRect(0, 0, 240, 35, tft.color565(150, 0, 0));
  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("BEEF TRACKER", 120, 17, 2);

  tft.drawRoundRect(10, 45, 220, 100, 5, TFT_RED);
  tft.setTextColor(TFT_RED, TFT_BLACK);
  tft.drawFloat(t, 1, 120, 95, 6);

  tft.drawRoundRect(10, 155, 220, 80, 5, TFT_WHITE);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.drawString("UMIDADE: " + String(h) + "%", 120, 185, 2);
  tft.drawString("RASTREANDO CARGA...", 120, 210, 1);

  tft.fillRoundRect(10, 250, 220, 60, 5, TFT_RED);
  tft.setTextColor(TFT_WHITE);
  tft.drawString("SISTEMA OK", 120, 280, 4);
}

void drawAlert(float temp)
{
  tft.fillScreen(TFT_BLACK);
  tft.drawRect(0, 0, 240, 320, TFT_RED);
  tft.setTextColor(TFT_RED);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("ALERTA CRITICO", 120, 60, 4);

  tft.fillRect(0, 120, 240, 80, TFT_RED);
  tft.setTextColor(TFT_WHITE);
  tft.drawFloat(temp, 1, 120, 160, 7);
  tft.drawString("TEMPERATURA ALTA!", 120, 220, 2);
}