<template>
    <div>
        <Row>
            <Button type="primary" @click="addHttp" style="margin-right: 8px;">
                <Icon type="plus"></Icon>创建HTTP
            </Button>
            <Button type="primary" @click="addReported">
                <Icon type="plus"></Icon>创建上报
            </Button>
        </Row>
        <Row class="margin-top-10">
            <label>应用：</label>
            <app-select v-model="appCascade" style="margin-right: 5px;"></app-select>
            <Input v-model="table.query.name" placeholder="站点名称" clearable style="width: 200px" />
            <label class="margin-left-10">类型：</label>
            <Select v-model="table.query.type" @on-change="doQuery" style="width: 200px">
                <Option v-for="item in SiteType" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">状态：</label>
            <Select v-model="table.query.status" @on-change="doQuery" style="width: 200px">
                <Option v-for="item in Status" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">正在报警：</label>
            <i-switch v-model="table.query.alarm" size="large" />
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <Row class="margin-top-10">
            <Col>
            <Table :columns="table.col" :data="table.data"></Table>
            <div style="margin: 10px;overflow: hidden">
                <div style="float: right;">
                    <Page :total="table.total" :show-sizer="true" placement="top" @on-page-size-change="changePageSize" @on-change="changePage"></Page>
                </div>
            </div>
            </Col>
        </Row>
    </div>
</template>

<script>
import { SiteType, Status } from '@/libs/dic';
import AppSelect from '@/components/app-select.vue';
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';

export default {
    name: 'SiteMonitorList',
    components: { AppSelect },
    mixins: [ TableColRender, TableDataView, ModelView ],
    data () {
        return {
            Status,
            SiteType,
            appCascade: [],
            table: {
                col: [{
                    title: '景区',
                    key: 'spotName'
                }, {
                    title: '应用',
                    key: 'appName'
                }, {
                    title: '站点名称',
                    key: 'name'
                }, {
                    title: '报警状态',
                    key: 'lastStatus',
                    render: (h, params) => this.renderStatusDiy(h, params)('正常', '正在报警', status => !status || ![1, 2].includes(status)),
                    width: 140
                }, {
                    title: '频率',
                    key: 'interval',
                    render: (h, params) => this.renderAppend(h, params)('分钟')
                }, {
                    title: '状态',
                    key: 'status',
                    render: this.renderStatus
                }, {
                    title: '操作',
                    key: 'action',
                    width: 200,
                    align: 'center',
                    render: (h, params) => this.renderActions(h, params, [{
                        config: { loading: this.loadingBtn, style: {marginRight: '5px'} },
                        click: () => this.$router.push({
                            name: this.table.query.type === 1 ? 'SiteMonitorHttpEdit' : 'SiteMonitorReportedEdit',
                            params: params.row
                        }),
                        text: '修改'
                    }, {
                        type: 'delete',
                        click: () => this.remove(params.row)
                    }])
                }],
                query: {
                    spot: null,
                    app: null,
                    name: null,
                    status: null,
                    alarm: null,
                    type: 1
                }
            }
        };
    },
    methods: {
        async beforeQuery () {
            this.table.url = this.table.query.type === 1 ? '/api/monitor/http/list' : '/api/monitor/reported/list';
            this.table.query.spot = this.appCascade[0];
            this.table.query.app = this.appCascade[1];
        },
        async beforeRemove (item) {
            this.removeUrl = this.table.query.type === 1 ? '/api/monitor/http/remove' : '/api/monitor/reported/remove';
            this.$super(ModelView).beforeRemove(item);
        },
        addHttp () {
            this.$router.push({
                name: 'SiteMonitorHttpEdit',
                params: {}
            });
        },
        addReported () {
            this.$router.push({
                name: 'SiteMonitorReportedEdit',
                params: {}
            });
        },
        appSelectChange (index, val) {
            if (index === 0) {
                this.table.query.spot = val;
            }
        }
    }
};
</script>
