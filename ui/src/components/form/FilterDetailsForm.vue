<script setup lang="ts">
import CriteriaGroup from "@/components/form/CriteriaGroup.vue";
import type {Option} from "@/models/Option";
import RadioGroup from "@/components/form/RadioGroup.vue";
import type {Criteria} from "@/models/Criteria";
import {useI18n} from "vue-i18n";
import {type Filter, FilterState} from "@/models/Filter";

defineProps<{
  currentFilter: Filter,
  filterSaving: boolean,
  resettingFilter?: boolean,
  selectionTypeOptions: (string | number | Record<string, unknown>)[],
}>()

const emit = defineEmits<{
  (e: 'addCriteria'): void
  (e: 'close'): void
  (e: 'removeCriteria', val: number): void
  (e: 'resetFilter', event: any): void
  (e: 'saveFilter', event: any): void
  (e: 'updateCriteria', val: Criteria, index: number): void
  (e: 'updateField', field: string, value: string, index: number): void
  (e: 'updateName', val: string): void
  (e: 'updateSelection', val: string): void
}>()

const {t} = useI18n()
</script>

<template>
  <BForm
      @reset="(event: any) => emit('resetFilter', event)"
      @submit="(event: any) => emit('saveFilter', event)"
  >
    <BRow>
      <BCol class="text-start mt-3 mb-2">
        <Input
            class-name="'pb-0'"
            required
            :id="`${currentFilter.id}`"
            :label="t('labels.name')"
            :placeholder="t('placeholders.name')"
            :value="currentFilter.name"
            @update-value="(value: string) => emit('updateName', value)"
        />
      </BCol>
    </BRow>

    <BRow>
      <BCol class="text-start">
        <CriteriaGroup
            v-if="currentFilter.criterias"
            :criterias="currentFilter.criterias"
            :id="`${currentFilter.id}`"
            :label="t('labels.criteria')"
            @remove-criteria="(index) => emit('removeCriteria', index)"
            @update-criteria="(criteria, index) => emit('updateCriteria', criteria, index)"
            @update-field="(field, value, index) => emit('updateField', field, value, index)"
        />
      </BCol>
    </BRow>

    <BRow>
      <BCol class="text-end mt-3">
        <BButton
            variant="outline-secondary"
            @click="() => emit('addCriteria')"
        >
          {{ t('buttons.addCriteria')}}
        </BButton>
      </BCol>
    </BRow>

    <BRow>
      <BCol class="text-start mt-3">
        <RadioGroup
            v-if="currentFilter.selection"
            :id="`${currentFilter.id}`"
            :options="selectionTypeOptions"
            :label="t('labels.selection')"
            :value="currentFilter.selection"
            @update-value="(value) => emit('updateSelection', value)"
        />
      </BCol>
    </BRow>

    <BRow class="mt-3">
      <BCol class="text-end">
        <BButton
            class="me-2"
            type="reset"
            v-if="currentFilter.id !== FilterState.NEW"
            variant="outline-secondary"
            :loading="resettingFilter"
            :loading-text="t('buttons.resetting')"
        >
          {{ t('buttons.reset')}}
        </BButton>

        <BButton
            class="me-2"
            variant="outline-secondary"
            @click="() => emit('close')"
        >
          {{ t('buttons.close')}}
        </BButton>

        <BButton
            type="submit"
            :loading="filterSaving"
            :loading-text="t('buttons.saving')"
        >
          {{ t('buttons.saveChanges')}}
        </BButton>
      </BCol>
    </BRow>
  </BForm>
</template>

<style scoped>

</style>