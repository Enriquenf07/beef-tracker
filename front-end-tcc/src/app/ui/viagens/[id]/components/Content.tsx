'use client'
import { map } from "d3"
import { LeiturasChart } from "./Charts"
import { MapComponent } from "./Map"
import MapTelemetria from "./MapTelemetria"
import PainelStats, { LeituraStats } from "./Stats"
import { useState } from "react"
import MapTelemetria2 from "./MapTelemetria2"
import { Button } from "@/components/ui/button"

export interface Leitura {
    timestamp: number
    temp: number
    umidade: number
    lat: number
    lon: number
}

export function LeituraItem({ leitura }: { leitura: Leitura }) {
    return (
        <div className="flex items-center justify-between px-5 py-3 bg-card hover:bg-muted transition-colors duration-150">
            <span className="text-xs text-muted-foreground w-40 shrink-0">
                {new Date(leitura.timestamp / 1_000_000).toLocaleString("pt-BR")}
            </span>



            <div className="flex items-center gap-6">
                <div className="text-center">
                    <p className="text-[10px] uppercase tracking-wide text-muted-foreground">Temp.</p>
                    <p className="text-sm font-semibold text-accent-foreground">{Number(leitura.temp).toFixed(2)}°C</p>
                </div>
                <div className="text-center">
                    <p className="text-[10px] uppercase tracking-wide text-muted-foreground">Umidade</p>
                    <p className="text-sm font-semibold text-primary">{Number(leitura.umidade).toFixed(2)}%</p>
                </div>
            </div>

            <span className="text-xs font-mono text-muted-foreground text-right">
                {leitura.lat.toFixed(4)}, {leitura.lon.toFixed(4)}
            </span>
        </div>
    )
}

function Content({ leituras, stats }: { leituras: Leitura[]; stats: LeituraStats | null  }) {
    const [mapaTipo, setMapaTipo] = useState(2);
    if(leituras.length === 0) {
        return (
            <div className="flex flex-col gap-3 justify-start">
                <h1 className="text-2xl font-bold">Viagem</h1>
                <p className="text-gray-500">Aqui estão os detalhes da viagem.</p>
                <div className="flex justify-center items-center h-40 w-full">
                    <p>Nenhuma leitura encontrada para esta viagem.</p>
                </div>
            </div>
        )
    }
    return (
        <div className="flex flex-col gap-3 justify-start">
            <h1 className="text-2xl font-bold">Viagem</h1>
            <p className="text-gray-500">Aqui estão os detalhes da viagem.</p>
            <div>
                <PainelStats stats={stats}/>
            </div>
            <div>
                <Button variant="outline" size="sm" onClick={() => setMapaTipo(mapaTipo === 1 ? 2 : 1)}>
                    {mapaTipo === 1 ? "Ver Mapa Principal (com ajustes na rota)" : "Ver Mapa Alternativo (com rota original do gps)"}
                </Button>
            </div>
            <div>
                {
                    mapaTipo === 1 ? (
                        <MapTelemetria leituras={leituras} />
                    ) : (
                        <MapTelemetria2 leituras={leituras} />
                    )
                }
            </div>
            <div>
                <LeiturasChart leituras={leituras} />
            </div>
            <div>
                <h2 className="text-lg font-semibold mb-3 text-primary">Logs da viagem</h2>
                <div className="flex flex-col divide-y divide-border border border-border rounded-xl overflow-hidden">
                    {leituras.map((leitura: Leitura) => (
                        <LeituraItem key={leitura.timestamp} leitura={leitura} />
                    ))}
                </div>
            </div>
        </div>
    )
}

export default Content