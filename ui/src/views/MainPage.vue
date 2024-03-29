<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import filterService from "@/services/filterService";
import {Filter, FilterSelectionType, type FilterSelectionTypeRecord, FilterState} from "@/models/Filter";
import {BContainer} from "bootstrap-vue-next";
import type {Option} from "@/models/Option";
import {createCriteria, type Criteria} from "@/models/Criteria";
import Loading from "@/components/Loading.vue";
import {Action} from "@/enums/Action";
import SimpleDialog from "@/components/dialog/SimpleDialog.vue";
import FilterDetailsForm from "@/components/form/FilterDetailsForm.vue";
import LocalStorageManager from "@/services/LocalStorageManager";
import {useResizableInteract} from "@/views/MainPageHelpers";
import {useI18n} from "vue-i18n";
import toastService from "@/services/toastService";

const {t, locale} = useI18n();

const ADD_EDIT_IN_MODAL = 'addEditInModal';
const FILTER = 'filter';
const HIDE_DISCLAIMER = 'hideDisclaimer';
const LOCALE = 'locale';
const PAGE_SIZE = 'pageSize';
const PAGE = 'page';
const SORT_FIELD = 'sortField';
const SORT_DESC = 'sortDesc';

const currentFilter = ref<Filter | undefined>(LocalStorageManager.get<Filter>(FILTER) || undefined);
const removableFilterId = ref<number | FilterState>();
const filters = ref<Filter[]>([]);
const openCollapseId = ref<number | FilterState | undefined>(currentFilter.value?.id ?? undefined);

const editInModal = ref<boolean>(LocalStorageManager.hasKey(ADD_EDIT_IN_MODAL) ? LocalStorageManager.get<boolean>(ADD_EDIT_IN_MODAL) : true);
const filterSaving = ref(false);
const hideDisclaimer = ref(LocalStorageManager.get<boolean>(HIDE_DISCLAIMER) || false);
const loadAdditionalFilters = ref(false);
const loadError = ref(false);
const loadFilterError = ref(false);
const loadingFilter = ref(false);
const loadInitialFilters = ref(true);
const removingFilter = ref(false);
const resetFilterError = ref(false);
const resettingFilter = ref(false);
const showFilterDetailsModal = ref(false);
const showFormResetDialog = ref(false);
const showRemoveFilterDialog = ref(false);

const currentPage = ref(LocalStorageManager.get<number>(PAGE) || 1);
const lang = ref(LocalStorageManager.get<string>(LOCALE) || locale);
const pageSize = ref(LocalStorageManager.get<number>(PAGE_SIZE) || 5);
const totalRows = ref(0);
const sortField = ref(LocalStorageManager.get<string>(SORT_FIELD) || 'id');
const sortDesc = ref(LocalStorageManager.get<boolean>(SORT_DESC) || false);

const selectionTypeOptions: (string | number | Record<string, unknown>)[] = Object.keys(FilterSelectionType).map((key: string) => ({
  text: (FilterSelectionType as FilterSelectionTypeRecord)[key],
  value: key,
}));

const pageSizeOptions: Option[] = [{text: '1', value: 1}, {text: '5', value: 5}, {text: '10', value: 10}];

const localeOptions: Option[] = [{text: 'EN', value: 'en'}, {text: 'ET', value: 'et'}];

const getCriteriasCount = (item: Filter) => {
  return item.criterias?.length ?? 0;
};

const showFilterLoadingSpinner = (id?: number | FilterState) => {
  return loadingFilter.value === true && openCollapseId.value === id && editInModal.value;
};

const showFilterRemoveSpinner = (id?: number | FilterState) => {
  return removingFilter.value === true && removableFilterId.value !== undefined && removableFilterId.value === id;
};

const toggleFilterDetails = (item: Filter) => {
  if (openCollapseId.value === item.id) {
    updateOrRemoveFilterStorage();
    return;
  }

  updateOrRemoveFilterStorage();
  getFilter(item.id as number, Action.LOAD);
};

const onDisclaimerClose = () => {
  hideDisclaimer.value = true;
  LocalStorageManager.save(HIDE_DISCLAIMER, true);
};

const onSortChanged = (field: string, sortDescending: boolean) => {
  updateSortField(field);
  updateSortDesc(sortDescending);
  loadFilters(field, sortDescending, currentPage.value, pageSize.value)
};

const onPaginationChanged = (event: any, page: number) => {
  event.preventDefault();

  updateOrRemoveFilterStorage();
  loadFilters(sortField.value, sortDesc.value, page, pageSize.value)
};

