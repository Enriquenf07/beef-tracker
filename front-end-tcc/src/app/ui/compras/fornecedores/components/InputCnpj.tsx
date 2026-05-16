'use client'

import { Input } from "@/components/ui/input"
import { onlyDigits } from "@/app/lib/masks"
import { useState } from "react"

interface InputCnpjProps {
    name: string
    defaultValue?: string
    placeholder?: string
}

function maskCnpj(digits: string): string {
    return digits
        .replace(/(\d{2})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1/$2')
        .replace(/(\d{4})(\d{1,2})$/, '$1-$2')
}

export function InputCnpj({ name, defaultValue = '', placeholder = 'CNPJ' }: InputCnpjProps) {
    const [display, setDisplay] = useState(defaultValue ? maskCnpj(onlyDigits(defaultValue).slice(0, 14)) : '')
    const [raw, setRaw] = useState(defaultValue ? onlyDigits(defaultValue) : '')

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const digits = onlyDigits(e.target.value).slice(0, 14)
        setRaw(digits)
        setDisplay(maskCnpj(digits))
    }

    return (
        <>
            <input type="hidden" name={name} value={raw} />
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