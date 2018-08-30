import { setToken, getToken } from '@/libs/util';

export default {
    state: {
        username: '',
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
            state.username = name;
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
        async handleLogin ({ commit }, {username, password}) {
            username = username.trim();
            const result = await this._vm.fetch('/open/login', {
                method: 'post',
                data: {
                    username,
                    password
                }
            });
            if (result) {
                commit('setToken', 'login');
            }
            return result;
        },
        // 退出登录
        async handleLogOut ({ state, commit }) {
            await this._vm.fetch('/open/logout');
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
