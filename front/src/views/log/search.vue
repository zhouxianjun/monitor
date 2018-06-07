<template>
    <div>
        <Row>
            <label class="margin-left-10">范围：</label>
            <DatePicker v-model="range" type="datetimerange" :options="dateRangeOpt" format="yyyy-MM-dd HH:mm" placeholder="请选择查询范围" style="width: 250px"></DatePicker>
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <Row class="margin-top-10">
            <Collapse>
                <Panel :name="typeItem.key" v-for="typeItem in types" :key="typeItem.key">
                    <label>{{typeItem.description}}</label>
                    <div slot="content">
                        <Row v-for="(queryItem, index) in query[typeItem.key]" :key="index" class="margin-bottom-10">
                            <Select v-model="queryItem.type" style="width: 100px">
                                <Option v-for="item in ESFilterType" :value="item.id" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                            <AutoComplete v-model="queryItem.key" :data="fields" placeholder="字段" clearable style="width: 200px"/>
                            <span v-if="queryItem.type === 'range'">
                                <label>&gt;=</label>
                                <InputNumber v-model="queryItem.gte" style="width: 90px"></InputNumber>
                                <label>&lt;=</label>
                                <InputNumber v-model="queryItem.lte" style="width: 90px"></InputNumber>
                            </span>
                            <span v-if="queryItem.type === 'match'">
                                ==
                                <Input v-model="queryItem.value" placeholder="值" clearable style="width: 200px"/>
                            </span>
                            <Button type="ghost" @click="remove(typeItem.key, index)">删除一项</Button>
                        </Row>
                        <Row>
                            <Col span="8">
                                <Button type="dashed" long @click="add(typeItem.key)" icon="plus-round">新增一项</Button>
                            </Col>
                        </Row>
                    </div>
                </Panel>
            </Collapse>
        </Row>
        <GridKeepaliveTable ref="table" :columns="table.col" :data="table.data" class="margin-top-10">
            <Detail v-highlight="{keyword: highlight}" slot="expand" slot-scope="{record}" :trace_id="record.traceId" :load="true"/>
            <Icon slot="col-expand" slot-scope="{record}" :type="record.expand ? 'ios-arrow-down' : 'ios-arrow-right'" size="14"></Icon>
        </GridKeepaliveTable>
        <Row>
            <Button v-show="table.size <= table.data.length && table.data.length > 0" type="text" long @click="pull">加载更多</Button>
        </Row>
    </div>
</template>

<script>
    import dayjs from 'dayjs';
    import {ESFilterType} from '../../libs/dic';
    import Common from '../../libs/common';
    import Detail from './detail';
    import GridKeepaliveTable from '../../components/grid-keepalive-table';
    import highlight from '../../components/highlight-directive';

    export default {
        name: "log-search",
        components: {
            GridKeepaliveTable,
            Detail
        },
        directives: {
            highlight
        },
        data() {
            return {
                fields: ['cusmsg', 'trace_id', 'level'],
                highlight: [],
                ESFilterType,
                types: [{
                    description: 'AND -- 多个查询条件的完全匹配',
                    key: 'must'
                }, {
                    description: 'NOT -- 多个查询条件的相反匹配',
                    key: 'must_not'
                }, {
                    description: 'OR -- 至少有一个查询条件匹配',
                    key: 'should'
                }],
                query: {
                    sort: [{
                        key: '@timestamp',
                        type: 'desc'
                    }],
                    must: [],
                    must_not: [],
                    should: [],
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
                        key: '@timestamp',
                        render: (h, params) => Common.RENDER.DATE(h, params, (val) => new Date(val))
                    }],
                    data: [],
                    size: 0,
                    pageSize: 15
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
            }
        },
        mounted() {
            this.doQuery();
        },
        methods: {
            generate() {
                return {
                    type: 'match',
                    gte: null,
                    lte: null,
                    key: null,
                    value: null
                };
            },
            toFilterType(item) {
                const value = {};
                switch (item.type) {
                    case 'match':
                        value[item.type] = {};
                        value[item.type][item.key] = item.value;
                        this.highlight.push(item.value);
                        break;
                    case 'range':
                        value[item.type] = {};
                        value[item.type][item.key] = {
                            gte: item.gte ? typeof item.gte === 'object' ? dayjs(item.gte).valueOf() : item.gte : null,
                            lte: item.lte ? typeof item.lte === 'object' ? dayjs(item.lte).valueOf() : item.lte : null
                        };
                        this.highlight.concat([value[item.type][item.key].gte, value[item.type][item.key].lte]);
                        break;
                    default:
                        break;
                }
                return value;
            },
            add(key) {
                this.query[key].push(this.generate());
            },
            remove(key, index) {
                this.query[key].splice(index, 1);
            },
            async doQuery() {
                this.table.size = 0;
                this.table.data = [];
                this.highlight = [];
                await this.pull();
            },
            async pull() {
                this.table.size += this.table.pageSize;
                const params = {
                    size: 0,
                    query: {
                        bool: {
                            filter: [{
                                bool: {}
                            }, this.toFilterType({
                                type: 'range',
                                key: '@timestamp',
                                gte: this.range[0],
                                lte: this.range[1]
                            })]
                        }
                    },
                    aggs: {
                        trace: {
                            terms: {
                                field: 'trace_id.keyword',
                                size: this.table.size,
                                order: { "_term": "desc" }
                            },
                            aggs: {
                                one: {
                                    top_hits: {
                                        size: 1,
                                        sort: {
                                            '@timestamp': 'desc'
                                        },
                                        _source: ['source', '@timestamp', 'host']
                                    }
                                }
                            }
                        }
                    }
                };

                Reflect.ownKeys(this.query)
                    .filter(key => Array.isArray(this.query[key]) && this.query[key].length && key !== 'sort')
                    .map(key => Object.assign({
                        key,
                        value: this.query[key].filter(item => !!item.key).map(item => this.toFilterType(item))
                    }))
                    .forEach(item => params.query.bool.filter[0].bool[item.key] = item.value);

                let result = await this.fetch('/api/log/search', {method: 'post', data: params});
                if (result) {
                    let data = JSON.parse(result.value);
                    let lastDom = this.$refs.table.getRow(this.table.data.length - 1);
                    this.table.data = this.table.data.concat(data['aggregations']['trace']['buckets']
                        .slice(this.table.size - this.table.pageSize)
                        .map(trace => Object.assign({
                            traceId: trace.key,
                            expand: false,
                            load: false
                        }, trace['one']['hits']['hits'][0]['_source'])));
                    if (lastDom) {
                        lastDom.classList.add('animated', 'infinite', 'flash', 'bg-color-yellow');
                        setTimeout(() => lastDom.classList.remove('animated', 'infinite', 'flash', 'bg-color-yellow'), 2000);
                    }
                }
            }
        }
    }
</script>

<style lang="less">
    @import '../../styles/common.less';
    @import '~animate.css';
</style>