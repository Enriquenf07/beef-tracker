'use client'
import * as Plot from "@observablehq/plot";
import { useEffect, useRef, useState } from "react";
import * as d3 from "d3";

export default function Charts() {
    const containerRef = useRef();
    const [data, setData] = useState(null);

useEffect(() => {
    const url = "https://raw.githubusercontent.com/codeforamerica/click_that_hood/master/public/data/brazil-states.geojson";
    d3.json(url).then(setData);
  }, []);

  useEffect(() => {
    if (!data) return;

    const chart = Plot.plot({
      // Configuração da Projeção
      projection: {
        type: "mercator",
        domain: data // Faz o zoom automático para caber os dados na tela
      },
      marks: [
        Plot.geo(data, { 
          stroke: "white", 
          fill: "#2c3e50",
          strokeWidth: 0.5 
        })
      ],
      width: 600,
      height: 600
    });

    containerRef.current.append(chart);
    return () => chart.remove();
  }, [data]);

return <div ref={containerRef} />;
}