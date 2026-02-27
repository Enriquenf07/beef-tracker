
import { checkSession, isAuthenticated } from "@/app/lib/sessions";

export default async function Ui() {
    const session = await isAuthenticated();
    
    return (
        <div className="font-sans ">
            <p>teste</p>
        </div>
    );
}
