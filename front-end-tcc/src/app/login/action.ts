'use server'

import { redirect, RedirectType } from 'next/navigation'
import { createSession } from '@/app/lib/sessions'

export async function handleLogin(formData: FormData) {
    let isSuccess = false;

    try {
        const email = formData.get('email')
        const password = formData.get('password')

        // Certifique-se que seu .env tenha a variável API_URL
        const response = await fetch(`${process.env.API_URL}/public/auth`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha: password }),
        })

        if (!response.ok) throw new Error('Auth failed');

        const data: any = await response.json();

        // Supondo que sua API retorne o token no campo 'token'
        if (data.token) {
            await createSession(data.token)
            isSuccess = true;
        }

    } catch (error) {
        console.error('Login failed:', error)
    }

    if (isSuccess) {
        redirect('/', RedirectType.push)
    } else {
        redirect('/login?error=true', RedirectType.push)
    }
}