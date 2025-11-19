import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/user',
    component: Layout,
    redirect:'/user/list',
    meta: { title: '用户', icon: 'sfont system-shequ' },
    children: [
      {
        path: 'list',
        component: createNameComponent(() => import('@/views/main/userList/index.vue')),
        meta: { title: '用户列表' }
      },
    ]
  }
]

export default route