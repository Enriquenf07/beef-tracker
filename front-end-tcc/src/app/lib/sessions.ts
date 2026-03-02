import 'server-only'
import { cookies } from 'next/headers'
import { redirect, RedirectType } from 'next/navigation'
import { revalidatePath } from 'next/cache'
import { publicApi, createApi } from './api'

export async function createSession(jwt: string) {
    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
    const cookieStore = await cookies()

    cookieStore.set('jwt', jwt, {
        httpOnly: true,
        secure: false,
        expires: expiresAt,
        sameSite: 'lax',
        path: '/',
    })
}

export async function checkSession() {
    const cookieStore = await cookies()
    return cookieStore.get('jwt')
}

export async function clearSession() {
    "use server"
    const cookieStore = await cookies()
    cookieStore.delete('jwt')
    const jwt = await checkSession()
}

export async function isAuthenticated() {
    const jwt = await checkSession()
     const api = await createApi();
    let success = false
    try {
        const response = await api.get("/auth/validate")
        success = true
    }catch(error){
        success = false
    }
    if (!success) {
        redirect('/login', RedirectType.push)
    }
    return jwt
}