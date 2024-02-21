import axios, {type AxiosInstance, type AxiosRequestConfig, type AxiosResponse} from 'axios';

const apiClient: Readonly<AxiosInstance> = axios.create({
    timeout: import.meta.env.VITE_APP_QUERY_TIMEOUT,
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
    }
});

const API_URL = import.meta.env.VITE_APP_API_URL;

export function doGet<T>(path: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return new Promise((resolve, reject) => {
        apiClient.get<T>(`${API_URL}${path}`, config)
            .then(resolve)
            .catch((error) => reject(error.code));
    });
}

export function doPut<T>(url: string, data: T, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return new Promise((resolve, reject) => {
        apiClient.put<T>(url, data, config)
            .then(resolve)
            .catch((error) => reject(error.code));
    });
}

export function doPost<T>(url: string, data: T, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return new Promise((resolve, reject) => {
        apiClient.post<T>(url, data, config)
            .then(resolve)
            .catch((error) => reject(error.code));
    });
}

export function doDelete<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return new Promise((resolve, reject) => {
        apiClient.delete<T>(url, config)
            .then(resolve)
            .catch((error) => reject(error.code));
    });
}