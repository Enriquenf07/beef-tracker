import { cookies } from "next/headers"
import { redirect, RedirectType } from "next/navigation"

export async function logout(formData: FormData) {
    'use server'
    console.log('Logging out')
    const cookieStore = await cookies()
    cookieStore.delete('jwt')
    redirect('/', RedirectType.push)
}