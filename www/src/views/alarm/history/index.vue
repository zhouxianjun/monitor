<template>
    <div>
        <Row class="margin-top-10">
            <Input v-model="table.query.name" placeholder="规则名称" clearable style="width: 200px" />
            <label class="margin-left-10">景区：</label>
            <Select v-model="table.query.spot" @on-change="doQuery" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">应用：</label>
            <Select v-model="table.query.app" @on-change="doQuery" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
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
import Common from '@/libs/common';
import dayjs from 'dayjs';
import { HistoryStatus } from '@/libs/dic';

export default {
    name: 'alarm-history-index',
    data () {
        return {
            HistoryStatus,
            table: {
                col: [{
                    title: '故障资源',
                    key: 'spotName',
                    render: (h, params) => Common.RENDER.JOIN(h, params)('spotName', 'appName')
                }, {
                    title: '发生时间',
                    key: 'createTime',
                    render: Common.RENDER.DATE
                }, {
                    title: '持续时间',
                    key: 'duration',
                    render: (h, params) => Common.RENDER.APPEND(h, params)('分钟')
                }, {
                    title: '规则名称',
                    key: 'ruleName'
                }, {
                    title: '状态',
                    key: 'status',
                    render: (h, params) => Common.RENDER.STATUS_DIC(h, params)({1: '#bbbec4', 2: 'red', 3: 'green'}, HistoryStatus)
                }],
                data: [],
                query: {
                    spot: null,
                    app: null,
                    name: null,
                    status: null,
                    start: '',
                    end: '',
                    pageNum: 1,
                    pageSize: 10
                }
            },
            spots: [],
            apps: [],
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
    watch: {
        'table.query.spot' (val) {
            this.apps = this.loadApp(val);
        }
    },
    async mounted () {
        this.doQuery();
        this.initSpots();
    },
    methods: {
        async doQuery () {
            if (this.range) {
                this.table.query.start = dayjs(this.range[0]).format('YYYY-MM-DD HH:mm');
                this.table.query.end = dayjs(this.range[1]).format('YYYY-MM-DD HH:mm');
            }
            let list = await this.fetch('/api/alarm/history/list', {params: this.table.query});
            list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
            list && (this.table.total = list.value.total);
            this.table.query.start = '';
            this.table.query.end = '';
        },
        async changePage (page) {
            this.table.query.pageNum = page;
            this.doQuery();
        },
        async changePageSize (size) {
            this.table.query.pageSize = size;
            this.doQuery();
        },
        async initSpots () {
            let list = await this.fetch('/api/spot/list');
            list && (this.spots = (!list.value || list.value.length === 0) ? [] : list.value);
        },
        async loadApp (spot) {
            let list = await this.fetch('/api/app/list', {params: {spot}});
            return list.value ? list.value : [];
        }
    }
};
</script>
