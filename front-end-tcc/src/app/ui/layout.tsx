import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Menu from "./components/Menu";
import { MenuContent } from "./components/MenuContent";
import { isAuthenticated } from "../lib/sessions";
import Protected from "./components/Protected";
import { createApi } from "../lib/api";


const geistMono = Geist_Mono({
	variable: "--font-geist-mono",
	subsets: ["latin"],
});

export const metadata: Metadata = {
	title: "Beef Tracker",
};

async function getRolesFromBackend() {
	const api = await createApi()
	const res = await api.get("/auth/validate/roles");
	return res?.data?.roles || [];
}

export default async function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	await isAuthenticated();
	const roles = await getRolesFromBackend()

	return (
		<main className="flex gap-1 min-h-screen">
			<Protected>
				<Menu roles={roles}/>
				<section className="p-5 w-full">{children}</section>
			</Protected>
		</main>
	);
}
