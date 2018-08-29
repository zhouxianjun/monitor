<template>
    <iview-cascade-select :selects="selects" v-model="app" @input="handler"></iview-cascade-select>
</template>
<script>
import IviewCascadeSelect from 'iview-cascade-select';
export default {
    name: 'AppSelect',
    components: { IviewCascadeSelect },
    props: {
        value: [Array, Number],
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
        },
        single: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            app: this.single ? [] : this.value,
            selects: [{
                style: {
                    width: '150px'
                },
                label: this.spotLabel,
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
        handler (value) {
            this.$emit('input', this.single ? value[1] : value);
        }
    }
};
</script>
