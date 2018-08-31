<template>
    <div>
        <app-business-service v-model="appId" :path="path">
            <div>
                <tree-table ref="tree" :columns="table.columns" :data="table.data" arrow-icon-right="md-arrow-dropright" arrow-icon-down="md-arrow-dropdown" show-checkbox show-header border bottom-line>
                    <template slot="actions" slot-scope="{row, column}">
                        <Button v-if="row.type === 602" class="margin-right-5" @click="add(row)" type="primary" size="small">新增</Button>
                        <Button @click="update(row)" type="primary" size="small">修改</Button>
                    </template>
                    <template slot="diff" slot-scope="{row, column}">
                        <Tooltip v-if="row._diff && row._diff[column.key] !== undefined" :content="row._diff[column.key]">
                            <span style="color: orange">{{row[column.key]}}</span>
                        </Tooltip>
                        <template v-else>
                            {{row[column.key]}}
                        </template>
                    </template>
                </tree-table>
            </div>
            <div slot="ext" style="float: right">
                <Button :loading="loadingBtn" :disabled="disabled" @click="confirm" type="success">确定</Button>
            </div>
        </app-business-service>
        <Modal v-model="model" :scrollable="true" :title="modelTitle" :loading="loadingBtn" @on-ok="addOrUpdate" @on-cancel="cancel">
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="name">
                    <Input v-model="vo.name" />
                </Form-item>
                <FormItem label="类型" prop="type">
                    <Select v-model.number="vo.type">
                        <Option v-for="item in FuncType" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="序号" prop="seq">
                    <InputNumber v-model.number="vo.seq" :min="0" />
                </FormItem>
                <Form-item label="路径" prop="path">
                    <Input v-model="vo.path" />
                </Form-item>
                <FormItem label="选项卡" prop="target">
                    <Input v-model="vo.target" />
                </FormItem>
                <FormItem label="图标" prop="icon">
                    <Input v-model="vo.icon" />
                </FormItem>
                <FormItem label="企业类型" prop="epType">
                    <CheckboxGroup v-model="vo.epType">
                        <Checkbox :label="item.id" v-for="item in EpType" :key="item.id">{{item.name}}</Checkbox>
                    </CheckboxGroup>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="changeModel" :width="1000" title="确认变更" :loading="loadingBtn" @on-ok="apply" @on-cancel="cancel">
            <Table class="margin-bottom-10" size="small" :columns="changeTable.columns" :data="changeTable.add"></Table>
            <Table class="margin-bottom-10" size="small" :columns="changeTable.columns" :data="changeTable.update" :show-header="false"></Table>
            <Table size="small" :columns="changeTable.columns" :data="changeTable.delete" :show-header="false"></Table>
        </Modal>
    </div>
</template>
<script>
import AppBusinessService from '@/components/app-business-service';
import TreeTable from 'iview-tree-table';
import { ToCamelCaseKeys, diffVo, MakeTree, ToUnderScoreCaseKeys } from '@/libs/common';
import { FuncType, EpType, calc } from '@/libs/dic';
import TableColRender from '@/components/mixins/table-col-render';
import ModelView from '@/components/mixins/model-view';

