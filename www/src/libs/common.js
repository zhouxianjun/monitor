import dayjs from 'dayjs';

/**
 * 把vo对象中的字符串数字转为Number
 * 当数字>=13位则不处理
 * @param { * } vo 对象
 */
export const StringToNumber = vo => {
    if (!vo) return vo;
    if (Array.isArray(vo)) {
        vo.forEach((item, index) => vo[index] = StringToNumber(item));
    } else {
        if (typeof vo === 'object') {
            Reflect.ownKeys(vo).forEach(key => {
                let value = vo[key];
                if (!value || value === '' || value === true || value === false || typeof value === 'function') return;
                if (Array.isArray(value) || typeof value === 'object') {
                    vo[key] = StringToNumber(value);
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
};

/**
 * 数字格式化展示 例: 123456 => 123,456
 * @param {Number | String} num 源数字
 * @param {Number} count 多少位分隔
 * @param {String} sp 分隔符
 */
export const NumberFormat = (num, count = 3, sp = ',') => {
    return num.toString().replace(new RegExp(`(\\d)(?=(?:\\d{${count}})+$)`, 'g'), `$1${sp}`);
};

/**
 * 强制小数位 例4.5 => 4.50; 1 => 1.00; 1.123 => 1.12
 * @param {Number | String} num 源数字
 * @param {Number} count 小数位数
 */
export const ForceDecimal = (num, count = 2) => {
    let str = num.toString();
    let [ start, end = '' ] = str.split('.');
    return `${start}.${end.length > count ? end.substring(0, count) : end.padEnd(count, '0')}`;
};

/**
 * 手机号码验证
 * @param {Number | String} mobile 手机号码
 * @returns Boolean
 */
export const ValidateMobile = mobile => {
    return mobile && mobile.match(/^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/);
};

/**
 * 时间戳格式化
 * @param {String | Number} val 时间戳
 * @param {String} format 格式
 */
export const DateFormat = (val, format = 'YYYY-MM-DD HH:mm:ss') => {
    return val ? dayjs(Number(val)).format(format) : '-';
};

/**
 * 邮箱链接
 * @param {String} val 邮箱地址
 */
export const EmailLink = (val) => {
    return `<a href="mailto:${val}">${val}</a>`;
};

/**
 * 把数组转换为树形结构
 * @param {Array} array 源数据
 * @param {String | Number} pid 根ID 默认:0
 * @param {String} parentProp 父级属性名称 默认:pid
 * @param {String} idProp ID属性名称 默认:id
 * @param {String} childProp 下级属性名称 默认:children
 * @param {Function} renderer 渲染函数
 */
export const MakeTree = (array, pid = 0, parentProp = 'pid', idProp = 'id', childProp = 'children', renderer) => {
    let result = [];
    let temp = [];
    for (let item of array) {
        if (item[parentProp] === pid) {
            let clone = Object.assign({}, item);
            result.push(clone);
            temp = MakeTree(array, item[idProp], parentProp, idProp, childProp);
            if (temp.length > 0) {
                clone[childProp] = temp;
            }
            if (typeof renderer === 'function') {
                renderer(clone);
            }
        }
    }
    return result;
};

/**
 * 驼峰转下划线命名
 */
export const ToUnderScoreCase = (str) => {
    return str.replace(/([A-Z])/g, '_$1').toLowerCase();
};

/**
 * 对象属性驼峰转下划线命名
 */
export const ToUnderScoreCaseKeys = (vo) => {
    if (!vo) return vo;
    if (Array.isArray(vo)) {
        return vo.map(v => ToUnderScoreCaseKeys(v));
    }
    if (typeof vo === 'object') {
        return Object.keys(vo).reduce((result, key) => {
            let value = vo[key];
            if (typeof value === 'object' || Array.isArray(value)) {
                value = ToUnderScoreCaseKeys(value);
            }

            result[ToUnderScoreCase(key)] = value;
            return result;
        }, {});
    }
    return vo;
};

/**
 * 下划线转驼峰命名
 */
export const ToCamelCase = (str) => {
    return str.replace(/_([a-z])/g, function ($1) { return $1.toUpperCase().replace('_', ''); });
};

/**
 * 对象属性下划线转驼峰命名
 */
export const ToCamelCaseKeys = (vo) => {
    if (!vo) return vo;
    if (Array.isArray(vo)) {
        return vo.map(v => ToCamelCaseKeys(v));
    }
    if (typeof vo === 'object') {
        return Object.keys(vo).reduce((result, key) => {
            let value = vo[key];
            if (typeof value === 'object' || Array.isArray(value)) {
                value = ToCamelCaseKeys(value);
            }

            result[ToCamelCase(key)] = value;
            return result;
        }, {});
    }
    return vo;
};

/**
 * 两个对象
 * @param {Object} a 对象 A
 * @param {Object} b 对象 B
 * @param {Array | String} keys 需要比对的KEY,如果为 * 则所有key
 * @param {Function} fn 每个属性回调函数
 */
export const diffVo = (a, b, keys = '*', fn) => {
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
};
