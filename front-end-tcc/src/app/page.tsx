import Image from "next/image";
import { checkSession, isAuthenticated } from "./lib/sessions";

import { redirect, RedirectType } from "next/navigation";

export default async function Home() {
	const session = await isAuthenticated();
	redirect('/ui', RedirectType.push)
	
}
