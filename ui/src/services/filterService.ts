import type Filter from "@/models/Filter";
import type {AxiosResponse} from "axios";
import {delayedGet} from "@/services/delayedApiClient";

const API_URL = 'http://localhost:8080/api/v1';

export default {
    async getFilters(field: string, sortDesc: boolean, page: number, pageSize: number): Promise<AxiosResponse<PageableResponse<Filter>>> {
        try {
            return await delayedGet<PageableResponse<Filter>>(`${API_URL}/filters`, 2000, {
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
};