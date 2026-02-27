'use client'

import { logout } from "@/app/actions/auth";
import { MenuContent } from "./MenuContent";
import { useState } from "react";
import { ChevronLeft, ChevronRight, CircleUser } from 'lucide-react';
import MenuItem from "./MenuItem";
import { useRouter } from "next/navigation";


export default function Menu() {
    const [open, setOpen] = useState(true);
    const router = useRouter();

    return (
        <div className="flex gap-1">
            <aside
                className={`
                    bg-[#0F172A] min-h-screen text-white p-4 
                    transition-all duration-600 ease-in-out
                    ${open ? 'w-64' : 'w-20'} 
                `}
            >
                <div className="h-1/12">
                    <div className={`w-full flex p-2 ${open ? 'justify-end' : 'justify-start'}`}>
                        <button onClick={() => setOpen(prev => !prev)} className="hover:bg-slate-800 p-1 rounded">
                            {open ? <ChevronLeft size={20} /> : <ChevronRight size={20} />}
                        </button>
                    </div>
                </div>
                <div className="h-10/12">
                    <MenuContent open={open} />
                </div>
                <div className="h-1/12">
                    <MenuItem open={open} label="Configurações" icon="settings" onClick={() => router.push('/ui/configuracoes')} />
                </div>
            </aside>
        </div>
    );
}