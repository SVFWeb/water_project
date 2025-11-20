import request from '@/utils/system/request'

// 获取设备列表
export function apiGetMachineList() {
    return request({
        url: '/machine'
    })
}

// 修改设备信息
export function apiPatMachine(data: any) {
    return request({
        url: `/machine/${data.machineId}`,
        data,
        method: 'PATCH'
    })
}

// 添加设备
export function apiAddMachine(data:any) {
    return request({
        url: '/machine',
        method: 'POST',
        data
    })
}