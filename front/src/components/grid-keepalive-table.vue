<template>
    <Row class="grid-table">
        <Row class="grid-table-header" v-if="showHeader" ref="header">
            <Row>
                <slot name="header">
                    <template v-for="col in cols">
                        <Col :style="{width: `${col._width}px`, float: 'left'}">
                            <template v-if="col.type === 'expand'">
                                &nbsp;
                            </template>
                            <template v-else>
                                {{col.title}}
                            </template>
                        </Col>
                    </template>
                </slot>
            </Row>
        </Row>
        <Row class="grid-table-body" v-show="records && records.length > 0">
            <Row class="grid-table-row" v-for="record in records" :key="record.traceId">
                <Row type="flex" justify="center" align="middle" @click.native="clickRow($event, record)">
                    <template v-for="col in cols">
                        <Col :class="col.className"
                             :style="cellStyle(col)" :data-index="col._index">
                            <div v-if="col.type === 'expand'" @click.stop="toggleExpand(col, record)">
                                <Icon
                                      :type="record.expand ? 'ios-arrow-down' : 'ios-arrow-right'"
                                      size="14"></Icon>
                            </div>
                            <Render v-else-if="col.type === 'render'" :render="col.render" :params="{row: record, column: col}"/>
                            <template v-else>
                                {{record[col.key]}}
                            </template>
                        </Col>
                    </template>
                </Row>
                <keep-alive>
                    <Row class="grid-table-expand" v-if="record.expand">
                        <slot name="expand" :record="record"></slot>
                    </Row>
                </keep-alive>
            </Row>
        </Row>
        <Row class="grid-table-no-data" v-show="!records || records.length === 0">
            <slot name="noData">
                <Row type="flex" justify="center" align="middle">
                    <Col>{{noDataText}}</Col>
                </Row>
            </slot>
        </Row>
    </Row>
</template>

<script>
    import Render from './render';
    import elementResizeDetectorMaker from 'element-resize-detector';

    export default {
        name: "grid-keepalive-table",
        components: {Render},
        props: {
            data: {
                type: Array,
                default() {
                    return [];
                }
            },
            columns: {
                type: Array,
                default() {
                    return [];
                }
            },
            showHeader: {
                type: Boolean,
                default: true
            },
            noDataText: {
                type: String,
                default: '没有数据'
            }
        },
        data() {
            return {
                tableWidth: 0,
                columnsWidth: {},
                cols: [],
                records: []
            }
        },
        mounted() {
            this.handleResize();
            window.addEventListener('resize', this.handleResize, false);
            this.observer = elementResizeDetectorMaker();
            this.observer.listenTo(this.$el, this.handleResize);
        },
        watch: {
            data: {
                handler() {
                    this.records = this.data.map((item, index) => Object.assign(this.records[index] || {expand: false}, item));
                },
                deep: true
            }
        },
        methods: {
            cellStyle(cell) {
                const style = {width: `${cell._width}px`, float: 'left'};
                if (cell.ellipsis) {
                    style.overflow = 'hidden';
                    style.wordWrap = 'normal'
                }
                return style;
            },
            handleResize() {
                let tableWidth = this.$el.offsetWidth - 1;
                // 最小宽度和
                let sumMinWidth = 0;
                // 有自定义宽度的列
                let hasWidthColumns = [];
                // 没有自定义宽度的列
                let noWidthColumns = [];
                // 有自定义最大宽度的列
                let maxWidthColumns = [];
                // 没有自定义最大宽度的列
                let noMaxWidthColumns = [];
                this.cols = this.columns.map((col, index) => {
                    let column = Object.assign({_index: index, _width: null}, col);
                    if (typeof column.render === 'function') {
                        column.type = 'render';
                    }
                    if (column.width) {
                        hasWidthColumns.push(column);
                    } else {
                        noWidthColumns.push(column);
                        if (column.minWidth) {
                            sumMinWidth += column.minWidth;
                        }
                        if (column.maxWidth) {
                            maxWidthColumns.push(column);
                        } else {
                            noMaxWidthColumns.push(column);
                        }
                    }
                    return column;
                });

                // 把所有自定义宽度加起来
                let unUsableWidth = hasWidthColumns.map(cell => cell.width).reduce((a, b) => a + b, 0);
                // 总宽度 - 自定义宽度和 - 最小宽度和 - 1
                let usableWidth = tableWidth - unUsableWidth - sumMinWidth - 1;
                let usableLength = noWidthColumns.length;
                let columnWidth = 0;
                if (usableWidth > 0 && usableLength > 0) {
                    columnWidth = parseInt(`${usableWidth / usableLength}`);
                }

                this.cols.forEach(col => {
                    let width = columnWidth + (col.minWidth ? col.minWidth : 0);
                    if (col.width) {
                        width = col.width;
                    } else {
                        if (col._width) {
                            width = col._width;
                        } else {
                            if (col.minWidth > width) {
                                width = col.minWidth;
                            } else if (col.maxWidth < width) {
                                width = col.maxWidth;
                            }

                            if (usableWidth > 0) {
                                usableWidth -= width - (col.minWidth ? col.minWidth : 0);
                                usableLength--;
                                if (usableLength > 0) {
                                    columnWidth = parseInt(`${usableWidth / usableLength}`);
                                } else {
                                    columnWidth = 0;
                                }
                            } else {
                                columnWidth = 0;
                            }
                        }
                    }

                    col._width = width;
                });
                if (usableWidth > 0) {
                    usableLength = noMaxWidthColumns.length;
                    columnWidth = parseInt(`${usableWidth / usableLength}`);
                    noMaxWidthColumns.forEach(column => {
                        let width = column._width + columnWidth;
                        if (usableLength > 1) {
                            usableLength--;
                            usableWidth -= columnWidth;
                            columnWidth = parseInt(`${usableWidth / usableLength}`);
                        }
                        else {
                            columnWidth = 0;
                        }

                        column._width = width;
                    });
                }

                this.tableWidth = this.cols.map(cell => cell._width).reduce((a, b) => a + b, 0) + 1;
            },
            clickRow(event, record) {
                this.$emit('on-click-row', event.target.getAttribute('data-index'), JSON.parse(JSON.stringify(record)));
            },
            toggleExpand(col, record) {
                record.expand = !record.expand;
                this.$emit('on-expand', JSON.parse(JSON.stringify(record)), col);
            }
        }
    }
</script>

<style scoped lang="less">
    .grid-table {
        border: 1px solid #dddee1;
        border-bottom: 0;
        .grid-table-row {
            border-bottom: 1px solid #e9eaec;
        }
        .grid-table-header {
            .ivu-row {
                border-bottom: 1px solid #e9eaec;
            }
        }
        .grid-table-no-data {
            border-bottom: 1px solid #e9eaec;
        }
        .grid-table-header {
            overflow: hidden;
            font-weight: bold;
            .ivu-col {
                height: 40px;
                white-space: nowrap;
                overflow: hidden;
                background-color: #f8f8f9;
            }
        }
        .grid-table-row {
            .ivu-col:first-child {
                text-align: center;
                cursor: pointer;
            }
        }
        .ivu-col {
            padding: 18px;
            text-align: left;
            word-wrap: break-word;
            text-overflow: ellipsis;
            vertical-align: middle;
        }
        .grid-table-expand {
            border-top: 1px solid #e9eaec;
            min-height: 50px;
            padding: 15px 50px;
            background: #f8f8f9;
        }
    }
</style>