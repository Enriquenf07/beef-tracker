"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Eye, EyeOff, ShieldCheck, Lock, CheckCircle2, XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { createApi } from "../lib/api";
import { finalizarCadastroAction } from "./action";


export default function FinalizarCadastroPage() {
    const searchParams = useSearchParams();
    const token = searchParams.get("token");

    const [senha, setSenha] = useState("");
    const [confirmarSenha, setConfirmarSenha] = useState("");
    const [showSenha, setShowSenha] = useState(false);
    const [showConfirmar, setShowConfirmar] = useState(false);
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState<"idle" | "success" | "error">("idle");
    const [errorMsg, setErrorMsg] = useState("");


    const senhasIguais = senha === confirmarSenha && confirmarSenha.length > 0;
    const formValid = senhasIguais;
    const router = useRouter();
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log("submit", { senha, token });
        setLoading(true);
        setErrorMsg("");
        const result = await finalizarCadastroAction({ senha, token });
        if (result.success) {
            setStatus("success");
        } else {
            setErrorMsg(result.message || "Erro desconhecido");
            setStatus("error");
            setLoading(false);
        }
    };

    if (!token) {
        return (
            <div className="min-h-screen bg-[#0a0a0f] flex items-center justify-center p-4">
                <Card className="w-full max-w-md bg-[#111118] border-red-900/40 shadow-2xl">
                    <CardHeader className="text-center">
                        <XCircle className="mx-auto mb-3 text-red-500" size={48} />
                        <CardTitle className="text-red-400 font-serif text-2xl">Link Inválido</CardTitle>
                        <CardDescription className="text-zinc-400">
                            O link de cadastro está incompleto ou é inválido. Solicite um novo convite.
                        </CardDescription>
                    </CardHeader>
                </Card>
            </div>
        );
    }

    if (status === "success") {
        return (
            <div className="min-h-screen bg-background flex items-center justify-center p-4">
                <Card className="w-full max-w-md bg-primary border-emerald-900/40 shadow-2xl">
                    <CardHeader className="text-center pb-2">
                        <CheckCircle2 className="mx-auto mb-3 text-emerald-400" size={52} />
                        <CardTitle className="text-emerald-400 font-serif text-2xl">Cadastro Concluído!</CardTitle>
                        <CardDescription className="text-zinc-400 mt-2">
                            Sua senha foi definida com sucesso. Você já pode fazer login na plataforma.
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="pt-4">
                        <Button
                            className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-semibold tracking-wide transition-all"
                            onClick={() => (window.location.href = "/login")}
                        >
                            Ir para o Login
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[var(--background)] flex items-center justify-center p-4 relative overflow-hidden text-[var(--foreground)]">



            <div className="w-full max-w-md relative z-10">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-serif font-semibold text-[var(--foreground)] tracking-tight">
                        Finalizar Cadastro
                    </h1>
                    <p className="text-[var(--muted-foreground)] mt-2 text-sm leading-relaxed">
                        Defina sua senha para ativar o acesso à plataforma.
                    </p>
                </div>

                <Card className="bg-[var(--card)] border-[var(--border)] shadow-xl rounded-2xl">
                    <CardContent className="pt-8 pb-8 px-8">
                        <form onSubmit={handleSubmit} className="space-y-6">

                            <div className="space-y-2">
                                <Label htmlFor="senha" className="text-[var(--foreground)] text-sm font-medium">
                                    Nova Senha
                                </Label>
                                <div className="relative">
                                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" size={16} />
                                    <Input
                                        id="senha"
                                        type={showSenha ? "text" : "password"}
                                        value={senha}
                                        onChange={(e) => setSenha(e.target.value)}
                                        placeholder="••••••••"
                                        className="pl-9 pr-10 bg-[var(--background)] border-[var(--input)] text-[var(--foreground)] placeholder:text-[var(--muted-foreground)] focus-visible:ring-[var(--ring)] rounded-xl h-11 transition-all"
                                        autoComplete="new-password"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowSenha((v) => !v)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors"
                                    >
                                        {showSenha ? <EyeOff size={16} /> : <Eye size={16} />}
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="confirmar" className="text-[var(--foreground)] text-sm font-medium">
                                    Confirmar Senha
                                </Label>
                                <div className="relative">
                                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" size={16} />
                                    <Input
                                        id="confirmar"
                                        type={showConfirmar ? "text" : "password"}
                                        value={confirmarSenha}
                                        onChange={(e) => setConfirmarSenha(e.target.value)}
                                        placeholder="••••••••"
                                        className={cn(
                                            "pl-9 pr-10 bg-[var(--background)] border-[var(--input)] text-[var(--foreground)] placeholder:text-[var(--muted-foreground)] focus-visible:ring-[var(--ring)] rounded-xl h-11 transition-all",
                                            confirmarSenha.length > 0 && !senhasIguais && "border-[var(--destructive)] focus-visible:ring-[var(--destructive)]"
                                        )}
                                        autoComplete="new-password"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowConfirmar((v) => !v)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors"
                                    >
                                        {showConfirmar ? <EyeOff size={16} /> : <Eye size={16} />}
                                    </button>
                                </div>
                                {confirmarSenha.length > 0 && !senhasIguais && (
                                    <p className="text-[var(--destructive)] text-xs mt-1">As senhas não coincidem.</p>
                                )}
                            </div>



                            {status === "error" && (
                                <div className="flex items-start gap-2.5 bg-[var(--destructive)]/10 border border-[var(--destructive)]/20 rounded-xl px-4 py-3">
                                    <XCircle className="text-[var(--destructive)] shrink-0 mt-0.5" size={15} />
                                    <p className="text-[var(--destructive)] text-sm font-medium">{errorMsg}</p>
                                </div>
                            )}

                            <Button
                                type="submit"
                                disabled={!formValid || loading}
                                className="w-full bg-primary hover:opacity-90 disabled:opacity-30 disabled:cursor-not-allowed text-[var(--primary-foreground)] font-bold tracking-wide h-12 rounded-xl transition-all shadow-lg"
                            >
                                {loading ? (
                                    <span className="flex items-center gap-2">
                                        <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
                                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
                                        </svg>
                                        Salvando...
                                    </span>
                                ) : (
                                    "ATIVAR CONTA"
                                )}
                            </Button>
                        </form>
                    </CardContent>
                </Card>

            </div>
        </div>
    );
}