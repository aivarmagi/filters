<script setup lang="ts">
import {ref, watch} from "vue";
import type {InputType} from "bootstrap-vue-next";
import {useI18n} from "vue-i18n";
import {Language, LanguageCode} from "@/enums/Language";

const props = defineProps<{
  className?: string,
  id: string,
  label?: string,
  labelCols?: number,
  placeholder: string,
  required: boolean,
  type?: InputType,
  value: string,
}>()

const emit = defineEmits<(e: 'updateValue', val: string) => void>()

const {locale, t} = useI18n();

const internalType = ref<InputType>()
const internalValue = ref(props.value)
const getDatepickerLocale = () => {
  if (Language.ET === locale.value as Language) {
    return LanguageCode.ET_EE;
  } else {
    return LanguageCode.EN_US;
  }
}

const datepickerLocale = ref(getDatepickerLocale())

const showDatepicker = () => internalType.value === 'date';

const onDateChanged = (val: Date) => emit('updateValue', val.toLocaleDateString())

watch(() => props.type, (newVal) => {
  internalType.value = newVal;
})

watch(() => props.value, (newVal) => {
  internalValue.value = newVal;
})
</script>

<template>
  <BFormGroup
      label-class="text-nowrap"
      :id="'text-input-group-' + id"
      :label="label ? label : ''"
      :label-cols="label ? labelCols || 2 : undefined"
      :label-for="'text-input-' + id"
  >
    <BRow>
      <BCol :class="{'col-md-6' : label, className}" >
        <BFormInput
            v-if="!showDatepicker()"
            v-model.trim="internalValue"
            :id="`${internalType}-input-${id}`"
            :placeholder="placeholder"
            :type="internalType"
            @input="() => emit('updateValue', internalValue)"
        />

        <VueDatePicker
            auto-apply
            teleport="body"
            v-if="showDatepicker()"
            v-model="internalValue"
            :clearable="false"
            :locale="datepickerLocale"
            :enable-time-picker="false"
            :id="`date-input-${id}`"
            :placeholder="placeholder"
            :format="'dd.MM.yyyy'"
            @update:model-value="onDateChanged"
        />

<!--        todo: form validation-->
<!--            :state="!required ? null : internalValue?.length > 0 ? null : false"-->
        <BFormInvalidFeedback>{{t('feedbacks.valueRequired')}}</BFormInvalidFeedback>
      </BCol>
    </BRow>
  </BFormGroup>
</template>