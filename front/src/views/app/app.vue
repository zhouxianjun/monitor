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
            <div style="max-height: 400px;" v-slimscroll>
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
                    <FormItem label="授权ID" prop="authId" v-show="expand">
                        <Input v-model="vo.authId" />
                    </FormItem>
                    <FormItem label="授权KEY" prop="authKey" v-show="expand">
                        <Input v-model="vo.authKey" />
                    </FormItem>
                    <FormItem label="服务URL" prop="service" v-show="expand">
                        <Input v-model="vo.service" />
                    </FormItem>
                    <FormItem label="应用类型" prop="type" v-show="expand">
                        <InputNumber v-model="vo.type" min="1" />
                    </FormItem>
                </Form>
            </div>
        </Modal>
    </div>
</template>

<script>
    import Common from '../../libs/common';
    import {StatusDiy} from '../../libs/dic';
    import VueTagsInput from '@johmun/vue-tags-input';
    export default {
        name: 'app',
        components: {
            VueTagsInput
        },
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
                        render: Common.RENDER.SPLIT_COUNT_POP
                    }, {
                        title: '健康状态',
                        key: 'alertCount',
                        render (h, params) {
                            let count = params.row[params.column.key] || 0;
                            return Common.RENDER.STATUS_DIY(h, params)('健康', `正在报警: ${count}`, count <= 0);
                        }
                    }, {
                        title: '是否报警',
                        key: 'alarm',
                        render: Common.RENDER.STATUS
                    }, {
                        title: '创建时间',
                        key: 'createTime',
                        render: Common.RENDER.DATE
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
                                            this.model = true;
                                            this.modelTitle = '修改应用';
                                            this.loadingBtn = true;
                                            Object.keys(this.vo).forEach(key => this.vo[key] = params.row[key]);
                                            this.hosts = this.vo.hosts ? this.vo.hosts.split(',') : [];
                                        }
                                    }
                                }, '修改'),
                                Common.tableBtnPop(h, '您确定要删除这条数据吗?', '删除', 'error', () => this.remove(params.row))
                            ]);
                        }
                    }],
                    data: [],
                    query: {
                        spot: null,
                        name: null,
                        normal: null,
                        pageNum: 1,
                        pageSize: 10
                    }
                },
                spots: [],
                model: false,
                modelTitle: '',
                loadingBtn: false,
                vo: {
                    id: null,
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
                host: ''
            };
        },
        async mounted () {
            this.doQuery();
            this.initSpots();
        },
        methods: {
            async doQuery () {
                let list = await this.fetch('/api/app/list', {params: this.table.query});
                list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
                list && (this.table.total = list.value.total);
                this.loadingBtn = false;
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
            async addOrUpdate () {
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        let url = this.vo.id ? '/api/app/update' : '/api/app/add';
                        let success = await this.fetch(url, {method: 'post', data: this.vo});
                        if (success === false) {
                            this.resetLoadingBtn();
                            return;
                        }
                        this.model = false;
                        setTimeout(() => this.doQuery(), 500);
                    } else {
                        this.resetLoadingBtn();
                        this.$Message.error('表单验证失败!');
                    }
                });
            },
            async remove (item) {
                if (!item) return;
                let success = await this.fetch('/api/app/remove', {method: 'post', data: {id: item.id}});
                if (success === false) {
                    this.resetLoadingBtn();
                    return;
                }
                setTimeout(() => this.doQuery(), 500);
            },
            add () {
                this.model = true;
                this.modelTitle = '创建应用';
                this.loadingBtn = true;
                this.$refs['form'].resetFields();
                this.hosts = [];
            },
            cancel () {
                this.loadingBtn = false;
            },
            resetLoadingBtn () {
                this.loadingBtn = false;
                this.$nextTick(() => this.loadingBtn = true);
            },
            tagChanged (newTags) {
                this.hosts = newTags;
                this.vo.hosts = newTags.map(tag => tag.text).join();
            }
        }
    };
</script>
