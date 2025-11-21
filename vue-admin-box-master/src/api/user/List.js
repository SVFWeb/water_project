import request from '@/utils/system/request'

export function apiGetUserList() {
  return request({
    url: '/user',
  })
}

export function apiAddUser(data){
  return request({
    url:'/user/register',
    method:'POST',
    data
  })
}
