import dayjs from 'dayjs';
import { getAttribute } from './dic';

const Common = {
    valid: {
        ip (rule, value, callback) {
            if (rule.required && (value === undefined || value === '' || value.length <= 0)) {
                callback(new Error('不能为空'));
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
    dateFormat (val, format = 'YYYY-MM-DD HH:mm:ss') {
        return val ? dayjs(Number(val)).format(format) : '-';
    },
    statusFormat (val, trueTxt = '启用', falseTxt = '禁用') {
        return `<span class="${val === true ? 'text-green' : 'text-muted'}">${val === true ? trueTxt : falseTxt}</span>`;
    },
    emailFormat (val) {
        return `<a href="mailto:${val}">${val}</a>`;
    },
    RENDER: {
        DATE (h, params, convert = (val => val)) {
            return h('span', Common.dateFormat(convert(params.row[params.column.key])));
        },
        DATE_RANGE (h, params) {
            return (start, end) => h('span', `${Common.dateFormat(params.row[start])}~${Common.dateFormat(params.row[end])}`);
        },
        APPEND (h, params) {
            return append => h('span', `${params.row[params.column.key]}${append}`);
        },
        STATUS (h, params) {
            let status = params.row[params.column.key];
            return h('Tag', {
                props: {
                    type: 'dot',
                    color: status === true ? 'success' : 'error'
                }
            }, status === true ? '启用' : '禁用');
        },
        STATUS_DIY (h, params) {
            let status = params.row[params.column.key];
            return function (trueTxt = '启用', falseTxt = '禁用', value = status) {
                return h('Tag', {
                    props: {
                        type: 'dot',
                        color: value === true ? 'success' : 'error'
                    }
                }, value === true ? trueTxt : falseTxt);
            };
        },
        STATUS_DIC (h, params) {
            let status = params.row[params.column.key];
            return (color, dic) => h('Tag', {
                props: {
                    type: 'border',
                    color: color[status]
                }
            }, getAttribute(dic, 'id', status).name);
        },
        DIC (h, params) {
            return (dic, prop = 'id') => {
                let val = getAttribute(dic, prop, params.row[params.column.key]);
                return h('span', val ? val.name : '');
            };
        },
        POPTIP (h, params) {
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
        SPLIT_COUNT (h, params) {
            return h('span', Common.tableSplitCount(params));
        },
        SPLIT_COUNT_POP (h, params) {
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
        JOIN (h, params) {
            return (...args) => h('span', args.map(arg => params.row[arg]).join('/'));
        }
    },
    renderTree (data, fn) {
        if (Array.isArray(data)) {
            data.forEach(item => {
                Reflect.apply(fn, data, [item]);
                if (item.children && Array.isArray(item.children)) {
                    this.renderTree(item.children, fn);
                }
            });
        }
    },
    makeTree (array, pid = 0, parentProp = 'pid', idProp = 'id', childProp = 'children', renderer) {
        let result = [];
        let temp = [];
        for (let item of array) {
            if (item[parentProp] === pid) {
                let clone = Object.assign({}, item);
                result.push(clone);
                temp = this.makeTree(array, item[idProp], parentProp, idProp, childProp);
                if (temp.length > 0) {
                    clone[childProp] = temp;
                }
                if (typeof renderer === 'function') {
                    renderer(clone);
                }
            }
        }
        return result;
    },
    tableSplitCount (params) {
        let val = params.row[params.column.key] || '';
        return val ? val.split(',').length : 0;
    },
    stringToNumber (vo) {
        if (!vo) return vo;
        if (Array.isArray(vo)) {
            vo.forEach((item, index) => vo[index] = Common.stringToNumber(item));
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
            } else {
                if (!isNaN(vo) && vo.length < 13) {
                    vo = Number(vo);
                }
            }
        }
        return vo;
    },
    dynamicObjKey (obj, key = 'key', value = 'value') {
        const result = {};
        result[obj[key]] = obj[value];
        return result;
    },
    tableColBtn (h, params, fn) {
        return h('a', {
            on: {
                click: fn
            }
        }, params.row[params.column.key]);
    },
    tableBtnPop (h, title, text, type, fn, config) {
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
    switchTableBtnPop: (h, bool, fn, config) => Common.tableBtnPop(h, `您确定要${bool ? '禁用' : '启用'}这条数据吗?`, bool ? '禁用' : '启用', bool ? 'warning' : 'success', fn, config),
    toUnderScoreCase (str) {
        return str.replace(/([A-Z])/g, '_$1').toLowerCase();
    },
    toUnderScoreCaseKeys (vo) {
        if (!vo) return vo;
        if (Array.isArray(vo)) {
            return vo.map(v => Common.toUnderScoreCaseKeys(v));
        }
        if (typeof vo === 'object') {
            return Object.keys(vo).reduce((result, key) => {
                let value = vo[key];
                if (typeof value === 'object' || Array.isArray(value)) {
                    value = Common.toUnderScoreCaseKeys(value);
                }

                result[Common.toUnderScoreCase(key)] = value;
                return result;
            }, {});
        }
        return vo;
    },
    toCamelCase (str) {
        return str.replace(/_([a-z])/g, function ($1) { return $1.toUpperCase().replace('_', ''); });
    },
    toCamelCaseKeys (vo) {
        if (!vo) return vo;
        if (Array.isArray(vo)) {
            return vo.map(v => Common.toCamelCaseKeys(v));
        }
        if (typeof vo === 'object') {
            return Object.keys(vo).reduce((result, key) => {
                let value = vo[key];
                if (typeof value === 'object' || Array.isArray(value)) {
                    value = Common.toCamelCaseKeys(value);
                }

                result[Common.toCamelCase(key)] = value;
                return result;
            }, {});
        }
        return vo;
    },
    diffVo (a, b, keys = '*', fn) {
        Object.keys(a).forEach(k => {
            if (Reflect.has(b, k) && a[k] !== b[k] && (keys === '*' || keys.includes(k))) {
                a._diff = a._diff || {};
                a._diff[k] = b[k];
                if (typeof fn === 'function') {
                    fn(a, k, [a[k], b[k]]);
                }
            }
        });
        return a;
    }
};
export default Common;
