<template>
    <div>
        <Row>
            <Button type="primary" @click="addHttp"><Icon type="plus"></Icon>创建HTTP</Button>
            <Button type="primary" @click="addReported"><Icon type="plus"></Icon>创建上报</Button>
        </Row>
        <Row class="margin-top-10">
            <Input v-model="table.query.name" placeholder="站点名称" clearable style="width: 200px" />
            <label class="margin-left-10">景区：</label>
            <Select v-model="table.query.spot" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">应用：</label>
            <Select v-model="table.query.app" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">类型：</label>
            <Select v-model="table.query.type" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in SiteType" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">状态：</label>
            <Select v-model="table.query.status" style="width: 200px">
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
    </div>
</template>

<script>
    import Common from '../../libs/common';
    import {Status, SiteType} from '../../libs/dic';
    export default {
        name: "site-monitor-index",
        data() {
            return {
                Status,
                SiteType,
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
                        render(h, params) {
                            let lastStatus = params.row[params.column.key] || 0;
                            return Common.RENDER.STATUS_DIY(h, params)('正常', `正在报警`, !lastStatus || ![1, 2].includes(lastStatus));
                        },
                        width: 140
                    }, {
                        title: '频率',
                        key: 'interval',
                        render: (h, params) => Common.RENDER.APPEND(h, params)('分钟')
                    }, {
                        title: '状态',
                        key: 'status',
                        render: Common.RENDER.STATUS
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
                                                name: 'monitor-http-edit',
                                                params: params.row
                                            });
                                        }
                                    }
                                }, '修改'),
                                h('Poptip', {
                                    props: {
                                        confirm: true,
                                        title: '您确定要删除这条数据吗?',
                                        placement: 'top',
                                        transfer: true
                                    },
                                    on: {
                                        'on-ok': async () => {
                                            await this.remove(params.row);
                                        }
                                    }
                                }, [
                                    h('Button', {
                                        props: {
                                            type: 'error',
                                            size: 'small'
                                        }
                                    }, '删除')
                                ])
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
                        type: 1,
                        pageNum: 1,
                        pageSize: 10
                    }
                },
                spots: [],
                apps: [],
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
                let list = await this.fetch(this.table.query.type === 1 ? '/api/monitor/http/list' : '/api/monitor/reported/list', {params: this.table.query});
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
            addHttp() {
                this.$router.push({
                    name: 'monitor-http-edit',
                    params: {}
                });
            },
            addReported() {
                this.$router.push({
                    name: 'monitor-reported-edit',
                    params: {}
                });
            },
            async remove(item) {
                if (!item) return;
                let success = await this.fetch(this.table.query.type === 1 ? '/api/monitor/http/remove' : '/api/monitor/reported/remove', {method: 'post', data: {id: item.id}});
                if (success === false) {
                    return;
                }
                setTimeout(() => this.doQuery(), 500);
            }
        }
    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>