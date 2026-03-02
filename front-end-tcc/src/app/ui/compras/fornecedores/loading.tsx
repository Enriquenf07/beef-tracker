import { Skeleton } from "@/components/ui/skeleton";
import { TableBody, TableHeader, TableRow } from "@/components/ui/table";
import { Table } from "lucide-react";

export default function Loading() {
    return (
        <>
            <div className="flex justify-between">
                <p className="font-bold text-2xl">Fornecedores</p>
                <div>
                    <Skeleton className="w-30 h-10" />
                </div>
            </div>
            <div className="p-5 flex flex-col gap-1 justify-start">
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />
                <Skeleton className="w-full h-10 bg-muted" />

            </div>
        </>
    )
}