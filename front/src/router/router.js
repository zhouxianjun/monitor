import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: 'Login - 登录'
    },
    component: () => import('@/views/login.vue')
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: () => import('@/views/error-page/404.vue')
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: () => import('@//views/error-page/403.vue')
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: () => import('@/views/error-page/500.vue')
};

export const locking = {
    path: '/locking',
    name: 'locking',
    component: () => import('@/views/main-components/lockscreen/components/locking-page.vue')
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    component: Main,
    children: [
        { path: 'home', title: {i18n: 'home'}, name: 'home_index', component: () => import('@/views/home/home.vue') },
        { path: 'ownspace', title: '个人中心', name: 'ownspace_index', component: () => import('@/views/own-space/own-space.vue') },
        { path: 'message', title: '消息中心', name: 'message_index', component: () => import('@/views/message/message.vue') },
        { path: 'alarm/rule/edit', title: '报警规则', name: 'alarm-rule-edit', component: () => import('@/views/alarm/rule/edit.vue') },
        { path: 'monitor/http/edit', title: 'HTTP监控', name: 'monitor-http-edit', component: () => import('@/views/site-monitor/http/edit.vue') },
        { path: 'monitor/reported/edit', title: '上报监控', name: 'monitor-reported-edit', component: () => import('@/views/site-monitor/reported/edit.vue') },
        { path: 'log-service/watcher/edit', title: '日志监控', name: 'log-watcher-edit', component: () => import('@/views/log/watcher/edit.vue') },
        { path: 'log-service/watcher/list', title: '日志监控记录', name: 'log-watcher-list', component: () => import('@/views/log/watcher/list.vue') }
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [{
    path: '/app',
    icon: 'android-apps',
    name: 'app',
    title: '应用管理',
    component: Main,
    children: [
        { path: 'index', title: '应用管理', name: 'app-list', component: () => import('@/views/app/app.vue') }
    ]
}, {
    path: '/alarm',
    icon: 'android-alert',
    name: 'alarm',
    title: '报警服务',
    component: Main,
    children: [
        { path: 'rule', icon: 'ios-settings', title: '报警规则', name: 'rule-list', component: () => import('@/views/alarm/rule/index.vue') },
        { path: 'history', icon: 'ios-list' , title: '报警历史', name: 'history-list', component: () => import('@/views/alarm/history/index.vue') }
    ]
},{
    path: '/contacts',
    icon: 'android-contacts',
    name: 'contacts',
    title: '联系人',
    component: Main,
    children: [
        { path: 'list', icon: 'android-contact', title: '联系人', name: 'contacts-list', component: () => import('@/views/contacts/list.vue') },
        { path: 'group', icon: 'android-contacts', title: '联系人组', name: 'contacts_group', component: () => import('@/views/contacts/group/index.vue') }
    ]
}, {
    path: '/site-monitor',
    icon: 'monitor',
    name: 'site-monitor',
    title: '站点监控',
    component: Main,
    children: [
        { path: 'index', title: '站点列表', name: 'monitor-index', component: () => import('@/views/site-monitor/index.vue') }
    ]
}, {
    path: '/log-service',
    icon: 'log-out',
    name: 'log-service',
    title: '日志服务',
    component: Main,
    children: [
        { path: 'search', title: '日志搜索', name: 'search', component: () => import('@/views/log/search.vue') },
        { path: 'watcher', title: '日志监控', name: 'watcher', component: () => import('@/views/log/watcher/index.vue') }
    ]
}];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter,
    page500,
    page403,
    page404
];
