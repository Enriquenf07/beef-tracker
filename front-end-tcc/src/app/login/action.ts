import { redirect, RedirectType } from 'next/navigation'
import {  createSession } from '@/app/lib/sessions'
import { publicApi } from '../lib/api';

export async function handleLogin(formData: FormData) {
    'use server'

    let isSuccess = false;

    try {
        const email = formData.get('email')
        const password = formData.get('password')
       const {data} = await publicApi.post("/auth", {
            email, senha: password
        })
        await createSession(data.token)
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
