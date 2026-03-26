import type { RouteRecordRaw } from 'vue-router'
import {
  CONTROLLED_VIEW_ROLES,
  INVENTORY_VIEW_ROLES,
  LOG_VIEW_ROLES,
  MEDICINE_MANAGE_ROLES,
  PURCHASE_MANAGE_ROLES,
  USER_MANAGE_ROLES,
  WARNING_MANAGE_ROLES,
} from '@/constants/permission'

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
        meta: { title: '药品与主数据', allowedRoles: [...MEDICINE_MANAGE_ROLES] },
        component: () => import('@/views/medicine/index.vue'),
      },
      {
        path: 'purchase',
        name: 'purchase',
        meta: { title: '采购入库', allowedRoles: [...PURCHASE_MANAGE_ROLES] },
        component: () => import('@/views/purchase/index.vue'),
      },
      {
        path: 'sale',
        name: 'sale',
        meta: { title: '销售出库', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER', 'SALES_CLERK'] },
        component: () => import('@/views/sale/index.vue'),
      },
      {
        path: 'inventory',
        name: 'inventory',
        meta: { title: '库存与盘点', allowedRoles: [...INVENTORY_VIEW_ROLES] },
        component: () => import('@/views/inventory/index.vue'),
      },
      {
        path: 'warning',
        name: 'warning',
        meta: { title: '预警中心', allowedRoles: [...WARNING_MANAGE_ROLES] },
        component: () => import('@/views/warning/index.vue'),
      },
      {
        path: 'controlled',
        name: 'controlled',
        meta: { title: '管制药品', allowedRoles: [...CONTROLLED_VIEW_ROLES] },
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
        meta: { title: '用户管理', allowedRoles: [...USER_MANAGE_ROLES] },
        component: () => import('@/views/user/index.vue'),
      },
      {
        path: 'logs',
        name: 'logs',
        meta: { title: '操作日志', allowedRoles: [...LOG_VIEW_ROLES] },
        component: () => import('@/views/log/index.vue'),
      },
    ],
  },
]