const onLocaleChanged = (val: any) => {
  lang.value = val;
  locale.value = val;
  LocalStorageManager.save(LOCALE, val);
};

const onFilterNameUpdated = (val: string) => {
  currentFilter.value!.name = val;
  updateOrRemoveFilterStorage(currentFilter.value);
}

const onFilterSelectionUpdated = (val: string) => {
  currentFilter.value!.selection = val as FilterSelectionType;
  updateOrRemoveFilterStorage(currentFilter.value);
}

const onCriteriaUpdated = (val: Criteria, index: number) => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias[index] = {...val}
    updateOrRemoveFilterStorage(currentFilter.value);
  }
};

const onCriteriaFieldUpdated = (field: string, value: string, index: number) => {
  if (currentFilter.value?.criterias?.[index]) {
    (currentFilter.value.criterias[index] as any)[field] = value;
    updateOrRemoveFilterStorage(currentFilter.value);
  }
};

const onCriteriaRemoved = (index: number) => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias.splice(index, 1);
    updateOrRemoveFilterStorage(currentFilter.value);
  }
};

const onCriteriaAdded = () => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias.push(createCriteria());
    updateOrRemoveFilterStorage(currentFilter.value);
  }
};

const onShowAddFilter = () => {
  if (openCollapseId.value) {
    openCollapseId.value = undefined;
    currentFilter.value = undefined;
  }

  if (editInModal.value) {
    showFilterDetailsModal.value = true;
  }

  const addedFilter: Filter = {
    id: FilterState.NEW,
    name: '',
    selection: Object.keys(FilterSelectionType)[0] as FilterSelectionType,
    criterias: [createCriteria()],
  };

  currentFilter.value = {...addedFilter};
  updateOrRemoveFilterStorage({...addedFilter});
};

const onHideAddFilterModal = () => {
  currentFilter.value = undefined;
  showFilterDetailsModal.value = false;
  updateOrRemoveFilterStorage();
}

const onModalModeChanged = (value: any) => {
  const modalMode = value as boolean;

  editInModal.value = modalMode;
  LocalStorageManager.save(ADD_EDIT_IN_MODAL, modalMode);

  if (modalMode && currentFilter.value?.id) {
    showFilterDetailsModal.value = true;
  }
}

const onFilterSave = (event: any) => {
  event.preventDefault();
  filterSaving.value = true;

  const filterToSave = {...currentFilter.value!};
  const isNewFilter = filterToSave.id === FilterState.NEW;

  if (isNewFilter) {
    delete filterToSave.id;
    addFilter(filterToSave);
  } else {
    updateFilter(filterToSave);
  }
};

const addFilter = (filter: Filter) => {
  filterService.postFilter(filter).subscribe({
    next: (response) => handleAddFilterSuccess(response),
    error: (error) => console.error('Error adding filter:', error),
    complete: () => filterSaving.value = false
  })
}

const handleAddFilterSuccess = (response: Filter) => {
  toastService.showInfo(t('messages.filterAdded'))
  filters.value.push(response);
  totalRows.value = filters.value.length;
  onHideAddFilterModal();
}

const updateFilter = (filter: Filter) => {
  filterService.putFilter(filter).subscribe({
    next: (response) => handleUpdateFilterSuccess(response),
    error: (error) => console.error('Error updating filter:', error),
    complete: () => filterSaving.value = false
  })
}

const handleUpdateFilterSuccess = (response: Filter) => {
  toastService.showInfo(t('messages.filterSaved'))
  filters.value = filters.value.map(f => f.id === response.id ? {...response} : f);
}

const updateOrRemoveFilterStorage = (filter?: Filter) => {
  if (filter) {
    LocalStorageManager.save(FILTER, filter);
  } else {
    LocalStorageManager.remove(FILTER);
    currentFilter.value = undefined;
    openCollapseId.value = undefined;
  }
}

const updateSortField = (field: string) => {
  sortField.value = field;
  LocalStorageManager.save(SORT_FIELD, field);
}

const updateSortDesc = (sortDescending: boolean) => {
  sortDesc.value = sortDescending;
  LocalStorageManager.save(SORT_DESC, sortDescending);
}

const updateCurrentPage = (page: number) => {
  currentPage.value = page;
  LocalStorageManager.save(PAGE, page);
}

const updatePageSizeStorage = (size: number) => {
  LocalStorageManager.save(PAGE_SIZE, size);
  pageSize.value = size;
}

const onShowFormResetDialog = (event: any) => {
  event.preventDefault();
  if (showFormResetDialog.value === false) {
    showFormResetDialog.value = true;
  }
}

