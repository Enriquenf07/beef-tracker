
import { checkSession, isAuthenticated } from "@/app/lib/sessions";

import Protected from "./components/Protected";
import GeoChart from "./components/Plot";

export default async function Ui() {

    return (

        <div className="font-sans ">
            <p>teste</p>
            <GeoChart data={null} />
        </div>
    );
}
