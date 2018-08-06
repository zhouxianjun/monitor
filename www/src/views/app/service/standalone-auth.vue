<template>
    <div>
        <app-business-service v-model="appId" :path="path" @on-change-app="changeAppHandler">
            <div>
                <tree-table ref="tree" :columns="table.columns" :data="table.data" show-checkbox show-header border bottom-line>
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
                <Button :loading="loading" :disabled="disabled" @click="confirm" type="success">确定</Button>
            </div>
        </app-business-service>
        <Modal v-model="model" :scrollable="true" :title="modelTitle" :loading="loading" @on-ok="addOrUpdate" @on-cancel="cancel">
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
        <Modal v-model="changeModel" :width="1000" title="确认变更" :loading="loading" @on-ok="apply" @on-cancel="cancel">
            <Table class="margin-bottom-10" size="small" :columns="changeTable.columns" :data="changeTable.add"></Table>
            <Table class="margin-bottom-10" size="small" :columns="changeTable.columns" :data="changeTable.update" :show-header="false"></Table>
            <Table size="small" :columns="changeTable.columns" :data="changeTable.delete" :show-header="false"></Table>
        </Modal>
    </div>
</template>
<script>
import AppBusinessService from '@/components/app-business-service';
import TreeTable from 'iview-tree-table';
import Common from '@/libs/common';
import { FuncType, EpType, calc } from '@/libs/dic';
export default {
    name: 'standalone-auth',
    components: {
        AppBusinessService,
        TreeTable
    },
    data () {
        return {
            FuncType,
            EpType,
            path: 'api/business/standalone/auth',
            appId: null,
            allFunc: [],
            appFunc: [],
            loading: false,
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
                    render: (h, params) => Common.RENDER.DIC(h, params)(FuncType)
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
                    render: (h, params) => Common.RENDER.DIC(h, params)(FuncType)
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
            model: false,
            modelTitle: '',
            vo: {
                id: null,
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
            }
        };
    },
    watch: {

    },
    mounted () {

    },
    methods: {
        async changeAppHandler (appId) {
            this.disabled = true;
            this.appId = appId;
            if (!this.table.data.length && !isNaN(appId) && appId !== null) {
                await this.loadAllFunc();
            }
            await this.loadAppAuth();
        },
        async loadAllFunc () {
            let result = await this.fetch(`/${this.path}/list`, {params: {appId: this.appId}});
            this.allFunc = result ? result.value || [] : [];
            this.allFunc.forEach(r => r.epTypeText = calc(EpType, r.epType).map(t => t.name).join(','));
        },
        async loadAppAuth () {
            let result = !this.appId ? false : await this.fetch('/api/business/proxy', {params: {
                appId: this.appId,
                url: '/func/getAll',
                method: 'GET'
            }});

            if (result && result.value && result.value.data) {
                // 应用权限
                this.appFunc = Common.toCamelCaseKeys(result.value.data) || [];
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
                        Common.diffVo(f, r, ['name', 'seq', 'type', 'path', 'target', 'funcId', 'icon', 'ep_type'], u => this.changeTable.update.push(u));
                    } else {
                        // 应用有的权限 这里没有的
                        r._new = true;
                        this.allFunc.push(r);
                    }
                });
                // 转换树形结构
                let data = Common.makeTree(this.allFunc);
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
            this.loading = true;
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
            } catch (e) {
                this.cancel();
            }
        },
        async apply () {
            let result = await this.fetch('/api/business/proxy', {
                params: {
                    appId: this.appId,
                    url: '/func/distribution',
                    method: 'POST'
                },
                method: 'post',
                data: {
                    addList: Common.toUnderScoreCaseKeys(this.changeTable.add),
                    upaList: Common.toUnderScoreCaseKeys(this.changeTable.update),
                    delList: this.changeTable.delete.map(r => r.id)
                }
            });
            if (result && result.value && result.value.success) {
                this.$Notice.success({
                    title: '更新权限成功，正在重新加载...'
                });
                this.loading = false;
                await this.loadAppAuth();
            } else {
                this.resetLoadingBtn();
                this.$Notice.error({
                    title: '更新权限失败...'
                });
            }
        },
        add (row) {
            this.modelTitle = '新增权限';
            this.model = true;
            this.loading = true;
            this.$refs['form'].resetFields();
            this.vo.pid = row.id;
        },
        update (row) {
            this.modelTitle = '修改权限';
            this.model = true;
            this.loading = true;
            Object.keys(this.vo).forEach(key => this.vo[key] = row[key]);
            this.vo.epType = calc(EpType, row.epType).map(r => r.id);
        },
        async addOrUpdate () {
            this.$refs['form'].validate(async (valid) => {
                if (valid) {
                    let url = this.vo.id ? `/${this.path}/update` : `/${this.path}/add`;
                    let epType = this.vo.epType.reduce((value, current) => value + current, 0);
                    let success = await this.fetch(url, {params: {appId: this.appId}, method: 'post', data: Object.assign({}, this.vo, {epType})});
                    if (success === false) {
                        this.resetLoadingBtn();
                        return;
                    }
                    this.model = false;
                    this.loading = false;
                    setTimeout(() => this.reload(), 500);
                } else {
                    this.resetLoadingBtn();
                    this.$Message.error('表单验证失败!');
                }
            });
        },
        async reload () {
            await this.loadAllFunc();
            await this.loadAppAuth();
        },
        cancel () {
            this.loading = false;
        },
        resetLoadingBtn () {
            this.loading = false;
            this.$nextTick(() => this.loading = true);
        }
    }
};
</script>
