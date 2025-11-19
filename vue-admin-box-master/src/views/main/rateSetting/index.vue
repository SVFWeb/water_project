<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      
      <div class="layout-container-form-handle">
          <el-button type="primary" :icon="Plus" >设置设备费率</el-button>
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
      <Table ref="table" v-model:page="page" v-loading="loading" :showIndex="true"
        :data="tableData" @getTableData="getTableData" @selection-change="handleSelectionChange">
        <el-table-column prop="transactionId" label="订单编号" align="center" />
        <el-table-column prop="machineId" label="设备名称" align="center" />
        <el-table-column prop="finalAmount" label="服务费" align="center" />
        <el-table-column prop="orderStatus" label="水费" align="center" />
        <el-table-column label="操作" align="center" fixed="right" width="200">
          <el-button type="primary">修改费率</el-button>
        </el-table-column>
      </Table>
    </div>
  </div>
</template>


<script setup>
import { ref, reactive, onMounted } from 'vue'
import Table from '@/components/table/index.vue'
import {  Search, Plus } from '@element-plus/icons'

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

// 初始化数据
onMounted(() => {
  getTransactionRecord()
  getTableData()
})
</script>

<style lang="scss" scoped>
</style>