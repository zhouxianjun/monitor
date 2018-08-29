import Vue from 'vue';
import axios from 'axios';
import App from './App';
import router from './router';
import store from './store';
import iView from 'iview';
import 'iview/dist/styles/iview.css';
import '@/assets/icons/iconfont.css';
import './common.less';

import VueSlimScroll from 'vue-slimscroll';
import { StringToNumber } from './libs/common';

Vue.use(VueSlimScroll);

const BASE_URL = process.env.VUE_APP_SERVER_ADDRESS;

Vue.prototype.$super = function (options) {
    return new Proxy(options, {
        get: (options, name) => {
            if (options.methods && name in options.methods) {
                return options.methods[name].bind(this);
            }
        }
    });
};
/**
 * 拉取服务器信息
 * @param url
 * @param error
 * @param showError
 * @param config
 * @returns {Promise.<*>}
 */
Vue.prototype.fetch = async (url, config, showError = true, error) => {
    iView['LoadingBar'].start();
    let response = null;
    let result = null;
    try {
        if (!url) {
            throw new Error('url must be not null');
        }
        response = await axios(
            url,
            Object.assign(
                {
                    baseURL: BASE_URL,
                    withCredentials: true
                },
                config
            )
        );
        result = response ? response.data : null;
        if (
            !response ||
            response.status !== 200 ||
            !result ||
            !result.success
        ) {
            throw new Error(
                `fetch ${url} data ${JSON.stringify(config)} error`
            );
        }
        iView['LoadingBar'].finish();
        StringToNumber(result);
        return result;
    } catch (err) {
        iView['LoadingBar'].error();
        if (result && result.code && result.code === 99) {
            app.$router.replace('/login');
            return false;
        }
        showError &&
            iView['Notice'].error({ title: result ? result.msg : '操作失败' });
        if (typeof error === 'function') {
            Reflect.apply(error, response, result);
        }
        return false;
    }
};

Vue.use(iView);
Vue.config.productionTip = false;

/* eslint-disable no-new */
const app = new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
});
