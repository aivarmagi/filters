import { createApp } from 'vue'
import App from './App.vue'
import './assets/main.css'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css'
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'

createApp(App)
    .component('VueDatePicker', VueDatePicker)
    .mount('#app')
