<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
                <Icon type="plus"></Icon>创建联系人
            </Button>
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
                    <Page :total="table.total" :show-sizer="true" placement="top" @on-page-size-change="changePageSize" @on-change="changePage"></Page>
                </div>
            </div>
            </Col>
        </Row>
        <Modal v-model="model" :title="modelTitle" :loading="loadingBtn" @on-ok="addOrUpdate" @on-cancel="cancel">
            <div style="max-height: 400px;" v-slimscroll>
                <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                    <Form-item label="姓名" prop="name">
                        <Input v-model="vo.name" />
                    </Form-item>
                    <FormItem label="邮箱" prop="email">
                        <Input type="email" v-model="vo.email" />
                    </FormItem>
                    <FormItem label="openid" prop="openid">
                        <Input v-model="vo.openid" />
                    </FormItem>
                    <FormItem label="钉钉机器人" prop="dingding">
                        <Input type="url" v-model="vo.ding" />
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
    name: 'ContactsList',
    mixins: [ TableColRender, TableDataView, ModelView ],
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
                    render: (h, params) => this.renderActions(h, params, [{
                        config: { loading: this.loadingBtn, style: {marginRight: '5px'} },
                        click: () => this.update(params.row),
                        text: '修改'
                    }, {
                        type: 'delete',
                        click: () => this.remove(params.row)
                    }])
                }],
                url: '/api/contacts/list',
                query: {
                    groupId: null,
                    keyword: null
                }
            },
            vo: {
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
            addUrl: '/api/contacts/add',
            updateUrl: '/api/contacts/update',
            removeUrl: '/api/contacts/remove'
        };
    },
    activated () {
        this.table.query.groupId = this.$route.query.groupId || this.table.query.groupId;
    }
};
</script>
