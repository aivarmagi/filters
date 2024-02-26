import axios, {type AxiosInstance, type AxiosRequestConfig, type AxiosResponse} from "axios";
import {catchError, from, Observable, of} from "rxjs";

const apiClient: Readonly<AxiosInstance> = axios.create({
    timeout: import.meta.env.VITE_APP_QUERY_TIMEOUT,
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
    }
});

const API_URL = import.meta.env.VITE_APP_API_URL;

export function doGet<T>(path: string, config?: AxiosRequestConfig): Observable<AxiosResponse<T>> {
    return from(apiClient.get<T>(`${API_URL}${path}`, config))
        .pipe(catchError((error) => of(error)))
}

export function doPut<T>(path: string, data: T, config?: AxiosRequestConfig): Observable<AxiosResponse<T>> {
    return from(apiClient.put<T>(`${API_URL}${path}`, data, config))
        .pipe(catchError((error) => of(error)))
}

export function doPost<T>(path: string, data: T, config?: AxiosRequestConfig): Observable<AxiosResponse<T>> {
    return from(apiClient.post<T>(`${API_URL}${path}`, data, config))
        .pipe(catchError((error) => of(error)))
}

export function doDelete<T>(path: string, config?: AxiosRequestConfig): Observable<AxiosResponse<T>> {
    return from(apiClient.delete<T>(`${API_URL}${path}`, config))
        .pipe(catchError((error) => of(error)))
}