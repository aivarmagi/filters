package ee.aivar.filters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.aivar.filters.model.db.Filter;

public interface FilterRepository extends JpaRepository<Filter, Long> {
}
