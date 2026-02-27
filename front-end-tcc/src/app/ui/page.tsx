
import { checkSession, isAuthenticated } from "@/app/lib/sessions";
import Charts from "./components/Plot";

export default async function Ui() {
    const session = await isAuthenticated();

    return (
        <div className="font-sans ">
            <p>teste</p>
            <Charts/>
        </div>
    );
}
