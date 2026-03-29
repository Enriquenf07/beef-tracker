import { handleLogin } from "./action";
import Link from "next/link";

export default async function LoginPage() {
    return (
        <main className="min-h-screen grid grid-cols-1 md:grid-cols-2 font-sans bg-white">
            {/* LADO ESQUERDO: Formulário */}
            <div className="flex flex-col justify-center items-center p-8 md:p-16">
                <div className="w-full max-w-md">
                    <h1 className="text-4xl font-bold mb-2 text-black">Welcome back!</h1>
                    <p className="text-gray-500 mb-8">Enter your Credentials to access your account</p>

                    <form action={handleLogin} className="flex flex-col gap-5">
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-semibold text-gray-700">Email address</label>
                            <input
                                type="email"
                                name="email"
                                placeholder="Enter your email"
                                className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 outline-none text-black transition-all"
                                required
                            />
                        </div>

                        <div className="flex flex-col gap-1">
                            <div className="flex justify-between items-center text-black">
                                <label className="text-sm font-semibold text-gray-700">Password</label>
                                <Link href="#" className="text-xs text-blue-600 hover:underline">forgot password</Link>
                            </div>
                            <input
                                type="password"
                                name="password"
                                placeholder="Password"
                                className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 outline-none text-black transition-all"
                                required
                            />
                        </div>

                        <div className="flex items-center gap-2">
                            <input
                                type="checkbox"
                                id="remember"
                                className="w-4 h-4 rounded border-gray-300 text-red-600 focus:ring-red-500"
                            />
                            <label htmlFor="remember" className="text-sm text-gray-600">Remember for 30 days</label>
                        </div>

                        <button
                            type="submit"
                            className="w-full py-3 rounded-xl font-bold text-white bg-gradient-to-r from-red-600 via-red-900 to-black hover:opacity-90 transition-opacity mt-2"
                        >
                            Login
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
                        <button className="flex items-center justify-center gap-2 border border-gray-300 p-2.5 rounded-xl hover:bg-gray-50 transition-colors text-sm font-medium text-black">
                            <img src="https://www.svgrepo.com/show/475656/google-color.svg" className="w-5 h-5" alt="Google" />
                            Sign in with Google
                        </button>
                        <button className="flex items-center justify-center gap-2 border border-gray-300 p-2.5 rounded-xl hover:bg-gray-50 transition-colors text-sm font-medium text-black">
                            <img src="https://www.svgrepo.com/show/511330/apple-173.svg" className="w-5 h-5" alt="Apple" />
                            Sign in with Apple
                        </button>
                    </div>

                    <p className="text-center mt-8 text-sm text-gray-600 text-black">
                        Don't have an account? <Link href="/signup" className="text-blue-600 font-bold hover:underline">Sign Up</Link>
                    </p>
                </div>
            </div>


            <div className="hidden md:flex flex-col items-center justify-center bg-[#F3F3F3] border-l border-gray-100">
                <div className="relative w-full max-w-lg aspect-square flex items-center justify-center">

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