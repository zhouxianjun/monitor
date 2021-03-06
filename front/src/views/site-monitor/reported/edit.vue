<template>
    <div>
        <Card :bordered="false" shadow>
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="monitor.name">
                    <Input v-model="vo.monitor.name" />
                </Form-item>
                <FormItem label="应用" prop="monitor.appId">
                    <Select v-model="vo.monitor.spotId" style="width: 200px">
                        <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                    <Select v-model="vo.monitor.appId" style="width: 200px">
                        <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <Form-item label="频率" prop="rule.interval">
                    <InputNumber v-model="vo.rule.interval" :min="1" :max="1500" :formatter="val => `${val}分钟`" :parser="val => val.replace('分钟', '')" />
                </Form-item>
                <FormItem label="脚本" prop="rule.script">
                    <MonacoEditor height="600" srcPath="" language="java" @mounted="onMounted" @codeChange="onCodeChange">
                    </MonacoEditor>
                </FormItem>
                <FormItem label="NODATA" prop="rule.nodata">
                    <i-switch v-model="vo.rule.nodata" size="large">
                        <span slot="open">开启</span>
                        <span slot="close">禁用</span>
                    </i-switch>
                </FormItem>
                <Form-item label="连续几次" prop="rule.count">
                    <InputNumber v-model="vo.rule.count" :min="1" :max="10" :formatter="val => `${val}次后报警`" :parser="val => val.replace('次后报警', '')" />
                </Form-item>
                <Form-item label="沉默间隔" prop="rule.silenceInterval">
                    <InputNumber v-model="vo.rule.silenceInterval" :min="1" :max="4320" :formatter="val => `${val}分钟`" :parser="val => val.replace('分钟', '')" />
                </Form-item>
                <FormItem label="通知联系组">
                    <Select v-model="vo.rule.alarmGroupId" filterable>
                        <Option v-for="item in contactsGroups" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="回调地址" prop="rule.alarmCallback">
                    <Input v-model="vo.rule.alarmCallback" type="url" />
                </FormItem>
                <FormItem label="状态" prop="monitor.status">
                    <i-switch v-model="vo.monitor.status" size="large">
                        <span slot="open">开启</span>
                        <span slot="close">禁用</span>
                    </i-switch>
                </FormItem>
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
    import QL from '../../../libs/ql.editor';

    export default {
        name: 'monitor-reported-edit',
        components: {
            MonacoEditor
        },
        data () {
            return {
                editor: null,
                vo: {
                    monitor: {
                        id: null,
                        name: null,
                        appId: null,
                        spotId: null,
                        status: true,
                        alarmRuleId: null
                    },
                    rule: {
                        script: null,
                        interval: 1,
                        nodata: true,
                        count: 1,
                        silenceInterval: 1440,
                        alarmGroupId: null,
                        alarmCallback: null
                    }
                },
                spots: [],
                apps: [],
                contactsGroups: [],
                validate: {
                    'monitor.name': [{required: true, trigger: 'blur'}],
                    'monitor.appId': [{type: 'number', required: true, trigger: 'change'}],
                    'rule.interval': [{type: 'number', required: true, trigger: 'blur'}],
                    'rule.nodata': [{type: 'boolean', required: true, trigger: 'blur'}],
                    'rule.count': [{type: 'number', required: true, trigger: 'blur'}],
                    'rule.silenceInterval': [{type: 'number', required: true, trigger: 'blur'}],
                    'monitor.status': [{type: 'boolean', required: true, trigger: 'blur'}]
                }
            };
        },
        mounted () {
            this.initSpots();
            this.initContactsGroups();
            Reflect.ownKeys(this.vo.monitor).forEach(key => this.vo.monitor[key] = this.$route.params[key] || this.vo.monitor[key]);
            Reflect.ownKeys(this.vo.rule).forEach(key => this.vo.rule[key] = this.$route.params[key] || this.vo.rule[key]);
            this.editor && this.editor.setValue(this.vo.rule.script);
            this.$store.commit('changeTagTitle', {name: 'monitor-reported-edit', title: this.vo.id ? '修改上报监控' : '新增上报监控'});
        },
        watch: {
            async 'vo.monitor.spotId' (val) {
                this.apps = await this.loadApp(val);
            }
        },
        methods: {
            async initSpots () {
                let list = await this.fetch('/api/spot/list');
                list && (this.spots = (!list.value || list.value.length === 0) ? [] : list.value);
            },
            async initContactsGroups () {
                let list = await this.fetch('/api/contacts/group/list');
                list && (this.contactsGroups = (!list.value || list.value.length === 0) ? [] : list.value);
            },
            async loadApp (spot) {
                let list = await this.fetch('/api/app/list', {params: {spot}});
                return list.value ? list.value : [];
            },
            async addOrUpdate () {
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        let url = this.vo.monitor.id ? '/api/monitor/reported/update' : '/api/monitor/reported/add';
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
                    if (tag.name === 'monitor-reported-edit') {
                        tag.close({
                            target: tag.$children[0].$el
                        });
                        return false;
                    }
                    return true;
                });
            },
            onMounted (editor) {
                QL(monaco);
                this.editor = editor;
                this.vo.rule.script && this.editor.setValue(this.vo.rule.script);
            },
            onCodeChange (editor) {
                this.vo.rule.script = editor.getValue();
            }
        }
    };
</script>
