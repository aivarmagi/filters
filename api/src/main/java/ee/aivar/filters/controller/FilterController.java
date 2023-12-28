package ee.aivar.filters.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.aivar.filters.service.FilterService;

@RestController
@RequestMapping("/api/v1/filter")
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

}
