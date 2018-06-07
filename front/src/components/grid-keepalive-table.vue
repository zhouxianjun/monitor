<template>
    <div class="grid-table">
        <div class="header" v-if="showHeader" ref="header">
            <slot name="header">
                <template v-for="col in cols">
                    <div :style="{width: `${columnsWidth[col._index]}px`}">
                        <template v-if="col.type === 'expand'">
                            &nbsp;
                        </template>
                        <template v-else-if="col.type === 'index'">
                            #
                        </template>
                        <div v-else-if="col.type === 'checkbox'" @click.stop="selectAll(col)">
                            <slot name="header-checkbox">
                                <input type="checkbox"/>
                            </slot>
                        </div>
                        <template v-else>
                            {{col.title}}
                        </template>
                    </div>
                </template>
            </slot>
        </div>
        <div class="body" v-show="records && records.length > 0">
            <div :class="rowClass(record)"
                 :data-index="index"
                 v-for="(record, index) in records"
                 @mouseenter.stop="handleMouse(record, true)"
                 @mouseleave.stop="handleMouse(record, false)"
                 @click="clickRow($event, record)"
                 :key="record.traceId">
                <template v-for="col in cols">
                    <div class="wrapper">
                        <div :class="['cell', {'cell-with-expand': col.type === 'expand'}, col.className]"
                             :style="cellStyle(col)" :data-index="col._index">
                            <div v-if="col.type === 'expand'" @click.stop="toggleExpand(col, record)">
                                <slot name="col-expand" :record="record">
                                    <span>{{record.expand ? 'v' : '>'}}</span>
                                </slot>
                            </div>
                            <div v-else-if="col.type === 'checkbox'" @click.stop="toggleSelect(col, record)">
                                <slot name="col-checkbox">
                                    <input type="checkbox" :checked="record.isChecked"/>
                                </slot>
                            </div>
                            <template v-else-if="col.type === 'index'">
                                {{index + 1}}
                            </template>
                            <Render v-else-if="col.type === 'render'" :render="col.render" :params="{row: record, column: col}"/>
                            <template v-else>
                                {{record[col.key]}}
                            </template>
                        </div>
                    </div>
                </template>
                <div class="expand" v-show="record.expand">
                    <keep-alive>
                        <slot name="expand" :record="record" v-if="record.expand"></slot>
                    </keep-alive>
                </div>
                <div style="clear:both"></div>
            </div>
        </div>
        <div class="no-data" v-show="!records || records.length === 0">
            <slot name="noData">
                <div>{{noDataText}}</div>
            </slot>
        </div>
    </div>
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
            },
            disabledHover: {
                type: Boolean,
                default: false
            },
            hoverClass: {
                type: String,
                default: 'row-hover'
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
                    this.records = this.data.map((item, index) => Object.assign(this.records[index] || {expand: false, isChecked: false, hover: false}, item));
                },
                deep: true
            }
        },
        methods: {
            cellStyle(cell) {
                const style = {width: `${this.columnsWidth[cell._index]}px`};
                if (cell.ellipsis) {
                    style.overflow = 'hidden';
                    style.wordWrap = 'normal'
                }
                return style;
            },
            rowClass(record) {
                const result = ['row'];
                if (record.hover) {
                    result.push(this.hoverClass.toString());
                }
                return result;
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
                let columnsWidth = {};
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
                    columnsWidth[col._index] = width;
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
                        columnsWidth[column._index] = width;
                    });
                }

                this.tableWidth = this.cols.map(cell => cell._width).reduce((a, b) => a + b, 0) + 1;
                this.columnsWidth = columnsWidth;
            },
            clickRow(event, record) {
                this.$emit('on-click-row', JSON.parse(JSON.stringify(record)), event.target.getAttribute('data-index'), event);
            },
            toggleExpand(col, record) {
                record.expand = !record.expand;
                this.$emit('on-expand', JSON.parse(JSON.stringify(record)), col);
            },
            toggleSelect(col, record) {
                record.isChecked = !record.isChecked;
                this.$emit('on-select', JSON.parse(JSON.stringify(record)), col);
                this.$emit('on-selection-change', this.getSelection(), col);
            },
            selectAll(col) {
                const status = this.isSelectAll();
                this.records.filter(r => !r.disabled).forEach(r => r.isChecked = status);
                this.$emit('on-selection-change', this.getSelection(), col);
            },
            isSelectAll() {
                if (!this.data.length) return false;
                if (!this.data.find(item => !item.disabled)) return false;
                return !!this.records.find(record => !record.isChecked && !record.disabled);
            },
            getSelection() {
                return JSON.parse(JSON.stringify(this.records.filter(r => r.isChecked)));
            },
            getRow(index) {
                return document.querySelector(`.grid-table div.row[data-index='${index}']`);
            },
            handleMouse(record, status) {
                if (this.disabledHover) return;
                record.hover = status;
            }
        }
    }
</script>

<style scoped lang="less">
    .grid-table {
        border: 1px solid #dddee1;
        border-bottom: 0;
        .body {
            .row {
                border-bottom: 1px solid #e9eaec;
                .wrapper {
                    display: table-cell;
                    vertical-align: middle;
                    height: 40px;
                    .cell {
                        float: left;
                        display: inline-block;
                        word-wrap: break-word;
                        text-overflow: ellipsis;
                        text-align: left;
                        padding: 5px 14px;
                    }
                    .cell-with-expand {
                        cursor: pointer;
                        text-align: center;
                    }
                }
            }
            .row-hover {
                background: #ebf7ff;
            }
        }
        .header {
            height: 36px;
            display:table-cell;
            vertical-align:middle;
            background-color: #f8f8f9;
            border-bottom: 1px solid #e9eaec;
            > div {
                float: left;
                padding: 0 18px;
                overflow: hidden;
                font-weight: bold;
                display: inline-block;
                white-space: nowrap;
            }
        }
        .expand {
            border-top: 1px solid #e9eaec;
            min-height: 50px;
            padding: 15px 50px;
            background: #f8f8f9;
        }
        .no-data {
            text-align: center;
            border-bottom: 1px solid #e9eaec;
            padding: 18px 0;
        }
    }
</style>