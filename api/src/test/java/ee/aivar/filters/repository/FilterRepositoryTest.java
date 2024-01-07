package ee.aivar.filters.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Test;

import ee.aivar.filters.model.db.Filter;
import ee.aivar.filters.enums.Selection;

@DataJpaTest
class FilterRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FilterRepository filterRepository;

    @Test
    public void whenFindById_thenReturnFilter() {
        Filter filter = new Filter();
        filter.setName("Test");
        filter.setSelection(Selection.SELECTION_1);
        filter.setCriterias(new HashSet<>());
        entityManager.persist(filter);
        entityManager.flush();

        Optional<Filter> found = filterRepository.findById(filter.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(filter.getId());
    }
}