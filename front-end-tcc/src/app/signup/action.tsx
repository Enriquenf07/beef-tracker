'use server'

import { redirect, RedirectType } from 'next/navigation'
import { createSession } from '@/app/lib/sessions'

export async function handleSignUp(formData: FormData) {
    let isSuccess = false;

    try {
        const name = formData.get('name')
        const email = formData.get('email')
        const password = formData.get('password')

        // Quando você conectar o backend, a rota será algo como:
        // const response = await fetch(`${process.env.API_URL}/public/users`, {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify({ nome: name, email, senha: password }),
        // })

        // if (!response.ok) throw new Error('Erro ao cadastrar');

        // Por enquanto, vamos simular que deu certo para você testar a tela
        isSuccess = true;

    } catch (error) {
        console.error('Registration failed:', error)
    }

    if (isSuccess) {
        // Após o cadastro, geralmente mandamos para o login
        redirect('/login', RedirectType.push)
    } else {
        redirect('/signup?error=true', RedirectType.push)
    }
}