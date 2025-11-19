<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      
      <div class="layout-container-form-handle">

        <!-- 批量删除 -->
        <el-popconfirm title="确定删除选中的数据吗？" @confirm="handleDel(chooseData)">
          <template #reference>
            <el-button type="danger" :icon="Delete" :disabled="chooseData.length === 0">批量删除</el-button>
          </template>
        </el-popconfirm>
      </div>

      <!-- 搜索 -->
      <div class="layout-container-form-search">
        <el-input v-model="query.input" placeholder="请输入搜索的订单编号"
          @change="getTableData(true)"></el-input>
        <el-button type="primary" :icon="Search" class="search-btn" @click="getTableData(true)">搜索</el-button>
      </div>

    </div>

    <!-- 数据表格 -->
    <div class="layout-container-table">
      <Table ref="table" v-model:page="page" v-loading="loading" :showIndex="true" :showSelection="true"
        :data="tableData" @getTableData="getTableData" @selection-change="handleSelectionChange">
        <el-table-column prop="transactionId" label="订单编号" align="center" />
        <el-table-column prop="userName" label="用户" align="center" />
        <el-table-column prop="machineId" label="设备" align="center" />
        <el-table-column prop="startTime" label="交易开始时间" align="center" >
          <template #default="scope">
            {{ dayjs(scope.row.startTime).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="交易结束时间" align="center" >
          <template #default="scope">
            {{ dayjs(scope.row.endTime).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="最终结算余额" align="center" />
        <el-table-column prop="orderStatus" label="交易状态" align="center" />
        <el-table-column prop="totalLiters" label="出水量" align="center" />
        <el-table-column label="操作" align="center" fixed="right" width="200">
          <template #default="scope">
            <el-popconfirm title="确定删除选中的数据吗？" @confirm="handleDel([scope.row])">
              <template #reference>
                <el-button type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </Table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import dayjs from 'dayjs'
import Table from '@/components/table/index.vue'
import { apiGetTransactionRecord } from '@/api/transaction/record'
 //import { Page } from '@/components/table/type'
// import { ElMessage } from 'element-plus'
import {  Search, Delete } from '@element-plus/icons'
// import { selectData, radioData } from './enum'

// 表格数据
const tableData = ref([])
// 存储搜索用的数据
const query = reactive({
  input: ''
})
// 分页参数, 供table使用
const page = reactive({
  index: 1,
  size: 10,
  total: 0
})
// 表格加载状态
const loading = ref(true)
// 选择删除的数据
const chooseData = ref([])

// 获取列表数据
async function getTransactionRecord() {
  // const res = await apiGetTransactionRecord({
  //   page: page.index,
  //   pageSize: page.size,
  // })
  
  // console.log(res);

  tableData.value=[
    {
        transactionId: "TXN202310011200001",
        userId: "uu1",
        userName: "张三",
        machineId: "MACH001",
        orderStatus: "COMPLETED",
        totalLiters: 5.5,
        finalAmount: 11,
        startTime: "2023-10-01 10:00:00",
        endTime: "2023-10-01 10:05:30"
    },
    {
        transactionId: "2",
        userId: "uu1",
        userName: "张三",
        machineId: "ma1",
        orderStatus: "0",
        totalLiters: 262.81,
        finalAmount: 493.77,
        startTime: "2019-01-02 07:43:51",
        endTime: "2000-04-09 21:52:30"
    },
    {
        transactionId: "1",
        userId: "uu1",
        userName: "张三",
        machineId: "ma1",
        orderStatus: "0",
        totalLiters: 719.33,
        finalAmount: 434.68,
        startTime: "2017-06-16 01:42:00",
        endTime: "2017-11-19 08:27:46"
    }
]

  
}

// 删除数据
const handleSelectionChange = (val) => {
  chooseData.value = val
}

// 获取表格数据
const getTableData = () => {
  loading.value=false
}

// 删除功能
const handleDel = (data) => {
  console.log(data);
  
}

// 初始化数据
onMounted(() => {
  getTransactionRecord()
  getTableData()
})
</script>

<style lang="scss" scoped>
</style>