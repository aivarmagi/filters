import type {Filter} from "@/models/Filter";
import {doDelete, doGet, doPost, doPut} from "@/services/apiClient";
import {map, Observable} from "rxjs";

export default {
    getFilters(field: string, sortDesc: boolean, page: number, pageSize: number): Observable<PageableResponse<Filter>> {
        return doGet<PageableResponse<Filter>>('/filters', {
            params: {
                page: page - 1,
                size: pageSize,
                sort: `${field},${sortDesc ? 'desc' : 'asc'}`
            }
        }).pipe(map(response => response.data));
    },

    getFilter(id: number): Observable<Filter> {
        return doGet<Filter>(`/filters/${id}`)
            .pipe(map(response => response.data));
    },

    putFilter(filter: Filter): Observable<Filter> {
        return doPut<Filter>(`/filters/${filter.id}`, filter)
            .pipe(map(response => response.data));
    },

    postFilter(filter: Filter): Observable<Filter> {
        return doPost<Filter>('/filters', filter)
            .pipe(map(response => response.data));
    },

    deleteFilter(id: number): Observable<void> {
        return doDelete<void>(`/filters/${id}`)
            .pipe(map(response => response.data));
    }
};