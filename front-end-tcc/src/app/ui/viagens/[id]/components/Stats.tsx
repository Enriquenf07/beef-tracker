"use client";

import React from 'react';

export interface LeituraStats {
  totalLeituras: number;
  tempMedia: number;
  tempMin: number;
  tempMax: number;
  umidadeMedia: number;
  umidadeMin: number;
  umidadeMax: number;
  duracaoSegundos: number;
}

interface PainelStatsProps {
  stats: LeituraStats | null;
}

export default function PainelStats({ stats }: PainelStatsProps) {
  if (!stats) {
    return (
      <div className="w-full p-6 text-center bg-slate-50 border border-slate-200 rounded-2xl">
        <p className="text-sm text-slate-400">Nenhum dado estatístico disponível.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 w-full">
      <div className="p-5 bg-white border border-slate-100 rounded-2xl shadow-sm">
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">Temperatura Média</p>
        <div className="flex items-baseline gap-1 mt-2">
          <span className="text-3xl font-bold text-slate-800">{stats.tempMedia.toFixed(1)}</span>
          <span className="text-sm font-semibold text-slate-500">°C</span>
        </div>
        <div className="flex gap-3 mt-3 text-xs text-slate-500 border-t border-slate-50 pt-2">
          <span>Min: <strong className="text-blue-500">{stats.tempMin.toFixed(1)}°C</strong></span>
          <span>Max: <strong className="text-red-500">{stats.tempMax.toFixed(1)}°C</strong></span>
        </div>
      </div>

      <div className="p-5 bg-white border border-slate-100 rounded-2xl shadow-sm">
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">Umidade Média</p>
        <div className="flex items-baseline gap-1 mt-2">
          <span className="text-3xl font-bold text-slate-800">{stats.umidadeMedia.toFixed(1)}</span>
          <span className="text-sm font-semibold text-slate-500">%</span>
        </div>
        <div className="flex gap-3 mt-3 text-xs text-slate-500 border-t border-slate-50 pt-2">
          <span>Min: <strong className="text-amber-600">{stats.umidadeMin.toFixed(0)}%</strong></span>
          <span>Max: <strong className="text-indigo-600">{stats.umidadeMax.toFixed(0)}%</strong></span>
        </div>
      </div>

      <div className="p-5 bg-white border border-slate-100 rounded-2xl shadow-sm">
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">Duração do Trajeto</p>
        <div className="flex items-baseline gap-1 mt-2">
          <span className="text-3xl font-bold text-slate-800">{((stats.duracaoSegundos/60)).toFixed(0)}</span>
          <span className="text-sm font-semibold text-slate-500">minutos</span>
        </div>
        <div className="mt-3 text-xs text-slate-400 border-t border-slate-50 pt-2">
          Intervalo total monitorado
        </div>
      </div>

      <div className="p-5 bg-white border border-slate-100 rounded-2xl shadow-sm">
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">Amostras Coletadas</p>
        <div className="flex items-baseline gap-1 mt-2">
          <span className="text-3xl font-bold text-slate-800">{stats.totalLeituras}</span>
          <span className="text-sm font-semibold text-slate-500">registros</span>
        </div>
        <div className="mt-3 text-xs text-slate-400 border-t border-slate-50 pt-2">
          Frequência ativa de telemetria
        </div>
      </div>
    </div>
  );
}