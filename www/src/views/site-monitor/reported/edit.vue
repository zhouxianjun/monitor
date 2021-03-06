<template>
    <div>
        <Card :bordered="false" shadow>
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="monitor.name">
                    <Input v-model="vo.monitor.name" />
                </Form-item>
                <FormItem label="应用" prop="monitor.appId">
                    <app-select v-model="vo.monitor.appId" :signle="true" :spot-all="false" :app-all="false"></app-select>
                </FormItem>
                <Form-item label="频率" prop="rule.interval">
                    <InputNumber v-model="vo.rule.interval" :min="1" :max="1500" :formatter="val => `${val}分钟`" :parser="val => val.replace('分钟', '')" />
                </Form-item>
                <FormItem label="脚本" prop="rule.script">
                    <MonacoEditor style="height: 600px;" v-model="vo.rule.script" theme="vs-dark" language="java">
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
                    <Button type="dashed" class="margin-left-10" @click="back">取消</Button>
                </FormItem>
            </Form>
        </Card>
    </div>
</template>

<script>
import MonacoEditor from 'vue-monaco';
import QL from '@/libs/ql.editor';
import AppSelect from '@/components/app-select.vue';
import ModelView from '@/components/mixins/model-view';
import TagNav from '@/components/mixins/tag-nav';

export default {
    name: 'MonitorReportedEdit',
    components: {
        MonacoEditor, AppSelect
    },
    mixins: [ ModelView, TagNav ],
    data () {
        return {
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
            contactsGroups: [],
            validate: {
                'monitor.name': [{required: true, trigger: 'blur'}],
                'monitor.appId': [{type: 'number', required: true, trigger: 'change'}],
                'rule.interval': [{type: 'number', required: true, trigger: 'blur'}],
                'rule.nodata': [{type: 'boolean', required: true, trigger: 'blur'}],
                'rule.count': [{type: 'number', required: true, trigger: 'blur'}],
                'rule.silenceInterval': [{type: 'number', required: true, trigger: 'blur'}],
                'monitor.status': [{type: 'boolean', required: true, trigger: 'blur'}]
            },
            addUrl: '/api/monitor/reported/add',
            updateUrl: '/api/monitor/reported/update'
        };
    },
    computed: {
        isUpdate () {
            return this.vo.monitor.id !== null && this.vo.monitor.id !== undefined;
        }
    },
    activated () {
        Reflect.ownKeys(this.vo.monitor).forEach(key => {
            const val = this.$route.params[key];
            this.vo.monitor[key] = (val === undefined || val === null) ? this.vo.monitor[key] : val;
        });
        Reflect.ownKeys(this.vo.rule).forEach(key => {
            const val = this.$route.params[key];
            this.vo.rule[key] = (val === undefined || val === null) ? this.vo.rule[key] : val;
        });
    },
    created () {
        this.initContactsGroups();
        QL();
    },
    methods: {
        async initContactsGroups () {
            let list = await this.fetch('/api/contacts/group/list');
            list && (this.contactsGroups = (!list.value || list.value.length === 0) ? [] : list.value);
        },
        afterAddOrUpdate () {
            setTimeout(this.back, 500);
        },
        back () {
            this.tagNavBack('SiteMonitorReportedEdit');
        }
    }
};
</script>
