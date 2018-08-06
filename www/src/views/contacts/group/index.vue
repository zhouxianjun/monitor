<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
                <Icon type="plus"></Icon>创建组
            </Button>
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

        <Modal v-model="model" :title="modelTitle" :width="550" :loading="loadingBtn" @on-ok="addOrUpdate" @on-cancel="cancel">
            <div style="max-height: 400px;" v-slimscroll="{height: 300}">
                <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                    <Form-item label="组名" prop="group.name">
                        <Input v-model="vo.group.name" />
                    </Form-item>
                    <FormItem label="备注" prop="group.remark">
                        <Input type="textarea" v-model="vo.group.remark" />
                    </FormItem>
                    <FormItem label="选择联系人" prop="contacts">
                        <Transfer :data="contacts" :target-keys="vo.contacts" :titles="['已有联系人', '已选联系人']" filterable :filter-method="(data, query) => data.label.indexOf(query) > -1" @on-change="val => vo.contacts = val"></Transfer>
                    </FormItem>
                </Form>
            </div>
        </Modal>
    </div>
</template>

<script>
import Common from '@/libs/common';
export default {
    name: 'contacts-group-index',
    data () {
        return {
            model: false,
            modelTitle: null,
            loadingBtn: false,
            contacts: [],
            table: {
                col: [{
                    title: '组名',
                    key: 'name',
                    render: (h, params) => Common.tableColBtn(h, params, () => {
                        this.$router.push({
                            name: 'contacts-list',
                            query: {
                                groupId: params.row['id']
                            }
                        });
                    })
                }, {
                    title: '备注',
                    key: 'remark',
                    tooltip: true
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
                                        this.modelTitle = '修改组';
                                        this.loadingBtn = true;
                                        Object.keys(this.vo.group).forEach(key => this.vo.group[key] = params.row[key]);
                                        await this.loadContactsForUpdate(params.row);
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
            vo: {
                group: {
                    id: null,
                    name: null,
                    remark: null
                },
                contacts: []
            },
            validate: {
                'group.name': [{required: true, trigger: 'blur'}]
            }
        };
    },
    mounted () {
        this.doQuery();
        this.initContacts();
    },
    methods: {
        async initContacts () {
            let result = await this.fetch('/api/contacts/list');
            if (result && result.value && result.value.length) {
                this.contacts = result.value.map(val => Object.assign({}, {key: val.id, label: val.name}));
            }
        },
        async loadContactsForUpdate (item) {
            let result = await this.fetch('/api/contacts/list', {params: {groupId: item.id}});
            if (result && result.value && result.value.length) {
                this.vo.contacts = result.value.map(val => val.id);
            }
        },
        async doQuery () {
            let list = await this.fetch('/api/contacts/group/list', {params: this.table.query});
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
                    let url = this.vo.group.id ? '/api/contacts/group/update' : '/api/contacts/group/add';
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
            let success = await this.fetch('/api/contacts/group/remove', {method: 'post', data: {id: item.id}});
            if (success === false) {
                return;
            }
            setTimeout(() => this.doQuery(), 500);
        },
        add () {
            this.model = true;
            this.modelTitle = '创建组';
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
