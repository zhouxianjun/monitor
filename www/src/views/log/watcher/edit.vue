<template>
    <div>
        <Card :bordered="false" shadow>
            <Form ref="form" :model="vo" :label-width="80" :rules="validate">
                <Form-item label="名称" prop="name">
                    <Input v-model="vo.name"/>
                </Form-item>
                <FormItem label="应用" prop="appId">
                    <app-select v-model="vo.appId" :spot-all="false" :app-all="false"></app-select>
                </FormItem>
                <FormItem label="监控配置" prop="config">
                    <MonacoEditor style="height: 600px;" v-model="vo.config" theme="vs-dark" language="json">
                    </MonacoEditor>
                </FormItem>
                <FormItem label="是否上报" prop="reported">
                    <i-switch v-model="vo.reported" size="large">
                        <span slot="open">开启</span>
                        <span slot="close">禁用</span>
                    </i-switch>
                </FormItem>
                <FormItem label="上报脚本" prop="reportedScript" v-if="vo.reported">
                    <MonacoEditor style="height: 600px;" v-model="vo.reportedScript" theme="vs-dark" language="java">
                    </MonacoEditor>
                </FormItem>
                <Form-item label="描述" prop="memo">
                    <Input type="textarea" v-model="vo.memo"/>
                </Form-item>
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
import AppSelect from '@/components/app-select.vue';
import ModelView from '@/components/mixins/model-view';
import TagNav from '@/components/mixins/tag-nav';

export default {
    name: 'LogWatcherEdit',
    components: {
        MonacoEditor, AppSelect
    },
    mixins: [ ModelView, TagNav ],
    data () {
        return {
            vo: {
                name: null,
                appId: null,
                spotId: null,
                config: null,
                reported: false,
                reportedScript: null,
                memo: null,
                status: true
            },
            validate: {
                name: [{required: true, trigger: 'blur'}],
                appId: [{type: 'number', required: true, trigger: 'change'}],
                config: [{required: true, trigger: 'blur'}],
                interval: [{type: 'number', required: true, trigger: 'blur'}],
                reported: [{type: 'boolean', required: true, trigger: 'blur'}],
                status: [{type: 'boolean', required: true, trigger: 'blur'}]
            },
            addUrl: '/api/log/watcher/add',
            updateUrl: '/api/log/watcher/update'
        };
    },
    created () {
        this.vo = Object.assign(this.vo, this.$route.params);
    },
    methods: {
        afterAddOrUpdate () {
            setTimeout(this.back, 500);
        },
        back () {
            this.tagNavBack('log-watcher-edit');
        }
    }
};
</script>
