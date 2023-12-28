package ee.aivar.filters.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.service.FilterService;

@RestController
@RequestMapping("/api/v1/filters")
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    @GetMapping
    public ResponseEntity<Page<FilterDTO>> getAllFilters(Pageable pageable) {
        Page<FilterDTO> filters = filterService.getAllFilters(pageable);

        return ResponseEntity.ok(filters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilterDTO> getFilterById(@PathVariable Long id) {
        FilterDTO filter = filterService.getFilterById(id);

        return ResponseEntity.ok(filter);
    }

    @PostMapping
    public ResponseEntity<FilterDTO> createFilter(@RequestBody FilterDTO filterDTO) {
        FilterDTO createdFilter = filterService.createFilter(filterDTO);

        return ResponseEntity.ok(createdFilter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilterDTO> updateFilter(@PathVariable Long id, @RequestBody FilterDTO filterDTO) {
        FilterDTO updatedFilter = filterService.updateFilter(id, filterDTO);

        return ResponseEntity.ok(updatedFilter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long id) {
        filterService.deleteFilter(id);

        return ResponseEntity.noContent().build();
    }
}
