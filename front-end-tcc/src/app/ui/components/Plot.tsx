"use client"; // Importante se estiver na pasta 'app' do Next.js

import { useEffect, useRef } from "react";
import * as Plot from "@observablehq/plot";

// Definindo uma interface flexível para o GeoJSON
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
  // 1. Tipamos o Ref explicitamente como HTMLDivElement
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Verificações de segurança
    if (!data || !containerRef.current) return;

    // 2. Criamos o gráfico
    const chart = Plot.plot({
      projection: {
        type: "mercator",
        domain: data as any // 'any' aqui evita conflitos de tipos de projeção do D3
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

    // 3. Limpeza e Adição
    // Usamos appendChild e fazemos o cast para 'Node' para evitar o erro de build
    const container = containerRef.current;
    container.innerHTML = ""; // Limpa duplicatas (comum no Strict Mode do React)
    container.appendChild(chart as unknown as Node);

    // 4. Cleanup
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