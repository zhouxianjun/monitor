<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
            <Icon type="plus"></Icon>创建应用</Button>
        </Row>
        <Row class="margin-top-10">
            <Input v-model="table.query.name" placeholder="应用名称" clearable style="width: 200px" />
            <label class="margin-left-10">景区：</label>
            <Select v-model="table.query.spot" @on-change="doQuery" style="width: 200px">
                <Option value="">全部</Option>
                <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <label class="margin-left-10">健康状态：</label>
            <Select v-model="table.query.normal" @on-change="doQuery" style="width: 200px">
                <Option v-for="item in normalStatus" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <Row class="margin-top-10">
            <Table :columns="table.col" :data="table.data"></Table>
            <div style="margin: 10px;overflow: hidden">
                <div style="float: right;">
                    <Page :total="table.total" :show-sizer="true" placement="top" @on-page-size-change="changePageSize" @on-change="changePage"></Page>
                </div>
            </div>
        </Row>

        <Modal v-model="model" :title="modelTitle" :loading="loadingBtn" @on-ok="addOrUpdate" @on-cancel="cancel">
            <div style="height: 300px;">
                <vue-scroll>
                    <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                        <Form-item label="名称" prop="name">
                            <Input v-model="vo.name" />
                        </Form-item>
                        <FormItem label="景区" prop="spotId">
                            <Select v-model="vo.spotId">
                                <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </Select>
                        </FormItem>
                        <FormItem label="报警" prop="alarm">
                            <i-switch v-model="vo.alarm" size="large">
                                <span slot="open">开启</span>
                                <span slot="close">禁用</span>
                            </i-switch>
                        </FormItem>
                        <FormItem label="主机" prop="hosts" class="no-line-height-form">
                            <vue-tags-input :tags="hosts" v-model="host" @tags-changed="tagChanged"></vue-tags-input>
                        </FormItem>
                        <FormItem label="扩展信息">
                            <i-switch v-model="expand" />
                        </FormItem>
                        <template v-if="expand">
                            <FormItem label="授权ID" prop="authId">
                                <Input v-model="vo.authId" />
                            </FormItem>
                            <FormItem label="授权KEY" prop="authKey">
                                <Input v-model="vo.authKey" />
                            </FormItem>
                            <FormItem label="服务URL" prop="service">
                                <Input v-model="vo.service" />
                            </FormItem>
                            <FormItem label="应用类型" prop="type">
                                <InputNumber v-model="vo.type" :min="1" />
                            </FormItem>
                        </template>
                    </Form>
                </vue-scroll>
            </div>
        </Modal>
    </div>
</template>

<script>
import {StatusDiy} from '@/libs/dic';
import VueTagsInput from '@johmun/vue-tags-input';
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';
export default {
    name: 'AppList',
    components: {
        VueTagsInput
    },
    mixins: [ TableDataView, TableColRender, ModelView ],
    data () {
        return {
            expand: false,
            normalStatus: StatusDiy('正常', '异常'),
            table: {
                col: [{
                    title: '景区',
                    key: 'spotName'
                }, {
                    title: '应用名称',
                    key: 'name'
                }, {
                    title: '主机数量',
                    key: 'hosts',
                    render: (h, params) => this.renderListPop(h, params, val => val ? String(val).split(',') : [])
                }, {
                    title: '健康状态',
                    key: 'alertCount',
                    render: (h, params) => {
                        let count = params.row[params.column.key] || 0;
                        return this.renderStatusDiy(h, params)('健康', `正在报警: ${count}`, val => count <= 0);
                    }
                }, {
                    title: '是否报警',
                    key: 'alarm',
                    render: this.renderStatus
                }, {
                    title: '创建时间',
                    key: 'createTime',
                    render: this.renderDate
                }, {
                    title: '操作',
                    key: 'action',
                    width: 200,
                    align: 'center',
                    render: (h, params) => this.renderActions(h, params, [{
                        config: { loading: this.loadingBtn, style: {marginRight: '5px'} },
                        click: () => this.update(params.row),
                        text: '修改'
                    }, {
                        type: 'delete',
                        click: () => this.remove(params.row)
                    }])
                }],
                url: '/api/app/list',
                query: {
                    spot: null,
                    name: null,
                    normal: null
                }
            },
            spots: [],
            vo: {
                name: null,
                spotId: null,
                alarm: true,
                hosts: '',
                authId: null,
                authKey: null,
                service: null,
                type: null
            },
            validate: {
                name: [{required: true, trigger: 'blur'}],
                spotId: [{type: 'number', required: true, trigger: 'change'}],
                alarm: [{type: 'boolean', required: true, trigger: 'blur'}],
                service: [{type: 'url', required: false, trigger: 'blur'}],
                type: [{type: 'number', required: false, trigger: 'blur'}]
            },
            hosts: [],
            host: '',
            addUrl: '/api/app/add',
            updateUrl: '/api/app/update',
            removeUrl: '/api/app/remove',
            addTitle: '创建应用',
            updateTitle: '修改应用'
        };
    },
    async mounted () {
        this.initSpots();
    },
    methods: {
        async initSpots () {
            let list = await this.fetch('/api/spot/list');
            list && (this.spots = (!list.value || list.value.length === 0) ? [] : list.value);
        },
        add () {
            this.$super(ModelView).add();
            this.hosts = [];
        },
        update (item) {
            this.$super(ModelView).update(item);
            this.hosts = this.vo.hosts ? String(this.vo.hosts).split(',').map(v => Object.assign({text: v})) : [];
        },
        tagChanged (newTags) {
            this.hosts = newTags;
            this.vo.hosts = newTags.map(tag => tag.text).join();
        }
    }
};
</script>
