# BeefTracker

O **BeefTracker** é uma plataforma web voltada para a gestão logística e rastreabilidade em indústrias frigoríficas. O sistema realiza o controle completo de lotes desde a entrada do pedido de compra de matéria-prima até a venda final ao cliente, integrando **Internet das Coisas (IoT)** para monitorar em tempo real variáveis críticas de transporte como temperatura, umidade e localização geográfica.

---

## O Problema e a Solução

Produtos perecíveis exigem rigoroso controle térmico para manter a qualidade e atender às normas sanitárias. O BeefTracker resolve isso centralizando a gestão operacional e oferecendo um ecossistema de monitoramento ponta a ponta:

* **Rastreabilidade de Lotes:** Controle rígido de entrada e saída de mercadorias.
* **Telemetria IoT em Tempo Real:** Captura contínua de dados de sensores durante o transporte, gerando gráficos de desempenho e alertas de oscilação de temperatura.
* **Gestão Logística Completa:** Controle de motoristas, veículos (frota), rotas, pedidos de compra e ordens de venda.
* **Transparência para o Cliente:** Disponibilização de links de rastreamento para que o comprador acompanhe a carga e o histórico térmico da viagem.

---

## Stack Tecnológica

O projeto foi construído utilizando uma arquitetura moderna dividida em microsserviços/módulos independentes:

### Backend & Dados
* **Core API:** Java 17 + Spring Boot (Spring Security, Spring Data JPA)
* **Banco de Dados Principal:** PostgreSQL (Persistência de cadastros, auditorias e relatórios)
* **Banco Temporal (IoT):** InfluxDB (Otimizado para séries temporais de sensores)
* **Mensageria / Protocolo:** MQTT (Broker HiveMQ) para comunicação leve com o hardware

### Frontend (Web App)
* **Framework:** Next.js (React)
* **Estilização:** Tailwind CSS  

### Engenharia de Hardware (IoT)
* **Microcontrolador:** ESP32
* **Sensores:** DHT22 (Temperatura e Umidade) + Módulo GPS Neo-6M
* **Interface Física:** Display OLED TFT 2.4” (Driver ILI9341) para comunicação direta com o motorista


##  Estrutura do Projeto

O repositório está organizado da seguinte forma:

*  `/backend` — API REST desenvolvida em Spring Boot.
*  `/front-end-tcc` — Interface administrativa e painel do cliente em Next.js.
 *  `iot/beeftrackeriot` — Código da Firmware do IOT.

## Backend
Para rodar o backend, instale o projeto com o maven normalmente. 

## Frontend
Para rodar o front end, rode npm install e depois npm run dev.
     


