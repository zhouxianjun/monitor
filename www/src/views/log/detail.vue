<template>
    <div>
        <div class="log-record">
            <p v-for="(record, index) in records" :key="`record-${index}`">
                {{record['systemmsg']}}  {{record['cusmsg']}}
            </p>
        </div>
        <Button v-show="total > from + 20" type="text" long @click="pull">加载更多</Button>
    </div>
</template>

<script>
export default {
    name: 'log-detail',
    props: {
        trace_id: {type: [String, Array], require: true},
        load: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            total: 0,
            records: [],
            from: 0
        };
    },
    mounted () {
        if (this.load) {
            this.pull();
        }
    },
    methods: {
        async pull () {
            let traceId = Array.isArray(this.trace_id) ? this.trace_id : [this.trace_id];
            let result = await this.fetch('/api/log/search', {
                method: 'post',
                data: {
                    _source: ['systemmsg', 'cusmsg'],
                    query: {
                        bool: {
                            filter: [{
                                terms: {
                                    trace_id: traceId
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
};
</script>
