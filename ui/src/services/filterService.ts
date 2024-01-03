import type Filter from "@/models/Filter";
import type {AxiosResponse} from "axios";
import {delayedGet} from "@/services/delayedApiClient";

const API_URL = 'http://localhost:8080/api/v1';

export default {
    async getFilters(): Promise<AxiosResponse<PageableResponse<Filter>>> {
        try {
            return await delayedGet<PageableResponse<Filter>>(`${API_URL}/filters`, 2000, {
                params: {
                    page: 0,
                    size: 10,
                }
            });
        } catch (error) {
            throw error;
        }
    },
};