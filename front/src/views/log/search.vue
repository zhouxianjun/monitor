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
                            <Input v-model="queryItem.key" placeholder="字段" clearable style="width: 200px"/>
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
                <Panel name="sort">
                    <label>排序</label>
                    <div slot="content">
                        <Row v-for="(queryItem, index) in query.sort" :key="index" class="margin-bottom-10">
                            <Input v-model="queryItem.key" placeholder="字段" clearable style="width: 200px"/>
                            <Select v-model="queryItem.type" style="width: 100px">
                                <Option v-for="item in SortType" :value="item.id" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                            <Button type="ghost" @click="remove('sort', index)">删除一项</Button>
                        </Row>
                        <Row>
                            <Col span="8">
                                <Button type="dashed" long @click="add('sort')" icon="plus-round">新增一项</Button>
                            </Col>
                        </Row>
                    </div>
                </Panel>
            </Collapse>
        </Row>
        <Row class="margin-top-10">
            <Col>
                <Table :columns="table.col" :data="table.data"></Table>
            </Col>
        </Row>
    </div>
</template>

<script>
    import dayjs from 'dayjs';
    import {ESFilterType, SortType} from '../../libs/dic';
    import Detail from './detail';

    export default {
        name: "log-search",
        components: {
            Detail
        },
        data() {
            return {
                ESFilterType,
                SortType,
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
                        type: 'expand',
                        width: 50,
                        render: (h, params) => {
                            return h('keep-alive', [
                                h(Detail, {
                                    props: {
                                        trace_id: params.row['traceId']
                                    }
                                })
                            ]);
                        }
                    }, {
                        title: 'TRACEID',
                        key: 'traceId'
                    }, {
                        title: '文件路径',
                        key: 'source'
                    }, {
                        title: '主机',
                        key: 'host'
                    }, {
                        title: '记录时间',
                        key: '@timestamp'
                    }],
                    data: [],
                    expanded: {}
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
                        break;
                    case 'range':
                        value[item.type] = {};
                        value[item.type][item.key] = {
                            gte: item.gte ? typeof item.gte === 'object' ? dayjs(item.gte).valueOf() : item.gte : null,
                            lte: item.lte ? typeof item.lte === 'object' ? dayjs(item.lte).valueOf() : item.lte : null
                        };
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
                                size: 200,
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
                    this.table.data = data['aggregations']['trace']['buckets']
                        .map(trace => Object.assign({
                            traceId: trace.key
                        }, trace['one']['hits']['hits'][0]['_source']));
                }
            }
        }
    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>