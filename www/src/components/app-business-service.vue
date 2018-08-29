<template>
    <div>
        <Row class="margin-bottom-10">
            <Card>
                <p slot="title">选择应用</p>
                <app-select v-model="app" :spot-all="false" :app-all="false" spot-label="景区：" app-label="应用：" :app-filter="filter" @input="handler"></app-select>
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
            app: [Number(localStorage.getItem('business-spot-id')), Number(localStorage.getItem('business-app-id'))]
        };
    },
    async created () {
        this.service = await this.initService();
    },
    mounted () {
        this.$emit('input', this.app[1]);
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
        handler (value) {
            this.$emit('input', value[1]);
            localStorage.setItem('business-spot-id', value[0]);
            localStorage.setItem('business-app-id', value[1]);
        }
    }
};
</script>
