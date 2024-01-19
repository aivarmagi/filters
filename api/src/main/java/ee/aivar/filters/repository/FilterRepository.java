package ee.aivar.filters.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ee.aivar.filters.model.db.Filter;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    Page<Filter> findByDeletedAtIsNull(Pageable pageable);
    Optional<Filter> findByIdAndDeletedAtIsNull(Long id);
}
