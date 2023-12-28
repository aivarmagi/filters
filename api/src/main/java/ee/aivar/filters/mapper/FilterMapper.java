package ee.aivar.filters.mapper;

import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.model.db.Filter;

public class FilterMapper {

    public Filter toFilter(FilterDTO from) {
        return Filter.builder()
                .id(from.getId())
                .name(from.getName())
                .selectionType(from.getSelectionType())
                .criterias(from.getCriterias().stream()
                        .map(new CriteriaMapper()::toCriteria)
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }

    public FilterDTO toFilterDTO(Filter from) {
        return FilterDTO.builder()
                .id(from.getId())
                .name(from.getName())
                .selectionType(from.getSelectionType())
                .criterias(from.getCriterias().stream()
                        .map(new CriteriaMapper()::toCriteriaDTO)
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}
