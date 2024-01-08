<script setup lang="ts">
import {ref, watch} from "vue";

const props = defineProps<{
  className?: string,
  id: string,
  label?: string,
  labelCols?: number,
  placeholder: string,
  required: boolean,
  value: string,
}>()

const emit = defineEmits<{
  (e: 'updateValue', val: string): void
}>()

const internalValue = ref(props.value)

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
      <BCol :class="{'col-md-6' : label, className}">
        <BFormInput
            v-model.trim="internalValue"
            :id="'text-input-' + id"
            :placeholder="placeholder"
            @input="() => emit('updateValue', internalValue)"
        />
<!--        todo: form validation-->
<!--            :state="!required ? null : internalValue?.length > 0 ? null : false"-->
        <BFormInvalidFeedback>Value is required</BFormInvalidFeedback>
      </BCol>
    </BRow>
  </BFormGroup>
</template>

<style scoped>

</style>