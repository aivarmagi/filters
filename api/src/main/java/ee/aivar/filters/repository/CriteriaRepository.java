package ee.aivar.filters.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ee.aivar.filters.model.db.Criteria;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    Set<Criteria> findByFilterIdAndDeletedAtIsNull(Long filterId);
}
