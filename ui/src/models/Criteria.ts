export default interface Criteria {
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

export enum CriteriaAmountType {
    EQUAL = 'EQUAL',
    GREATER_THAN = 'GREATER_THAN',
    LESS_THAN = 'LESS_THAN',
}

export enum CriteriaTitleType {
    CONTAINS = 'CONTAINS',
    EQUAL = 'EQUAL',
    STARTS_WITH = 'STARTS_WITH',
    ENDS_WITH = 'ENDS_WITH',
}

export enum CriteriaDateType {
    EQUAL = 'EQUAL',
    FROM = 'FROM',
    TO = 'TO',
}