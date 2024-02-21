import type {Filter} from "@/models/Filter";
import type {AxiosResponse} from "axios";
import {doDelete, doGet, doPost, doPut} from "@/services/apiClient";

export default {
    async getFilters(field: string, sortDesc: boolean, page: number, pageSize: number): Promise<AxiosResponse<PageableResponse<Filter>>> {
        return await doGet<PageableResponse<Filter>>('/filters', {
            params: {
                page: page - 1,
                size: pageSize,
                sort: `${field},${sortDesc ? 'desc' : 'asc'}`
            }
        });
    },

    async getFilter(id: number): Promise<AxiosResponse<Filter>> {
        return await doGet<Filter>(`/filters/${id}`);
    },

    async putFilter(filter: Filter): Promise<AxiosResponse<Filter>> {
        return await doPut<Filter>(`/filters/${filter.id}`, filter);
    },

    async postFilter(filter: Filter): Promise<AxiosResponse<Filter>> {
        return await doPost<Filter>('/filters', filter);
    },

    async deleteFilter(id: number): Promise<AxiosResponse<void>> {
        return await doDelete<void>(`/filters/${id}`);
    }
};