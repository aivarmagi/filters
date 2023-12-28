package ee.aivar.filters.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.db.Criteria;

class CriteriaMapperTest {

    private final CriteriaMapper criteriaMapper = new CriteriaMapper();

    @Test
    void toCriteria() {
        CriteriaDTO dto = new CriteriaDTO();
        dto.setAmount(100L);
        dto.setTitle("Test");
        dto.setDate(LocalDate.now());

        Criteria criteria = criteriaMapper.toCriteria(dto);

        assertEquals(dto.getAmount(), criteria.getAmount());
        assertEquals(dto.getTitle(), criteria.getTitle());
        assertEquals(dto.getDate(), criteria.getDate());
    }

    @Test
    void toCriteriaDTO() {
        Criteria criteria = new Criteria();
        criteria.setId(2L);
        criteria.setAmount(200L);
        criteria.setTitle("Test");
        criteria.setDate(LocalDate.now());

        CriteriaDTO dto = criteriaMapper.toCriteriaDTO(criteria);

        assertEquals(criteria.getId(), dto.getId());
        assertEquals(criteria.getAmount(), dto.getAmount());
        assertEquals(criteria.getTitle(), dto.getTitle());
        assertEquals(criteria.getDate(), dto.getDate());
    }
}