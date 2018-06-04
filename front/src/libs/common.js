import dayjs from 'dayjs';
import {getAttribute} from './dic';

const Common = {
    valid: {
        ip(rule, value, callback) {
            if (rule.required && (value === undefined || value === '' || value.length <= 0)) {
                callback(new Error(`不能为空`));
                return;
            }
            if (value) {
                value = Array.isArray(value) ? value : value.split(',');
                for (let val of value) {
                    let split = val.split('.');
                    if (split.length < 4 && !val.endsWith('*')) {
                        callback(new Error(`${val} 不是正确的IP地址`));
                        return;
                    }
                    for (let s of split) {
                        if (!/1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d\*\d|\*\d|\d\*|\d|\*/.test(s) || s.length > 3) {
                            callback(new Error(`${val} 不是正确的IP地址`));
                            return;
                        }
                    }
                }
            }
            callback();
        }
    },
    dateFormat(val, format = 'YYYY-MM-DD HH:mm:ss') {
        return val ? dayjs(Number(val)).format(format) : '-';
    },
    statusFormat(val, trueTxt = '启用', falseTxt = '禁用') {
        return `<span class="${val === true ? 'text-green' : 'text-muted'}">${val === true ? trueTxt : falseTxt}</span>`;
    },
    emailFormat(val) {
        return `<a href="mailto:${val}">${val}</a>`;
    },
    RENDER: {
        DATE(h, params) {
            return h('span', Common.dateFormat(params.row[params.column.key]));
        },
        DATE_RANGE(h, params) {
            return (start, end) => h('span', `${Common.dateFormat(params.row[start])}~${Common.dateFormat(params.row[end])}`);
        },
        APPEND(h, params) {
            return append => h('span', `${params.row[params.column.key]}${append}`);
        },
        STATUS(h, params) {
            let status = params.row[params.column.key];
            return h('Tag', {
                props: {
                    type: 'dot',
                    color: status === true ? 'green' : 'red'
                }
            }, status === true ? '启用' : '禁用');
        },
        STATUS_DIY(h, params) {
            let status = params.row[params.column.key];
            return function (trueTxt = '启用', falseTxt = '禁用', value = status) {
                return h('Tag', {
                    props: {
                        type: 'dot',
                        color: value === true ? 'green' : 'red'
                    }
                }, value === true ? trueTxt : falseTxt);
            };
        },
        STATUS_DIC(h, params) {
            let status = params.row[params.column.key];
            return (color, dic) => h('Tag', {
                props: {
                    type: 'border',
                    color: color[status]
                }
            }, getAttribute(dic, 'id', status).name);
        },
        POPTIP(h, params) {
            return h('Poptip', {
                props: {
                    trigger: 'hover',
                    content: params.row[params.column.key],
                    placement: 'top-start'
                }
            }, [
                h('span', params.row[params.column.key])
            ]);
        },
        SPLIT_COUNT(h, params) {
            return h('span', Common.tableSplitCount(params));
        },
        SPLIT_COUNT_POP(h, params) {
            let val = params.row[params.column.key] || '';
            let array = val ? val.split(',') : [];
            return h('Poptip', {
                props: {
                    trigger: 'hover',
                    placement: 'bottom'
                }
            }, [
                h('Tag', array.length),
                h('div', {
                    slot: 'content'
                }, [
                    h('ul', array.map(item => {
                        return h('li', {
                            style: {
                                textAlign: 'center',
                                padding: '4px'
                            }
                        }, item);
                    }))
                ])
            ]);
        },
        JOIN(h, params) {
            return (...args) => h('span', args.map(arg => params.row[arg]).join('/'));
        }
    },
    tableSplitCount(params) {
        let val = params.row[params.column.key] || '';
        return val ? val.split(',').length : 0;
    },
    stringToNumber(vo) {
        if (!vo) return vo;
        if (Array.isArray(vo)) {
            vo.forEach(item => {
                Common.stringToNumber(item);
            });
        } else {
            if (typeof vo === 'object') {
                Reflect.ownKeys(vo).forEach(key => {
                    let value = vo[key];
                    if (!value || value === '' || value === true || value === false || typeof value === 'function') return;
                    if (Array.isArray(value) || typeof value === 'object') {
                        vo[key] = Common.stringToNumber(value);
                        return;
                    }
                    if (!isNaN(value) && value.length < 13) {
                        vo[key] = Number(value);
                    }
                });
            }
        }
        return vo;
    },
    dynamicObjKey(obj, key = 'key', value = 'value') {
        const result = {};
        result[obj[key]] = obj[value];
        return result;
    },
    tableColBtn(h, params, fn) {
        return h('a', {
            on: {
                click: fn
            }
        }, params.row[params.column.key]);
    },
    tableBtnPop(h, title, text, type, fn, config) {
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
    switchTableBtnPop: (h, bool, fn, config) => Common.tableBtnPop(h, `您确定要${bool ? '禁用' : '启用'}这条数据吗?`, bool ? '禁用' : '启用', bool ? 'warning' : 'success', fn, config)
};
export default Common;