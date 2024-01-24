package ee.aivar.filters.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ee.aivar.filters.exception.ResourceNotFoundException;
import ee.aivar.filters.mapper.CriteriaMapper;
import ee.aivar.filters.mapper.FilterMapper;
import ee.aivar.filters.model.api.FilterDTO;
import ee.aivar.filters.repository.CriteriaRepository;
import ee.aivar.filters.repository.FilterRepository;

@Service
public class FilterService {
    private final CriteriaRepository criteriaRepository;
    private final FilterRepository filterRepository;
    private final CriteriaMapper criteriaMapper;
    private final FilterMapper filterMapper;

    public FilterService(CriteriaRepository criteriaRepository, FilterRepository filterRepository, CriteriaMapper criteriaMapper, FilterMapper filterMapper) {
        this.criteriaRepository = criteriaRepository;
        this.filterRepository = filterRepository;
        this.criteriaMapper = criteriaMapper;
        this.filterMapper = filterMapper;
    }

    public Page<FilterDTO> getAllFilters(Pageable pageable) {
        var filters = filterRepository.findByDeletedAtIsNull(pageable);
        var filterDTOs = new ArrayList<FilterDTO>();

        filters.forEach(filter -> {
            var criterias = criteriaRepository.findByFilterIdAndDeletedAtIsNull(filter.getId());
            filter.setCriterias(criterias);
            filterDTOs.add(filterMapper.toFilterDTO(filter));
        });

        return new PageImpl<>(filterDTOs, pageable, filters.getTotalElements());
    }

    public FilterDTO getFilterById(Long id) {
        var filter = filterRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));
        var criterias = criteriaRepository.findByFilterIdAndDeletedAtIsNull(id);
        filter.setCriterias(criterias);

        return filterMapper.toFilterDTO(filter);
    }

    @Transactional
    public FilterDTO createFilter(FilterDTO filterDTO) {
        var filter = filterMapper.toFilterWithoutCriteria(filterDTO);
        var savedFilter = filterRepository.save(filter);

        if (filterDTO.getCriterias() != null) {
            var criterias = filterDTO.getCriterias().stream()
                    .map(criteriaMapper::toCriteria)
                    .collect(Collectors.toSet());

            criterias.forEach(criteria -> criteria.setFilter(savedFilter));

            var savedCriterias = criteriaRepository.saveAll(criterias);
            savedFilter.setCriterias(new HashSet<>(savedCriterias));
        }

        return filterMapper.toFilterDTO(savedFilter);
    }

    @Transactional
    public FilterDTO updateFilter(Long id, FilterDTO filterDTO) {
        var filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));
        filter.setName(filterDTO.getName());
        filter.setSelection(filterDTO.getSelection());

        var criterias = filterDTO.getCriterias().stream()
                .map(criteriaMapper::toCriteria)
                .collect(Collectors.toSet());
        criterias.forEach(criteria -> {
            criteria.setFilter(filter);
        });

        var currentCriterias = filter.getCriterias();
        currentCriterias.forEach(cc -> {
            if (criterias.stream().noneMatch(c -> c.getId() != null && c.getId().equals(cc.getId()))) {
                cc.setDeletedAt(LocalDateTime.now());
            }
        });

        filter.setCriterias(criterias);

        var updatedFilter = filterRepository.save(filter);

        return filterMapper.toFilterDTO(updatedFilter);
    }

    @Transactional
    public void deleteFilter(Long id) {
        var filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filter not found"));
        filter.setDeletedAt(LocalDateTime.now());

        filterRepository.save(filter);
    }
}
