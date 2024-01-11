<script setup lang="ts">
import type {Option} from "@/models/Option";
import {type Criteria, CriteriaName} from "@/models/Criteria";
import {onMounted, ref, watch} from "vue";
import TextInput from "@/components/form/TextInput.vue";
import {isEqual} from 'lodash';

const props = defineProps<{
  amountOptions: Option[],
  criteria: Criteria,
  dateOptions: Option[],
  id: string,
  keepCriteriaValue?: boolean,
  nameOptions: Option[],
  titleOptions: Option[],
}>()

const emit = defineEmits<{
  (e: 'resetKeepCriteriaValue'): void
  (e: 'updateCriteria', val: Criteria): void
  (e: 'updateField', field: string, value: string): void
}>()

const name = ref("")
const operator = ref("")
const operatorOptions= ref<Option[]>([])
const criteriaValue = ref("")

const getOperatorOptions = (criteriaName: string): Option[] => {
  if (CriteriaName.TITLE === criteriaName) {
    return props.titleOptions
  } else if (CriteriaName.DATE === criteriaName) {
    return props.dateOptions
  } else {
    return props.amountOptions
  }
}

watch(() => props.criteria, (newCriteria) => {
  if (newCriteria.name !== name.value) {
    name.value = newCriteria.name;
  } else if (newCriteria.operator !== operator.value) {
    operator.value = newCriteria.operator;
  } else if (newCriteria.value !== criteriaValue.value) {
    criteriaValue.value = newCriteria.value;
  }
}, { deep: true });

watch(() => name.value, (newVal) => {
    if (newVal && !isEqual(operatorOptions.value, getOperatorOptions(newVal))) {
      operatorOptions.value = getOperatorOptions(newVal);
      operator.value = operatorOptions.value[0].value as string;

      const newCriteriaValue = props.keepCriteriaValue ? props.criteria.value : '';
      criteriaValue.value = newCriteriaValue;
      emit('resetKeepCriteriaValue')

      const newCriteria = {...props.criteria, name: newVal, operator: operatorOptions.value[0].value, value: newCriteriaValue}
      emit('updateCriteria', newCriteria as Criteria)
    }
})

watch(() => operator.value, (newVal) => {
  if (operatorOptions.value?.every((opt: Option) => opt.value !== newVal)) {
    operator.value = operatorOptions.value[0].value as string;
  }
})

onMounted(() => {
  name.value = props.criteria.name
  operatorOptions.value = getOperatorOptions(name.value)
  operator.value = props.criteria.operator
  criteriaValue.value = props.criteria.value
})
</script>

<template>
  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="name"
        :id="'criteria-name-' + id"
        :options="nameOptions"
    />
  </BCol>

  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="operator"
        :id="'criteria-operator-' + id"
        :options="operatorOptions"
        @change="(val) => emit('updateField', 'operator', val as string)"
    />
  </BCol>

  <BCol class="col-12 col-md-4 mt-3">
    <BRow>
      <BCol>
        <TextInput
            required
            :id="'criteria-value-' + id"
            :placeholder="'Enter value'"
            :value="criteriaValue"
            @update-value="(val) => emit('updateField', 'value', val)"
        />
      </BCol>

      <BCol class="col-auto px-0 me-3 mt-1">
        <ITypcnDelete
            disabled
            class="hover-pointer"
            @click="() => console.log('TODO: Delete criteria ' + id)"
        />
      </BCol>
    </BRow>
  </BCol>
</template>

<style scoped>

</style>