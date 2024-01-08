package ee.aivar.filters.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ee.aivar.filters.exception.ResourceNotFoundException;
import ee.aivar.filters.mapper.CriteriaMapper;
import ee.aivar.filters.mapper.FilterMapper;
import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.model.db.Filter;
import ee.aivar.filters.repository.FilterRepository;

@Service
public class FilterService {
    private final FilterRepository filterRepository;
    private final FilterMapper filterMapper;
    private final CriteriaMapper criteriaMapper;

    public FilterService(FilterRepository filterRepository, FilterMapper filterMapper, CriteriaMapper criteriaMapper) {
        this.filterRepository = filterRepository;
        this.filterMapper = filterMapper;
        this.criteriaMapper = criteriaMapper;
    }

    public Page<FilterDTO> getAllFilters(Pageable pageable) {
        return filterRepository.findAll(pageable).map(filterMapper::toFilterDTO);
    }

    public FilterDTO getFilterById(Long id) {
        Filter filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));

        return filterMapper.toFilterDTO(filter);
    }

    public FilterDTO createFilter(FilterDTO filterDTO) {
        Filter filter = filterMapper.toFilter(filterDTO);
        Filter savedFilter = filterRepository.save(filter);

        return filterMapper.toFilterDTO(savedFilter);
    }

    @Transactional
    public FilterDTO updateFilter(Long id, FilterDTO filterDTO) {
        Filter filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));
        filter.setName(filterDTO.getName());
        filter.setSelection(filterDTO.getSelection());

        filter.getCriterias().clear();

        var criterias = filterDTO.getCriterias().stream()
                .map(criteriaMapper::toCriteria)
                .collect(Collectors.toSet());
        criterias.forEach(criteria -> criteria.setFilter(filter));
        filter.setCriterias(criterias);

        Filter updatedFilter = filterRepository.save(filter);

        return filterMapper.toFilterDTO(updatedFilter);
    }

    @Transactional
    public void deleteFilter(Long id) {
        Filter filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));
        filter.setDeletedAt(LocalDateTime.now());

        filterRepository.save(filter);
    }
}
