<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
                <Icon type="plus"></Icon>创建监控</Button>
        </Row>
        <Row class="margin-top-10">
            <Input v-model="table.query.name" placeholder="监控名称" clearable style="width: 200px" />
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
    import Common from '../../../libs/common';
    import {Status} from '../../../libs/dic';

    export default {
        name: 'log-watcher-index',
        data () {
            return {
                Status,
                table: {
                    col: [{
                        title: '监控名称',
                        key: 'name',
                        render: (h, params) => Common.tableColBtn(h, params, () => {
                            this.$router.push({
                                name: 'log-watcher-list',
                                query: {
                                    watchId: params.row['id']
                                }
                            });
                        })
                    }, {
                        title: '景区',
                        key: 'spotName'
                    }, {
                        title: '应用',
                        key: 'appName'
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
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: async () => {
                                            this.$router.push({
                                                name: 'log-watcher-edit',
                                                params: params.row
                                            });
                                        }
                                    }
                                }, '修改'),
                                Common.switchTableBtnPop(h, params.row.status, () => this.status(params.row), {
                                    style: {
                                        marginRight: '5px'
                                    }
                                }),
                                Common.tableBtnPop(h, '您确定要删除这条数据吗?', '删除', 'error', () => this.remove(params.row))
                            ]);
                        }
                    }],
                    data: [],
                    query: {
                        spot: null,
                        app: null,
                        name: null,
                        status: null,
                        pageNum: 1,
                        pageSize: 10
                    },
                    total: 0
                },
                spots: [],
                apps: []
            };
        },
        async mounted () {
            this.doQuery();
            this.initSpots();
        },
        watch: {
            async 'table.query.spot' (val) {
                this.apps = await this.loadApp(val);
            }
        },
        methods: {
            async doQuery () {
                let list = await this.fetch('/api/log/watcher/list', {params: this.table.query});
                list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
                list && (this.table.total = list.value.total);
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
            },
            add () {
                this.$router.push({
                    name: 'log-watcher-edit',
                    params: {}
                });
            },
            async remove (item) {
                if (!item) return;
                let success = await this.fetch('/api/log/watcher/remove', {method: 'post', data: {id: item.id}});
                if (success === false) {
                    return;
                }
                setTimeout(() => this.doQuery(), 500);
            },
            async status (item) {
                if (!item) return;
                let success = await this.fetch('/api/log/watcher/update', {method: 'post', data: {id: item.id, status: !item.status}});
                if (success === false) {
                    return;
                }
                setTimeout(() => this.doQuery(), 500);
            }
        }
    };
</script>
