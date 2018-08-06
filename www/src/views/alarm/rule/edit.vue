<template>
    <div>
        <Card :bordered="false" shadow>
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="name">
                    <Input v-model="vo.name" />
                </Form-item>
                <FormItem label="应用" prop="appId">
                    <Select v-model="vo.spotId" style="width: 200px">
                        <Option v-for="item in spots" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                    <Select v-model="vo.appId" style="width: 200px">
                        <Option v-for="item in apps" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <Form-item label="监控项" prop="metric">
                    <Input v-model="vo.metric" />
                </Form-item>
                <Form-item label="资源标识" prop="resource">
                    <Input v-model="vo.resource" />
                </Form-item>
                <Form-item label="频率" prop="interval" v-if="vo.interval > 0">
                    <InputNumber v-model="vo.interval" :min="1" :max="1500" :formatter="val => `${val}分钟`" :parser="val => val.replace('分钟', '')" />
                </Form-item>
                <FormItem label="脚本" prop="script">
                    <MonacoEditor ref="monaco" style="height: 600px;" v-model="vo.script" theme="vs-dark" language="java">
                    </MonacoEditor>
                </FormItem>
                <FormItem label="NODATA" prop="nodata">
                    <i-switch v-model="vo.nodata" size="large">
                        <span slot="open">开启</span>
                        <span slot="close">禁用</span>
                    </i-switch>
                </FormItem>
                <Form-item label="连续几次" prop="count">
                    <InputNumber v-model="vo.count" :min="1" :max="10" :formatter="val => `${val}次后报警`" :parser="val => val.replace('次后报警', '')" />
                </Form-item>
                <Form-item label="沉默间隔" prop="silenceInterval">
                    <InputNumber v-model="vo.silenceInterval" :min="1" :max="4320" :formatter="val => `${val}分钟`" :parser="val => val.replace('分钟', '')" />
                </Form-item>
                <FormItem label="通知联系组">
                    <Select v-model="vo.alarmGroupId" filterable>
                        <Option v-for="item in contactsGroups" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="回调地址" prop="alarmCallback">
                    <Input v-model="vo.alarmCallback" type="url" />
                </FormItem>
                <FormItem label="状态" prop="status">
                    <i-switch v-model="vo.status" size="large">
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
import { getTagNavListFromLocalstorage } from '@/libs/util';
import { mapMutations } from 'vuex';

export default {
    name: 'alarm-rule-edit',
    components: {
        MonacoEditor
    },
    data () {
        return {
            vo: {
                id: null,
                name: null,
                appId: null,
                spotId: null,
                metric: null,
                resource: null,
                interval: 1,
                script: null,
                nodata: true,
                count: 1,
                silenceInterval: 1440,
                alarmGroupId: null,
                alarmCallback: null,
                status: true
            },
            spots: [],
            apps: [],
            contactsGroups: [],
            validate: {
                name: [{required: true, trigger: 'blur'}],
                appId: [{type: 'number', required: true, trigger: 'change'}],
                metric: [{required: true, trigger: 'blur'}],
                resource: [{required: true, trigger: 'blur'}],
                interval: [{type: 'number', required: true, trigger: 'blur'}],
                nodata: [{type: 'boolean', required: true, trigger: 'blur'}],
                count: [{type: 'number', required: true, trigger: 'blur'}],
                silenceInterval: [{type: 'number', required: true, trigger: 'blur'}],
                status: [{type: 'boolean', required: true, trigger: 'blur'}]
            }
        };
    },
    watch: {
        async 'vo.spotId' (val) {
            this.apps = await this.loadApp(val);
        }
    },
    mounted () {
        this.initSpots();
        this.initContactsGroups();
        this.vo = Object.assign(this.vo, this.$route.params);
        QL();
    },
    methods: {
        ...mapMutations([
            'setTagNavList'
        ]),
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
                    let url = this.vo.id ? '/api/alarm/rule/update' : '/api/alarm/rule/add';
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
            let res = getTagNavListFromLocalstorage().filter(item => item.name !== 'alarm-rule-edit');
            this.setTagNavList(res);
            this.$router.back();
        }
    }
};
</script>
