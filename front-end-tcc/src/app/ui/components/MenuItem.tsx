'use client'
import { Camera, House, Undo, ShoppingBag, Store, Beef, Refrigerator, Settings, Route, Globe, Thermometer, ThermometerSnowflake, Truck, User, FileInput, Building2, BarChart, BarChart2, Handshake, Bell, Timer, Users} from 'lucide-react';
import { JSX } from 'react';

export default function MenuItem({ open, label, icon, onClick }: { open: boolean, label: string, icon: string, onClick: () => void }) {
    const icons: Record<string, JSX.Element> = {
        home: <House size={20} className="shrink-0" />,
        undo: <Undo size={20} className="shrink-0" />,
        shoppingBag: <ShoppingBag size={20} className='shrink-0' />,
        beef: <Beef size={20} className='shrink-0' />,
        refrigerator: <Refrigerator size={20} className='shrink-0' />,
        store: <Store size={20} className='shrink-0' />,
        settings: <Settings size={20} className='shrink-0' />,
        route: <Route size={20} className='shrink-0' />,
        globe: <Globe size={20} className='shrink-0' />,
        thermometer: <ThermometerSnowflake size={20} className='shrink-0' />,
        truck: <Truck size={20} className='shrink-0' />,
        user: <User size={20} className='shrink-0' />,
        fileInput: <FileInput size={20} className='shrink-0' />,
        building2: <Building2 size={20} className='shrink-0' />,
        barchart: <BarChart2 size={20} className='shrink-0' />,
        handshake: <Handshake size={20} className='shrink-0' />,
        timer: <Timer size={20} className='shrink-0' />,
        bell: <Bell size={20} className='shrink-0' />,
        users: <Users size={20} className='shrink-0' />,
    }

    return (
        <button onClick={onClick}
            className={`
                flex items-center gap-3 p-2 rounded-md
                text-md text-gray-100 hover:bg-[#3B82F6] 
                transition-all duration-300 group font-bold
                ${open ? 'w-full' : 'w-9'}
            `}
        >
            {/* shrink-0 impede o ícone de ser "esmagado" durante a animação */}
            {icons[icon]}

            {/* O container do texto precisa lidar com o overflow para não quebrar a linha */}
            <span className={`
                overflow-hidden transition-all duration-1000 ease-in-out whitespace-nowrap
                ${open ? 'opacity-100 w-auto' : 'opacity-0 w-0'}
            `}>
                {label}
            </span>
        </button>
    );
}