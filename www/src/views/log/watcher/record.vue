<template>
    <div>
        <div class="log-record">
            <p v-for="(record, index) in records" :class="[{marker: record.marker}]" :key="`r-${index}`">
                {{record.msg}}
            </p>
        </div>
        <div class="margin-top-10">
            <Button v-show="total > query.pageNum * query.pageSize" type="text" long @click="pull">加载更多</Button>
            <div style="width: 100%;text-align: center;color: gray;" v-show="total <= query.pageNum * query.pageSize">我是有底线的</div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'log-watcher-record',
    props: {
        traceId: {type: String, require: true},
        load: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            records: [],
            total: 0,
            query: {
                pageNum: 0,
                pageSize: 50
            }
        };
    },
    async mounted () {
        if (this.load) {
            await this.pull();
        }
    },
    methods: {
        async pull () {
            this.query.pageNum++;
            let result = await this.fetch('/api/log/watcher/log/detail', {params: Object.assign({traceId: this.traceId}, this.query)});
            if (result && result.value.size) {
                this.records = this.records.concat(result.value.list);
                this.total = result.value.total;
            }
        },
        async doQuery () {
            this.query.pageNum = 0;
            this.records = [];
            await this.pull();
        }
    }
};
</script>
