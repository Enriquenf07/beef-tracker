import { redirect, RedirectType } from "next/navigation";
import { handleLogin } from "./action";
import { checkSession } from "../lib/sessions";


export default async function LoginPage() {

    return (
        <main className="font-sans flex flex-col items-center justify-center min-h-screen p-8 gap-4">
            <div className="flex flex-col bg-red-200 p-10 rounded-xl">
                <h1 className="text-center text-2xl mb-4 ">Login</h1>
                <form className="flex flex-col gap-4" action={handleLogin}>
                    <input type="email" name="email" placeholder="Email" />
                    <input type="password" name="password" placeholder="Senha" />
                    <button type="submit">Entrar</button>
                </form>
            </div>
        </main>
    );
}