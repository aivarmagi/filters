<script setup lang="ts">
import type {Option} from "@/models/Option";
import {type Criteria, CriteriaItemFieldType, CriteriaName} from "@/models/Criteria";
import {onMounted, ref, watch} from "vue";
import TextInput from "@/components/form/TextInput.vue";

const props = defineProps<{
  amountOptions: Option[],
  criteria: Criteria,
  dateOptions: Option[],
  id: string,
  nameOptions: Option[],
  titleOptions: Option[],
}>()

const emit = defineEmits<{
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

const updateCriteriaValues = (nameValue: string, optionsValue: Option[], operatorValue: string, value: string) => {
  name.value = nameValue
  operatorOptions.value = optionsValue
  operator.value = operatorValue
  criteriaValue.value = value
}

const onCriteriaNameChanged = (val: string) => {
  const changedOptions = getOperatorOptions(val);
  const newCriteria = {...props.criteria, name: val, operator: changedOptions[0].value, value: ''}
  emit('updateCriteria', newCriteria as Criteria)
}

const onCriteriaFieldChanged = (field: string, val: string) => {
  emit('updateField', field, val)
}

watch(() => props.criteria, (changedCriteria) => {
  const newOptions = getOperatorOptions(changedCriteria.name)
  updateCriteriaValues(changedCriteria.name, newOptions, changedCriteria.operator, `${changedCriteria.value}`)
}, { deep: true });

onMounted(() => {
  updateCriteriaValues(props.criteria.name, getOperatorOptions(props.criteria.name),
      props.criteria.operator, props.criteria.value)
})
</script>

<template>
  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="name"
        :id="'criteria-name-' + id"
        :options="nameOptions"
        @change="(val) => onCriteriaNameChanged(val as string)"
    />
  </BCol>

  <BCol class="col-12 col-md-4 mt-3">
    <BFormSelect
        v-model="operator"
        :id="'criteria-operator-' + id"
        :options="operatorOptions"
        @change="(val) => onCriteriaFieldChanged(CriteriaItemFieldType.OPERATOR, val as string)"
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
            @update-value="(val) => onCriteriaFieldChanged(CriteriaItemFieldType.VALUE, val)"
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
<!--    keepValues: {{props.keepCriteriaValue}}-->
  </BCol>
</template>

<style scoped>

</style>