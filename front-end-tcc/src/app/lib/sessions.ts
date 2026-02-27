import 'server-only'
import { cookies } from 'next/headers'
import { redirect, RedirectType } from 'next/navigation'
import { revalidatePath } from 'next/cache'

export async function createSession(jwt: string) {
    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
    const cookieStore = await cookies()

    cookieStore.set('jwt', jwt, {
        httpOnly: true,
        secure: true,
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
    console.log('clearing session')
    const cookieStore = await cookies()
    cookieStore.delete('jwt')
    const jwt = await checkSession()
    
    console.log('jwt after clearing:', jwt)
}

export async function isAuthenticated() {
    const jwt = await checkSession()
    console.log(jwt)
    if (!jwt) {
        redirect('/login', RedirectType.push)
    }
    return jwt
}