import Main from '@/views/main';

/**
 * 除了原生参数外可配置的参数:
 * meta: {
 *  hideInMenu: (false) 设为true后在左侧菜单不会显示该页面选项
 *  notCache: (false) 设为true后页面不会缓存
 *  access: (null) 可访问该页面的权限数组，当前路由设置的权限会影响子路由
 *  icon: (-) 该页面在左侧菜单、面包屑和标签导航处显示的图标，如果是自定义图标，需要在图标名称前加下划线'_'
 * }
 */

export default [
    {
        path: '/login',
        name: 'login',
        meta: {
            title: 'Login - 登录',
            hideInMenu: true
        },
        component: () => import('@/views/login.vue')
    },
    {
        path: '/',
        name: '_home',
        redirect: '/home',
        component: Main,
        meta: {
            hideInMenu: true,
            notCache: true
        },
        children: [
            {
                path: '/home',
                name: 'home',
                meta: {
                    hideInMenu: true,
                    title: '首页',
                    notCache: true
                },
                component: () => import('@/views/single-page/home.vue')
            }
        ]
    },
    {
        path: '/app',
        name: 'app',
        component: Main,
        meta: {
            title: '应用管理'
        },
        children: [
            {
                path: 'list',
                name: 'AppList',
                meta: {
                    icon: 'md-apps',
                    title: '应用列表'
                },
                component: () => import('@/views/app/app.vue')
            },
            {
                path: 'version',
                name: 'AppVersion',
                meta: {
                    icon: 'md-apps',
                    title: '版本列表'
                },
                component: () => import('@/views/app/version.vue')
            }
        ]
    },
    {
        path: '/app-service',
        name: 'app-service',
        meta: {
            icon: 'md-apps',
            title: '应用服务'
        },
        component: Main,
        children: [
            {
                path: 'auth',
                meta: {
                    title: '权限配置'
                },
                name: 'AppServiceStandaloneAuth',
                component: () =>
                    import('@/views/app/service/standalone-auth.vue')
            }
        ]
    },
    {
        path: '/alarm',
        name: 'alarm',
        meta: {
            icon: 'md-alert',
            title: '报警服务'
        },
        component: Main,
        children: [
            {
                path: 'rule',
                meta: {
                    icon: 'ios-cog',
                    title: '报警规则'
                },
                name: 'AlarmRule',
                component: () => import('@/views/alarm/rule/index.vue')
            },
            {
                path: 'rule/edit',
                meta: {
                    title: '编辑',
                    hideInMenu: true
                },
                name: 'AlarmRuleEdit',
                component: () => import('@/views/alarm/rule/edit.vue')
            },
            {
                path: 'history',
                meta: {
                    icon: 'ios-list',
                    title: '报警历史'
                },
                name: 'AlarmHistory',
                component: () => import('@/views/alarm/history/index.vue')
            }
        ]
    },
    {
        path: '/contacts',
        name: 'contacts',
        meta: {
            icon: 'md-contacts',
            title: '联系人'
        },
        component: Main,
        children: [
            {
                path: 'list',
                meta: {
                    icon: 'md-contact',
                    title: '联系人'
                },
                name: 'ContactsList',
                component: () => import('@/views/contacts/list.vue')
            },
            {
                path: 'group',
                meta: {
                    icon: 'md-contacts',
                    title: '联系人组'
                },
                name: 'ContactsGroup',
                component: () => import('@/views/contacts/group/index.vue')
            }
        ]
    },
    {
        path: '/log-service',
        name: 'log-service',
        meta: {
            icon: 'md-log-out',
            title: '日志服务'
        },
        component: Main,
        children: [
            {
                path: 'search',
                meta: {
                    title: '日志搜索'
                },
                name: 'LogSearch',
                component: () => import('@/views/log/search.vue')
            },
            {
                path: 'watcher/edit',
                meta: {
                    title: '编辑日志监控',
                    hideInMenu: true
                },
                name: 'LogWatcherEdit',
                component: () => import('@/views/log/watcher/edit.vue')
            },
            {
                path: 'watcher/list',
                meta: {
                    title: '日志监控记录',
                    hideInMenu: true
                },
                name: 'LogWatcherList',
                component: () => import('@/views/log/watcher/list.vue')
            },
            {
                path: 'watcher',
                meta: {
                    title: '日志监控'
                },
                name: 'LogWatcher',
                component: () => import('@/views/log/watcher/index.vue')
            }
        ]
    },
    {
        path: '/site-monitor',
        meta: {
            name: 'site-monitor',
            title: '站点监控'
        },
        component: Main,
        children: [
            {
                path: 'list',
                meta: {
                    title: '站点列表',
                    icon: 'ios-desktop'
                },
                name: 'SiteMonitorList',
                component: () => import('@/views/site-monitor/index.vue')
            },
            {
                path: 'http/edit',
                meta: {
                    title: 'HTTP监控',
                    hideInMenu: true
                },
                name: 'SiteMonitorHttpEdit',
                component: () => import('@/views/site-monitor/http/edit.vue')
            },
            {
                path: 'reported/edit',
                meta: {
                    title: '上报监控',
                    hideInMenu: true
                },
                name: 'SiteMonitorReportedEdit',
                component: () => import('@/views/site-monitor/reported/edit.vue')
            }
        ]
    },
    {
        path: '/401',
        name: 'error_401',
        meta: {
            hideInMenu: true
        },
        component: () => import('@/views/error-page/401.vue')
    },
    {
        path: '/500',
        name: 'error_500',
        meta: {
            hideInMenu: true
        },
        component: () => import('@/views/error-page/500.vue')
    },
    {
        path: '*',
        name: 'error_404',
        meta: {
            hideInMenu: true
        },
        component: () => import('@/views/error-page/404.vue')
    }
];
