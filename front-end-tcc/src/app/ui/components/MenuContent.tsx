'use client'

import { logout } from "@/app/actions/auth";
import MenuItem from "./MenuItem";
import { useState } from "react";
import { useRouter } from "next/navigation";
import path from "path";

export function MenuContent(props: Readonly<{ open: boolean, roles: any[] }>) {
    const menus = [
        {
            id: "home",
            path: "/ui",
            icon: "home",
            label: "Home"
        },
        {
            id: "compras",
            label: "Compras",
            icon: "shoppingBag",
            menu: [
                { id: "fornecedores", path: "/ui/compras/fornecedores", icon: "building2", label: "Fornecedores" },
                { id: "pedidos-compra", path: "/ui/compras/pedidos", icon: "fileInput", label: "Pedidos de Compra" },
                { id: "relatorios-compra", path: "/ui/compras/relatorios", icon: "barchart", label: "Relatórios" }
            ]
        },
        {
            id: "vendas",
            label: "Vendas",
            icon: "store",
            menu: [
                { id: "clientes", path: "/ui/vendas/clientes", icon: "users", label: "Clientes" }, 
                { id: "pedidos-venda", path: "/ui/vendas/pedidos", icon: "fileInput", label: "Pedidos de Venda" },
                { id: "relatorios-venda", path: "/ui/vendas/relatorios", icon: "barchart", label: "Relatórios" }
            ]
        },
        {
            id: "monitoramento",
            label: "Monitoramento",
            icon: "timer",
            menu: [
                { id: "viagens", path: "/ui/viagens", icon: "globe", label: "Viagens" },
                { id: "alertas", path: "/ui/viagens/alertas", icon: "bell", label: "Alertas de Carga" }
            ]
        },
        {
            id: "logistica",
            label: "Gestão de Ativos",
            icon: "route",
            menu: [
                { id: "estoque", path: "/ui/estoque", icon: "refrigerator", label: "Estoque" },
                { id: "veiculos", path: "/ui/veiculos", icon: "truck", label: "Veículos" },
                { id: "motoristas", path: "/ui/motorista", icon: "user", label: "Motoristas" },
                { id: "sensores", path: "/ui/sensores", icon: "thermometer", label: "Sensores (Hardware)" }
            ]
        },
                {
            id: "usuarios",
            label: "Usuários do sistema",
            roles: ["ADMIN"],
            icon: "users",
            path: "/ui/usuarios"
        },
    ];

    const [menuAtual, setMenuAtual] = useState(menus);
    const router = useRouter()

    const handleClick = (item: any) => {
        if (item.voltar) {
            setMenuAtual(menus);
            return;
        }
        if (item.menu) {
            const newMenu = [
                {
                    id: "voltar",
                    voltar: true,
                    icon: "undo",
                    label: "Voltar"
                },
                ...item.menu
            ];
            setMenuAtual(newMenu);
        } else {
            router.push(item.path);
        }
    }


    return (
        <div className="p-1">
            {menuAtual.filter(i => !i.roles || i.roles.some(role => props.roles.map(r => r.nome)?.includes(role))).map(menu => (
                <MenuItem onClick={() => handleClick(menu)} key={menu.id} open={props.open} label={menu.label} icon={menu.icon} />
            ))}
        </div>
    );
}