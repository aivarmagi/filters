<script setup lang="ts">
import {
  type Criteria,
  CriteriaAmountType, CriteriaDateType, CriteriaTitleType, CriteriaName,
  type CriteriaNameRecord, type CriteriaAmountTypeRecord, type CriteriaTitleTypeRecord, type CriteriaDateTypeRecord
} from "@/models/Criteria";
import type {Option} from "@/models/Option";
import CriteriaGroupItem from "@/components/form/CriteriaGroupItem.vue";

defineProps<{
  id: string,
  criterias: Criteria[],
  keepCriteriaValue?: boolean,
  label: string,
  labelCols?: number,
}>()

const emit = defineEmits<{
  (e: 'resetKeepCriteriaValue'): void
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
</script>

<template>
  <BFormGroup
      label-class="text-nowrap"
      :id="'criteria-group-' + id"
      :label="label"
      :label-cols="labelCols || 2"
      :label-for="'criteria-input-' + id"
  >
    <BRow v-for="(criteria, index) in criterias">
      <CriteriaGroupItem
          :amount-options="amountOptions"
          :criteria="criteria"
          :date-options="dateOptions"
          :id="'criteria-group-item-' + index"
          :keep-criteria-value="keepCriteriaValue"
          :name-options="nameOptions"
          :title-options="titleOptions"
          @reset-keep-criteria-value="emit('resetKeepCriteriaValue')"
          @update-criteria="(val) => emit('updateCriteria', {...val}, index)"
          @update-field="(field, value) => emit('updateField', field, value, index)"
      />
    </BRow>
  </BFormGroup>
</template>

<style scoped>

</style>