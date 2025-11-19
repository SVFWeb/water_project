<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      <div class="layout-container-form-handle">
        <el-button type="primary" :icon="Plus" @click="handleAdd">{{
          $t("message.common.add")
        }}</el-button>
        <el-popconfirm :title="$t('message.common.delTip')" @confirm="handleDel(chooseData)">
          <template #reference>
            <el-button type="danger" :icon="Delete" :disabled="chooseData.length === 0">{{ $t("message.common.delBat")
            }}</el-button>
          </template>
        </el-popconfirm>
      </div>
      <div class="layout-container-form-search">
        <el-input v-model="query.input" :placeholder="$t('message.common.searchTip')"></el-input>
        <el-button type="primary" :icon="Search" class="search-btn" @click="getTableData(true)">{{
          $t("message.common.search")
        }}</el-button>
      </div>
    </div>
    <div class="layout-container-table">
      <Table ref="table" v-model:page="page" v-loading="loading" :showSelection="true" :data="tableData"
        @getTableData="getTableData" @selection-change="handleSelectionChange">
        <el-table-column prop="id" label="用户Id" align="center" width="80" />
        <el-table-column prop="name" label="微信id" align="center" />
        <el-table-column prop="nickName" label="用户昵称" align="center" />
        <el-table-column prop="role" label="用户密码" align="center" />
        <el-table-column prop="role" label="用户金额" align="center" />
        <el-table-column :label="$t('message.common.handle')" align="center" fixed="right" width="200">
          <template #default="scope">
            <el-button @click="handleEdit(scope.row)">{{ $t("message.common.update") }}</el-button>
            <el-popconfirm :title="$t('message.common.delTip')" @confirm="handleDel([scope.row])">
              <template #reference>
                <el-button type="danger">{{ $t("message.common.del") }}</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </Table>

      <!-- 添加用户 -->
      <el-dialog v-model="AddDialogVisible" title="添加用户" width="500px">
        <el-form :model="AddFormData" :rules="AddRules" ref="AddRuleForm" label-width="120px"
          style="margin-right:30px;">
          <el-form-item label="用户名称" prop="userName">
            <el-input v-model="AddFormData.userName" placeholder="请输入名称"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="AddFormData.password" placeholder="请输入密码" />
          </el-form-item>
        </el-form>
        <div>
          <el-button type="primary" @click="confirm">确认</el-button>
          <el-button @click="close">取消</el-button>
        </div>
      </el-dialog>


    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { getData, del, updateStatus } from "@/api/system/user";
import { ElMessage } from "element-plus";
import Table from "@/components/table/index.vue";
import Layer from "./layer.vue";
import { Plus, Delete, Search } from '@element-plus/icons'

const AddRuleForm=ref()
const AddDialogVisible = ref(false)
const AddFormData = ref({
  userName: '',
  password: ''
})
const AddRules = {
  userName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}
// 添加用户
function handleAdd() {
  AddDialogVisible.value = true
}
// 添加用户 -确认
function confirm(){
  AddRuleForm.value.validate((valid)=>{
    if(valid){
      AddDialogVisible.value = false
    }
  })
}
// 添加用户 -取消
function close(){
  AddDialogVisible.value = false
}

// 存储搜索用的数据
const query = reactive({
  input: "",
});
// 弹窗控制器
const layer = reactive({
  show: false,
  title: "新增",
  showButton: true,
});
// 分页参数, 供table使用
const page = reactive({
  index: 1,
  size: 20,
  total: 0,
});
const loading = ref(true);
const tableData = ref([]);
const chooseData = ref([]);
const handleSelectionChange = (val) => {
  chooseData.value = val;
};
// 获取表格数据
// params <init> Boolean ，默认为false，用于判断是否需要初始化分页
const getTableData = (init) => {
  loading.value = true
  if (init) {
    page.index = 1
  }
  let params = {
    page: page.index,
    pageSize: page.size,
    ...query
  }
  getData(params)
    .then((res) => {
      let data = res.data.list
      data.forEach((d) => {
        d.loading = false
      })
      tableData.value = data
      page.total = Number(res.data.pager.total);
    })
    .catch((error) => {
      tableData.value = [];
      page.index = 1;
      page.total = 0;
    })
    .finally(() => {
      loading.value = false;
    });
}
// 删除功能
const handleDel = (data) => {
  console.log(1213123);
}
// 编辑弹窗功能
const handleEdit = (row) => {
  layer.title = "编辑数据";
  layer.row = row;
  layer.show = true;
}

getTableData(true)

</script>

<style lang="scss" scoped>
.statusName {
  margin-right: 10px;
}
</style>