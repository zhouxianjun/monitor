<template>
    <iview-cascade-select :selects="selects" v-model="app" @input="$emit('input', $event)" @change="handlerChange"></iview-cascade-select>
</template>
<script>
import IviewCascadeSelect from 'iview-cascade-select';
export default {
    name: 'AppSelect',
    components: { IviewCascadeSelect },
    props: {
        value: Number,
        spot: Number,
        spotLabel: String,
        appLabel: String,
        spotFilter: Function,
        appFilter: Function,
        spotAll: {
            type: Boolean,
            default: true
        },
        appAll: {
            type: Boolean,
            default: true
        }
    },
    data () {
        return {
            app: this.value,
            selects: [{
                style: {
                    width: '150px'
                },
                label: this.spotLabel,
                defaultValue: this.spot,
                filter: this.spotFilter,
                all: this.spotAll,
                options: async p => {
                    let list = await this.fetch('/api/spot/list');
                    return list && list.value ? list.value : [];
                }
            }, {
                style: {
                    width: '150px'
                },
                label: this.appLabel,
                defaultValue: this.value,
                filter: this.appFilter,
                all: this.appAll,
                options: async p => {
                    let list = await this.fetch('/api/app/list', {params: {spot: p}});
                    return list && list.value ? list.value : [];
                }
            }]
        };
    },
    methods: {
        handlerChange (index, val) {
            this.$emit('change', index, val);
        }
    }
};
</script>
