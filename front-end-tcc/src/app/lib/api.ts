import axios, { AxiosRequestConfig } from "axios"
import { checkSession } from "./sessions"

export const publicApi = axios.create({
    baseURL: `http://${process.env.API_URL}/public`,
});

export const createApi = async () => {
    const instance = axios.create({
        baseURL: `http://${process.env.API_URL}`,
    });
    const jwt = await checkSession()
    instance.interceptors.request.use((config) => {
        if (jwt) {
            config.headers['Authorization'] = `Bearer ${jwt.value}`;
        }

        return config;
    });

    return instance;
};