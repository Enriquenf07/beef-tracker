'use client'

import { logout } from "@/app/actions/auth";
import { MenuContent } from "./MenuContent";
import { useState } from "react";
import { ChevronLeft, ChevronRight, CircleUser } from 'lucide-react';
import MenuItem from "./MenuItem";
import { useRouter } from "next/navigation";


export default function Menu(props: Readonly<{ roles: string[] }>) {
    const [open, setOpen] = useState(true);
    const router = useRouter();

    return (
        <aside
            className={`
                bg-sidebar h-screen sticky top-0 text-white p-4 
                transition-all duration-500 ease-in-out shrink-0 z-50
                flex flex-col justify-between
                ${open ? 'w-64' : 'w-20'} 
            `}
        >
            <div className="h-12 flex items-center">
                <div className={`w-full flex p-2 ${open ? 'justify-end' : 'justify-start'}`}>
                    <button onClick={() => setOpen(prev => !prev)} className="hover:bg-slate-800 p-1 rounded">
                        {open ? <ChevronLeft size={20} /> : <ChevronRight size={20} />}
                    </button>
                </div>
            </div>

            <div className="flex-1 my-4 overflow-y-auto overflow-x-hidden">
                <MenuContent open={open} roles={props.roles} />
            </div>

            <div className="pt-2 border-t border-slate-700 shrink-0">
                <MenuItem open={open} label="Configurações" icon="settings" onClick={() => router.push('/ui/configuracoes')} />
            </div>
        </aside>
    );
}