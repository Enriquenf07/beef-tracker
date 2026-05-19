// components/LeiturasChart.tsx
"use client"

import * as Plot from "@observablehq/plot"
import { useEffect, useRef } from "react"
import { Leitura } from "./Content"



function usePlot(leituras: Leitura[], renderFn: (data: Leitura[]) => (SVGSVGElement | HTMLElement)) {
    const ref = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (!ref.current) return
        const chart = renderFn(leituras)
        ref.current.innerHTML = ""
        ref.current.appendChild(chart)
        return () => chart.remove()
    }, [leituras])

    return ref
}
function TempChart({ data }: { data: any[] }) {
    const ref = usePlot(data, () =>
        Plot.plot({
            height: 260,
            marginLeft: 50,
            marginRight: 20,
            style: { background: "transparent", fontFamily: "inherit" },
            x: { label: null, tickFormat: (d: Date) => d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" }) },
            y: { label: "Temperatura (°C)", grid: true },
            marks: [
                Plot.lineY(data, { x: "date", y: "temp", stroke: "var(--accent)", strokeWidth: 2, tip: true }),
                Plot.dotY(data, { x: "date", y: "temp", fill: "var(--accent)", r: 3 }),
            ],
        })
    );

    return (
        <div className="bg-card border border-border rounded-xl p-5">
            <p className="text-sm font-medium text-muted-foreground mb-2">Temperatura</p>
            <div className="flex gap-4 mb-3">
                <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <span className="w-3 h-0.5 rounded inline-block" style={{ backgroundColor: "var(--accent)" }} />
                    Temperatura (°C)
                </span>
            </div>
            <div ref={ref} />
        </div>
    );
}


function UmidadeChart({ data }: { data: any[] }) {
    const ref = usePlot(data, () =>
        Plot.plot({
            height: 260,
            marginLeft: 50,
            marginRight: 20,
            style: { background: "transparent", fontFamily: "inherit" },
            x: { label: null, tickFormat: (d: Date) => d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" }) },
            y: { label: "Umidade (%)", grid: true },
            marks: [
                Plot.lineY(data, { x: "date", y: "umidade", stroke: "var(--primary)", strokeWidth: 2, tip: true }),
                Plot.dotY(data, { x: "date", y: "umidade", fill: "var(--primary)", r: 3 }),
            ],
        })
    );

    return (
        <div className="bg-card border border-border rounded-xl p-5">
            <p className="text-sm font-medium text-muted-foreground mb-2">Umidade</p>
            <div className="flex gap-4 mb-3">
                <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <span className="w-3 h-0.5 rounded inline-block" style={{ backgroundColor: "var(--primary)" }} />
                    Umidade (%)
                </span>
            </div>
            <div ref={ref} />
        </div>
    );
}


export function LeiturasChart({ leituras }: { leituras: Leitura[] }) {
    if (!leituras.length) return null;


    const data = leituras.map(l => ({
        ...l,
        date: new Date(l.timestamp / 1_000_000),
    }));

    return (
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
            <TempChart data={data} />
            <UmidadeChart data={data} />
        </div>
    );
}