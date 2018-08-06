import { setToken, getToken } from '@/libs/util';

export default {
    state: {
        userName: '',
        userId: '',
        avatorImgPath: '',
        token: getToken(),
        access: ''
    },
    mutations: {
        setAvator (state, avatorPath) {
            state.avatorImgPath = avatorPath;
        },
        setUserId (state, id) {
            state.userId = id;
        },
        setUserName (state, name) {
            state.userName = name;
        },
        setAccess (state, access) {
            state.access = access;
        },
        setToken (state, token) {
            state.token = token;
            setToken(token);
        }
    },
    actions: {
        // 登录
        async handleLogin ({ commit }, {userName, password}) {
            userName = userName.trim();
            commit('setToken', 'login');
        },
        // 退出登录
        async handleLogOut ({ state, commit }) {
            commit('setToken', '');
            commit('setAccess', []);
        },
        // 获取用户相关信息
        async getUserInfo ({ state, commit }) {
            let data = {
                avator: '',
                user_name: '管理员',
                access: 1
            };
            return data;
        }
    }
};
