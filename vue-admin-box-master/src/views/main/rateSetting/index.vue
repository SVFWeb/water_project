<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">

      <div class="layout-container-form-handle">
        <el-button type="primary" :icon="Plus" @click="addRateSetting.open">设置设备费率</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="layout-container-table">
      <Table ref="table" v-model:page="page" v-loading="loading" :showIndex="true" :data="tableData"
        @getTableData="getTableData">
        <el-table-column prop="rateId" label="费率配置ID" align="center" />
        <el-table-column prop="machineId" label="设备名称" align="center" />
        <el-table-column prop="serviceFee" label="服务费" align="center" />
        <el-table-column prop="pricePerLiter" label="水费" align="center" />
        <el-table-column label="操作" align="center" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" @click="onEdit(row)">修改费率</el-button>
          </template>
        </el-table-column>
      </Table>
    </div>

    <BaseDialog v-model:visible="addRateSetting.visible.value" :title="'设置费率'" @confirm="confirm" @close="initFormData">
      <template #content>
        <el-form :model="formData" label-width="120px" style="margin-right:30px;">
          <el-form-item label="设备名称" prop="machineId">
            <el-select v-model="formData.machineId" placeholder="选择未配置费率的设备" style="width: 240px">
              <el-option v-for="(item, index) in options" :key="item.machineId + index" :label="item.machineId"
                :value="item.machineId" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务费" prop="serviceFee">
            <el-input v-model="formData.serviceFee" placeholder="设置服务费" />
          </el-form-item>
          <el-form-item label="水费" prop="pricePerLiter">
            <el-input v-model="formData.pricePerLiter" placeholder="设置水费" />
          </el-form-item>
        </el-form>
      </template>
    </BaseDialog>

    <BaseDialog v-model:visible="EditRateSetting.visible.value" :title="'修改费率'" @confirm="editConfirm"
      @close="initFormData">
      <template #content>
        <el-form :model="formData" label-width="120px" style="margin-right:30px;">
          <el-form-item label="设备名称" prop="machineId">
            <el-input v-model="formData.machineId" disabled />
          </el-form-item>
          <el-form-item label="服务费" prop="serviceFee">
            <el-input v-model="formData.serviceFee" placeholder="设置服务费" />
          </el-form-item>
          <el-form-item label="水费" prop="pricePerLiter">
            <el-input v-model="formData.pricePerLiter" placeholder="设置水费" />
          </el-form-item>
        </el-form>
      </template>
    </BaseDialog>
  </div>
</template>


<script setup>
import { ref, reactive, onMounted } from 'vue'
import Table from '@/components/table/index.vue'
import { Search, Plus } from '@element-plus/icons'
import { apiGetRateSetting, apiAddRateSetting, apiGetNotRateSetting, apiPutRateSetting } from '@/api/rateSetting/rateSetting.js'
import BaseDialog from '@/components/dialog/index.vue'
import useDialog from '@/hooks/useDialog.js'

const formData = ref({
  machineId: '',
  serviceFee: '',
  pricePerLiter: ''
})

const addRateSetting = useDialog()
const EditRateSetting = useDialog()

// 表格数据
const tableData = ref([])
// 分页参数, 供table使用
const page = reactive({
  index: 1,
  size: 10,
  total: 3
})
// 表格加载状态
const loading = ref(true)
// 未配置费率的设备
const options = ref([])

function initFormData() {
  formData.value = {
    machineId: '',
    serviceFee: '',
    pricePerLiter: ''
  }
}

// 获取列表数据
async function getTransactionRecord() {
  let res = await apiGetRateSetting()
  tableData.value = res.data
}

async function confirm() {
  await apiAddRateSetting(formData.value)
  addRateSetting.close()
  initFormData()
  await getTransactionRecord()
  await getNotRateSetting()
}

function onEdit(row) {
  EditRateSetting.open()
  formData.value = row
}

async function editConfirm() {
  let res = await apiPutRateSetting(formData.value)
  if (res.code === 200) {
    EditRateSetting.close()
    initFormData()
  }

}

// 获取表格数据
const getTableData = () => {
  loading.value = false
}
// 获取未配置费率的设备列表
async function getNotRateSetting() {
  let res = await apiGetNotRateSetting()
  options.value = res.data.list
}

// 初始化数据
onMounted(async () => {
  getTransactionRecord()
  getTableData()
  getNotRateSetting()

})
</script>

<style lang="scss" scoped></style>