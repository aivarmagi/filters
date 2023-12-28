package ee.aivar.filters.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.model.api.CriteriaDTO;
import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.model.db.Criteria;
import ee.aivar.filters.model.db.Filter;
import ee.aivar.filters.model.db.SelectionType;

@SpringBootTest
class FilterMapperTest {

    @Autowired
    private FilterMapper filterMapper;

    @Test
    void testToFilter() {
        CriteriaDTO criteriaDTO = new CriteriaDTO();
        criteriaDTO.setId(1L);
        criteriaDTO.setAmount(100L);
        criteriaDTO.setTitle("CriteriaDTOTitle");
        criteriaDTO.setDate(LocalDate.now());
        Set<CriteriaDTO> criteriaSet = new HashSet<>();
        criteriaSet.add(criteriaDTO);

        FilterDTO dto = new FilterDTO();
        dto.setId(1L);
        dto.setName("Meow");
        dto.setSelectionType(SelectionType.SELECTION_1);
        dto.setCriterias(criteriaSet);

        Filter filter = filterMapper.toFilter(dto);

        assertEquals(dto.getId(), filter.getId());
        assertEquals(dto.getName(), filter.getName());
        assertEquals(dto.getSelectionType(), filter.getSelectionType());
        assertEquals(dto.getCriterias().size(), filter.getCriterias().size());
    }

    @Test
    void testToFilterDTO() {
        Criteria criteria = new Criteria();
        criteria.setId(1L);
        criteria.setAmount(100L);
        criteria.setTitle("CriteriaTitle");
        criteria.setDate(LocalDate.now());
        Set<Criteria> criteriaSet = new HashSet<>();
        criteriaSet.add(criteria);

        Filter filter = new Filter();
        filter.setId(2L);
        filter.setName("Woof");
        filter.setSelectionType(SelectionType.SELECTION_2);
        filter.setCriterias(criteriaSet);

        FilterDTO dto = filterMapper.toFilterDTO(filter);

        assertEquals(filter.getId(), dto.getId());
        assertEquals(filter.getName(), dto.getName());
        assertEquals(filter.getSelectionType(), dto.getSelectionType());
        assertEquals(filter.getCriterias().size(), dto.getCriterias().size());
    }
}