const onCloseDialog = (action: Action) => {
  if (action === Action.RESET && showFormResetDialog.value === true) {
    showFormResetDialog.value = false;
  } else if (action === Action.DELETE && showRemoveFilterDialog.value === true) {
    showRemoveFilterDialog.value = false;
  }
}

const onShowRemoveFilterDialog = (id: number | FilterState) => {
  removableFilterId.value = id;
  showRemoveFilterDialog.value = true;
}

const onFormReset = () => getFilter(currentFilter.value!.id as number, Action.RESET);

const onFilterRemoved = () => {
  if (removableFilterId.value) {
    removingFilter.value = true;
    filterService.deleteFilter(removableFilterId.value as number).subscribe({
      next: () => handleDeleteFilterSuccess(),
      error: (error) => console.error('Error removing filter:', error),
      complete: () => removingFilter.value = false
    })
  }
}

const handleDeleteFilterSuccess = () => {
  toastService.showInfo(t('messages.filterRemoved'))
  filters.value = filters.value.filter(f => f.id !== removableFilterId.value);
  removableFilterId.value = undefined;
  totalRows.value = filters.value.length;
}

const onPageSizeChanged = (size: any) => {
  updatePageSizeStorage(size);
  loadFilters(sortField.value, sortDesc.value, currentPage.value, size);
}

const loadFilters = (field: string, sortDescending: boolean, page: number, size: number) => {
  if (loadInitialFilters.value === false) {
    loadAdditionalFilters.value = true;
  }

  filterService.getFilters(field, sortDescending, page, size).subscribe({
    next: (response) => handleGetFiltersSuccess(response),
    error: (error) => handleGetFiltersError(error),
    complete: () => handleGetFiltersComplete(field, sortDescending, page, size)
  })
}

const handleGetFiltersSuccess = (response: PageableResponse<Filter>) => {
  const loadedFilters = response.content;
  loadedFilters.forEach((filter: Filter) => {
    filter.criterias = filter.criterias?.sort((a, b) => a.id! - b.id!);
  });
  filters.value = loadedFilters;
  totalRows.value = response.totalElements;
}

const handleGetFiltersError = (error: any) => {
  console.error('Error loading filters:', error);
  loadError.value = true;
}

const handleGetFiltersComplete = (field: string, sortDescending: boolean, page: number, size: number) => {
  updateSortField(field);
  updateSortDesc(sortDescending);
  updateCurrentPage(page);
  updatePageSizeStorage(size);

  if (!currentFilter.value) {
    updateOrRemoveFilterStorage()
  }

  if (loadInitialFilters.value) {
    loadInitialFilters.value = false;
  } else {
    loadAdditionalFilters.value = false;
  }
}

const getFilter = (id: number, action: Action) => {
    if (Action.LOAD === action) {
      loadFilterError.value = false;
      loadingFilter.value = true;
    } else if (Action.RESET === action) {
      resetFilterError.value = false;
      resettingFilter.value = true;
    }

  openCollapseId.value = id;
  filterService.getFilter(id).subscribe({
    next: (response) => handleGetFilterSuccess(id, action, response),
    error: (error) => handleGetFilterError(action, error),
    complete: () => handleGetFilterComplete(action)
  })
};

const handleGetFilterSuccess = (id: number, action: Action, response: Filter) => {
  const loadedFilter: Filter = response;
  loadedFilter.criterias?.sort((a, b) => a.id! - b.id!);

  if (Action.RESET === action) {
    toastService.showInfo(t('messages.filterReset'))
  }

  filters.value = filters.value.map(f => f.id === id ? {...loadedFilter} : f);
  currentFilter.value = response;
  updateOrRemoveFilterStorage(response);

  if (editInModal.value && Action.LOAD === action) {
    showFilterDetailsModal.value = true;
  }
}

const handleGetFilterError = (action: Action, error: any) => {
  console.error(`Error with filter ${action.toLowerCase()}:`, error);
  if (Action.LOAD === action) {
    loadFilterError.value = true;
  } else if (Action.RESET === action) {
    resetFilterError.value = true;
  }
}

const handleGetFilterComplete = (action: Action) => {
  if (Action.LOAD === action) {
    loadingFilter.value = false;
  } else if (Action.RESET === action) {
    resettingFilter.value = false;
  }
}

const showFilterDetailsInline = (filterId?: number): boolean => {
  return openCollapseId.value === filterId && !loadingFilter.value && !loadFilterError.value && !editInModal.value;
}

onMounted(() => {
  loadFilters(sortField.value, sortDesc.value, currentPage.value, pageSize.value)
  nextTick(() => useResizableInteract('.modal-content'));
})
</script>

