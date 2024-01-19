<script setup lang="ts">
import {
  type Criteria,
  CriteriaAmountType,
  CriteriaDateType,
  CriteriaTitleType,
  CriteriaName,
  type CriteriaNameRecord,
  type CriteriaAmountTypeRecord,
  type CriteriaTitleTypeRecord,
  type CriteriaDateTypeRecord,
} from "@/models/Criteria";
import type {Option} from "@/models/Option";
import CriteriaGroupItem from "@/components/form/CriteriaGroupItem.vue";

defineProps<{
  id: string,
  criterias: Criteria[],
  label: string,
  labelCols?: number,
}>()

const emit = defineEmits<{
  (e: 'removeCriteria', val: number): void
  (e: 'updateCriteria', val: Criteria, index: number): void
  (e: 'updateField', field: string, value: string, index: number): void
}>()

const nameOptions: Option[] = Object.keys(CriteriaName).map(key =>
    ({text: (CriteriaName as CriteriaNameRecord)[key], value: key}));
const amountOptions: Option[] = Object.keys(CriteriaAmountType).map(key =>
    ({text: (CriteriaAmountType as CriteriaAmountTypeRecord)[key], value: key}));
const titleOptions: Option[] = Object.keys(CriteriaTitleType).map(key =>
    ({text: (CriteriaTitleType as CriteriaTitleTypeRecord)[key], value: key}));
const dateOptions: Option[] = Object.keys(CriteriaDateType).map(key =>
    ({text: (CriteriaDateType as CriteriaDateTypeRecord)[key], value: key}));

const onCriteriaUpdated = (val: Criteria, index: number) => {
  emit('updateCriteria', val, index)
}

const onCriteriaFieldUpdated = (field: string, value: string, index: number) => {
  emit('updateField', field, value, index)
}

const onCriteriaRemoved = (index: number) => {
  emit('removeCriteria', index)
}
</script>

<template>
  <BFormGroup
      label-class="text-nowrap mt-3"
      :id="'criteria-group-' + id"
      :label="label"
      :label-cols="labelCols || 2"
      :label-for="'criteria-input-' + id"
  >
    <BRow class="mt-3" v-for="(criteria, index) in criterias">
      <CriteriaGroupItem
          :amount-options="amountOptions"
          :criteria="criteria"
          :criteria-count="criterias.length"
          :date-options="dateOptions"
          :id="'criteria-group-item-' + index"
          :name-options="nameOptions"
          :title-options="titleOptions"
          @remove-criteria="() => onCriteriaRemoved(index)"
          @update-criteria="(val) => onCriteriaUpdated(val, index)"
          @update-field="(field, value) => onCriteriaFieldUpdated(field, value, index)"
      />
    </BRow>
  </BFormGroup>
</template>

<style scoped>

</style>