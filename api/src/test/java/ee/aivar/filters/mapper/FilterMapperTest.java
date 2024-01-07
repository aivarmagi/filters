package ee.aivar.filters.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.enums.CriteriaDate;
import ee.aivar.filters.enums.CriteriaName;
import ee.aivar.filters.enums.CriteriaTitle;
import ee.aivar.filters.enums.Selection;
import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.model.db.Criteria;
import ee.aivar.filters.model.db.Filter;

@SpringBootTest
class FilterMapperTest {

    @Autowired
    private FilterMapper filterMapper;

    @Test
    void testToFilter() {
        CriteriaDTO criteriaDTO = new CriteriaDTO();
        criteriaDTO.setId(1L);
        criteriaDTO.setName(CriteriaName.TITLE.toString());
        criteriaDTO.setOperator(CriteriaTitle.STARTS_WITH.toString());
        criteriaDTO.setValue("ow");
        Set<CriteriaDTO> criteriaSet = new HashSet<>();
        criteriaSet.add(criteriaDTO);

        FilterDTO dto = new FilterDTO();
        dto.setId(1L);
        dto.setName("Meow");
        dto.setSelection(Selection.SELECTION_1);
        dto.setCriterias(criteriaSet);

        Filter filter = filterMapper.toFilter(dto);

        assertEquals(dto.getId(), filter.getId());
        assertEquals(dto.getName(), filter.getName());
        assertEquals(dto.getSelection(), filter.getSelection());
        assertEquals(dto.getCriterias().size(), filter.getCriterias().size());
    }

    @Test
    void testToFilterDTO() {
        Criteria criteria = new Criteria();
        criteria.setId(1L);
        criteria.setName(CriteriaName.DATE);
        criteria.setOperator(CriteriaDate.EQUAL.toString());
        criteria.setValue(LocalDate.now().toString());
        Set<Criteria> criteriaSet = new HashSet<>();
        criteriaSet.add(criteria);

        Filter filter = new Filter();
        filter.setId(2L);
        filter.setName("Woof");
        filter.setSelection(Selection.SELECTION_2);
        filter.setCriterias(criteriaSet);

        FilterDTO dto = filterMapper.toFilterDTO(filter);

        assertEquals(filter.getId(), dto.getId());
        assertEquals(filter.getName(), dto.getName());
        assertEquals(filter.getSelection(), dto.getSelection());
        assertEquals(filter.getCriterias().size(), dto.getCriterias().size());
    }
}