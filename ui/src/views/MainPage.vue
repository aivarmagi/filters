<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import filterService from "@/services/filterService";
import {Filter, FilterSelectionType, type FilterSelectionTypeRecord, FilterState} from "@/models/Filter";
import {BContainer, useToast} from "bootstrap-vue-next";
import type {Option} from "@/models/Option";
import {createCriteria, type Criteria} from "@/models/Criteria";
import Loading from "@/components/Loading.vue";
import {Action} from "@/enums/Action";
import SimpleDialog from "@/components/dialog/SimpleDialog.vue";
import FilterDetailsForm from "@/components/form/FilterDetailsForm.vue";
import LocalStorageManager from "@/services/LocalStorageManager";
// import '@interactjs/auto-start'
// import '@interactjs/actions/drag'
// import '@interactjs/actions/resize'
// import '@interactjs/modifiers'
// import '@interactjs/dev-tools'
import interact from 'interactjs'
import {useResizableInteract} from "@/views/MainPageHelpers";

const {show} = useToast();

const FILTER = 'filter';
const HIDE_DISCLAIMER = 'hideDisclaimer';
const PAGE_SIZE = 'pageSize';
const PAGE = 'page';
const SORT_FIELD = 'sortField';
const SORT_DESC = 'sortDesc';

const currentFilter = ref<Filter | undefined>(LocalStorageManager.get<Filter>(FILTER) || undefined);
const removableFilterId = ref<number | FilterState>();
const filters = ref<Filter[]>([]);
const openCollapseId = ref<number | FilterState | undefined>(currentFilter.value?.id || undefined);

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
const showAddFilterModal = ref(false);
const showFormResetDialog = ref(false);
const showRemoveFilterDialog = ref(false);

const pageSize = ref(LocalStorageManager.get<number>(PAGE_SIZE) || 5);
const currentPage = ref(LocalStorageManager.get<number>(PAGE) || 1);
const totalRows = ref(0);
const sortField = ref(LocalStorageManager.get<string>(SORT_FIELD) || 'id');
const sortDesc = ref(LocalStorageManager.get<boolean>(SORT_DESC) || false);

const selectionTypeOptions: Option[] = Object.keys(FilterSelectionType).map((key: string) => ({
  text: (FilterSelectionType as FilterSelectionTypeRecord)[key],
  value: key,
}));

const pageSizeOptions: Option[] = [{text: '1', value: 1}, {text: '5', value: 5}, {text: '10', value: 10}];

const getCriteriasCount = (item: Filter) => {
  return item.criterias?.length || 0;
};

const showFilterRemoveSpinner = (id?: number | FilterState) => {
  return removingFilter.value === true && removableFilterId.value !== undefined && removableFilterId.value === id;
};

const toggleCollapse = async (item: Filter) => {
  if (openCollapseId.value === item.id) {
    openCollapseId.value = undefined;
    currentFilter.value = undefined;
    updateOrRemoveFilterStorage();
    return;
  }

  openCollapseId.value = item.id;
  await getFilter(item.id as number, Action.LOAD);
};

const onDisclaimerClose = () => {
  hideDisclaimer.value = true;
  LocalStorageManager.save(HIDE_DISCLAIMER, true);
};

const onSortChanged = async (field: string, sortDescending: boolean) => {
  updateSortField(field);
  updateSortDesc(sortDescending);
  await loadFilters(field, sortDescending, currentPage.value, pageSize.value)
};

