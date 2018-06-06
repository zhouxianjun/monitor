import uuid from 'uuid/v4';
const key = `mark-${uuid()}`;
function findNodes(el) {
    let walk = document.createTreeWalker(el, NodeFilter.SHOW_TEXT, null, false);
    let result = [];
    while (walk.nextNode()) result.push(walk.currentNode);
    return result;
}
function parseConfig(binding) {
    let cfg = Object.assign({
        caseSensitive: false
    }, binding.value);

    if (!cfg.keyword || !cfg.keyword.length) {
        return false;
    }
    cfg.class = Array.isArray(cfg.class) ? cfg.class : cfg.class ? [cfg.class] : [];
    cfg.keyword = Array.isArray(cfg.keyword) ? cfg.keyword : [cfg.keyword];
    // 最小的关键字长度(减少不必要的匹配)
    cfg.minLength = Math.max(...cfg.keyword.map(word => word ? word.length : 0));
    // 生成正则
    cfg.regex = new RegExp(`(${cfg.keyword.map(word => word.replace(/[-\\^$*+?.()|[\]{}/]/g, '\\$&')).join('|')})`, cfg.caseSensitive ? 'g' : 'ig');
    return cfg;
}
function remove(el) {
    el.querySelectorAll(`mark[data-mark-key=${key}]`).forEach(item => {
        let parent = item.parentNode;
        parent.replaceChild(item.firstChild, item);
        parent.normalize();
    })
}
function apply(el, cfg) {
    if (!cfg) return;
    findNodes(el).filter(node => node.textContent.length >= cfg.minLength).forEach(node => {
        let mark = document.createElement('mark');
        mark.innerHTML = node.textContent.replace(cfg.regex, `<mark data-mark-key="${key}" class="${cfg.class.join(' ')}">$1</mark>`);
        mark.childNodes.forEach((item) => node.parentNode.insertBefore(item.cloneNode(true), node));
        node.parentNode.removeChild(node);
    });
}
export default {
    bind(el, binding) {
        apply(el, parseConfig(binding));
    },
    componentUpdated(el, binding) {
        remove(el);
        apply(el, parseConfig(binding));
    },
    unbind(el) {
        remove(el);
    }
}