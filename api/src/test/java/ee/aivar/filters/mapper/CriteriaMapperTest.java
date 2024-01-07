package ee.aivar.filters.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.enums.CriteriaDate;
import ee.aivar.filters.enums.CriteriaName;
import ee.aivar.filters.enums.CriteriaTitle;
import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.db.Criteria;

@SpringBootTest
class CriteriaMapperTest {

    @Autowired
    private CriteriaMapper criteriaMapper;

    @Test
    void toCriteria() {
        CriteriaDTO dto = new CriteriaDTO();
        dto.setId(1L);
        dto.setName(CriteriaName.DATE.toString());
        dto.setOperator(CriteriaDate.EQUAL.toString());
        dto.setValue(LocalDate.now().toString());

        Criteria criteria = criteriaMapper.toCriteria(dto);

        assertEquals(dto.getName(), criteria.getName().toString());
        assertEquals(dto.getOperator(), criteria.getOperator());
        assertEquals(dto.getValue(), criteria.getValue());
    }

    @Test
    void toCriteriaDTO() {
        Criteria criteria = new Criteria();
        criteria.setId(2L);
        criteria.setName(CriteriaName.TITLE);
        criteria.setOperator(CriteriaTitle.CONTAINS.toString());
        criteria.setValue("Meow");

        CriteriaDTO dto = criteriaMapper.toCriteriaDTO(criteria);

        assertEquals(criteria.getId(), dto.getId());
        assertEquals(criteria.getName().toString(), dto.getName());
        assertEquals(criteria.getOperator(), dto.getOperator());
        assertEquals(criteria.getValue(), dto.getValue());
    }
}