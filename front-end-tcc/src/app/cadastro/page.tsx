import { Suspense } from "react";
import FinalizarCadastroForm from "./FinalizarCadastroForm";

export default function FinalizarCadastroPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-[#0a0a0f] flex items-center justify-center" />}>
      <FinalizarCadastroForm />
    </Suspense>
  );
}