package ee.aivar.filters.model.db;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "criteria", schema = "public")
@Data
public class Criteria {

    @Id
    @SequenceGenerator(name = "criteria_id_seq", sequenceName = "criteria_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "criteria_id_seq")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false)
    private Filter filter;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private Date date;
}