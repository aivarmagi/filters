import type {AxiosRequestConfig, AxiosResponse} from "axios";
import apiClient from "@/services/apiClient";

export function delayedGet<T>(url: string, delay: number, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            apiClient.get<T>(url, config)
                .then(resolve)
                .catch((error) => reject(error.code));
        }, delay);
    });
}

export function delayedPut<T>(url: string, data: T, delay: number, config?: AxiosRequestConfig): Promise<VoidFunction> {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            apiClient.put<T>(url, data, config)
                .then(resolve)
                .catch((error) => reject(error.code));
        }, delay);
    });
}
