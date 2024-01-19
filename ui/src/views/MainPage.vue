<script setup lang="ts">
import {onMounted, ref} from "vue";
import filterService from "@/services/filterService";
import {Filter, FilterSelectionType, type FilterSelectionTypeRecord} from "@/models/Filter";
import {BContainer, useToast} from "bootstrap-vue-next";
import Input from "@/components/form/Input.vue";
import RadioGroup from "@/components/form/RadioGroup.vue";
import CriteriaGroup from "@/components/form/CriteriaGroup.vue";
import type {Option} from "@/models/Option";
import {createCriteria, type Criteria} from "@/models/Criteria";
import Loading from "@/components/Loading.vue";
import {Action} from "@/enums/Action";
import SimpleDialog from "@/components/dialog/SimpleDialog.vue";

const {show} = useToast();

const currentFilter = ref<Filter>()
const filters = ref<Filter[]>([]);
const openCollapseId = ref<number>();

const filterSaving = ref(false);
const loadAdditionalFilters = ref(false);
const loadError = ref(false);
const loadFilterError = ref(false);
const loadingFilter = ref(false);
const loadInitialFilters = ref(true);
const resetFilterError = ref(false);
const resettingFilter = ref(false);
const showDisclaimer = ref(true);
const showFormResetDialog = ref(false);

const pageSize = ref(5);
const currentPage = ref(1);
const totalRows = ref(0);
const sortField = ref('id');
const sortDesc = ref(false);

const selectionTypeOptions: Option[] = Object.keys(FilterSelectionType).map((key: string) => ({
  text: (FilterSelectionType as FilterSelectionTypeRecord)[key],
  value: key,
}));

const pageSizeOptions: Option[] = [{text: '1', value: 1}, {text: '5', value: 5}, {text: '10', value: 10}];

const getCriteriasCount = (item: Filter) => {
  return item.criterias?.length || 0;
};

const toggleCollapse = async (item: Filter) => {
  if (openCollapseId.value === item.id) {
    openCollapseId.value = undefined;
    currentFilter.value = undefined;
    return;
  }

  openCollapseId.value = item.id;
  await getFilter(item.id, Action.LOAD);
};

const onDisclaimerClose = () => {
  showDisclaimer.value = false;
  //todo save it to local storage?
};

const onSortChanged = async (field: string, sortDescending: boolean) => {
  await loadFilters(field, sortDescending, currentPage.value, pageSize.value)
};

const onPaginationChanged = async (event: any, page: number) => {
  event.preventDefault();
  await loadFilters(sortField.value, sortDesc.value, page, pageSize.value)
};

const onFilterNameUpdated = (val: string) => currentFilter.value!.name = val;

const onFilterSelectionUpdated = (val: string) => currentFilter.value!.selection = val as FilterSelectionType;

const onCriteriaUpdated = (val: Criteria, index: number) => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias[index] = {...val}
  }
};

const onCriteriaFieldUpdated = (field: string, value: string, index: number) => {
  if (currentFilter.value?.criterias && currentFilter.value.criterias[index]) {
    (currentFilter.value.criterias[index] as any)[field] = value;
  }
};

const onCriteriaRemoved = (index: number) => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias.splice(index, 1);
  }
};

const onCriteriaAdded = () => {
  if (currentFilter.value?.criterias) {
    currentFilter.value.criterias.push(createCriteria());
  }
};

const onFormSave = async (event: any) => {
  event.preventDefault();
  filterSaving.value = true;

  await filterService.putFilter(currentFilter.value!)
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
};

const onShowFormResetDialog = (event: any) => {
  event.preventDefault();
  if (showFormResetDialog.value === false) {
    showFormResetDialog.value = true;
  }
}

const onCloseFormResetDialog = () => {
  if (showFormResetDialog.value === true) {
    showFormResetDialog.value = false;
  }
}

const onFormReset = async () => {
  await getFilter(currentFilter.value!.id, Action.RESET)
};

const onPageSizeChanged = async (size: any) => await loadFilters(sortField.value, sortDesc.value, currentPage.value, size);

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
        sortField.value = field;
        sortDesc.value = sortDescending;
        currentPage.value = page;
        pageSize.value = size;

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
})
</script>

