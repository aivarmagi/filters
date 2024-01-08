<script setup lang="ts">
import {onMounted, ref} from "vue";
import filterService from "@/services/filterService";
import Filter, {FilterSelectionType} from "@/models/Filter";
import {BContainer, useToast} from "bootstrap-vue-next";
import TextInput from "@/components/form/TextInput.vue";
import RadioGroup from "@/components/form/RadioGroup.vue";
import CriteriaGroup from "@/components/form/CriteriaGroup.vue";
import type {Option} from "@/models/Option";
import type Criteria from "@/models/Criteria";
import Loading from "@/components/Loading.vue";

const {show} = useToast();

const filters = ref<Filter[]>([]);
const loadInitialFilters = ref(true);
const loadAdditionalFilters = ref(false);
const loadSingleFilter = ref(false);
const loadError = ref(false);
const loadFilterError = ref(false);
const pageSize = ref(5);
const currentPage = ref(1);
const totalRows = ref(0);
const sortField = ref('id');
const sortDesc = ref(false);
const openCollapseId = ref(null as number);
const currentFilter = ref(undefined as Filter)
const filterSaving = ref(false);
const showDisclaimer = ref(true);

const selectionTypeOptions: Option[] = Object.keys(FilterSelectionType).map(key => {
  return {
    text: FilterSelectionType[key],
    value: key,
  }
});

const pageSizeOptions: Option[] = [{text: '1', value: 1}, {text: '5', value: 5}, {text: '10', value: 10}];

const getCriteriasCount = (item: Filter) => {
  return item.criterias.length;
};

const toggleCollapse = async (item: Filter) => {
  if (openCollapseId.value === item.id) {
    openCollapseId.value = undefined;
    currentFilter.value = undefined;
    return;
  }

  openCollapseId.value = item.id;
  await loadFilter(item.id);
};

const onDisclaimerClose = () => {
  showDisclaimer.value = false;
  //todo save it to local storage?
};

const onSortChanged = async (field: string, sortDescending: boolean) => {
  await loadFilters(field, sortDescending, currentPage.value, pageSize.value)
};

const onPaginationChanged = async (event, page) => {
  event.preventDefault();
  await loadFilters(sortField.value, sortDesc.value, page, pageSize.value)
};

const onCriteriaUpdated = (val: Criteria, index: number) => {
  currentFilter.value.criterias[index] = {...val}
};

//todo filter reset

const onFormSave = async (event) => {
  event.preventDefault();
  filterSaving.value = true;

  console.log('values to save', currentFilter.value);
  await filterService.putFilter(currentFilter.value)
      .then(() => {
        filterSaving.value = false;
        console.log('should display toast')
        show('Filter saved successfully', { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
        // toggleCollapse(currentFilter.value);
  //todo save and toast
      })
      .catch((error) => {
        console.error('Error updating filter:', error);
      })
      .finally(() => {
        filterSaving.value = false;
      });
};

const onPageSizeChanged = async (size) => await loadFilters(sortField.value, sortDesc.value, currentPage.value, size);

const loadFilters = async (field: string, sortDescending: boolean, page: number, size: number) => {
  try {
    if (loadInitialFilters.value === false) {
      loadAdditionalFilters.value = true;
    }

    const response = await filterService.getFilters(field, sortDescending, page, size);
    filters.value = response.data.content;
    totalRows.value = response.data.totalElements;
  } catch (error) {
    console.error('Error loading filters:', error);
    loadError.value = true;
  } finally {
    sortField.value = field;
    sortDesc.value = sortDescending;
    currentPage.value = page;
    pageSize.value = size;

    if (loadInitialFilters.value) {
      loadInitialFilters.value = false;
    } else {
      loadAdditionalFilters.value = false;
    }
  }
};

const loadFilter = async (id: number) => {
  loadFilterError.value = false;
  loadSingleFilter.value = true;

  try {
    const response = await filterService.getFilter(id);
    currentFilter.value = response.data;
  } catch (error) {
    console.error('Error loading filter:', error);
    loadFilterError.value = true;
  } finally {
    loadSingleFilter.value = false;
  }
};

onMounted(() => loadFilters(sortField.value, sortDesc.value, currentPage.value, pageSize.value));
</script>

<template>
  <BContainer fluid>
    <BRow class="mt-4">
      <BCol class="text-center">
        <BAlert dismissible variant="secondary" @update:model-value="onDisclaimerClose" :model-value="showDisclaimer">
          This page is created with Spring Boot 3 and Vue.js 3. Purpose of it is to learn Vue.js basics.<br/>
          It should contain CRUD functionality at the end. However only getting filters from backend is currently functional.<br/>
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
              <BTh class="col-1"></BTh>
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
                    {{ getCriteriasCount(filter) }}
                  </BBadge>
                </BTd>
                <BTd class="bg-light">{{ FilterSelectionType[filter.selection] }}</BTd>
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
                      :loading="loadSingleFilter"
                  />

                  <BAlert class="text-center mx-3 mt-3" variant="danger" :model-value="loadFilterError">
                    Error loading filter. Please try again later.
                  </BAlert>

                  <BCard class="border-0" :title="'Edit filter'" v-if="!loadSingleFilter && !loadFilterError">
                    <BForm @submit="onFormSave">
                      <BRow>
                        <BCol class="text-start mt-3">
                          <TextInput
                              class-name="'pb-0'"
                              required
                              :id="`${currentFilter.id}`"
                              :label="'Name'"
                              :placeholder="'Enter name'"
                              :value="currentFilter.name"
                              @update-value="(val) => currentFilter.name = val"
                          />
                        </BCol>
                      </BRow>

                      <BRow>
                        <BCol class="text-start mt-0">
                          <CriteriaGroup
                              :criterias="currentFilter.criterias"
                              :id="`${currentFilter.id}`"
                              :label="'Criteria'"
                              @update-criteria="onCriteriaUpdated"
                          />
                        </BCol>
                      </BRow>

                      <BRow>
                        <BCol class="text-start mt-3">
                          <RadioGroup
                              :id="`${currentFilter.id}`"
                              :options="selectionTypeOptions"
                              :label="'Selection'"
                              :value="currentFilter.selection"
                              @update-value="(val) => currentFilter.selection = val"
                          />
                        </BCol>
                      </BRow>

                      <BRow class="mt-3">
                        <BCol class="text-end">
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
  </BContainer>
</template>

<style scoped>
.hover-pointer {
  cursor: pointer;
}
</style>