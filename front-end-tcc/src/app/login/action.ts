import { redirect, RedirectType } from 'next/navigation'
import { clearSession, createSession } from '@/app/lib/sessions'
import { cookies } from 'next/headers';

export async function handleLogin(formData: FormData) {
    'use server'

    let isSuccess = false;

    try {
        const email = formData.get('email')
        const password = formData.get('password')

        await createSession('fake-jwt-token2')
        isSuccess = true;

    } catch (error) {
        console.error('Login failed:', error)
    }

    if (isSuccess) {
        redirect('/', RedirectType.push)
    } else {
        redirect('/login?error=true', RedirectType.push)
    }
}
