package ee.aivar.filters.model.api;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriteriaDTO {

    private Long id;
    private Long amount;
    private String title;
    private LocalDate date;
}
