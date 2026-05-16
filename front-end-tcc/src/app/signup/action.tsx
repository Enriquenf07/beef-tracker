'use server'

import { redirect, RedirectType } from 'next/navigation'
import { createSession } from '@/app/lib/sessions'

export async function handleSignUp(formData: FormData) {
    let isSuccess = false;

    try {
        const name = formData.get('name')
        const email = formData.get('email')
        const password = formData.get('password')


        isSuccess = true;

    } catch (error) {
        console.error('Registration failed:', error)
    }

    if (isSuccess) {

        redirect('/login', RedirectType.push)
    } else {
        redirect('/signup?error=true', RedirectType.push)
    }
}