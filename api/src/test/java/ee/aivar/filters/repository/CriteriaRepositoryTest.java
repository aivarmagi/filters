package ee.aivar.filters.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.enums.CriteriaDate;
import ee.aivar.filters.enums.CriteriaName;
import ee.aivar.filters.enums.CriteriaTitle;
import ee.aivar.filters.enums.Selection;
import ee.aivar.filters.model.db.Criteria;
import ee.aivar.filters.model.db.Filter;

@DataJpaTest
class CriteriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Test
    public void whenFindById_thenReturnCriteria() {
        Filter filter = new Filter();
        filter.setName("Test");
        filter.setSelection(Selection.SELECTION_1);
        filter.setCriterias(new HashSet<>());
        entityManager.persist(filter);

        Criteria criteria = new Criteria();
        criteria.setName(CriteriaName.DATE);
        criteria.setOperator(CriteriaDate.FROM.toString());
        criteria.setValue(LocalDate.now().toString());
        criteria.setFilter(filter);
        entityManager.persist(criteria);
        entityManager.flush();

        Optional<Criteria> found = criteriaRepository.findById(criteria.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(criteria.getId());
    }
}