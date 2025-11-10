import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/faultLog',
    component: Layout,
    redirect:'/faultLog/list',
    meta: { title: '故障', icon: 'sfont system-shequ' },
    children: [
      {
        path: 'list',
        component: createNameComponent(() => import('@/views/main/machineList/index.vue')),
        meta: { title: '故障列表' }
      },
    ]
  }
]

export default route