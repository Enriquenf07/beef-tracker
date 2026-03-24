'use client';
import { clearSession } from "@/app/lib/sessions";
import { Button } from "@/components/ui/button";

export default function Content() {
    return (
        <div className="flex flex-col gap-3 justify-start">
            <Button onClick={clearSession} variant='secondary'>Sair da conta</Button>
        </div>
    )
}

