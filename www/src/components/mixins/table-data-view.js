export default {
    data () {
        return {
            table: {
                data: [],
                query: {
                    pageNum: 1,
                    pageSize: 10
                },
                total: 0
            },
            loadingBtn: false
        };
    },
    mounted () {
        this.doQuery();
    },
    methods: {
        async beforeQuery () {},
        async afterQuery () {},
        async doQuery () {
            let before = await this.beforeQuery();
            if (before !== false) {
                let list = await this.fetch(this.table.url, {params: this.table.query});
                list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
                list && (this.table.total = list.value.total);
            }
            this.loadingBtn = false;
            await this.afterQuery();
        },
        async changePage (page) {
            this.table.query.pageNum = page;
            this.doQuery();
        },
        async changePageSize (size) {
            this.table.query.pageSize = size;
            this.doQuery();
        }
    }
};
