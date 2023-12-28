package ee.aivar.filters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.aivar.filters.model.db.Criteria;

public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
}
