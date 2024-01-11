<script setup lang="ts">
import {ref, watch} from "vue";

const props = defineProps<{
  buttonCancelText?: string,
  buttonOkText?: string,
  id: string,
  message: string,
  show: boolean
}>()

const showDialog = ref(false)

const emit = defineEmits<{
  (e: 'cancel'): void
  (e: 'confirm'): void
}>()

const onCancelPressed = () => {
  showDialog.value = false;
}

const onCancelled = () => {
  emit('cancel')
}

const onConfirmed = () => {
  emit('onConfirm')
}

watch(() => props.show, (newVal) => {
  showDialog.value = newVal;
})
</script>

<template>
  <BModal
      centered
      hide-header
      lazy
      v-model="showDialog"
      :cancel-title="props.buttonCancelText || 'Cancel'"
      :id="props.id"
      :ok-title="props.buttonOkText || 'OK'"
      @cancel="onCancelPressed"
      @hidden="onCancelled"
      @ok="onConfirmed"
  >
    {{ props.message }}
  </BModal>
</template>

<style scoped>

</style>