export interface Criteria {
    id: number;
    name: CriteriaName;
    operator: string;
    value: string;
}

export enum CriteriaName {
    AMOUNT = 'AMOUNT',
    DATE = 'DATE',
    TITLE = 'TITLE',
}

export type CriteriaNameRecord = Record<string, CriteriaName>;

export enum CriteriaAmountType {
    EQUAL = 'EQUAL',
    GREATER_THAN = 'GREATER_THAN',
    LESS_THAN = 'LESS_THAN',
}

export type CriteriaAmountTypeRecord = Record<string, CriteriaAmountType>;

export enum CriteriaTitleType {
    CONTAINS = 'CONTAINS',
    EQUAL = 'EQUAL',
    STARTS_WITH = 'STARTS_WITH',
    ENDS_WITH = 'ENDS_WITH',
}

export type CriteriaTitleTypeRecord = Record<string, CriteriaTitleType>;

export enum CriteriaDateType {
    EQUAL = 'EQUAL',
    FROM = 'FROM',
    TO = 'TO',
}

export type CriteriaDateTypeRecord = Record<string, CriteriaDateType>;