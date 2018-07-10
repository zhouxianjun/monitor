'use strict';
exports.getAttribute = (dic, prop = 'id', value, isArray = false) => {
    let result = [];
    for (let item of dic) {
        if (item[prop] === value) {
            if (!isArray) return item;
            result.push(item);
        }
    }
    return isArray ? result : null;
};
exports.calc = (dic, value, valueProp = 'id') => {
    let v = parseInt(value.toString(2));
    return dic.filter(r => v & parseInt(r[valueProp].toString(2)));
};
exports.Status = [{
    id: 0,
    name: '启用'
}, {
    id: 1,
    name: '禁用'
}];
exports.StatusDiy = (...diy) => exports.Status.map(item => Object.assign(item, {name: diy.shift()}));
exports.HistoryStatus = [{
    id: 1,
    name: '通道沉默'
}, {
    id: 2,
    name: '报警发生'
}, {
    id: 3,
    name: '恢复正常'
}];
exports.NoticeType = [{
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
exports.SiteType = [{
    id: 1,
    name: 'HTTP'
}, {
    id: 2,
    name: '上报'
}];
exports.MethodType = [{
    id: 'GET',
    name: 'GET'
}, {
    id: 'POST',
    name: 'POST'
}, {
    id: 'HEAD',
    name: 'HEAD'
}];
exports.ESFilterType = [{
    id: 'range',
    name: '范围'
}, {
    id: 'match',
    name: '匹配'
}];
exports.SortType = [{
    id: 'asc',
    name: '正序'
}, {
    id: 'desc',
    name: '倒序'
}];
exports.FuncType = [{
    id: 600,
    name: '菜单'
}, {
    id: 601,
    name: '功能'
}, {
    id: 602,
    name: '目录'
}];
exports.EpType = [{
    id: 1,
    name: '平台商'
}, {
    id: 2,
    name: '供应商'
}, {
    id: 4,
    name: '销售商'
}];
