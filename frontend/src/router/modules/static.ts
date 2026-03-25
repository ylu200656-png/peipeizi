import type { RouteRecordRaw } from 'vue-router'

export const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    meta: { public: true, title: '登录' },
    component: () => import('@/views/login/index.vue'),
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'dashboard',
        meta: { title: '首页' },
        component: () => import('@/views/dashboard/index.vue'),
      },
      {
        path: 'medicine',
        name: 'medicine',
        meta: { title: '药品档案' },
        component: () => import('@/views/medicine/index.vue'),
      },
      {
        path: 'purchase',
        name: 'purchase',
        meta: { title: '采购入库' },
        component: () => import('@/views/purchase/index.vue'),
      },
      {
        path: 'sale',
        name: 'sale',
        meta: { title: '销售出库' },
        component: () => import('@/views/sale/index.vue'),
      },
      {
        path: 'inventory',
        name: 'inventory',
        meta: { title: '库存管理' },
        component: () => import('@/views/inventory/index.vue'),
      },
      {
        path: 'warning',
        name: 'warning',
        meta: { title: '预警中心' },
        component: () => import('@/views/warning/index.vue'),
      },
      {
        path: 'controlled',
        name: 'controlled',
        meta: { title: '管制药品' },
        component: () => import('@/views/controlled/index.vue'),
      },
      {
        path: 'stats',
        name: 'stats',
        meta: { title: '统计分析' },
        component: () => import('@/views/stats/index.vue'),
      },
      {
        path: 'users',
        name: 'users',
        meta: { title: '用户角色' },
        component: () => import('@/views/user/index.vue'),
      },
      {
        path: 'logs',
        name: 'logs',
        meta: { title: '操作日志' },
        component: () => import('@/views/log/index.vue'),
      },
    ],
  },
]
