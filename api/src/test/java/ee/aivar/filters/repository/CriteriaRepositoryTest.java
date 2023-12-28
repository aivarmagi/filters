package ee.aivar.filters.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.model.db.Criteria;
import ee.aivar.filters.model.db.Filter;
import ee.aivar.filters.model.db.SelectionType;

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
        filter.setSelectionType(SelectionType.SELECTION_1);
        filter.setCriterias(new HashSet<>());
        entityManager.persist(filter);

        Criteria criteria = new Criteria();
        criteria.setAmount(100L);
        criteria.setTitle("Title");
        criteria.setFilter(filter);
        criteria.setDate(LocalDate.now());
        entityManager.persist(criteria);
        entityManager.flush();

        Optional<Criteria> found = criteriaRepository.findById(criteria.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(criteria.getId());
    }
}