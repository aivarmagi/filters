import {createI18n} from "vue-i18n"
import en from "@/locale/en.json"
import et from "@/locale/et.json"
import {Language} from "@/enums/Language";
import LocalStorageManager from "@/services/LocalStorageManager";
import {ref} from "vue";

const messages = {
  en: en,
  et: et
}

const LOCALE = 'locale'

const selectedLocale = ref<string | undefined>(LocalStorageManager.get<string>(LOCALE))

const selectDefaultLocale = (): string => {
    if (selectedLocale.value) {
        return selectedLocale.value
    } else {
        LocalStorageManager.save(LOCALE, Language.EN)
        selectedLocale.value = Language.EN as string

        return selectedLocale.value
    }
}

const i18n = createI18n({
    legacy: false,
    locale: selectDefaultLocale(),
    fallbackLocale: Language.ET,
    messages
});

export default i18n