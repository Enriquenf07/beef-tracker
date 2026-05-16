"use client";

import { useEffect, useRef } from "react";
import * as Plot from "@observablehq/plot";


interface GeoData {
  type: string;
  features?: any[];
  geometries?: any[];
  [key: string]: any;
}

interface ChartProps {
  data: GeoData | null;
}

export default function GeoChart({ data }: ChartProps) {

  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {

    if (!data || !containerRef.current) return;


    const chart = Plot.plot({
      projection: {
        type: "mercator",
        domain: data as any
      },
      marks: [
        Plot.geo(data as any, {
          stroke: "white",
          fill: "#2c3e50",
          strokeWidth: 0.5
        })
      ],
      width: 600,
      height: 600
    });


    const container = containerRef.current;
    container.innerHTML = "";
    container.appendChild(chart as unknown as Node);


    return () => {
      chart.remove();
    };
  }, [data]);

  return (
    <div
      ref={containerRef}
      className="chart-container"
      style={{ width: "100%", height: "auto" }}
    />
  );
}