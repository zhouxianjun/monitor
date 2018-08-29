<template>
    <div>
        <Row class="margin-bottom-10">
            <Card>
                <p slot="title">选择应用</p>
                <app-select v-model="app" :spot="spot" :spot-all="false" :app-all="false" spot-label="景区：" app-label="应用：" :app-filter="filter" @change="handlerChange" @input="$emit('input', $event)"></app-select>
                <slot name="ext"></slot>
            </Card>
        </Row>
        <Row>
            <slot></slot>
        </Row>
    </div>
</template>
<script>
import AppSelect from '@/components/app-select.vue';
export default {
    name: 'AppBusinessService',
    components: { AppSelect },
    props: {
        path: String
    },
    data () {
        return {
            service: {},
            spot: Number(localStorage.getItem('business-spot-id')),
            app: Number(localStorage.getItem('business-app-id'))
        };
    },
    async created () {
        this.service = await this.initService();
    },
    mounted () {
        this.$emit('input', this.app);
    },
    methods: {
        async initService () {
            let result = await this.fetch('/api/business/list');
            return result.value ? result.value : {};
        },
        filter (data) {
            let types = this.service[this.path] || [];
            if (types.length) {
                return data.filter(app => app.type && types.includes(app.type));
            }
            return data;
        },
        handlerChange (index, val) {
            if (index === 0) {
                localStorage.setItem('business-spot-id', val);
            }
            if (index === 1) {
                localStorage.setItem('business-app-id', val);
            }
        }
    }
};
</script>
