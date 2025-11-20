import request from '@/utils/system/request'

// 获取交易数据
export function apiGetTransactionRecord(data: object) {
  return request({
    url: '/transaction',
    data
  })
}

// 删除交易数据
export function apiDeleteTransactionRecordId(id: string) {
  return request({
    url: `/transaction/${id}`,
    method: 'DELETE'
  })
}