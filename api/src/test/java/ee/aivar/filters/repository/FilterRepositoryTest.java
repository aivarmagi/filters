package ee.aivar.filters.repository;

import static ee.aivar.filters.DataHelpers.TEST_1;
import static ee.aivar.filters.DataHelpers.TEST_2;
import static ee.aivar.filters.DataHelpers.getFilterWithoutCriterias;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.enums.Selection;

@DataJpaTest
class FilterRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FilterRepository filterRepository;

    @Test
    public void whenFindById_thenReturnFilter() {
        var filter = getFilterWithoutCriterias(TEST_1, Selection.SELECTION_1);
        entityManager.persist(filter);
        entityManager.flush();

        var found = filterRepository.findById(filter.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(filter.getId());
    }

    @Test
    public void whenFindByDeletedAtIsNull_thenReturnFilters() {
        var filter1 = getFilterWithoutCriterias(TEST_1, Selection.SELECTION_1);
        entityManager.persist(filter1);

        var filter2 = getFilterWithoutCriterias(TEST_2, Selection.SELECTION_2);
        filter2.setDeletedAt(LocalDateTime.now());
        entityManager.persist(filter2);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        var found = filterRepository.findByDeletedAtIsNull(pageable);

        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getId()).isEqualTo(filter1.getId());
    }

    @Test
    public void whenFindByIdAndDeletedAtIsNull_thenReturnFilter() {
        var filter1 = getFilterWithoutCriterias(TEST_1, Selection.SELECTION_1);
        entityManager.persist(filter1);

        var filter2 = getFilterWithoutCriterias(TEST_2, Selection.SELECTION_2);
        filter2.setDeletedAt(LocalDateTime.now());
        entityManager.persist(filter2);

        entityManager.flush();

        var found = filterRepository.findByIdAndDeletedAtIsNull(filter1.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(filter1.getId());
    }
}