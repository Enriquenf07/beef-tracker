package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/InfluxCommunity/influxdb3-go/v2/influxdb3"
)

func main() {
    // Create client
    url := "https://us-east-1-1.aws.cloud2.influxdata.com"
    token := 

    // Create a new client using an InfluxDB server base URL and an authentication token
    client, err := influxdb3.New(influxdb3.ClientConfig{
      Host:  url,
      Token: token,
	  Database: "viagens",
    })

    if err != nil {
      panic(err)
    }
    // Close client at the end and escalate error if present
    defer client.Close()

	point := influxdb3.NewPoint("viagem",
		map[string]string{
			"sensor_uuid": "d16bb857-c478-4e5c-b11f-df8f3fca180a",
		},
		map[string]any{
			"lat":     -23.5505,
			"lon":     -46.6333,
			"temp":    28.3,
			"umidade": 65.2,
		},
		time.Now(),
	)

	if err := client.WritePoints(context.Background(), []*influxdb3.Point{point}); err != nil {
		log.Fatal(err)
	}

	fmt.Println("ponto gravado!")
}