export default {
    name: 'AppServiceStandaloneAuth',
    components: {
        AppBusinessService,
        TreeTable
    },
    mixins: [ TableColRender, ModelView ],
    data () {
        return {
            FuncType,
            EpType,
            path: 'api/business/standalone/auth',
            appId: null,
            allFunc: [],
            appFunc: [],
            disabled: true,
            table: {
                columns: [{
                    title: '名称',
                    key: 'name',
                    template: 'diff'
                }, {
                    title: '序号',
                    key: 'seq',
                    template: 'diff'
                }, {
                    title: '类型',
                    key: 'type',
                    render: (h, params) => this.renderDic(h, params)(FuncType)
                }, {
                    title: '企业类型',
                    key: 'epTypeText',
                    template: 'diff'
                }, {
                    title: '路径/功能',
                    key: 'path',
                    template: 'diff'
                }, {
                    title: '选项卡',
                    key: 'target',
                    template: 'diff'
                }, {
                    title: '图标',
                    key: 'icon',
                    template: 'diff'
                }, {
                    title: '操作',
                    template: 'actions'
                }],
                data: []
            },
            changeTable: {
                columns: [{
                    title: '名称',
                    key: 'name',
                    render: (h, params) => {
                        return this.$refs.tree.$scopedSlots.diff(params);
                    }
                }, {
                    title: '序号',
                    key: 'seq',
                    width: 60,
                    render: (h, params) => {
                        return this.$refs.tree.$scopedSlots.diff(params);
                    }
                }, {
                    title: '类型',
                    key: 'type',
                    width: 80,
                    render: (h, params) => this.renderDic(h, params)(FuncType)
                }, {
                    title: '企业类型',
                    key: 'epTypeText'
                }, {
                    title: '路径/功能',
                    key: 'path'
                }, {
                    title: '选项卡',
                    key: 'target'
                }, {
                    title: '图标',
                    key: 'icon'
                }],
                add: [],
                delete: [],
                update: []
            },
            changeModel: false,
            vo: {
                pid: null,
                name: null,
                type: null,
                seq: 1,
                path: null,
                target: null,
                icon: null,
                status: 1,
                epType: []
            },
            validate: {
                pid: [{type: 'number', required: true, trigger: 'blur'}],
                name: [{required: true, trigger: 'blur'}],
                type: [{type: 'number', required: true, trigger: 'change'}],
                seq: [{type: 'number', required: true, trigger: 'blur'}],
                epType: [{type: 'array', required: true, min: 1, trigger: 'change'}]
            },
            addTitle: '新增权限',
            updateTitle: '修改权限'
        };
    },
    computed: {
        addUrl () {
            return `/${this.path}/add/${this.appId}`;
        },
        updateUrl () {
            return `/${this.path}/update/${this.appId}`;
        },
        generateAddOrUpdate () {
            return {
                method: this.method,
                data: Object.assign({}, this.vo, {epType: this.vo.epType.reduce((value, current) => value + current, 0)})
            };
        }
    },
    watch: {
        async appId (val) {
            this.disabled = true;
            if (!this.table.data.length && !isNaN(val) && val) {
                await this.loadAllFunc();
            }
            await this.loadAppAuth();
        }
    },
    methods: {
        async loadAllFunc () {
            let result = await this.fetch(`/${this.path}/list/${this.appId}`);
            result = result ? result.value || [] : [];
            result = result.sort((a, b) => a.seq - b.seq);
            this.allFunc = result;
            this.allFunc.forEach(r => r.epTypeText = calc(EpType, r.epType).map(t => t.name).join(','));
        },
        async loadAppAuth () {
            let result = !this.appId ? false : await this.fetch(`/api/business/proxy/${this.appId}`, {params: {
                url: '/func/getAll',
                method: 'GET'
            }});
            this.changeTable.update = [];

            if (result && result.value && result.value.data) {
                // 应用权限
                this.appFunc = ToCamelCaseKeys(result.value.data) || [];
                // 去掉勾
                this.allFunc.filter(f => f.checked === true).forEach(f => f.checked = false);
                // 交叉循环
                this.appFunc.forEach(r => {
                    // 企业类型
                    r.epTypeText = calc(EpType, r.epType).map(t => t.name).join(',');
                    // 判断是否存在
                    let f = this.allFunc.find(f => f.id === r.id);
                    if (f) {
                        // 勾上
                        if (f.type !== 602) {
                            f.checked = true;
                        }
                        // 比对属性差异
                        diffVo(f, r, ['name', 'seq', 'type', 'path', 'target', 'funcId', 'icon', 'ep_type'], u => this.changeTable.update.push(u));
                    } else {
                        // 应用有的权限 这里没有的
                        r._new = true;
                        if (r.type !== 602) {
                            r.checked = false;
                        }
                        this.allFunc.push(r);
                    }
                });
                // 转换树形结构
                let data = MakeTree(this.allFunc);
                // 默认展开第一个节点
                if (data.length) {
                    data[0].expand = true;
                }
                this.table.data = data;
                this.disabled = false;
                return;
            }
            this.table.data = [];
        },
        confirm () {
            this.loadingBtn = true;
            try {
                let checked = this.$refs.tree.getCheckedNodes(true);
                // 新增的
                this.changeTable.add = checked.filter(item => !this.appFunc.find(r => r.id === item.id));
                // 删除的
                this.changeTable.delete = this.appFunc.filter(r => !checked.find(node => node.id === r.id));

                if (!this.changeTable.add.length && !this.changeTable.delete.length && !this.changeTable.update.length) {
                    this.$Notice.info({
                        title: '没有可更新的权限'
                    });
                    return;
                }

                this.changeModel = true;
            } finally {
                this.cancel();
            }
        },
        async apply () {
            let result = await this.fetch(`/api/business/proxy/${this.appId}`, {
                params: {
                    url: '/func/distribution',
                    method: 'POST'
                },
                method: 'post',
                data: {
                    addList: ToUnderScoreCaseKeys(this.changeTable.add),
                    upaList: ToUnderScoreCaseKeys(this.changeTable.update),
                    delList: this.changeTable.delete.map(r => r.id)
                }
            });
            if (result && result.value && result.value.success) {
                this.$Notice.success({
                    title: '更新权限成功，正在重新加载...'
                });
                this.loadingBtn = false;
                this.changeModel = false;
                await this.loadAppAuth();
            } else {
                this.resetLoadingBtn();
                this.$Notice.error({
                    title: '更新权限失败...'
                });
            }
        },
        add (row) {
            this.$super(ModelView).add();
            this.vo.pid = row.id;
        },
        update (row) {
            this.$super(ModelView).update(row);
            this.vo.epType = calc(EpType, row.epType).map(r => r.id);
        },
        async doQuery () {
            await this.loadAllFunc();
            await this.loadAppAuth();
            this.loadingBtn = false;
        }
    }
};
</script>
