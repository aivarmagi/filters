package ee.aivar.filters.model.db;

import static jakarta.persistence.FetchType.*;

import ee.aivar.filters.enums.CriteriaName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "criteria", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criteria extends BaseEntity {

    @Id
    @SequenceGenerator(name = "criteria_id_seq", sequenceName = "criteria_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "criteria_id_seq")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "filter_id", nullable = false)
    private Filter filter;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_name")
    private CriteriaName name;

    @Column(name = "criteria_operator")
    private String operator;

    @Column(name = "criteria_value")
    private String value;
}
