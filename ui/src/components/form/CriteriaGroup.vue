<script setup lang="ts">
import {ref, watch} from "vue";
import Criteria, {CriteriaAmountType, CriteriaDateType, CriteriaTitleType, CriteriaName} from "@/models/Criteria";
import type {Option} from "@/models/Option";
import CriteriaGroupItem from "@/components/form/CriteriaGroupItem.vue";
import {isEqual} from "lodash";

const props = defineProps<{
  id: string,
  criterias: Criteria[],
  label: string,
  labelCols?: number,
}>()

const emit = defineEmits<{
  (e: 'updateCriteria', val: Criteria, index: number): void
}>()

// const internalValues = ref(props.criterias)
const nameOptions: Option[] = Object.keys(CriteriaName).map(key => ({text: CriteriaName[key], value: key}));
const amountOptions: Option[] = Object.keys(CriteriaAmountType).map(key => ({text: CriteriaAmountType[key], value: key}));
const titleOptions: Option[] = Object.keys(CriteriaTitleType).map(key => ({text: CriteriaTitleType[key], value: key,}));
const dateOptions: Option[] = Object.keys(CriteriaDateType).map(key => ({text: CriteriaDateType[key], value: key,}));

// watch(() => props.criterias, (newVal) => {
//   console.log('group > criterias changed to', newVal);
//   if (!isEqual(newVal, internalValues.value)) {
//     console.log('reset group values')
//     internalValues.value = {...newVal};
//   }
// }, { deep: true });
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
          :name-options="nameOptions"
          :title-options="titleOptions"
          @update-criteria="(val) => emit('updateCriteria', {...val}, index)"
      />
<!--          @update-operator="(val) => criterias[index].operator = val"-->
    </BRow>
  </BFormGroup>
</template>

<style scoped>

</style>