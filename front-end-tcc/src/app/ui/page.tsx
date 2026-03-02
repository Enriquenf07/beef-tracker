
import { checkSession, isAuthenticated } from "@/app/lib/sessions";
import Charts from "./components/Plot";
import Protected from "./components/Protected";

export default async function Ui() {

    return (

        <div className="font-sans ">
            <p>teste</p>
            <Charts />
        </div>
    );
}
