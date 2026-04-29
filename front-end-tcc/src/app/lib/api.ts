import axios, { AxiosRequestConfig } from "axios"
import { checkSession } from "./sessions"

const BASE_URL = process.env.API_URL;

async function fetcher(url: string, options: RequestInit = {}) {
    const res = await fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    });


    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw { response: { data: error, status: res.status } };
    }
    if (res) {
        const text = await res.text();
        if (text) {
            const data = await JSON.parse(text);
            return { data };
        }

    }

}

export const publicApi = {
    post: (endpoint: string, body: any) =>
        fetcher(`${BASE_URL}/public${endpoint}`, {
            method: 'POST',
            body: JSON.stringify(body),
        }),
    get: (endpoint: string) =>
        fetcher(`${BASE_URL}/public${endpoint}`, { method: 'GET' }),
};

type FetchOptions = RequestInit & {
    params?: Record<string, string | number | boolean>;
};


export const createApi = async () => {
    const jwt = await checkSession();
    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        'Authorization': jwt ? `Bearer ${jwt.value}` : ``
    };


    return {
        get: (endpoint: string, options: FetchOptions = {}) => {
            const { params, ...rest } = options;
            const query = params
                ? '?' + new URLSearchParams(params as Record<string, string>).toString()
                : '';
            return fetcher(`${BASE_URL}${endpoint}${query}`, {
                ...rest,
                method: 'GET',
                headers: { ...headers, ...options.headers as Record<string, string> },
            });
        },
        post: (endpoint: string, body?: any, options: RequestInit = {}) =>
            fetcher(`${BASE_URL}${endpoint}`, {
                ...options,
                method: 'POST',
                body: body ? JSON.stringify(body) : undefined,
                headers: { ...headers, ...options.headers as Record<string, string> },
            }),
        put: (endpoint: string, body: any, options: RequestInit = {}) =>
            fetcher(`${BASE_URL}${endpoint}`, {
                ...options,
                method: 'PUT',
                body: JSON.stringify(body),
                headers: { ...headers, ...options.headers as Record<string, string> },
            }),
        delete: (endpoint: string, options: RequestInit = {}) =>
            fetcher(`${BASE_URL}${endpoint}`, {
                ...options,
                method: 'DELETE',
                headers: { ...headers, ...options.headers as Record<string, string> },
            }),
        patch: (endpoint: string, body?: any, options: RequestInit = {}) =>
            fetcher(`${BASE_URL}${endpoint}`, {
                ...options,
                method: 'PATCH',
                body: JSON.stringify(body || ""),
                headers: { ...headers, ...options.headers as Record<string, string> },
            }),
    };
};