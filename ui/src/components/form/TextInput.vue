<script setup lang="ts">
import {ref} from "vue";

const props = defineProps<{
  id: number,
  label: string,
  placeholder: string,
  required: boolean,
  value: string,
}>()

const emit = defineEmits<{
  (e: 'updateValue', val: string): void
}>()

const internalValue = ref(props.value)
</script>

<template>
  <BFormGroup
      :id="'input-name-group-' + id"
      :label="label"
      :label-for="'input-name-' + id"
  >
    <BFormInput
        v-model.trim="internalValue"
        :id="'input-name-' + id"
        :placeholder="placeholder"
        :state="!required ? null : internalValue.length > 0 ? null : false"
        @input="() => emit('updateValue', internalValue)"
    />
    <BFormInvalidFeedback>Value is required</BFormInvalidFeedback>
  </BFormGroup>
</template>

<style scoped>

</style>