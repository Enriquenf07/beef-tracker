export function onlyDigits(value: string): string {
    return value.replace(/\D/g, '')
}

function maskCpf(digits: string): string {
    return digits
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

function maskCnpj(digits: string): string {
    return digits
        .replace(/(\d{2})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1/$2')
        .replace(/(\d{4})(\d{1,2})$/, '$1-$2')
}

export function maskCpfCnpj(value: string): string {
    const digits = onlyDigits(value).slice(0, 14)
    if (digits.length <= 11) return maskCpf(digits)
    return maskCnpj(digits)
}

export function maskCpfCnpjPrivate(value: string): string {
    const digits = onlyDigits(value)
    if (!digits) return '-'

    if (digits.length <= 11) {

        const masked = '***.' + digits.slice(3, 6) + '.' + digits.slice(6, 9) + '-**'
        return masked
    } else {

        const masked = '**.' + digits.slice(2, 5) + '.' + digits.slice(5, 8) + '/' + digits.slice(8, 12) + '-**'
        return masked
    }
}