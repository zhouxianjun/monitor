<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
            <Icon type="plus"></Icon>创建规则</Button>
        </Row>
        <Row class="margin-top-10">
            <label class="margin-left-10">应用：</label>
            <app-select v-model="table.query.app" style="margin-right: 5px;" @change="appSelectChange"></app-select>
            <Input v-model="table.query.name" placeholder="规则名称" clearable style="width: 200px" />
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
import { Status } from '@/libs/dic';
import AppSelect from '@/components/app-select.vue';
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';

export default {
    name: 'AlarmRuleIndex',
    components: { AppSelect },
    mixins: [ TableColRender, TableDataView, ModelView ],
    data () {
        return {
            Status,
            table: {
                col: [{
                    title: '景区',
                    key: 'spotName'
                }, {
                    title: '应用',
                    key: 'appName'
                }, {
                    title: '规则名称',
                    key: 'name'
                }, {
                    title: '报警状态',
                    key: 'lastStatus',
                    render: (h, params) => this.renderStatusDiy(h, params)('正常', '正在报警', status => !status || ![1, 2].includes(status)),
                    width: 140
                }, {
                    title: '监控项',
                    key: 'metric'
                }, {
                    title: '资源标识',
                    key: 'resource',
                    tooltip: true
                }, {
                    title: '频率',
                    key: 'interval',
                    render: (h, params) => this.renderAppend(h, params)('分钟')
                }, {
                    title: '沉默',
                    key: 'silenceInterval',
                    render: (h, params) => this.renderAppend(h, params)('分钟')
                }, {
                    title: '次数',
                    key: 'count',
                    render: (h, params) => this.renderAppend(h, params)('次')
                }, {
                    title: '状态',
                    key: 'status',
                    render: this.renderStatus
                }, {
                    title: '创建时间',
                    key: 'createTime',
                    render: this.renderDate,
                    width: 148
                }, {
                    title: '操作',
                    key: 'action',
                    width: 200,
                    align: 'center',
                    render: (h, params) => {
                        return h('div', [
                            h('Button', {
                                props: {
                                    type: 'primary',
                                    size: 'small',
                                    loading: this.loadingBtn
                                },
                                style: {
                                    marginRight: '5px'
                                },
                                on: {
                                    click: async () => {
                                        this.$router.push({
                                            name: 'alarm-rule-edit',
                                            params: params.row
                                        });
                                    }
                                }
                            }, '修改'),
                            this.renderBtnPop(h, '您确定要删除这条数据吗?', '删除', 'error', () => this.remove(params.row))
                        ]);
                    }
                }],
                url: '/api/alarm/rule/list',
                query: {
                    spot: null,
                    app: null,
                    name: null,
                    status: null,
                    alarm: null
                }
            },
            removeUrl: '/api/alarm/rule/remove'
        };
    },
    methods: {
        add () {
            this.$router.push({
                name: 'alarm-rule-edit',
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
