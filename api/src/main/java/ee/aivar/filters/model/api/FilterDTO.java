package ee.aivar.filters.model.api;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.aivar.filters.model.db.SelectionType;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterDTO {

    private Long id;
    private String name;
    private SelectionType selectionType;
    private Set<CriteriaDTO> criterias;
}
