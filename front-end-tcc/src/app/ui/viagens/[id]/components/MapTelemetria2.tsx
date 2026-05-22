"use client";

import React, { useEffect, useRef, useState } from 'react';
import mapboxgl from 'mapbox-gl';
import { Leitura } from './Content';

mapboxgl.accessToken = process.env.NEXT_PUBLIC_MAP_TOKEN || '';

interface MapaRotaProps {
  leituras: Leitura[];
}

interface RouteInfo {
  distanceKm: number;
  durationMin: number;
}

export default function MapTelemetria2({ leituras }: MapaRotaProps) {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);
  const [routeInfo, setRouteInfo] = useState<RouteInfo | null>(null);
  const [loadingRoute, setLoadingRoute] = useState(false);

  const fetchRoute = async (coords: [number, number][]): Promise<{
    geometry: GeoJSON.LineString;
    distance: number;
    duration: number;
  } | null> => {
    const MAX_WAYPOINTS = 25;

    const waypoints: [number, number][] = Array.from(
      { length: MAX_WAYPOINTS },
      (_, i) => {
        const index = Math.round((i / (MAX_WAYPOINTS - 1)) * (coords.length - 1));
        return coords[index];
      }
    );

    const coordsString = waypoints.map(([lng, lat]) => `${lng},${lat}`).join(';');

    const url = new URL(`https://api.mapbox.com/directions/v5/mapbox/driving/${coordsString}`);
    url.searchParams.set('geometries', 'geojson');
    url.searchParams.set('overview', 'full');
    url.searchParams.set('steps', 'false');
    url.searchParams.set('access_token', mapboxgl.accessToken);

    try {
      setLoadingRoute(true);
      const res = await fetch(url.toString());
      const data = await res.json();

      if (!data.routes?.length) return null;

      const route = data.routes[0];
      return {
        geometry: route.geometry as GeoJSON.LineString,
        distance: route.distance,
        duration: route.duration,
      };
    } catch (err) {
      console.error(err);
      return null;
    } finally {
      setLoadingRoute(false);
    }
  };

  useEffect(() => {
    if (!mapContainerRef.current || !leituras?.length) return;

    const coordenadasRota = [...leituras]
      .sort((a, b) => a.timestamp - b.timestamp)
      .map((l) => [l.lon, l.lat] as [number, number]);

    if (!coordenadasRota.length) return;

    const initOrUpdateMap = async () => {
      const routeData = await fetchRoute(coordenadasRota);

      const geometry: GeoJSON.LineString = routeData?.geometry ?? {
        type: 'LineString',
        coordinates: coordenadasRota,
      };

      if (routeData) {
        setRouteInfo({
          distanceKm: routeData.distance / 1000,
          durationMin: routeData.duration / 60,
        });
      }

      if (mapRef.current) {
        const source = mapRef.current.getSource('rota-telemetria') as mapboxgl.GeoJSONSource;
        if (source) {
          source.setData({ type: 'Feature', properties: {}, geometry });
          const bounds = new mapboxgl.LngBounds();
          (geometry.coordinates as [number, number][]).forEach((c) => bounds.extend(c));
          mapRef.current.fitBounds(bounds, { padding: 40, duration: 1000 });
        }
        return;
      }

      mapRef.current = new mapboxgl.Map({
        container: mapContainerRef.current!,
        style: 'mapbox://styles/mapbox/streets-v12',
        center: coordenadasRota[0],
        zoom: 14,
      });

      mapRef.current.addControl(new mapboxgl.NavigationControl(), 'top-right');

      mapRef.current.on('load', () => {
        if (!mapRef.current) return;

        mapRef.current.addSource('rota-telemetria', {
          type: 'geojson',
          data: { type: 'Feature', properties: {}, geometry },
        });

        mapRef.current.addLayer({
          id: 'linha-rota',
          type: 'line',
          source: 'rota-telemetria',
          layout: { 'line-join': 'round', 'line-cap': 'round' },
          paint: {
            'line-color': '#38bdf8',
            'line-width': 5,
            'line-opacity': 0.85,
          },
        });

        const bounds = new mapboxgl.LngBounds();
        (geometry.coordinates as [number, number][]).forEach((c) => bounds.extend(c));
        mapRef.current.fitBounds(bounds, { padding: 40, duration: 1200 });
      });
    };

    initOrUpdateMap();

    return () => {
      if (mapRef.current) {
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, [leituras]);

  if (!leituras?.length) {
    return (
      <div className="w-full h-[500px] bg-slate-900 flex items-center justify-center rounded-2xl border border-slate-800">
        <p className="text-slate-400 text-sm animate-pulse">Aguardando dados do dispositivo IoT...</p>
      </div>
    );
  }

  return (
    <div className="relative w-full">
      <div
        ref={mapContainerRef}
        className="w-full h-[500px] rounded-2xl border border-slate-200 shadow-md overflow-hidden"
      />

      {routeInfo && !loadingRoute && (
        <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm rounded-xl shadow-md px-4 py-2 flex gap-4 text-sm text-slate-700">
          <span>🛣 {routeInfo.distanceKm.toFixed(2)} km</span>
          <span>⏱ {Math.round(routeInfo.durationMin)} min</span>
        </div>
      )}

      {loadingRoute && (
        <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm rounded-xl shadow-md px-4 py-2 text-sm text-slate-500 animate-pulse">
          Calculando rota...
        </div>
      )}
    </div>
  );
}