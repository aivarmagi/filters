import {useToast} from "bootstrap-vue-next";

const {show} = useToast();

export default {
    showInfo: (message: string) => {
        show(message, { value: 3000, interval: 100, progressProps: { variant: 'secondary' } })
    }
}