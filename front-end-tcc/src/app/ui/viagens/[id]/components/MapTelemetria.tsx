"use client";

import React, { useEffect, useRef } from 'react';
import mapboxgl from 'mapbox-gl';
import { Leitura } from './Content';

// Configura o token de acesso do ambiente Next.js
mapboxgl.accessToken = process.env.NEXT_PUBLIC_MAP_TOKEN || '';

interface MapaRotaProps {
  leituras: Leitura[];
}

export default function MapTelemetria({ leituras }: MapaRotaProps) {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);

  useEffect(() => {

    if (!mapContainerRef.current || !leituras || leituras.length === 0) return;


    const coordenadasRota = [...leituras]
      .sort((a, b) => a.timestamp - b.timestamp)
      .map((leitura) => [leitura.lon, leitura.lat] as [number, number]);

    if (coordenadasRota.length === 0) return;

    if (mapRef.current) {
      const source = mapRef.current.getSource('rota-telemetria') as mapboxgl.GeoJSONSource;
      if (source) {
        source.setData({
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: coordenadasRota,
          },
        });


        const bounds = new mapboxgl.LngBounds();
        coordenadasRota.forEach((coord) => bounds.extend(coord));
        mapRef.current.fitBounds(bounds, { padding: 40, duration: 1000 });
      }
      return;
    }


    mapRef.current = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v12', 
      center: coordenadasRota[0],
      zoom: 14,
    });

    mapRef.current.addControl(new mapboxgl.NavigationControl(), 'top-right');


    mapRef.current.on('load', () => {
      if (!mapRef.current) return;


      mapRef.current.addSource('rota-telemetria', {
        type: 'geojson',
        data: {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: coordenadasRota,
          },
        },
      });


      mapRef.current.addLayer({
        id: 'linha-rota',
        type: 'line',
        source: 'rota-telemetria',
        layout: {
          'line-join': 'round',
          'line-cap': 'round',
        },
        paint: {
          'line-color': '#38bdf8',
          'line-width': 5,
          'line-opacity': 0.85,
        },
      });


      const bounds = new mapboxgl.LngBounds();
      coordenadasRota.forEach((coord) => bounds.extend(coord));
      mapRef.current.fitBounds(bounds, {
        padding: 40,
        duration: 1200,
      });
    });


    return () => {
      if (mapRef.current) {
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, [leituras]); 


  if (!leituras || leituras.length === 0) {
    return (
      <div className="w-full h-[500px] bg-slate-900 flex items-center justify-center rounded-2xl border border-slate-800">
        <p className="text-slate-400 text-sm animate-pulse">Aguardando dados do dispositivo IoT...</p>
      </div>
    );
  }

  return (
    <div 
      ref={mapContainerRef} 
      className="w-full h-[500px] rounded-2xl border border-slate-200 shadow-md overflow-hidden" 
    />
  );
}