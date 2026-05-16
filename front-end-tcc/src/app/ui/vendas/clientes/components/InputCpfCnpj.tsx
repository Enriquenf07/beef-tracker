'use client'

import { Input } from "@/components/ui/input"
import { maskCpfCnpj, onlyDigits } from "@/app/lib/masks"
import { useState } from "react"

interface InputCpfCnpjProps {
    name: string
    defaultValue?: string
    placeholder?: string
}

export function InputCpfCnpj({ name, defaultValue = '', placeholder = 'CPF/CNPJ' }: InputCpfCnpjProps) {
    const [display, setDisplay] = useState(defaultValue ? maskCpfCnpj(defaultValue) : '')
    const [raw, setRaw] = useState(defaultValue ? onlyDigits(defaultValue) : '')

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const digits = onlyDigits(e.target.value).slice(0, 14)
        setRaw(digits)
        setDisplay(maskCpfCnpj(digits))
    }

    return (
        <>
            { }
            <input type="hidden" name={name} value={raw} />
            { }
            <Input
                placeholder={placeholder}
                type="text"
                value={display}
                onChange={handleChange}
                maxLength={18}
            />
        </>
    )
}