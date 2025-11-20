import request from '@/utils/system/request'

export function apiGetRateSetting() {
  return request({
    url: '/rate-config',
  })
}

export function apiAddRateSetting(data) {
  return request({
    url: '/rate-config',
    method: 'POST',
    data,
  })
}

export function apiGetNotRateSetting() {
  return request({
    url: '/machine/without-rates',
  })
}

export function apiPutRateSetting(data) {
  return request({
    url: `/rate-config/by-machine/${data.machineId}`,
    method: 'PUT',
    data,
  })
}
