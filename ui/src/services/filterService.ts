import type {Filter} from "@/models/Filter";
import type {AxiosResponse} from "axios";
import {delayedGet, delayedPut} from "@/services/delayedApiClient";

const API_URL = 'http://localhost:8080/api/v1';
const QUERY_DELAY = 500;

export default {
    async getFilters(field: string, sortDesc: boolean, page: number, pageSize: number): Promise<AxiosResponse<PageableResponse<Filter>>> {
        try {
            return await delayedGet<PageableResponse<Filter>>(`${API_URL}/filters`, QUERY_DELAY, {
                params: {
                    page: page - 1,
                    size: pageSize,
                    sort: `${field},${sortDesc ? 'desc' : 'asc'}`
                }
            });
        } catch (error) {
            throw error;
        }
    },

    async getFilter(id: number): Promise<AxiosResponse<Filter>> {
        try {
            return await delayedGet<Filter>(`${API_URL}/filters/${id}`, QUERY_DELAY);
        } catch (error) {
            throw error;
        }
    },

    async putFilter(filter: Filter): Promise<AxiosResponse<Filter>> {
        try {
            return await delayedPut<Filter>(`${API_URL}/filters/${filter.id}`, filter, QUERY_DELAY);
        } catch (error) {
            throw error;
        }
    }
};