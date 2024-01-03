<script setup lang="ts">
import {onMounted, ref} from "vue";
import filterService   from "@/services/filterService";
import type Filter from "@/models/Filter";
import {BSpinner} from "bootstrap-vue-next";

const filters = ref<Filter[]>([]);
const loading = ref(true);
const perPage = ref(5);
const currentPage = ref(1);
const rows = ref(0);

const tableFields = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Name' },
  { key: 'criterias', label: 'Criteria count' },
  { key: 'selectionType', label: 'Selection type' },
  { key: 'actions', label: 'Actions' },
];

const getCriteriasCount = (item: Filter) => {
  return item.criterias.length;
};

onMounted(async () => {
  try {
    const response = await filterService.getFilters();
    filters.value = response.data.content;
    rows.value = response.data.totalElements;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
});

</script>

<template>
  <BContainer fluid>
    <BRow class="mt-4">
      <BCol class="text-center">
        <div v-if="loading">
          <BSpinner small /> Loading filters ...
        </div>

        <BTable
            class="filters-table"
            hover
            striped
            v-if="!loading && filters.length > 0"
            :current-page="currentPage"
            :fields="tableFields"
            :items="filters"
            :per-page="perPage"
        >
          <template #cell(criterias)="row">
            {{ getCriteriasCount(row.item) }}
          </template>

          <template #cell(actions)="row">
            <div>
              <BButton
                  class="me-2"
                  variant="primary"
                  size="sm"
                  @click="() => console.log('Edit ' + row.item.id)"
              >Edit</BButton>

              <BButton
                  variant="danger"
                  size="sm"
                  @click="() => console.log('Delete ' + row.item.id)"
              >Delete</BButton>
            </div>
          </template>

          <BTfoot>
            <template #table-busy>
              <div class="text-center text-danger my-2">
<!--                <BIcon icon="exclamation-triangle-fill" aria-hidden="true"></BIcon>-->
                <strong> Loading...</strong>
              </div>
            </template>
          </BTfoot>
        </BTable>

        <BPagination
            align="center"
            size="sm"
            v-model="currentPage"
            :total-rows="rows"
            :per-page="perPage"
            last-text="Last"
            first-text="First"
            aria-controls="pagination-table-controls"
            v-if="!loading && filters.length > 0"
        ></BPagination>
      </BCol>
    </BRow>
  </BContainer>
</template>

<style scoped>
</style>