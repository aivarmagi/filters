package ee.aivar.filters.repository;

import static ee.aivar.filters.DataHelpers.TEST_1;
import static ee.aivar.filters.DataHelpers.getCriteria;
import static ee.aivar.filters.DataHelpers.getFilterWithoutCriterias;
import static ee.aivar.filters.DataHelpers.getFormattedDate;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.enums.CriteriaDate;
import ee.aivar.filters.enums.CriteriaName;
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
        var filter = getFilterWithoutCriterias("Test", Selection.SELECTION_1);
        entityManager.persist(filter);

        Criteria criteria = getCriteria(CriteriaName.DATE, CriteriaDate.FROM.toString(), getFormattedDate(LocalDate.now()), filter);
        entityManager.persist(criteria);
        entityManager.flush();

        Optional<Criteria> found = criteriaRepository.findById(criteria.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(criteria.getId());
    }

    @Test
    public void whenFindByFilterIdAndDeletedAtIsNull_thenReturnCriteria() {
        Filter filter = getFilterWithoutCriterias(TEST_1, Selection.SELECTION_1);
        entityManager.persist(filter);

        Criteria criteria1 = new Criteria();
        criteria1.setFilter(filter);
        entityManager.persist(criteria1);

        Criteria criteria2 = new Criteria();
        criteria2.setFilter(filter);
        criteria2.setDeletedAt(LocalDateTime.now());
        entityManager.persist(criteria2);

        entityManager.flush();

        var found = criteriaRepository.findByFilterIdAndDeletedAtIsNull(filter.getId()).stream().toList();

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo(criteria1.getId());
    }
}