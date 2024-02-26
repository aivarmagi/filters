<script setup lang="ts">
import {ref} from "vue";

const props = defineProps<{
  id: string,
  label: string,
  labelCols?: number,
  options: (string | number | Record<string, unknown>)[],
  value: string,
}>()

const emit = defineEmits<(e: 'updateValue', val: string) => void>()

const internalValue = ref(props.value)
</script>

<template>
  <BFormGroup
      label-class="text-nowrap"
      :id="'radio-group-' + id"
      :label="label"
      :label-cols="labelCols || 2"
      :label-for="'radio-' + id"
  >
    <BCol class="col-md-6">
      <BFormRadioGroup
          class="mt-2"
          name="selection-type"
          v-model="internalValue"
          :id="'radio-' + id"
          :options="options"
          @input="() => emit('updateValue', internalValue)"
      />
    </BCol>
  </BFormGroup>
</template>