import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/transaction',
    component: Layout,
    redirect:'/transaction/list',
    meta: { title: '交易', icon: 'sfont system-shequ' },
    children: [
      {
        path: 'record',
        component: createNameComponent(() => import('@/views/main/transactionRecord/index.vue')),
        meta: { title: '交易记录' }
      },
    ]
  }
]

export default route