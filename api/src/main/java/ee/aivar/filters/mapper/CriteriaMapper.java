package ee.aivar.filters.mapper;

import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.db.Criteria;

public class CriteriaMapper {

    public Criteria toCriteria(CriteriaDTO from) {
        return Criteria.builder()
                .amount(from.getAmount())
                .title(from.getTitle())
                .date(from.getDate())
                .build();
    }

    public CriteriaDTO toCriteriaDTO(Criteria from) {
        return CriteriaDTO.builder()
                .id(from.getId())
                .amount(from.getAmount())
                .title(from.getTitle())
                .date(from.getDate())
                .build();
    }
}
