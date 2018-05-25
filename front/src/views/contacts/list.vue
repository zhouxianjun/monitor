<template>
    <div>
        <Row class="margin-top-10">
            <Col>
                <Table :columns="table.col" :data="table.data"></Table>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="table.total" :show-sizer="true" placement="top"
                              @on-page-size-change="changePageSize" @on-change="changePage"></Page>
                    </div>
                </div>
            </Col>
        </Row>
    </div>
</template>

<script>
    import Common from '../../libs/common';
    import {Status} from '../../libs/dic';

    export default {
        name: "index",
        data() {
            return {
                table: {
                    col: [{
                        title: '姓名',
                        key: 'name'
                    }, {
                        title: '邮箱',
                        key: 'email'
                    }, {
                        title: '手机号码',
                        key: 'phone'
                    }, {
                        title: '钉钉',
                        key: 'ding'
                    }, {
                        title: '微信openId',
                        key: 'openid'
                    }],
                    data: [],
                    query: {
                        name: null,
                        pageNum: 1,
                        pageSize: 10
                    }
                },
                loadingBtn: false
            }
        },
        mounted() {
            this.doQuery();
        },
        methods: {
            async doQuery() {
                let list = await this.fetch('/api/contacts/list', {params: this.table.query});
                list && (this.table.data = list.value.size === 0 ? [] : list.value.list);
                list && (this.table.total = list.value.total);
                this.loadingBtn = false;
            },
            async changePage(page) {
                this.table.query.pageNum = page;
                this.doQuery();
            },
            async changePageSize(size) {
                this.table.query.pageSize = size;
                this.doQuery();
            }
        }
    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>