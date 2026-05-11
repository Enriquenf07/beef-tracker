#include <SPI.h>
#include <TFT_eSPI.h> 
#include <DHT.h>
#include "logo.h" 


#define DHTPIN 4
#define DHTTYPE DHT22
#define TOUCH_CS 21 

TFT_eSPI tft = TFT_eSPI(); 
DHT dht(DHTPIN, DHTTYPE);


uint16_t calData[5] = { 275, 3620, 264, 3532, 1 }; 

void setup() {
  Serial.begin(115200);
  
  
  tft.init();
  tft.setRotation(0); 
  tft.invertDisplay(true);  
  tft.setSwapBytes(true);    
  
  
  tft.setTouch(calData);
  
  tft.fillScreen(TFT_BLACK);

 
  tft.drawBitmap(20, 30, epd_bitmap_logo_beeftracker_emblema , 200, 142, 0xF800);

  
  tft.setTextColor(TFT_WHITE);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("Iniciando BeefTracker...", 120, 190, 2);

  
  int barraLarguraMax = 200; 
  int barraX = 20;            
  int barraY = 215;           
  tft.drawRect(barraX, barraY, barraLarguraMax, 10, TFT_WHITE);

  for (int i = 0; i < barraLarguraMax - 4; i++) {
    tft.fillRect(barraX + 2, barraY + 2, i, 6, TFT_RED);
    delay(10); 
  }

  dht.begin();
  delay(500);
  tft.fillScreen(TFT_BLACK);
}

void loop() {
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  
  
  if (isnan(temp)) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.drawString("Erro no Sensor DHT!", 120, 160, 2);
    return;
  }

  
  if (temp > 10.0) {
    drawAlert(temp);
  } else {
    drawDashboard(temp, hum);
  }

  
  uint16_t x, y;
  if (tft.getTouch(&x, &y)) {
   
    if (y > 250) {
      Serial.println("Toque detectado no Status!");
      tft.fillRoundRect(10, 250, 220, 60, 5, TFT_WHITE);
      tft.setTextColor(TFT_RED);
      tft.drawCentreString("CARGA VERIFICADA", 120, 280, 2);
      delay(500); 
    }
  }
  
  delay(100); 
}


void drawDashboard(float t, float h) {
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

void drawAlert(float temp) {
  tft.fillScreen(TFT_BLACK);
  tft.drawRect(0, 0, 240, 320, TFT_RED);
  tft.setTextColor(TFT_RED);
  tft.setTextDatum(MC_DATUM);
  tft.drawString("ALERTA CRITICO", 120, 60, 4);
  
  tft.fillRect(0, 120, 240, 80, TFT_RED);
  tft.setTextColor(TFT_WHITE); 
  tft.drawFloat(temp, 1, 120, 160, 7);
  tft.drawString("TEMP. ALTA!", 120, 220, 2);
}