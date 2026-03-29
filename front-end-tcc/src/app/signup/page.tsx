import { handleSignUp } from "./action";
import Link from "next/link";

export default async function SignUpPage() {
    return (
        <main className="min-h-screen grid grid-cols-1 md:grid-cols-2 font-sans bg-white text-black">
            {/* COLUNA ESQUERDA: Formulário de Cadastro */}
            <div className="flex flex-col justify-center items-center p-8 md:p-16">
                <div className="w-full max-w-md">
                    <h1 className="text-4xl font-bold mb-2">Get Started Now</h1>
                    <p className="text-gray-500 mb-8">Create your account to start tracking</p>

                    <form action={handleSignUp} className="flex flex-col gap-5">
                        {/* Campo Name */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-semibold text-gray-700">Name</label>
                            <input
                                type="text"
                                name="name"
                                placeholder="Enter your name"
                                className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
                                required
                            />
                        </div>

                        {/* Campo Email */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-semibold text-gray-700">Email address</label>
                            <input
                                type="email"
                                name="email"
                                placeholder="Enter your email"
                                className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
                                required
                            />
                        </div>

                        {/* Campo Password */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-semibold text-gray-700">Password</label>
                            <input
                                type="password"
                                name="password"
                                placeholder="Name"
                                className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
                                required
                            />
                        </div>

                        {/* Checkbox de Termos */}
                        <div className="flex items-center gap-2">
                            <input
                                type="checkbox"
                                id="terms"
                                className="w-4 h-4 rounded border-gray-300 text-red-600 focus:ring-red-500"
                                required
                            />
                            <label htmlFor="terms" className="text-sm text-gray-600">
                                I agree to the <Link href="#" className="underline hover:text-black">terms & policy</Link>
                            </label>
                        </div>

                        <button
                            type="submit"
                            className="w-full py-3 rounded-xl font-bold text-white bg-gradient-to-r from-red-600 via-red-900 to-black hover:opacity-95 transition-opacity mt-2"
                        >
                            Signup
                        </button>
                    </form>

                    {/* Divisor "Or" */}
                    <div className="relative my-8 text-center">
                        <div className="absolute inset-0 flex items-center">
                            <span className="w-full border-t border-gray-200"></span>
                        </div>
                        <span className="relative bg-white px-4 text-sm text-gray-400">Or</span>
                    </div>

                    {/* Botões Sociais */}
                    <div className="grid grid-cols-2 gap-4">
                        <button className="flex items-center justify-center gap-2 border border-gray-300 p-2.5 rounded-xl hover:bg-gray-50 transition-colors text-sm font-medium">
                            <img src="https://www.svgrepo.com/show/475656/google-color.svg" className="w-5 h-5" alt="Google" />
                            Sign in with Google
                        </button>
                        <button className="flex items-center justify-center gap-2 border border-gray-300 p-2.5 rounded-xl hover:bg-gray-50 transition-colors text-sm font-medium">
                            <img src="https://www.svgrepo.com/show/511330/apple-173.svg" className="w-5 h-5" alt="Apple" />
                            Sign in with Apple
                        </button>
                    </div>

                    <p className="text-center mt-8 text-sm text-gray-600">
                        Have an account? <Link href="/login" className="text-blue-600 font-bold hover:underline">Sign In</Link>
                    </p>
                </div>
            </div>

            {/* COLUNA DIREITA: Identidade Visual (BeefTracker) */}
            <div className="hidden md:flex flex-col items-center justify-center bg-[#F3F3F3] border-l border-gray-100">
                <div className="relative w-full max-w-lg flex items-center justify-center">
                    <img
                        src="/logo.png"
                        alt="BeefTracker Logo"
                        className="w-4/5 h-auto object-contain"
                    />
                </div>
            </div>
        </main>
    );
}