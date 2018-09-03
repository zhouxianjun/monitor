import { DateFormat } from '@/libs/common';
import { opt } from '@/libs/dic';
import iView from 'iview';
import merge from 'merge';

export default {
    methods: {
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
                return (
                    <iView.Tag type="dot" color={ status === true ? 'success' : 'error' }>
                        { status === true ? trueTxt : falseTxt }
                    </iView.Tag>
                );
            };
        },
        renderStatusDic (h, params) {
            const status = params.row[params.column.key];
            return (color, dic) => <iView.Tag type="border" color={color[status]}>{ opt(dic, status).name }</iView.Tag>;
        },
        renderDic (h, params) {
            return (dic, prop = 'id') => h('span', opt(dic, params.row[params.column.key], {name: ''}, prop).name);
        },
        renderListPop (h, params, convert = (val => val)) {
            let value = convert(params.row[params.column.key]);
            value = Array.isArray(value) ? value : Array.of(value);
            return (
                <iView.Poptip trigger="hover" placement="bottom">
                    <iView.Tag>{ value.length }</iView.Tag>
                    <div slot="content">
                        <ul>
                            { value.map(item => <li style="text-align: center; padding: 4px;">{ item }</li>) }
                        </ul>
                    </div>
                </iView.Poptip>
            );
        },
        renderJoin (h, params) {
            return (...args) => h('span', args.map(arg => params.row[arg]).join('/'));
        },
        renderLink (h, params, fn, convert = (val => val)) {
            return <a onClick={ fn }>{ convert(params.row[params.column.key]) }</a>;
        },
        renderBtnPop (h, title, text, fn, config) {
            return (
                <iView.Poptip confirm={ true } title={ title } placement="top" transfer={ true } onOn-ok={ fn }>
                    <iView.Button { ...merge.recursive(false, { props: {size: 'small'} }, config) }>{ text }</iView.Button>
                </iView.Poptip>
            );
        },
        renderActions (h, params, actions) {
            return actions.map(action => {
                const { config = {}, text, click } = action;
                const cfg = { props: config, style: config.style };
                switch (action.type) {
                    case 'delete':
                        const { title = '您确定要删除这条数据吗？' } = action;
                        return this.renderBtnPop(h, title, text || '删除', click, merge.recursive(false, {props: {type: 'error'}}, cfg));
                    case 'switch':
                        const { falseText = '禁用', trueText = '启用', bool } = action;
                        const name = bool ? falseText : trueText;
                        const type = bool ? 'warning' : 'success';
                        return this.renderBtnPop(h, `您确定要${name}这条数据吗?`, name, click, merge.recursive(false, { props: { type } }, cfg));
                    default:
                        return <iView.Button { ...merge.recursive(false, {props: {size: 'small', type: 'primary'}}, cfg) } onClick={ click }>{ text }</iView.Button>;
                }
            });
        }
    }
};
