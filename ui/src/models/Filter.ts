import {type Criteria} from "@/models/Criteria";

export class Filter {
    id: number;
    name: string;
    selection: FilterSelectionType;
    criterias?: Criteria[];

    constructor(id: number, name: string, selection: FilterSelectionType, criterias?: Criteria[]) {
        this.id = id;
        this.name = name;
        this.selection = selection;
        this.criterias = criterias;
    }
}

export enum FilterSelectionType {
    SELECTION_1 = 'A',
    SELECTION_2 = 'B',
    SELECTION_3 = 'C',
}

export type FilterSelectionTypeRecord = Record<string, FilterSelectionType>;