<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
                <Icon type="plus"></Icon>创建监控
            </Button>
        </Row>
        <Row class="margin-top-10">
            <label>应用：</label>
            <app-select v-model="appCascade" style="margin-right: 5px;"></app-select>
            <Input v-model="table.query.name" placeholder="监控名称" clearable style="width: 200px" />
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
import { Status } from '@/libs/dic';
import AppSelect from '@/components/app-select.vue';
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';

export default {
    name: 'LogWatcher',
    components: { AppSelect },
    mixins: [ TableColRender, TableDataView, ModelView ],
    data () {
        return {
            Status,
            appCascade: [],
            table: {
                col: [{
                    title: '监控名称',
                    key: 'name',
                    render: (h, params) => this.renderLink(h, params, () => {
                        this.$router.push({
                            name: 'LogWatcherList',
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
                    render: this.renderStatus
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
                                            name: 'LogWatcherEdit',
                                            params: params.row
                                        });
                                    }
                                }
                            }, '修改'),
                            this.renderSwitchBtnPop(h, params.row.status, () => this.status(params.row), {
                                style: {
                                    marginRight: '5px'
                                }
                            }),
                            this.renderBtnPop(h, '您确定要删除这条数据吗?', '删除', 'error', () => this.remove(params.row))
                        ]);
                    }
                }],
                url: '/api/log/watcher/list',
                removeUrl: '/api/log/watcher/remove',
                query: {
                    spot: null,
                    app: null,
                    name: null,
                    status: null
                }
            }
        };
    },
    methods: {
        add () {
            this.$router.push({
                name: 'LogWatcherEdit',
                params: {}
            });
        },
        async beforeQuery () {
            this.table.query.spot = this.appCascade[0];
            this.table.query.app = this.appCascade[1];
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
