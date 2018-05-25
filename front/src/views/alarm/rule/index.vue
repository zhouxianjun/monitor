<template>
    <div>
        <Row>
            <Button type="primary" @click="add"><Icon type="plus"></Icon>创建规则</Button>
        </Row>
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
                <Option v-for="item in Status" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">正在报警：</label>
            <i-switch v-model="table.query.alarm" size="large"/>
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

        <Modal v-model="removeModal" width="360" @on-cancel="cancel">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="information-circled"></Icon>
                <span>删除确认</span>
            </p>
            <div style="text-align:center">
                <p>确定删除 {{removeItem ? removeItem.name : ''}} 吗?，删除后将无法恢复。</p>
                <p>是否继续删除？</p>
            </div>
            <div slot="footer">
                <Button type="error" size="large" @click="remove">删除</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import Common from '../../../libs/common';
    import {Status} from '../../../libs/dic';
    export default {
        name: "alarm-rule-index",
        data() {
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
                        render(h, params) {
                            let lastStatus = params.row[params.column.key] || 0;
                            return Common.RENDER.STATUS_DIY(h, params)('正常', `正在报警`, !lastStatus || ![1, 2].includes(lastStatus));
                        },
                        width: 140
                    }, {
                        title: '监控项',
                        key: 'metric'
                    }, {
                        title: '资源标识',
                        key: 'resource',
                        render: Common.RENDER.POPTIP
                    }, {
                        title: '频率',
                        key: 'interval',
                        render: (h, params) => Common.RENDER.APPEND(h, params)('分钟')
                    }, {
                        title: '沉默',
                        key: 'silenceInterval',
                        render: (h, params) => Common.RENDER.APPEND(h, params)('分钟')
                    }, {
                        title: '次数',
                        key: 'count',
                        render: (h, params) => Common.RENDER.APPEND(h, params)('次')
                    }, {
                        title: '状态',
                        key: 'status',
                        render: Common.RENDER.STATUS
                    }, {
                        title: '创建时间',
                        key: 'createTime',
                        render: Common.RENDER.DATE,
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
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        size: 'small',
                                        loading: this.loadingBtn
                                    },
                                    on: {
                                        click: async () => {
                                            this.removeModal = true;
                                            this.removeItem = params.row;
                                            this.loadingBtn = true;
                                        }
                                    }
                                }, '删除')
                            ]);
                        }
                    }],
                    data: [],
                    query: {
                        spot: null,
                        app: null,
                        name: null,
                        status: null,
                        alarm: null,
                        pageNum: 1,
                        pageSize: 10
                    }
                },
                spots: [],
                apps: [],
                removeModal: false,
                removeItem: null,
                loadingBtn: false
            }
        },
        async mounted() {
            this.doQuery();
            this.initSpots();
        },
        watch: {
            async 'table.query.spot'(val) {
                this.apps = await this.loadApp(val);
            }
        },
        methods: {
            async doQuery() {
                let list = await this.fetch('/api/alarm/rule/list', {params: this.table.query});
                list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
                list && (this.table.total = list.value.total);
                this.loadingBtn = false;
            },
            async changePage(page) {
                this.table.query.pageNum = page;
                this.doQuery();
            },
            async changePageSize(size) {
                this.table.query.pageSize = size;
                this.doQuery();
            },
            async initSpots() {
                let list = await this.fetch('/api/spot/list');
                list && (this.spots = (!list.value || list.value.length === 0) ? [] : list.value);
            },
            async loadApp(spot) {
                let list = await this.fetch('/api/app/list', {params: {spot}});
                return list.value ? list.value : [];
            },
            add() {
                this.$router.push({
                    name: 'alarm-rule-edit',
                    params: {}
                });
            },
            async remove() {
                if (!this.removeItem) return;
                let success = await this.fetch('/api/alarm/rule/remove', {method: 'post', data: {id: this.removeItem.id}});
                if (success === false) {
                    this.resetLoadingBtn();
                    return;
                }
                this.removeItem = null;
                this.removeModal = false;
                setTimeout(() => this.doQuery(), 500);
            },
            cancel() {
                this.loadingBtn = false;
            },
            resetLoadingBtn() {
                this.loadingBtn = false;
                this.$nextTick(() => this.loadingBtn = true);
            }
        }
    }
</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>