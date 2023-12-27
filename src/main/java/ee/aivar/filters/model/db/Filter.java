package ee.aivar.filters.model.db;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "filter", schema = "public")
@Data
public class Filter {

    @Id
    @SequenceGenerator(name = "filter_id_seq", sequenceName = "filter_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "filter_id_seq")
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection")
    private SelectionType selectionType;

    @OneToMany(mappedBy = "filter")
    private Set<Criteria> criterias;
}
