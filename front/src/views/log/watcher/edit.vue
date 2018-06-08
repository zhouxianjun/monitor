<template>
    <div>
        <Card :bordered="false" shadow>
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="name">
                    <Input v-model="vo.name"/>
                </Form-item>
                <FormItem label="应用" prop="appId">
                    <Select v-model="vo.spotId" style="width: 200px">
                        <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                    <Select v-model="vo.appId" style="width: 200px">
                        <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="监控配置" prop="config">
                    <MonacoEditor
                            height="600"
                            srcPath=""
                            language="json"
                            @mounted="onMounted"
                            @codeChange="onCodeChange"
                    >
                    </MonacoEditor>
                </FormItem>
                <FormItem label="是否上报" prop="reported">
                    <i-switch v-model="vo.reported" size="large">
                        <span slot="open">开启</span>
                        <span slot="close">禁用</span>
                    </i-switch>
                </FormItem>
                <FormItem label="上报脚本" prop="reportedScript" v-if="vo.reported">
                    <MonacoEditor
                            height="600"
                            language="java"
                            srcPath=""
                            @mounted="onMountedReportedScript"
                            @codeChange="onCodeChangeReportedScript"
                    >
                    </MonacoEditor>
                </FormItem>
                <Form-item label="描述" prop="memo">
                    <Input type="textarea" v-model="vo.memo"/>
                </Form-item>
                <FormItem>
                    <Button type="primary" @click="addOrUpdate">确定</Button>
                    <Button type="ghost" class="margin-left-10" @click="back">取消</Button>
                </FormItem>
            </Form>
        </Card>
    </div>
</template>

<script>
    import MonacoEditor from 'vue-monaco-editor';

    export default {
        name: 'log-watcher-edit',
        components: {
            MonacoEditor
        },
        data () {
            return {
                editor: null,
                editorReportedScript: null,
                vo: {
                    id: null,
                    name: null,
                    appId: null,
                    spotId: null,
                    config: null,
                    reported: false,
                    reportedScript: null,
                    memo: null,
                    status: true
                },
                spots: [],
                apps: [],
                validate: {
                    name: [{required: true, trigger: 'blur'}],
                    appId: [{type: 'number', required: true, trigger: 'change'}],
                    config: [{required: true, trigger: 'blur'}],
                    interval: [{type: 'number', required: true, trigger: 'blur'}],
                    reported: [{type: 'boolean', required: true, trigger: 'blur'}],
                    status: [{type: 'boolean', required: true, trigger: 'blur'}]
                }
            };
        },
        mounted () {
            this.initSpots();
            this.vo = Object.assign(this.vo, this.$route.params);
            this.editor && this.editor.setValue(this.vo.config);
            this.editorReportedScript && this.editorReportedScript.setValue(this.vo.reportedScript);
            this.$store.commit('changeTagTitle', {name: 'log-watcher-edit', title: this.vo.id ? '修改日志监控' : '新增日志监控'});
        },
        watch: {
            async 'vo.spotId' (val) {
                this.apps = await this.loadApp(val);
            }
        },
        methods: {
            async initSpots () {
                let list = await this.fetch('/api/spot/list');
                list && (this.spots = (!list.value || list.value.length === 0) ? [] : list.value);
            },
            async loadApp (spot) {
                let list = await this.fetch('/api/app/list', {params: {spot}});
                return list.value ? list.value : [];
            },
            async addOrUpdate () {
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        let url = this.vo.id ? '/api/log/watcher/update' : '/api/log/watcher/add';
                        let success = await this.fetch(url, {method: 'post', data: this.vo});
                        if (success === false) {
                            return;
                        }
                        setTimeout(this.back, 500);
                    } else {
                        this.$Message.error('表单验证失败!');
                    }
                });
            },
            back () {
                this.$router.back();
                this.$parent.$refs['tags'].refsTag.every(tag => {
                    if (tag.name === 'log-watcher-edit') {
                        tag.close({
                            target: tag.$children[0].$el
                        });
                        return false;
                    }
                    return true;
                });
            },
            onMounted (editor) {
                this.editor = editor;
                this.vo.config && this.editor.setValue(this.vo.config);
            },
            onCodeChange (editor) {
                this.vo.config = editor.getValue();
            },
            onMountedReportedScript (editor) {
                this.editorReportedScript = editor;
                this.vo.reportedScript && this.editorReportedScript.setValue(this.vo.reportedScript);
            },
            onCodeChangeReportedScript (editor) {
                this.vo.reportedScript = editor.getValue();
            }
        }
    };
</script>

<style scoped>

</style>
