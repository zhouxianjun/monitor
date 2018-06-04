<template>
    <div>
        <div class="log-record">
            <p v-for="record in records">
                {{record['systemmsg']}}　{{record['cusmsg']}}
            </p>
        </div>
        <Button v-show="total > from + 20" type="text" long @click="pull">加载更多</Button>
    </div>
</template>

<script>
    export default {
        name: "log-detail",
        props: {
            trace_id: {type: [String, Array], require: true}
        },
        data() {
            return {
                total: 0,
                records: [],
                from: 0
            }
        },
        mounted() {
            this.pull();
        },
        methods: {
            async pull() {
                let result = await this.fetch('/api/log/search', {
                    method: 'post', data: {
                        _source: ['systemmsg', 'cusmsg'],
                        query: {
                            bool: {
                                filter: [{
                                    terms: {
                                        trace_id: Array.isArray(this.trace_id) ? this.trace_id : [this.trace_id]
                                    }
                                }]
                            }
                        },
                        from: this.from,
                        size: 20,
                        sort: [{
                            '@timestamp': 'asc'
                        }, {
                            offset: 'asc'
                        }]
                    }
                });
                if (result) {
                    let data = JSON.parse(result.value);
                    this.total = data['hits']['total'];
                    data['hits']['hits'].forEach(hit => this.records.push(hit['_source']));
                    this.from += 20;
                }
            }
        }
    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>