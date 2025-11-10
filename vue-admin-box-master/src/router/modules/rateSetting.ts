import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/rate',
    component: Layout,
    redirect:'/rate/setting',
    meta: { title: '费率', icon: 'sfont system-shequ' },
    children: [
      {
        path: 'setting',
        component: createNameComponent(() => import('@/views/main/rateSetting/index.vue')),
        meta: { title: '费率设置' }
      },
    ]
  }
]

export default route