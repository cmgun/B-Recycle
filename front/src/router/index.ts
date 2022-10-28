import { createRouter, createWebHashHistory, Router } from 'vue-router'
import Layout from '@/layout'
import { RouterTy } from '~/router'

export const constantRoutes: RouterTy = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/Login.vue'),
    hidden: true
  },
  // 消费者注册
  {
    path: '/customerRegist',
    component: () => import('@/views/regist/CustomerRegist.vue'),
    hidden: true
  },
  // 企业注册
  {
    path: '/entRegist',
    component: () => import('@/views/regist/EntRegist.vue'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404.vue'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        //using el svg icon, the elSvgIcon first when at the same time using elSvgIcon and icon
        meta: { title: 'Dashboard', elSvgIcon: 'Fold' }
      }
    ]
  },
  {
    path: '/setting-switch',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/setting-switch'),
        name: 'SettingSwitch',
        meta: { title: 'Setting Switch', icon: 'example' }
      }
    ]
  },
  {
    path: '/error-log',
    component: Layout,
    redirect: '/error-log/list',
    meta: { title: 'ErrorLog', icon: 'bug' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/error-log'),
        name: 'ErrorLog',
        meta: { title: 'Error Log' }
      },
      {
        path: 'error-log-test',
        component: () => import('@/views/error-log/ErrorLogTest.vue'),
        name: 'ErrorLogTest',
        meta: { title: 'ErrorLog Test' }
      }
    ]
  },
  {
    path: '/writing-demo',
    component: Layout,
    meta: { title: 'Writing Demo', icon: 'eye-open' },
    alwaysShow: true,
    children: [
      {
        path: 'hook',
        component: () => import('@/views/example/hook/Hook.vue'),
        name: 'Hook',
        meta: { title: 'Hook-Demo' }
      },
      {
        path: 'vuex-use',
        component: () => import('@/views/example/vuex-use/VuexUse.vue'),
        name: 'VuexUse',
        meta: { title: 'Vuex-Demo' }
      },
      {
        path: 'mock-test',
        component: () => import('@/views/example/mock-test/MockTest.vue'),
        name: 'MockTest',
        meta: { title: 'Mock-Demo' }
      },
      {
        path: 'svg-icon',
        component: () => import('@/views/example/svg-icon/SvgIcon.vue'),
        name: 'SvgIcon',
        meta: { title: 'Svg-Demo' }
      },
      {
        path: 'parent-children',
        component: () => import('@/views/example/parent-children/Parent.vue'),
        name: 'Parent',
        meta: { title: 'Parent-Children' }
      },
      {
        path: 'keep-alive',
        component: () => import('@/views/example/keep-alive'),
        name: 'KeepAlive',
        //cachePage: cachePage when page enter, default false
        //leaveRmCachePage: remove cachePage when page leave, default false
        meta: { title: 'Keep-Alive', cachePage: true, leaveRmCachePage: false }
      },
      {
        path: 'tab-keep-alive',
        component: () => import('@/views/example/keep-alive/TabKeepAlive.vue'),
        name: 'TabKeepAlive',
        //closeTabRmCache: remove cachePage when tabs close, default false
        meta: { title: 'Tab-Keep-Alive', cachePage: true, closeTabRmCache: true }
      },
      {
        path: 'router-demo-f',
        name: 'routerDemoF',
        hidden: true,
        component: () => import('@/views/example/keep-alive/RouterDemoF.vue'),
        meta: { title: 'RouterDemo-F', cachePage: true, activeMenu: '/writing-demo/keep-alive' }
      },
      {
        path: 'router-demo-s',
        name: 'routerDemoS',
        hidden: true,
        component: () => import('@/views/example/keep-alive/RouterDemoS.vue'),
        meta: { title: 'RouterDemo-S',cachePage: true, activeMenu: '/writing-demo/keep-alive' }
      },
      {
        path: 'deep-router-keep-alive',
        name: 'DeepRouterKeepAlive',
        component: () => import('@/views/example/keep-alive/DeepRouterKeepAlive.vue'),
        //注：移除父容器页面缓存会把子页面一起移除了
        meta: { title: 'Deep KeepAlive', cachePage: true, leaveRmCachePage: true },
        alwaysShow: true,
        children: [
          {
            path: 'deep-children',
            name: 'DeepChildren',
            component: () => import('@/views/example/keep-alive/deep-children/DeepChildren.vue'),
            meta: { title: 'DeepChildren', cachePage: false, leaveRmCachePage: true }
          },
          {
            path: 'deep-children-sd',
            name: 'DeepChildrenSd',
            component: () => import('@/views/example/keep-alive/deep-children/DeepChildrenSd.vue'),
            meta: { title: 'DeepChildrenSd', cachePage: true, leaveRmCachePage: false }
          }
        ]
      }
    ]
  },
  {
    path: '/example',
    component: Layout,
    redirect: '/example/table',
    name: 'Example',
    meta: { title: 'Example', icon: 'example' },
    children: [
      {
        path: 'table',
        name: 'Table',
        component: () => import('@/views/table/index.vue'),
        meta: { title: 'Table', icon: 'table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index.vue'),
        meta: { title: 'Tree', icon: 'tree' }
      },
      {
        path: 'worker-Demo',
        name: 'WorkerDemo',
        component: () => import('@/views/example/worker'),
        meta: { title: 'Worker Demo', icon: 'nested' }
      }
    ]
  },
  {
    path: '/form',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form',
        component: () => import('@/views/form/index.vue'),
        meta: { title: 'Form', icon: 'table' }
      }
    ]
  },

  {
    path: '/nested',
    component: Layout,
    redirect: '/nested/menu1',
    name: 'Nested',
    meta: {
      title: 'Nested',
      icon: 'nested'
    },
    children: [
      {
        path: 'menu1',
        component: () => import('@/views/nested/menu1/index.vue'), // Parent router-view
        name: 'Menu1',
        meta: { title: 'Menu1' },
        children: [
          {
            path: 'menu1-1',
            component: () => import('@/views/nested/menu1/menu1-1/index.vue'),
            name: 'Menu1-1',
            meta: { title: 'Menu1-1' }
          },
          {
            path: 'menu1-2',
            component: () => import('@/views/nested/menu1/menu1-2/index.vue'),
            name: 'Menu1-2',
            meta: { title: 'Menu1-2' },
            children: [
              {
                path: 'menu1-2-1',
                component: () => import('@/views/nested/menu1/menu1-2/menu1-2-1/index.vue'),
                name: 'Menu1-2-1',
                meta: { title: 'Menu1-2-1' }
              },
              {
                path: 'menu1-2-2',
                component: () => import('@/views/nested/menu1/menu1-2/menu1-2-2/index.vue'),
                name: 'Menu1-2-2',
                meta: { title: 'Menu1-2-2' }
              }
            ]
          },
          {
            path: 'menu1-3',
            component: () => import('@/views/nested/menu1/menu1-3/index.vue'),
            name: 'Menu1-3',
            meta: { title: 'Menu1-3' }
          }
        ]
      },
      {
        path: 'menu2',
        component: () => import('@/views/nested/menu2/index.vue'),
        name: 'Menu2',
        meta: { title: 'menu2' }
      }
    ]
  },
  {
    path: '/external-link',
    component: Layout,
    children: [
      {
        component: () => {},
        path: 'https://github.com/jzfai/vue3-admin-ts.git',
        meta: { title: 'External Link', icon: 'link' }
      }
    ]
  }
]
/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
// TODO: 权限路由位置，根据userInfo获取到的role
export const asyncRoutes: RouterTy = [
  {
    path: '/permission',
    component: Layout,
    redirect: '/permission/page',
    alwaysShow: true, // will always show the root menu
    name: 'Permission',
    meta: {
      title: 'Permission',
      icon: 'lock',
      roles: ['admin', 'editor'] // you can set roles in root nav
    },
    children: [
      {
        path: 'roleIndex',
        component: () => import('@/views/permission'),
        name: 'Permission',
        meta: {
          title: 'role Index'
          //roles: ['admin'] // or you can only set roles in sub nav
        }
      },
      {
        path: 'page',
        component: () => import('@/views/permission/page.vue'),
        name: 'PagePermission',
        meta: {
          title: 'Page Permission',
          roles: ['admin'] // or you can only set roles in sub nav
        }
      },
      {
        path: 'directive',
        component: () => import('@/views/permission/directive.vue'),
        name: 'DirectivePermission',
        meta: {
          title: 'Directive Permission'
          // if do not set roles, means: this page does not require permission
        }
      },
      {
        path: 'code-index',
        component: () => import('@/views/permission/CodePermission.vue'),
        name: 'CodePermission',
        meta: {
          title: 'Code Index'
        }
      },
      {
        path: 'code-page',
        component: () => import('@/views/permission/CodePage.vue'),
        name: 'CodePage',
        meta: {
          title: 'Code Page',
          code: 1
        }
      }
    ]
  },
   // 业务菜单开始
  {
    path: '/enterprise-access',
    component: Layout,
    name: '机构准入',
    alwaysShow: true,
    meta: { title: '机构准入', icon: 'tree', roles: ['recycle', 'audit', 'admin', 'supervision'] },
    children: [
      {
        path: 'access',
        name: '准入申请',
        component: () => import('@/views/enterprise-access/Access.vue'),
        meta: { title: '准入申请', roles: ['audit'] }
      },
      {
        path: 'access-result',
        name: '准入结果查询',
        component: () => import('@/views/enterprise-access/AccessResult.vue'),
        meta: { title: '准入结果查询', roles: ['recycle', 'audit']  }
      },
      {
        path: 'enterprise-list',
        name: '企业列表',
        component: () => import('@/views/enterprise-access/EntList.vue'),
        meta: { title: '企业列表', roles: ['admin', 'supervision'] }
      }
    ]
  },
  {
    path: '/battery',
    component: Layout,
    name: '电池管理',
    alwaysShow: true,
    meta: { title: '电池管理', icon: 'tree', roles: ['recycle', 'admin', 'supervision', 'customer', 'car', 'productor', 'rent', 'stored', 'safe', 'material'] },
    children: [
      {
        path: 'list',
        name: '电池信息查询',
        component: () => import('@/views/battery/BatteryList.vue'),
        meta: { title: '电池信息查询', roles: ['recycle', 'admin', 'supervision', 'customer', 'car', 'productor', 'rent', 'stored', 'safe', 'material'] }
      },
      {
        path: 'add',
        name: '电池信息录入',
        component: () => import('@/views/battery/BatteryAdd.vue'),
        meta: { title: '电池信息录入', roles: ['productor'] }
      },
    ]
  },
  {
    path: '/firstRecycle',
    component: Layout,
    name: '梯次利用',
    alwaysShow: true,
    meta: { title: '梯次利用', icon: 'tree', roles: ['productor', 'recycle', 'car', 'rent']},
    children: [
      {
        path: 'apply',
        name: '发起交易',
        component: () => import('@/views/battery/BatteryList.vue'),
        meta: { title: '发起交易', roles: ['productor', 'recycle', 'car', 'rent'] }
      },
      {
        path: 'myTrade',
        name: '我的交易',
        component: () => import('@/views/battery/FirstLifeRecycleTrade.vue'),
        meta: { title: '我的交易', roles: ['productor', 'recycle', 'car', 'rent'] }
      },
      {
        path: 'tradeList',
        name: '交易平台',
        component: () => import('@/views/battery/FirstLifeRecycleTrade.vue'),
        meta: { title: '交易平台', roles: ['productor', 'recycle', 'car', 'rent'] }
      },
    ]
  },
  {
    path: '/secondRecycle',
    component: Layout,
    name: '拆解回收',
    alwaysShow: true,
    meta: { title: '拆解回收', icon: 'example', roles: ['stored', 'recycle']},
    children: [
      {
        path: 'apply',
        name: '发起交易',
        component: () => import('@/views/battery/SecondLifeRecycleTrade.vue'),
        meta: { title: '发起交易', roles: ['stored', 'recycle'] }
      },
      {
        path: 'myTrade',
        name: '我的交易',
        component: () => import('@/views/battery/SecondLifeRecycleTrade.vue'),
        meta: { title: '我的交易', roles: ['stored', 'recycle'] }
      },
      {
        path: 'tradeList',
        name: '交易平台',
        component: () => import('@/views/battery/SecondLifeRecycleTrade.vue'),
        meta: { title: '交易平台', roles: ['stored', 'recycle'] }
      },
    ]
  },
  {
      path: '/point',
      component: Layout,
      name: '积分交易',
      alwaysShow: true, // will always show the root menu
      meta: { title: '积分交易', icon: 'tree', roles: ['recycle', 'admin', 'supervision', 'customer', 'car', 'productor', 'rent', 'stored', 'safe', 'material'] },
      children: [
        {
          path: 'tradeList',
          name: '交易所',
          component: () => import('@/views/point/TradeList.vue'),
          meta: { title: '交易所', roles: ['audit','supervision'] }
        },
        {
          path: 'account',
          name: '积分明细',
          component: () => import('@/views/point/Account.vue'),
          meta: { title: '积分明细', roles: ['recycle', 'audit', 'supervision']  }
        },
        {
          path: 'apply',
          name: '发起交易',
          component: () => import('@/views/point/TradeApply.vue'),
          meta: { title: '发起交易', roles: ['admin', 'supervision'] }
        },
        {
          path: 'myTradeList',
          name: '查看我的交易',
          component: () => import('@/views/point/MyTradeList.vue'),
          meta: { title: '查看我的交易', roles: ['admin', 'supervision'] }
        },
      ]
    },



  // 404 page must be placed at the end !!!
  // using pathMatch install of "*" in vue-router 4.0
  { path: '/:pathMatch(.*)', redirect: '/404', hidden: true }
]

const router: Router = createRouter({
  history: createWebHashHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: constantRoutes
})

// export function resetRouter() {
//   const newRouter = createRouter({
//     history: createWebHashHistory(),
//     scrollBehavior: () => ({ top: 0 }),
//     routes: constantRoutes
//   })
// }

export default router
