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
        <el-input v-model="query.input" placeholder="请输入搜索的订单编号" @change="getTableData(true)"></el-input>
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
        <el-table-column prop="startTime" label="交易开始时间" align="center">
          <template #default="scope">
            {{ dayjs(scope.row.startTime).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="交易结束时间" align="center">
          <template #default="scope">
            {{ dayjs(scope.row.endTime).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="最终结算余额" align="center" />
        <el-table-column prop="orderStatus" label="交易状态" align="center" />
        <el-table-column prop="totalLiters" label="出水量" align="center" />
        <el-table-column label="操作" align="center" fixed="right" width="200">
          <template #default="scope">
            <el-popconfirm title="确定删除选中的数据吗？" @confirm="handleDel(scope.row.transactionId)">
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
import { apiGetTransactionRecord, apiDeleteTransactionRecordId } from '@/api/transaction/record'
//import { Page } from '@/components/table/type'
// import { ElMessage } from 'element-plus'
import { Search, Delete } from '@element-plus/icons'
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
  const res = await apiGetTransactionRecord({
    page: page.index,
    pageSize: page.size,
  })
  tableData.value = res.data.list

}

// 获取表格数据
const getTableData = () => {
  loading.value = false
}

function handleSelectionChange(value){
  chooseData.value=value
}

// 删除功能
const handleDel = async (data) => {
  let res = await apiDeleteTransactionRecordId(data)
  console.log(res);

  getTransactionRecord()

}

// 初始化数据
onMounted(() => {
  getTransactionRecord()
  getTableData()
})
</script>

<style lang="scss" scoped></style>