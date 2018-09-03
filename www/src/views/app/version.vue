<template>
    <div>
        <Row>
            <Button type="primary" @click="add">
            <Icon type="plus"></Icon>发布版本</Button>
        </Row>
        <Row class="margin-top-10">
            <label>应用：</label>
            <app-select v-model="appCascade" style="margin-right: 5px;"></app-select>
            <Input v-model="table.query.version" placeholder="版本号" clearable style="width: 200px" />
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
                    <FormItem label="应用" prop="appId">
                        <app-select v-model="vo.appId" :single="true" :spot-all="false" :app-all="false"></app-select>
                    </FormItem>
                    <FormItem label="版本号" prop="version">
                        <Input v-model="vo.version" />
                    </FormItem>
                    <Upload action="" type="drag" :max-size="512000" :before-upload="handleUpload">
                        <div style="padding: 20px 0">
                            <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                            <p>点击 或者 拖拽文件上传</p>
                        </div>
                    </Upload>
                    <div v-if="file">{{ file.name }}</div>
                </Form>
            </div>
        </Modal>
    </div>
</template>
<script>
import TableDataView from '@/components/mixins/table-data-view';
import ModelView from '@/components/mixins/model-view';
import TableColRender from '@/components/mixins/table-col-render';
import AppSelect from '@/components/app-select.vue';
export default {
    name: 'AppVersion',
    components: { AppSelect },
    mixins: [ TableDataView, TableColRender, ModelView ],
    data () {
        return {
            appCascade: [],
            table: {
                url: '/api/app/version/list',
                query: {
                    spot: null,
                    app: null,
                    version: null
                },
                col: [{
                    title: '应用',
                    key: 'appName'
                }, {
                    title: '版本号',
                    key: 'version'
                }, {
                    title: '校验值',
                    key: 'md5'
                }, {
                    title: '文件名',
                    key: 'name',
                    tooltip: true
                }, {
                    title: '大小',
                    key: 'size',
                    render: (h, params) => this.renderAppend(h, params)('MB', val => Number(val / 1024 / 1024).toFixed(2))
                }, {
                    title: '存储路径',
                    key: 'path',
                    tooltip: true
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
                        type: 'delete',
                        click: () => this.remove(params.row)
                    }])
                }]
            },
            vo: {
                appId: null,
                version: null
            },
            addTitle: '发布版本',
            file: null,
            validate: {
                appId: [{type: 'number', required: true, trigger: 'change'}],
                version: [{required: true, trigger: 'blur'}]
            },
            addUrl: '/api/app/version/add',
            removeUrl: '/api/app/version/remove'
        };
    },
    computed: {
        generateAddOrUpdate () {
            const data = new FormData();
            data.append('file', this.file);
            return {
                params: this.vo,
                headers: {'Content-Type': 'multipart/form-data'},
                method: this.method,
                data
            };
        }
    },
    methods: {
        async beforeQuery () {
            this.table.query.spot = this.appCascade[0];
            this.table.query.app = this.appCascade[1];
        },
        handleUpload (file) {
            this.file = file;
            return false;
        }
    }
};
</script>
