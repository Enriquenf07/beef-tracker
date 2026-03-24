function Content({children}: any) {
    return (
        <div >
            {children}
        </div>
    )
}
function Filter({children}: any) {
    return (
        <div>
            {children}
        </div>
    )
}

function Header({children}: any) {
    return (
        <div className="flex justify-between">
            {children}
        </div>
    )
}

function Modal({children}: any) {
    return (
        <div>
            {children}
        </div>
    )
}

function Title({children}: any) {
    return (
        <h1 className="font-bold text-2xl">
            {children}
        </h1>
    )
}

function Table({children}: any) {
    return (
        <div className="p-5 flex flex-col gap-3 justify-start">
            {children}
        </div>
    )
}


const Page = {
    Content,
    Filter,
    Modal,
    Table,
    Header,
    Title
}

export default Page