<template>
    <div>
        <Row>
            <Button type="primary" @click="add"><Icon type="plus"></Icon>创建联系人</Button>
        </Row>
        <Row class="margin-top-10">
            <Input v-model="table.query.keyword" placeholder="keyword" clearable style="width: 200px" />
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <Row class="margin-top-10">
            <Col>
                <Table :columns="table.col" :data="table.data"></Table>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="table.total" :show-sizer="true" placement="top"
                              @on-page-size-change="changePageSize" @on-change="changePage"></Page>
                    </div>
                </div>
            </Col>
        </Row>
        <Modal v-model="model" :title="modelTitle" :loading="loadingBtn" @on-ok="addOrUpdate" @on-cancel="cancel">
            <div style="max-height: 400px;" v-slimscroll>
                <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                    <Form-item label="姓名" prop="name">
                        <Input v-model="vo.name"/>
                    </Form-item>
                    <FormItem label="邮箱" prop="email">
                        <Input type="email" v-model="vo.email"/>
                    </FormItem>
                    <FormItem label="openid" prop="openid">
                        <Input v-model="vo.openid"/>
                    </FormItem>
                    <FormItem label="钉钉机器人" prop="dingding">
                        <Input type="url" v-model="vo.ding"/>
                    </FormItem>
                </Form>
            </div>
        </Modal>
    </div>
</template>

<script>
    import Common from '../../libs/common';

    export default {
        name: 'contacts-list',
        data () {
            return {
                table: {
                    col: [{
                        title: '姓名',
                        key: 'name'
                    }, {
                        title: '邮箱',
                        key: 'email'
                    }, {
                        title: '手机号码',
                        key: 'phone'
                    }, {
                        title: '钉钉',
                        key: 'ding'
                    }, {
                        title: '微信openId',
                        key: 'openid'
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
                                            this.modelTitle = '修改联系人';
                                            this.loadingBtn = true;
                                            Object.keys(this.vo).forEach(key => this.vo[key] = params.row[key]);
                                        }
                                    }
                                }, '修改'),
                                Common.tableBtnPop(h, '您确定要删除这条数据吗?', '删除', 'error', () => this.remove(params.row))
                            ]);
                        }
                    }],
                    data: [],
                    query: {
                        groupId: null,
                        keyword: null,
                        pageNum: 1,
                        pageSize: 10
                    }
                },
                loadingBtn: false,
                vo: {
                    id: null,
                    name: null,
                    email: null,
                    phone: null,
                    openid: null,
                    ding: null
                },
                validate: {
                    name: [{required: true, trigger: 'blur'}],
                    email: [{type: 'email', trigger: 'blur'}],
                    phone: [{type: 'regexp', trigger: 'blur'}],
                    ding: [{type: 'url', trigger: 'blur'}]
                },
                model: false,
                modelTitle: null
            };
        },
        mounted () {
            this.table.query['groupId'] = this.$route.query['groupId'];
            this.doQuery();
        },
        methods: {
            async doQuery () {
                let list = await this.fetch('/api/contacts/list', {params: this.table.query});
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
            async addOrUpdate () {
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        let url = this.vo.id ? '/api/contacts/update' : '/api/contacts/add';
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
                let success = await this.fetch('/api/contacts/remove', {method: 'post', data: {id: item.id}});
                if (success === false) {
                    return;
                }
                setTimeout(() => this.doQuery(), 500);
            },
            add () {
                this.model = true;
                this.modelTitle = '创建联系人';
                this.loadingBtn = true;
                this.$refs['form'].resetFields();
            },
            cancel () {
                this.loadingBtn = false;
            },
            resetLoadingBtn () {
                this.loadingBtn = false;
                this.$nextTick(() => this.loadingBtn = true);
            }
        }
    };
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>