<template>
  <BContainer fluid>
    <BRow class="mt-4">
      <BCol class="text-center">
        <BAlert
            dismissible
            variant="secondary"
            :model-value="!hideDisclaimer"
            @update:model-value="onDisclaimerClose"
        >
          {{t('messages.disclaimer')}}
        </BAlert>

        <Loading
            :message="t('messages.loadingFilters')"
            :loading="loadInitialFilters"
        />

        <BAlert variant="danger" :model-value="loadError">
          {{t('errors.loadFilters')}}
        </BAlert>

        <BTableSimple
            class="filters-table"
            hover
            responsive
            v-if="totalRows > 0"
        >
          <BThead>
            <BTr>
              <BTh class="col-1 hover-pointer" @click="onSortChanged('id', !sortDesc)">
                {{t('labels.id')}}
                <span v-if="'id' === sortField">
                  <IIconoirSortUp v-if="sortDesc" />
                  <IIconoirSortDown v-else />
                </span>
              </BTh>
              <BTh class="hover-pointer col-auto text-start" @click="onSortChanged('name', !sortDesc)">
                {{t('labels.name')}}
                <span v-if="'name' === sortField">
                  <IIconoirSortUp v-if="sortDesc" />
                  <IIconoirSortDown v-else />
                </span>
              </BTh>
              <BTh class="col-1 text-nowrap">{{t('labels.criteriaCount')}}</BTh>
              <BTh class="col-1">{{t('labels.selection')}}</BTh>
              <BTh class="col-1">{{t('labels.actions')}}</BTh>
            </BTr>
          </BThead>

          <BTbody class="border-start border-end">
            <template
                v-for="filter in filters"
                :key="filter.id"
            >
              <BTr class="align-middle" v-if="FilterState.NEW !== filter.id">
                <BTd class="bg-light">{{ filter.id }}</BTd>
                <BTd class="bg-light text-start">{{ filter.name }}</BTd>
                <BTd class="bg-light">
                  <BBadge pill variant="secondary">
                    {{ getCriteriasCount(filter as Filter) }}
                  </BBadge>
                </BTd>
                <BTd class="bg-light">{{ (FilterSelectionType as FilterSelectionTypeRecord)[filter.selection] }}</BTd>
                <BTd class="bg-light">
                  <div class="action-icon-container">
                    <IBxsEdit
                      class="hover-pointer"
                      role="button"
                      v-b-tooltip="t('tooltips.editFilter')"
                      v-if="!showFilterLoadingSpinner(filter.id)"
                      @click="() => toggleFilterDetails(filter)"
                    />
                    <Loading class="me-1" :loading="showFilterLoadingSpinner(filter.id)" />

                    <ITypcnDelete
                      class="hover-pointer"
                      v-b-tooltip="t('tooltips.removeFilter')"
                      v-if="totalRows > 1 && !showFilterRemoveSpinner(filter.id)"
                      @click="() => onShowRemoveFilterDialog(filter.id!)"
                    />

                    <Loading :loading="showFilterRemoveSpinner(filter.id)" />
                  </div>
                </BTd>
              </BTr>

              <BTr class="text-start" v-if="openCollapseId === filter.id && !showFilterLoadingSpinner(filter.id)">
                <BTd class="p-0" colspan="5">
                  <Loading
                      class="text-center my-3"
                      :message="t('messages.loadingFilter')"
                      :loading="loadingFilter"
                  />

                  <BAlert class="text-center mx-3 mt-3" variant="danger" :model-value="loadFilterError">
                    {{t('errors.loadFilters')}}
                  </BAlert>

                  <template v-if="currentFilter && showFilterDetailsInline(filter.id as number)">
                    <BCard class="border-0" :title="t('titles.editFilter')">
                      <FilterDetailsForm
                          :current-filter="currentFilter"
                          :filter-saving="filterSaving"
                          :resetting-filter="resettingFilter"
                          :selection-type-options="selectionTypeOptions"
                          @add-criteria="onCriteriaAdded"
                          @close="() => toggleFilterDetails(filter)"
                          @remove-criteria="onCriteriaRemoved"
                          @reset="onShowFormResetDialog"
                          @save-filter="onFilterSave"
                          @update-criteria="onCriteriaUpdated"
                          @update-field="onCriteriaFieldUpdated"
                          @update-name="onFilterNameUpdated"
                          @update-selection="onFilterSelectionUpdated"
                        />
                    </BCard>
                  </template>
                </BTd>
              </BTr>
            </template>

            <BTr class="text-start" v-if="!editInModal && currentFilter && FilterState.NEW === currentFilter.id && !openCollapseId">
              <BTd class="p-0" colspan="5">
                <BCard class="border-0" :title="t('titles.addFilter')">
                  <FilterDetailsForm
                      :current-filter="currentFilter"
                      :filter-saving="filterSaving"
                      :selection-type-options="selectionTypeOptions"
                      @add-criteria="onCriteriaAdded"
                      @close="onHideAddFilterModal"
                      @remove-criteria="onCriteriaRemoved"
                      @save-filter="onFilterSave"
                      @update-criteria="onCriteriaUpdated"
                      @update-field="onCriteriaFieldUpdated"
                      @update-name="onFilterNameUpdated"
                      @update-selection="onFilterSelectionUpdated"
                  />
                </BCard>
              </BTd>
            </BTr>
          </BTbody>
        </BTableSimple>

        <BRow v-if="filters?.length > 0">
          <BCol class="col-auto ms-2 mt-1">
            <BFormSelect
                size="sm"
                v-model="lang"
                v-b-tooltip="t('tooltips.changeLocale')"
                :id="'page-size-select'"
                :options="localeOptions"
                @change="onLocaleChanged"
            />
          </BCol>

          <BCol class="col-auto mt-1">
            <BFormSelect
                size="sm"
                v-model="pageSize"
                v-b-tooltip="t('tooltips.changePageSize')"
                :id="'page-size-select'"
                :options="pageSizeOptions"
                @change="onPageSizeChanged"
            />
          </BCol>

          <BCol class="col-auto mt-1">
            <BPagination
                align="end"
                size="sm"
                v-model="currentPage"
                :hide-goto-end-buttons="true"
                :per-page="pageSize"
                :total-rows="totalRows"
                @page-click="onPaginationChanged"
            />
          </BCol>

          <BCol class="col-auto mt-1 pt-1">
            {{t('labels.totalCount')}}: {{ totalRows }}
          </BCol>

          <BCol class="col-auto mt-2" v-if="loadAdditionalFilters">
            <Loading :loading="loadAdditionalFilters"/>
          </BCol>

          <BCol class="d-flex justify-content-end mt-1 pt-1">
            <BFormCheckbox
                switch
                v-model="editInModal"
                @change="onModalModeChanged">
              <span v-b-tooltip="t('tooltips.editInModal')">
                {{ t('labels.modalMode') }}
              </span>
            </BFormCheckbox>
          </BCol>

          <BCol class="col-auto text-end mt-0">
            <BButton
                class="me-3"
                @click="() => onShowAddFilter()"
            >
              {{$t('buttons.addFilter')}}
            </BButton>
          </BCol>
        </BRow>
      </BCol>
    </BRow>

    <SimpleDialog
        :button-cancel-text="t('buttons.close')"
        :button-ok-text="t('buttons.yes')"
        :id="'disclaimer-dialog'"
        :message="t('messages.resetForm')"
        :show="showFormResetDialog"
        @cancel="() => onCloseDialog(Action.RESET)"
        @confirm="onFormReset"
    />

    <SimpleDialog
        :button-cancel-text="t('buttons.close')"
        :button-ok-text="t('buttons.yes')"
        :id="'disclaimer-dialog'"
        :message="t('messages.removeFilter')"
        :show="showRemoveFilterDialog"
        @cancel="() => onCloseDialog(Action.DELETE)"
        @confirm="onFilterRemoved"
    />

    <BModal
      body-class="scrollbar"
      cancel-variant="outline-secondary"
      hide-footer
      ok-variant="secondary"
      scrollable
      size="lg"
      v-if="editInModal && showFilterDetailsModal && currentFilter"
      v-model="showFilterDetailsModal"
      :title="openCollapseId ? t('titles.editFilter') : t('titles.addFilter')"
      @close="onHideAddFilterModal"
      @hide="onHideAddFilterModal"
    >
      <FilterDetailsForm
          :current-filter="currentFilter"
          :filter-saving="filterSaving"
          :selection-type-options="selectionTypeOptions"
          @add-criteria="onCriteriaAdded"
          @close="onHideAddFilterModal"
          @remove-criteria="onCriteriaRemoved"
          @reset="onShowFormResetDialog"
          @save-filter="onFilterSave"
          @update-criteria="onCriteriaUpdated"
          @update-field="onCriteriaFieldUpdated"
          @update-name="onFilterNameUpdated"
          @update-selection="onFilterSelectionUpdated"
      />
    </BModal>
  </BContainer>
</template>

<style scoped>
.hover-pointer {
  cursor: pointer;
}

.action-icon-container {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
}
</style>