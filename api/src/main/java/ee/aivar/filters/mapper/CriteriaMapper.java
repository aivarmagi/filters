package ee.aivar.filters.mapper;

import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.db.Criteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FilterMapper.class)
public interface CriteriaMapper {

    CriteriaMapper INSTANCE = Mappers.getMapper(CriteriaMapper.class);

    @Mapping(target = "filter", ignore = true)
    Criteria toCriteria(CriteriaDTO from);

    CriteriaDTO toCriteriaDTO(Criteria from);
}
