import request from '@/utils/system/request'

// 获取交易数据
export function apiGetTransactionRecord(data: object) {
  return request({
    url: '/transactions',
    data
  })
}