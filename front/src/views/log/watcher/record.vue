<template>
    <div>
        <Row>
            <Input v-model="query.traceId" placeholder="trace id" clearable style="width: 200px" />
            <Button class="margin-left-10" type="primary" @click="doQuery" icon="search">搜索</Button>
        </Row>
        <Row class="margin-top-10 log-record">
            <Card>
                <p v-for="record in records">
                    {{record.msg}}
                </p>
                <div class="margin-top-10">
                    <Button v-show="total > query.pageNum * query.pageSize" type="text" long @click="pull">加载更多</Button>
                    <div style="width: 100%;text-align: center;color: red;" v-show="total <= query.pageNum * query.pageSize" >我是有底线的</div>
                </div>
            </Card>
        </Row>
    </div>
</template>

<script>
    export default {
        name: "log-watcher-record",
        data() {
            return {
                records: [],
                total: 0,
                watchId: null,
                query: {
                    traceId: null,
                    pageNum: 0,
                    pageSize: 50
                }
            }
        },
        async mounted() {
            this.watchId = this.$route.query['watchId'];
            await this.pull();
        },
        methods: {
            async pull() {
                this.query.pageNum++;
                let result = await this.fetch('/api/log/watcher/log/list', {params: Object.assign({watchId: this.watchId}, this.query)});
                if (result && result.value.size) {
                    result.value.list.forEach(item => this.records.push(item));
                    this.total = result.value.total
                }
            },
            async doQuery() {
                this.query.pageNum = 0;
                this.records = [];
                await this.pull();
            }
        }
    }
</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>