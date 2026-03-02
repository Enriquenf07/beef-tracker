import { isAuthenticated } from "@/app/lib/sessions";
import { Suspense } from "react";

export default async function Protected({
    children,
}: {
    children: React.ReactNode;
}) {
    await isAuthenticated(); // sua função server-side
    return (
        <Suspense fallback={<p>carregando</p>}>
            {children}
        </Suspense>
    );
}