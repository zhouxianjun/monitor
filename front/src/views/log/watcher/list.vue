<template>
    <div>
        <Row>
            <Input class="margin-left-10" v-model="query.keyword" placeholder="keyword" clearable style="width: 200px" />
            <label class="margin-left-10">范围：</label>
            <DatePicker v-model="range" type="datetimerange" :options="dateRangeOpt" format="yyyy-MM-dd HH:mm" placeholder="请选择查询范围" style="width: 250px"></DatePicker>
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <GridKeepaliveTable :columns="table.col" :data="table.data" class="margin-top-10">
            <Record v-highlight="{keyword: query.keyword}" slot="expand" slot-scope="{record}" :traceId="record.traceId" :load="true"/>
            <Icon slot="col-expand" slot-scope="{record}" :type="record.expand ? 'ios-arrow-down' : 'ios-arrow-right'" size="14"></Icon>
        </GridKeepaliveTable>
        <Row>
            <Button v-show="table.size <= table.data.length && table.data.length > 0" type="text" long @click="pull">加载更多</Button>
        </Row>
    </div>
</template>

<script>
    import dayjs from 'dayjs';
    import Common from '../../../libs/common';
    import Record from './record';
    import GridKeepaliveTable from '../../../components/grid-keepalive-table';
    import highlight from '../../../components/highlight-directive';

    export default {
        name: "log-watcher-list",
        components: {
            GridKeepaliveTable,
            Record
        },
        directives: {
            highlight
        },
        data() {
            return {
                query: {
                    watchId: 0,
                    keyword: null,
                    pageNum: 0,
                    pageSize: 50
                },
                table: {
                    col: [{
                        type: 'index',
                        width: 50
                    }, {
                        type: 'expand',
                        width: 50
                    }, {
                        title: 'TRACEID',
                        key: 'traceId'
                    }, {
                        title: '文件路径',
                        key: 'source',
                        ellipsis: true
                    }, {
                        title: '主机',
                        key: 'host'
                    }, {
                        title: '记录时间',
                        key: 'logTime',
                        render: Common.RENDER.DATE
                    }],
                    data: [],
                    total: 0
                },
                range: [dayjs().subtract(1, 'day').toDate(), new Date()],
                dateRangeOpt: {
                    shortcuts: [{
                        text: '1 天',
                        value: () => [dayjs().subtract(1, 'day').toDate(), new Date()]
                    }, {
                        text: '3 天',
                        value: () => [dayjs().subtract(3, 'day').toDate(), new Date()]
                    }, {
                        text: '7 天',
                        value: () => [dayjs().subtract(7, 'day').toDate(), new Date()]
                    }]
                }
            }
        },
        mounted() {
            this.query.watchId = this.$route.query['watchId'];
            this.doQuery();
        },
        methods: {
            async doQuery() {
                this.query.pageNum = 0;
                this.table.data = [];
                await this.pull();
            },
            async pull() {
                this.query.pageNum++;
                let result = await this.fetch('/api/log/watcher/log/list', {params: this.query});
                if (result && result.value.size) {
                    this.table.data = this.table.data.concat(result.value.list);
                    this.table.total = result.value.total;
                }
            }
        }
    }
</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>