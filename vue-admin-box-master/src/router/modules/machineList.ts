import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/machine',
    component: Layout,
    redirect:'/machine/list',
    meta: { title: '设备', icon: 'sfont system-shequ' },
    children: [
      {
        path: 'list',
        component: createNameComponent(() => import('@/views/main/machineList/index.vue')),
        meta: { title: '设备列表' }
      },
    ]
  }
]

export default route