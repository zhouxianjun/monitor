import { DateFormat } from '@/libs/common';
import { opt } from '@/libs/dic';
export default {
    methods: {
        renderCurrent (h, params, convert = (val => val)) {
            return h('span', convert(params.row[params.column.key]));
        },
        renderDate (h, params, convert = (val => val)) {
            return h('span', DateFormat(convert(params.row[params.column.key])));
        },
        renderDateRange (h, params) {
            return (start, end) => h('span', `${DateFormat(params.row[start])}~${DateFormat(params.row[end])}`);
        },
        renderAppend (h, params) {
            return (append, convert = (val => val)) => h('span', `${convert(params.row[params.column.key])}${append}`);
        },
        renderStatus (h, params) {
            return this.renderStatusDiy(h, params)();
        },
        renderStatusDiy (h, params) {
            let status = params.row[params.column.key];
            return (trueTxt = '启用', falseTxt = '禁用', convert = (status => status)) => {
                status = convert(status);
                return h('Tag', {
                    props: {
                        type: 'dot',
                        color: status === true ? 'success' : 'error'
                    }
                }, status === true ? trueTxt : falseTxt);
            };
        },
        renderStatusDic (h, params) {
            let status = params.row[params.column.key];
            return (color, dic) => h('Tag', {
                props: {
                    type: 'border',
                    color: color[status]
                }
            }, opt(dic, status).name);
        },
        renderDic (h, params) {
            return (dic, prop = 'id') => h('span', opt(dic, params.row[params.column.key], {name: ''}, prop).name);
        },
        renderListPop (h, params, convert = (val => val)) {
            let value = convert(params.row[params.column.key]);
            value = Array.isArray(value) ? value : Array.of(value);
            return h('Poptip', {
                props: {
                    trigger: 'hover',
                    placement: 'bottom'
                }
            }, [
                h('Tag', value.length),
                h('div', {
                    slot: 'content'
                }, [
                    h('ul', value.map(item => h('li', {
                        style: {
                            textAlign: 'center',
                            padding: '4px'
                        }
                    }, item)))
                ])
            ]);
        },
        renderJoin (h, params) {
            return (...args) => h('span', args.map(arg => params.row[arg]).join('/'));
        },
        renderLink (h, params, fn, convert = (val => val)) {
            return h('a', {
                on: {
                    click: fn
                }
            }, convert(params.row[params.column.key]));
        },
        renderBtnPop (h, title, text, type, fn, config) {
            return h('Poptip', {
                props: {
                    confirm: true,
                    title,
                    placement: 'top',
                    transfer: true
                },
                on: {
                    'on-ok': fn
                }
            }, [
                h('Button', Object.assign({
                    props: {
                        type: type,
                        size: 'small'
                    }
                }, config), text)
            ]);
        },
        renderSwitchBtnPop (h, bool, fn, config) {
            return this.renderBtnPop(h, `您确定要${bool ? '禁用' : '启用'}这条数据吗?`, bool ? '禁用' : '启用', bool ? 'warning' : 'success', fn, config);
        }
    }
};
