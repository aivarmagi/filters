import type Criteria from "@/models/Criteria";

export default class Filter {
    id: number;
    name: string;
    selectionType: string;
    criterias: Criteria[];

    constructor(id: number, name: string, type: string, criterias: Criteria[]) {
        this.id = id;
        this.name = name;
        this.selectionType = type;
        this.criterias = criterias;
    }
}