<template>
  <BContainer fluid>
    <BRow class="mt-4">
      <BCol class="text-center">
        <BAlert dismissible variant="secondary" @update:model-value="onDisclaimerClose" :model-value="showDisclaimer">
          This page is created with Spring Boot 3 and Vue.js 3. Purpose of it is to learn Vue.js basics.<br/>
          It should contain full CRUD functionality at the end.<br/>
          Currently getting filters, update, filter reset and criteria removal features are functional.<br/>
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
            v-if="filters.length > 0"
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
              <BTr class="align-middle">
                <BTd class="bg-light">{{ filter.id }}</BTd>
                <BTd class="bg-light text-start">{{ filter.name }}</BTd>
                <BTd class="bg-light">
                  <BBadge pill variant="secondary">
                    {{ getCriteriasCount(filter as Filter) }}
                  </BBadge>
                </BTd>
                <BTd class="bg-light">{{ (FilterSelectionType as FilterSelectionTypeRecord)[filter.selection] }}</BTd>
                <BTd class="text-end bg-light">
                  <IBxsEdit
                    class="me-2 hover-pointer"
                    role="button"
                    @click="() => toggleCollapse(filter)"
                  />

                  <ITypcnDelete
                    class="hover-pointer me-2"
                    @click="() => console.log('TODO Delete ' + filter.id)"
                  />
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
                    <BForm @reset="onShowFormResetDialog" @submit="onFormSave">
                      <BRow>
                        <BCol class="text-start mt-3">
                          <Input
                              class-name="'pb-0'"
                              required
                              :id="`${currentFilter.id}`"
                              :label="'Name'"
                              :placeholder="'Enter name'"
                              :value="currentFilter.name"
                              @update-value="onFilterNameUpdated"
                          />
                        </BCol>
                      </BRow>

                      <BRow>
                        <BCol class="text-start mt-0">
                          <CriteriaGroup
                              v-if="currentFilter.criterias"
                              :criterias="currentFilter.criterias"
                              :id="`${currentFilter.id}`"
                              :label="'Criteria'"
                              @remove-criteria="onCriteriaRemoved"
                              @update-criteria="onCriteriaUpdated"
                              @update-field="onCriteriaFieldUpdated"
                          />
                        </BCol>
                      </BRow>

                      <BRow>
                        <BCol class="text-end mt-3">
                          <BButton
                              variant="outline-secondary"
                              @click="onCriteriaAdded"
                          >
                            Add criteria
                          </BButton>
                        </BCol>
                      </BRow>

                      <BRow>
                        <BCol class="text-start mt-3">
                          <RadioGroup
                              v-if="currentFilter.selection"
                              :id="`${currentFilter.id}`"
                              :options="selectionTypeOptions"
                              :label="'Selection'"
                              :value="currentFilter.selection"
                              @update-value="onFilterSelectionUpdated"
                          />
                        </BCol>
                      </BRow>

                      <BRow class="mt-3">
                        <BCol class="text-end">
                          <BButton
                              class="me-2"
                              type="reset"
                              variant="outline-secondary"
                              :loading="resettingFilter"
                              :loading-text="'Resetting ...'"
                          >
                            Reset
                          </BButton>

                          <BButton
                              class="me-2"
                              variant="outline-secondary"
                              @click="() => toggleCollapse(filter)"
                          >
                            Close
                          </BButton>

                          <BButton
                              type="submit"
                              :loading="filterSaving"
                              :loading-text="'Saving ...'"
                          >
                            Save changes
                          </BButton>
                        </BCol>
                      </BRow>
                    </BForm>
                  </BCard>
                </BTd>
              </BTr>
            </template>
          </BTbody>
        </BTableSimple>

        <BRow v-if="filters?.length > 0">
          <BCol class="col-auto ms-3">
            <BFormSelect
                size="sm"
                v-model="pageSize"
                :id="'page-size-select'"
                :options="pageSizeOptions"
                @change="onPageSizeChanged"
            />
          </BCol>

          <BCol class="col-1">
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
            <Loading :loading="loadAdditionalFilters"/>
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
        @cancel="onCloseFormResetDialog"
        @confirm="onFormReset"
    />
  </BContainer>
</template>

<style scoped>
.hover-pointer {
  cursor: pointer;
}
</style>