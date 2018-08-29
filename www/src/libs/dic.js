'use strict';
export function getAttribute (dic, prop = 'id', value, isArray = false) {
    let result = [];
    for (let item of dic) {
        if (item[prop] === value) {
            if (!isArray) return item;
            result.push(item);
        }
    }
    return isArray ? result : null;
};
export function opt (dic, value, defVal = {}, prop = 'id') {
    let v = getAttribute(dic, prop, value, false);
    return v || defVal;
}
export function calc (dic, value, valueProp = 'id') {
    let v = parseInt(value.toString(2));
    return dic.filter(r => v & parseInt(r[valueProp].toString(2)));
};
export const Status = [{
    id: 0,
    name: '启用'
}, {
    id: 1,
    name: '禁用'
}];
export const StatusDiy = (...diy) => Status.map(item => Object.assign(item, {name: diy.shift()}));
export const HistoryStatus = [{
    id: 1,
    name: '通道沉默'
}, {
    id: 2,
    name: '报警发生'
}, {
    id: 3,
    name: '恢复正常'
}];
export const NoticeType = [{
    id: 1,
    name: '邮箱'
}, {
    id: 2,
    name: '微信公众号'
}, {
    id: 3,
    name: '回调地址'
}, {
    id: 4,
    name: '钉钉机器人'
}];
export const SiteType = [{
    id: 1,
    name: 'HTTP'
}, {
    id: 2,
    name: '上报'
}];
export const MethodType = [{
    id: 'GET',
    name: 'GET'
}, {
    id: 'POST',
    name: 'POST'
}, {
    id: 'HEAD',
    name: 'HEAD'
}];
export const ESFilterType = [{
    id: 'range',
    name: '范围'
}, {
    id: 'match',
    name: '匹配'
}];
export const SortType = [{
    id: 'asc',
    name: '正序'
}, {
    id: 'desc',
    name: '倒序'
}];
export const FuncType = [{
    id: 600,
    name: '菜单'
}, {
    id: 601,
    name: '功能'
}, {
    id: 602,
    name: '目录'
}];
export const EpType = [{
    id: 1,
    name: '平台商'
}, {
    id: 2,
    name: '供应商'
}, {
    id: 4,
    name: '销售商'
}];
