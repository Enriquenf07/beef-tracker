package main

import (
	"context"
	"fmt"
	"log"
	"math/rand"
	"time"

	"github.com/InfluxCommunity/influxdb3-go/v2/influxdb3"
)

func main() {
    // Create client
    url := "https://us-east-1-1.aws.cloud2.influxdata.com"
    token := "6GX5vG7CGAI_OYPgTSp1yBNQviFVzJrRZombKuG7JlikBI9XpyPRD_gYBnVyTOOYAXglzgIMI68DRi_qxWttew=="

    client, err := influxdb3.New(influxdb3.ClientConfig{
		Host:     url,
		Token:    token,
		Database: "viagens",
	})
	if err != nil {
		panic(err)
	}
	defer client.Close()

	rng := rand.New(rand.NewSource(time.Now().UnixNano()))


	baseLat := -23.5505
	baseLon := -46.6333
	baseTemp := 28.3
	baseUmidade := 65.2

	points := make([]*influxdb3.Point, 0, 50)

	for i := 0; i < 50; i++ {

		lat := baseLat + float64(i)*0.001 + rng.Float64()*0.0005
		lon := baseLon + float64(i)*0.001 + rng.Float64()*0.0005
		temp := baseTemp + rng.Float64()*4 - 2      // ±2°C
		umidade := baseUmidade + rng.Float64()*10 - 5 // ±5%

		point := influxdb3.NewPoint("viagem",
			map[string]string{
				"sensor_uuid": "d16bb857-c478-4e5c-b11f-df8f3fca180a",
			},
			map[string]any{
				"lat":     lat,
				"lon":     lon,
				"temp":    temp,
				"umidade": umidade,
			},
			time.Now().Add(time.Duration(i) * time.Minute),
		)

		points = append(points, point)
	}

	if err := client.WritePoints(context.Background(), points); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("%d pontos gravados!\n", len(points))
}