const onPaginationChanged = async (event: any, page: number) => {
  event.preventDefault();

  updateOrRemoveFilterStorage();
  await loadFilters(sortField.value, sortDesc.value, page, pageSize.value)
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
  if (currentFilter.value?.criterias && currentFilter.value.criterias[index]) {
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

const onShowAddFilterModal = () => {
  const addedFilter: Filter = {
    id: FilterState.NEW,
    name: '',
    selection: Object.keys(FilterSelectionType)[0] as FilterSelectionType,
    criterias: [createCriteria()],
  };

  openCollapseId.value = undefined;
  currentFilter.value = {...addedFilter};
  updateOrRemoveFilterStorage({...addedFilter});
  showAddFilterModal.value = true;
};

const onHideAddFilterModal = () => {
  currentFilter.value = undefined;
  showAddFilterModal.value = false;
  updateOrRemoveFilterStorage();
}

const onFilterSave = async (event: any) => {
  event.preventDefault();
  filterSaving.value = true;

  const filterToSave = {...currentFilter.value!};
  const isNewFilter = filterToSave.id === FilterState.NEW;

  if (isNewFilter) {
    delete filterToSave.id;
    await addFilter(filterToSave);
  } else {
    await updateFilter(filterToSave);
  }
};

const addFilter = async (filter: Filter) => {
  await filterService.postFilter(filter)
      .then((response) => {
          show('Filter added successfully', { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
          filters.value.push(response.data);
          totalRows.value = filters.value.length;
          onHideAddFilterModal();
      })
      .catch((error) => {
          console.error('Error adding filter:', error);
      })
      .finally(() => {
        filterSaving.value = false;
      });
}

const updateFilter = async (filter: Filter) => {
  await filterService.putFilter(filter)
      .then((response) => {
        show('Filter saved successfully', { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
        filters.value = filters.value.map(f => f.id === response.data.id ? {...response.data} : f);
      })
      .catch((error) => {
        console.error('Error updating filter:', error);
      })
      .finally(() => {
        filterSaving.value = false;
      });
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

const onFormReset = async () => {
  await getFilter(currentFilter.value!.id as number, Action.RESET)
};

const onFilterRemoved = async () => {
  if (removableFilterId.value) {
    removingFilter.value = true;
    await filterService.deleteFilter(removableFilterId.value as number)
        .then(() => {
          show('Filter removed successfully', { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
          filters.value = filters.value.filter(f => f.id !== removableFilterId.value);
          removableFilterId.value = undefined;
          totalRows.value = filters.value.length;
        })
        .catch((error) => {
          console.error('Error removing filter:', error);
        })
        .finally(() => {
          removingFilter.value = false;
        });
  }
}

const onPageSizeChanged = async (size: any) => {
  updatePageSizeStorage(size);
  await loadFilters(sortField.value, sortDesc.value, currentPage.value, size);
}

const loadFilters = async (field: string, sortDescending: boolean, page: number, size: number) => {
  if (loadInitialFilters.value === false) {
    loadAdditionalFilters.value = true;
  }

  await filterService.getFilters(field, sortDescending, page, size)
      .then((response) => {
        const loadedFilters = response.data.content;
        loadedFilters.forEach((filter: Filter) => {
          filter.criterias = filter.criterias?.sort((a, b) => a.id! - b.id!);
        });
        filters.value = loadedFilters;
        totalRows.value = response.data.totalElements;
      })
      .catch((error) => {
        console.error('Error loading filters:', error);
        loadError.value = true;
      })
      .finally(() => {
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
      });
}

const getFilter = async (id: number, action: Action) => {
    if (Action.LOAD === action) {
      loadFilterError.value = false;
      loadingFilter.value = true;
    } else if (Action.RESET === action) {
      resetFilterError.value = false;
      resettingFilter.value = true;
    }

  await filterService.getFilter(id)
      .then((response) => {
        const loadedFilter: Filter = response.data;
        loadedFilter.criterias?.sort((a, b) => a.id! - b.id!);

        if (Action.RESET === action) {
          show('Filter has been reset', { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
        }
        filters.value = filters.value.map(f => f.id === id ? {...loadedFilter} : f);
        currentFilter.value = response.data;
        updateOrRemoveFilterStorage(response.data);
      })
      .catch((error) => {
        console.error(`Error with filter ${action.toLowerCase()}:`, error);
        if (Action.LOAD === action) {
          loadFilterError.value = true;
        } else if (Action.RESET === action) {
          resetFilterError.value = true;
        }
      })
      .finally(() => {
        if (Action.LOAD === action) {
          loadingFilter.value = false;
        } else if (Action.RESET === action) {
          resettingFilter.value = false;
        }
      });
};

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
          This page is created with Spring Boot 3 and Vue.js 3. Purpose of it is to learn Vue.js basics.<br/>
          Queries to backend are delayed by 500ms to simulate real world scenario.
        </BAlert>

        <Loading
            :message="'Loading filters ...'"
            :loading="loadInitialFilters"
        />

        <BAlert variant="danger" :model-value="loadError">
          Error loading filters. Please try again later.
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
                ID
                <span v-if="'id' === sortField">
                  <IIconoirSortUp v-if="sortDesc" />
                  <IIconoirSortDown v-else />
                </span>
              </BTh>
              <BTh class="hover-pointer col-auto text-start" @click="onSortChanged('name', !sortDesc)">
                Name
                <span v-if="'name' === sortField">
                  <IIconoirSortUp v-if="sortDesc" />
                  <IIconoirSortDown v-else />
                </span>
              </BTh>
              <BTh class="col-1 text-nowrap">Criteria count</BTh>
              <BTh class="col-1">Selection</BTh>
              <BTh class="col-1">Action</BTh>
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
                      class="mx-2 hover-pointer"
                      role="button"
                      v-b-tooltip="'Edit filter'"
                      @click="() => toggleCollapse(filter)"
                    />

                    <ITypcnDelete
                      class="hover-pointer"
                      v-b-tooltip="'Remove filter'"
                      v-if="totalRows > 1 && !showFilterRemoveSpinner(filter.id)"
                      @click="() => onShowRemoveFilterDialog(filter.id!)"
                    />

                    <Loading :loading="showFilterRemoveSpinner(filter.id)" />
                  </div>
                </BTd>
              </BTr>

              <BTr class="text-start" v-if="filter.id === openCollapseId">
                <BTd class="p-0" colspan="5">
                  <Loading
                      class="text-center my-3"
                      :message="'Loading filter ...'"
                      :loading="loadingFilter"
                  />

                  <BAlert class="text-center mx-3 mt-3" variant="danger" :model-value="loadFilterError">
                    Error loading filter. Please try again later.
                  </BAlert>

                  <BCard class="border-0" :title="'Edit filter'" v-if="!loadingFilter && !loadFilterError && currentFilter">
                    <FilterDetailsForm
                        :current-filter="currentFilter"
                        :filter-saving="filterSaving"
                        :resetting-filter="resettingFilter"
                        :selection-type-options="selectionTypeOptions"
                        @add-criteria="onCriteriaAdded"
                        @close="() => toggleCollapse(filter)"
                        @remove-criteria="onCriteriaRemoved"
                        @reset="onShowFormResetDialog"
                        @save-filter="onFilterSave"
                        @update-criteria="onCriteriaUpdated"
                        @update-field="onCriteriaFieldUpdated"
                        @update-name="onFilterNameUpdated"
                        @update-selection="onFilterSelectionUpdated"
                      />
                  </BCard>
                </BTd>
              </BTr>
            </template>
          </BTbody>
        </BTableSimple>

        <BRow v-if="filters?.length > 0">
          <BCol class="col-auto ms-2">
            <BFormSelect
                size="sm"
                v-model="pageSize"
                :id="'page-size-select'"
                :options="pageSizeOptions"
                @change="onPageSizeChanged"
            />
          </BCol>

          <BCol class="col-auto">
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

          <BCol class="col-auto mt-1">
            Total count: {{ totalRows }}
          </BCol>

          <BCol class="col-auto mt-2" v-if="loadAdditionalFilters">
            <Loading :loading="loadAdditionalFilters"/>
          </BCol>

          <BCol class="text-end">
            <BButton
                class="me-3"
                @click="onShowAddFilterModal"
            >
              Add filter
            </BButton>
          </BCol>
        </BRow>
      </BCol>
    </BRow>

    <SimpleDialog
        :button-cancel-text="'Close'"
        :button-ok-text="'Yes'"
        :id="'disclaimer-dialog'"
        :message="'Are you sure you want to reset the form?'"
        :show="showFormResetDialog"
        @cancel="() => onCloseDialog(Action.RESET)"
        @confirm="onFormReset"
    />

    <SimpleDialog
        :button-cancel-text="'Close'"
        :button-ok-text="'Yes'"
        :id="'disclaimer-dialog'"
        :message="'Are you sure you want to remove filter?'"
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
      :cancel-title="'Close'"
      :ok-title="'Confirm'"
      :title="'Add new filter'"
      v-if="currentFilter"
      v-model="showAddFilterModal"
      @hidden="onHideAddFilterModal"
    >
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
    </BModal>
  </BContainer>
</template>

<style scoped>
.hover-pointer {
  cursor: pointer;
}

.action-icon-container {
  display: flex;
  align-items: end;
}
</style>