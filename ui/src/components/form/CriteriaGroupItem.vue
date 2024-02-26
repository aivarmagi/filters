<script setup lang="ts">
import type {Option} from "@/models/Option";
import {type Criteria, CriteriaItemFieldType, CriteriaName} from "@/models/Criteria";
import {onMounted, ref, watch} from "vue";
import Input from "@/components/form/Input.vue";
import type {InputType} from "bootstrap-vue-next";
import SimpleDialog from "@/components/dialog/SimpleDialog.vue";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  amountOptions: Option[],
  criteria: Criteria,
  criteriaCount: number,
  dateOptions: Option[],
  id: string,
  nameOptions: Option[],
  titleOptions: Option[],
}>()

const emit = defineEmits<{
  (e: 'removeCriteria'): void
  (e: 'updateCriteria', val: Criteria): void
  (e: 'updateField', field: string, value: string): void
}>()

const name = ref('')
const operator = ref('')
const operatorOptions= ref<Option[]>([])
const currentValue = ref('')
const showRemoveCriteriaDialog = ref(false)
const valuePlaceholder = ref('')
const valueType = ref<InputType>()

const {t} = useI18n();

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
  chooseInputType(nameValue)
  chooseInputPlaceholder(nameValue)
  currentValue.value = value
}

const onCriteriaNameChanged = (val: string) => {
  const changedOptions = getOperatorOptions(val);
  const newCriteria = {...props.criteria, name: val, operator: changedOptions[0].value, value: ''}
  emit('updateCriteria', newCriteria as Criteria)
}

const onCriteriaFieldChanged = (field: string, val: string) => {
  emit('updateField', field, val)
}

const onShowRemoveCriteriaDialog = () => {
  showRemoveCriteriaDialog.value = true
}

const onCloseRemoveCriteriaDialog = () => {
  showRemoveCriteriaDialog.value = false
}

const onCriteriaRemove = () => {
  showRemoveCriteriaDialog.value = false
  emit('removeCriteria')
}

const chooseInputType = (type: string) => {
  if (CriteriaName.DATE === type) {
    valueType.value = 'date'
  } else if (CriteriaName.AMOUNT === type) {
    valueType.value = 'number'
  } else {
    valueType.value = 'text'
  }
}

const chooseInputPlaceholder = (type: string) => {
  if (CriteriaName.DATE === type) {
    valuePlaceholder.value = t('placeholders.date')
  } else if (CriteriaName.AMOUNT === type) {
    valuePlaceholder.value = t('placeholders.number')
  } else {
    valuePlaceholder.value = t('placeholders.value')
  }
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
  <BCol class="col-12 col-md-4 my-2">
    <BFormSelect
        v-model="name"
        :id="'criteria-name-' + id"
        :options="nameOptions"
        @change="(val) => onCriteriaNameChanged(val as string)"
    />
  </BCol>

  <BCol class="col-12 col-md-4 my-2">
    <BFormSelect
        v-model="operator"
        :id="'criteria-operator-' + id"
        :options="operatorOptions"
        @change="(val) => onCriteriaFieldChanged(CriteriaItemFieldType.OPERATOR, val as string)"
    />
  </BCol>

  <BCol class="col-12 col-md-4 my-2">
    <BRow>
      <BCol>
        <Input
            required
            :id="'criteria-value-' + id"
            :placeholder="valuePlaceholder"
            :type="valueType"
            :value="currentValue"
            @update-value="(val) => onCriteriaFieldChanged(CriteriaItemFieldType.VALUE, val)"
        />
      </BCol>

      <BCol class="col-auto px-0 me-3 mt-1 button-container">
        <ITypcnDelete
            class="hover-pointer"
            v-b-tooltip="t('tooltips.removeCriteria')"
            v-if="criteriaCount > 1"
            @click="onShowRemoveCriteriaDialog"
        />
      </BCol>
    </BRow>

    <SimpleDialog
        :button-cancel-text="t('buttons.cancel')"
        :button-ok-text="t('buttons.yes')"
        :id="`disclaimer-remove-criteria-${id}`"
        :message="t('messages.removeCriteria')"
        :show="showRemoveCriteriaDialog"
        @cancel="onCloseRemoveCriteriaDialog"
        @confirm="onCriteriaRemove"
    />
  </BCol>
</template>

<style scoped>
.button-container {
  min-width: 20px;
}
</style>