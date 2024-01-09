<script setup lang="ts">
import type {Option} from "@/models/Option";
import Criteria, {CriteriaName} from "@/models/Criteria";
import {ref, watch} from "vue";
import TextInput from "@/components/form/TextInput.vue";
import {isEqual} from 'lodash';

const props = defineProps<{
  amountOptions: Option[],
  criteria: Criteria,
  dateOptions: Option[],
  id: string,
  nameOptions: Option[],
  titleOptions: Option[],
}>()

const emit = defineEmits<{
  (e: 'updateOperator', val: string): void
  (e: 'updateCriteria', val: Criteria): void
}>()

const name = ref(props.criteria.name)
const operator = ref(props.criteria.operator)
const criteriaValue = ref(props.criteria.value || '')

const getOperatorOptions = (criteriaName: string): Option[] => {
  if (criteriaName === CriteriaName.TITLE) {
    return props.titleOptions
  } else if (criteriaName === CriteriaName.DATE) {
    return props.dateOptions
  } else {
    return props.amountOptions
  }
}

const operatorOptions = ref(getOperatorOptions(name.value))

const updateCriteria = (field: string, fieldValue: string, resetValueField?: boolean) => {
  const newCriteria = {...props.criteria, [field]: fieldValue}

  if (resetValueField) {
    newCriteria.value = '';
  }

  emit('updateCriteria', newCriteria)
}

watch(() => props.criteria, (newCriteria) => {
  console.log('item ->', props.id ,'criteria changes to', newCriteria)

  const opts = getOperatorOptions(newCriteria.name);
  console.log('item ->', props.id, 'opts', opts, 'criteria changed?:', isEqual(props.criteria, newCriteria))
  // if (!isEqual(opts, operatorOptions.value)) {
  //   operatorOptions.value = opts;
  //   operator.value = opts[0].value;
  //   criteriaValue.value = '';
  //   emit('updateOperator', opts[0].value)
  // } else {
  //   name.value = newCriteria.name;
  //   criteriaValue.value = newCriteria.value;
  //   console.log('only name changed')
  // }
}, { deep: true });
</script>

<template>
  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="name"
        :id="'criteria-name-' + id"
        :options="nameOptions"
        @change="(val) => updateCriteria('name', val, true)"
    />
  </BCol>

  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="operator"
        :id="'criteria-operator-' + id"
        :options="operatorOptions"
        @change="() => updateCriteria('operator', operator)"
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
            @update-value="(val) => updateCriteria('value', val)"
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