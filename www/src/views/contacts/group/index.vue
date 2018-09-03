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
import TableDataView from '@/components/mixins/table-data-view';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';

export default {
    name: 'ContactsGroup',
    mixins: [ TableColRender, TableDataView, ModelView ],
    data () {
        return {
            contacts: [],
            table: {
                col: [{
                    title: '组名',
                    key: 'name',
                    render: (h, params) => this.renderLink(h, params, () => {
                        this.$router.push({
                            name: 'ContactsList',
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
                    render: (h, params) => this.renderActions(h, params, [{
                        config: { loading: this.loadingBtn, style: {marginRight: '5px'} },
                        click: () => this.update(params.row),
                        text: '修改'
                    }, {
                        type: 'delete',
                        click: () => this.remove(params.row)
                    }])
                }],
                url: '/api/contacts/group/list',
                query: {
                    spot: null,
                    name: null,
                    normal: null
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
            },
            addUrl: '/api/contacts/group/add',
            updateUrl: '/api/contacts/group/update',
            removeUrl: '/api/contacts/group/remove'
        };
    },
    computed: {
        isUpdate () {
            return this.vo.group.id !== null && this.vo.group.id !== undefined;
        }
    },
    mounted () {
        this.initContacts();
    },
    methods: {
        add () {
            this.$super(ModelView).add();
            this.vo.group.id = null;
        },
        async update (item) {
            this.model = true;
            this.loadingBtn = true;
            Object.keys(this.vo.group).forEach(key => this.vo.group[key] = item[key]);
            await this.loadContactsForUpdate(item);
        },
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
        }
    }
};
</script>
