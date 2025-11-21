<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      <!-- 搜索 -->
      <div class="layout-container-form-search">
        <el-input v-model="query.input" placeholder="请输入搜索的订单编号" @change="getTableData"></el-input>
        <el-button type="primary" :icon="Search" class="search-btn" @click="getTableData">搜索</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="layout-container-table">
      <Table ref="table" v-model:page="page" v-loading="loading" showIndex :data="tableData">
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
import { apiGetTransactionRecord, apiDeleteTransactionRecordId, apiQueryTransactionRecordId } from '@/api/transaction/record'
import { Search } from '@element-plus/icons'
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

// 获取列表数据
async function getTransactionRecord() {
  loading.value = false
  const res = await apiGetTransactionRecord({
    page: page.index,
    pageSize: page.size,
  })
  tableData.value = res.data.list
}

// 搜索
const getTableData = async () => {
  loading.value = false
  let res = await apiQueryTransactionRecordId(query.input)
  if (query.input === '') {
    tableData.value = res.data.list
  } else {
    tableData.value = [res.data]
  }

}

// 删除功能
const handleDel = async (data) => {
  let res = await apiDeleteTransactionRecordId(data)
  getTransactionRecord()
}

// 初始化数据
onMounted(() => {
  getTransactionRecord()
})
</script>

<style lang="scss" scoped></style>