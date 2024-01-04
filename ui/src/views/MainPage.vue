<script setup lang="ts">
import {onMounted, ref} from "vue";
import filterService   from "@/services/filterService";
import Filter, {FilterSelectionType} from "@/models/Filter";
import {BContainer} from "bootstrap-vue-next";

const filters = ref<Filter[]>([]);
const loading = ref(true);
const pageSize = ref(1);
const currentPage = ref(1);
const totalRows = ref(0);
const sortField = ref('id');
const sortDesc = ref(true);
const openCollapseId = ref(null as number);
const currentFilter = ref(null as Filter)

const selectionTypes = Object.keys(FilterSelectionType).map(key => {
  return {
    text: FilterSelectionType[key],
    value: key,
  }
});

const getCriteriasCount = (item: Filter) => {
  return item.criterias.length;
};

const toggleCollapse = (item: Filter) => {
  if (openCollapseId.value === item.id) {
    openCollapseId.value = null;
    currentFilter.value = null;
    return;
  }

  openCollapseId.value = item.id;
  currentFilter.value = { ...item }
};

const onSortChanged = async (field: string, sortDescending: boolean) => {
  await loadFilters(field, sortDescending, currentPage.value)
};

const onPaginationChanged = async (sort) => {
  sort.preventDefault();
  //todo
};

const loadFilters = async (field: string, sortDescending: boolean, page: number, size: number) => {
  try {
    const response = await filterService.getFilters(field, sortDescending, page, size);
    filters.value = response.data.content;
    totalRows.value = response.data.totalElements;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
    sortField.value = field;
    sortDesc.value = sortDescending;
    currentPage.value = page;
    pageSize.value = size;
  }
};

onMounted(() => loadFilters('id', false, 1, 10));
</script>

<template>
  <BContainer fluid>
    <BRow class="mt-4">
      <BCol class="text-center">
        <Loading
            :message="'Loading filters ...'"
            :loading="loading"
        />

        <BTableSimple
            class="filters-table"
            hover
            responsive
            v-if="!loading && filters.length > 0"
        >
          <BThead>
            <BTr>
              <BTh
                  class="col-1 hover-pointer"
                  @click="onSortChanged('id', !sortDesc)"
              >
                ID
                <span v-if="'id' === sortField">
                  <IIconoirSortUp v-if="sortDesc" />
                  <IIconoirSortDown v-else />
                </span>
              </BTh>
              <BTh
                  class="hover-pointer col-auto"
                  @click="onSortChanged('name', !sortDesc)"
              >
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
              <BTr class="align-middle bg-info">
                <BTd>{{ filter.id }}</BTd>
                <BTd>{{ filter.name }}</BTd>
                <BTd>
                  <BBadge pill variant="secondary">
                    {{ getCriteriasCount(filter) }}
                  </BBadge>
                </BTd>
                <BTd>{{ filter.selectionType }}</BTd>
                <BTd class="text-end">
                  <IBxsEdit
                    class="me-2 hover-pointer"
                    role="button"
                    @click="() => toggleCollapse(filter)"
                  />

                  <ITypcnDelete
                    class="hover-pointer me-2"
                    @click="() => console.log('Delete ' + filter.id)"
                  />
                </BTd>
              </BTr>

              <BTr class="text-start" v-if="currentFilter">
                <BTd class="p-0" colspan="5">
                  <BCollapse
                      class="clearfix"
                      :id="'collapse-' + filter.id"
                      :visible="openCollapseId === filter.id"
                  >
                    <BCard
                        class="border-0"
                        :title="'Edit filter'"
                    >
                      <BForm @submit="console.log">
                        <BRow>
                          <BCol class="text-start col-md-6 col-12">
                              <BFormGroup id="input-group-2" label="Name" label-for="input-2">
                                <BFormInput id="input-2" v-model="currentFilter.name" placeholder="Enter name" required />
                              </BFormGroup>
                          </BCol>

                          <BCol class="col-xs-12 col-md-6 col-12">
                              <BFormGroup class="align-content-start" id="input-group-3" label="Selection type" label-for="input-3">
                                <BFormRadioGroup
                                    id="radio-group-1"
                                    v-model="currentFilter.selectionType"
                                    :options="selectionTypes"
                                    name="selection-type"
                                />
                              </BFormGroup>
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
                                  @click="() => console.log(currentFilter)"
                              >
                                Edit
                              </BButton>
                          </BCol>
                        </BRow>
                      </BForm>
                    </BCard>
                  </BCollapse>
                </BTd>
              </BTr>
            </template>
          </BTbody>
        </BTableSimple>

        <BPagination
            align="center"
            size="sm"
            v-if="!loading && filters.length > 0"
            v-model="currentPage"
            :first-text="'First'"
            :last-text="'Last'"
            :per-page="pageSize"
            :total-rows="totalRows"
            @page-click="onPaginationChanged"
        ></BPagination>
      </BCol>
    </BRow>
  </BContainer>
</template>

<style scoped>
.hover-pointer {
  cursor: pointer;
}
</style>