import Vue from 'vue';
import iView from 'iview';
import {router} from './router/index';
import {appRouter} from './router/router';
import store from './store';
import App from './app.vue';
import '@/locale';
import 'iview/dist/styles/iview.css';
import VueI18n from 'vue-i18n';
import axios from 'axios';

Vue.use(VueI18n);
Vue.use(iView);

const BASE_URL = baseURL;

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
    let response = null, result = null;
    try {
        response = await axios(url, Object.assign({
            baseURL: BASE_URL,
            withCredentials: true
        }, config));
        result = response ? response.data : null;
        if (!response || response.status !== 200 || !result || !result.success) {
            throw new Error(`fetch ${url} data ${JSON.stringify(config)} error`);
        }
        iView['LoadingBar'].finish();
        return result;
    } catch (err) {
        iView['LoadingBar'].error();
        if (result && result.code && result.code === 99) {
            app.$router.replace('/login');
            return false;
        }
        showError && iView['Notice'].error({title: result ? result.msg : '操作失败'});
        console.log(err.stack);
        if (typeof error === 'function') {
            Reflect.apply(error, response, result);
        }
        return false;
    }
};

new Vue({
    el: '#app',
    router: router,
    store: store,
    render: h => h(App),
    data: {
        currentPageName: ''
    },
    mounted () {
        this.currentPageName = this.$route.name;
        // 显示打开的页面的列表
        this.$store.commit('setOpenedList');
        this.$store.commit('initCachepage');
        // 权限菜单过滤相关
        this.$store.commit('updateMenulist');
    },
    created () {
        let tagsList = [];
        appRouter.map((item) => {
            if (item.children.length <= 1) {
                tagsList.push(item.children[0]);
            } else {
                tagsList.push(...item.children);
            }
        });
        this.$store.commit('setTagsList', tagsList);
    }
});
