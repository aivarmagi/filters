package ee.aivar.filters.mapper;

import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.model.db.Filter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = CriteriaMapper.class)
public interface FilterMapper {

    FilterMapper INSTANCE = Mappers.getMapper(FilterMapper.class);

    Filter toFilter(FilterDTO from);

    @Mapping(target = "id", expression = "java(from.getId() == null ? null : from.getId())")
    @Mapping(target = "criterias", ignore = true)
    Filter toFilterWithoutCriteria(FilterDTO from);

    FilterDTO toFilterDTO(Filter from);
}
