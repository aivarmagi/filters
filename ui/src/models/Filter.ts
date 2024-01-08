import type Criteria from "@/models/Criteria";

export default class Filter {
    id: number;
    name: string;
    selection: FilterSelectionType;
    criterias: Criteria[];
}

export enum FilterSelectionType {
    SELECTION_1 = 'A',
    SELECTION_2 = 'B',
    SELECTION_3 = 'C',
}