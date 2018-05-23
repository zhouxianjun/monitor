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
        { path: 'alarm/rule/edit', title: '编辑报警规则', name: 'alarm-rule-edit', component: () => import('@/views/alarm/rule/edit.vue') },
        { path: 'monitor/http/edit', title: '编辑HTTP监控', name: 'monitor-http-edit', component: () => import('@/views/site-monitor/http/edit.vue') }
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [{
    path: '/app',
    icon: 'key',
    name: 'app',
    title: '应用管理',
    component: Main,
    children: [
        { path: 'index', title: '应用管理', name: 'app_index', component: () => import('@/views/app/app.vue') }
    ]
}, {
    path: '/alarm',
    icon: 'key',
    name: 'alarm',
    title: '报警服务',
    component: Main,
    children: [
        { path: 'rule', title: '报警规则', name: 'rule_index', component: () => import('@/views/alarm/rule/index.vue') },
        { path: 'history', title: '报警历史', name: 'history_index', component: () => import('@/views/alarm/history/index.vue') }
    ]
}, {
    path: '/site-monitor',
    icon: 'key',
    name: 'site-monitor',
    title: '站点监控',
    component: Main,
    children: [
        { path: 'index', title: '站点列表', name: 'index', component: () => import('@/views/site-monitor/index.vue') }
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
