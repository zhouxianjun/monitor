<template>
    <div>
        <Row class="margin-top-10">
            <label>应用：</label>
            <app-select v-model="appCascade" style="margin-right: 5px;"></app-select>
            <Input v-model="table.query.name" placeholder="规则名称" clearable style="width: 200px" />
            <label class="margin-left-10">状态：</label>
            <Select v-model="table.query.status" @on-change="doQuery" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in HistoryStatus" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">范围：</label>
            <DatePicker v-model="range" type="datetimerange" :options="dateRangeOpt" format="yyyy-MM-dd HH:mm" placeholder="请选择查询范围" style="width: 250px"></DatePicker>
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
import dayjs from 'dayjs';
import { HistoryStatus } from '@/libs/dic';
import AppSelect from '@/components/app-select.vue';
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';

export default {
    name: 'AlarmHistory',
    components: { AppSelect },
    mixins: [ TableColRender, TableDataView ],
    data () {
        return {
            appCascade: [],
            HistoryStatus,
            table: {
                col: [{
                    title: '故障资源',
                    key: 'spotName',
                    render: (h, params) => this.renderJoin(h, params)('spotName', 'appName')
                }, {
                    title: '发生时间',
                    key: 'createTime',
                    render: this.renderDate
                }, {
                    title: '持续时间',
                    key: 'duration',
                    render: (h, params) => this.renderAppend(h, params)('分钟')
                }, {
                    title: '规则名称',
                    key: 'ruleName'
                }, {
                    title: '状态',
                    key: 'status',
                    render: (h, params) => this.renderStatusDic(h, params)({1: '#bbbec4', 2: 'red', 3: 'green'}, HistoryStatus)
                }],
                url: '/api/alarm/history/list',
                query: {
                    spot: null,
                    app: null,
                    name: null,
                    status: null,
                    start: '',
                    end: ''
                }
            },
            range: [dayjs().subtract(1, 'day').toDate(), dayjs().set('second', 0).toDate()],
            dateRangeOpt: {
                shortcuts: [{
                    text: '1 天',
                    value: () => [dayjs().subtract(1, 'day').toDate(), dayjs().set('second', 0).toDate()]
                }, {
                    text: '3 天',
                    value: () => [dayjs().subtract(3, 'day').toDate(), dayjs().set('second', 0).toDate()]
                }, {
                    text: '7 天',
                    value: () => [dayjs().subtract(7, 'day').toDate(), dayjs().set('second', 0).toDate()]
                }]
            }
        };
    },
    methods: {
        async beforeQuery () {
            if (this.range) {
                this.table.query.start = dayjs(this.range[0]).format('YYYY-MM-DD HH:mm');
                this.table.query.end = dayjs(this.range[1]).format('YYYY-MM-DD HH:mm');
            }
            this.table.query.spot = this.appCascade[0];
            this.table.query.app = this.appCascade[1];
        },
        async afterQuery () {
            this.table.query.start = '';
            this.table.query.end = '';
        }
    }
};
</script>
