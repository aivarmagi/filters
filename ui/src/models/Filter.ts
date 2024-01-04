import type Criteria from "@/models/Criteria";

export default class Filter {
    id: number;
    name: string;
    selectionType: string;
    criterias: Criteria[];
}

export enum FilterSelectionType {
    SELECTION_1 = 'A',
    SELECTION_2 = 'B',
    SELECTION_3 = 'C',
}