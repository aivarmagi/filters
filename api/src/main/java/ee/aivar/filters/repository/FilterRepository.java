package ee.aivar.filters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ee.aivar.filters.model.db.Filter;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
}
