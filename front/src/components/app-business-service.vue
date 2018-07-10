<template>
    <div>
        <Row class="margin-bottom-10">
            <Card>
                <p slot="title">选择应用</p>
                <label>景区：</label>
                <Select v-model="spotId" :clearable="true" style="width: 100px;margin-right: 20px;">
                    <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
                <label>应用：</label>
                <Select v-model="appId" :clearable="true" @on-change="select" style="width: 120px">
                    <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
                <slot name="ext"></slot>
            </Card>
        </Row>
        <Row>
            <slot></slot>
        </Row>
    </div>
</template>
<script>
export default {
    name: 'app-business-service',
    props: {
        path: String
    },
    data () {
        return {
            spotId: null,
            appId: null,
            spots: [],
            apps: [],
            service: {}
        };
    },
    async mounted () {
        await this.initSpots();
        this.service = await this.initService();
    },
    watch: {
        async spotId (val) {
            if (!val) {
                this.appId = null;
                this.apps = [];
                this.select();
                return;
            }
            localStorage.setItem('business-spot-id', val);
            let apps = await this.loadApp(val);
            let appId = localStorage.getItem('business-app-id');
            let types = this.service[this.path] || [];
            if (types.length) {
                apps = apps.filter(app => app.type && types.includes(app.type));
            }

            this.apps = apps;
            if (!isNaN(appId) && apps.some(a => a.id === parseInt(appId))) {
                this.appId = parseInt(appId);
                this.select();
            }
        }
    },
    methods: {
        async initSpots () {
            let list = await this.fetch('/api/spot/list');
            this.spots = list ? list.value || [] : [];
            let spotId = localStorage.getItem('business-spot-id');
            this.spotId = isNaN(spotId) ? null : parseInt(spotId);
        },
        async loadApp (spot) {
            let list = await this.fetch('/api/app/list', {params: {spot}});
            return list.value ? list.value : [];
        },
        async initService () {
            let result = await this.fetch('/api/business/list');
            return result.value ? result.value : {};
        },
        select () {
            localStorage.setItem('business-app-id', this.appId);
            this.$emit('on-change-app', this.appId);
        }
    }
};
</script>
