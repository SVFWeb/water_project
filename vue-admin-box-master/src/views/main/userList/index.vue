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
      <Table ref="table" v-model:page="page" v-loading="loading" showIndex :data="tableData"
        @getTableData="getTableData">
        <el-table-column prop="userId" label="用户Id" align="center" />
        <el-table-column prop="openId" label="微信id" align="center" />
        <el-table-column prop="userName" label="用户昵称" align="center" />
        <el-table-column prop="userPassword" label="用户密码" align="center" />
        <el-table-column prop="balance" label="用户金额" align="center" width="100" />
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
      <BaseDialog v-model:visible="AddUserDialog.visible.value" :title="'添加用户'" :width="'500px'" @confirm="confirm"
        @close="close">
        <template #content>
          <el-form :model="FormData" :rules="rules" ref="AddRuleForm" label-width="120px" style="margin-right:30px;">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="FormData.userName" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item label="密码" prop="userPassword">
              <el-input v-model="FormData.userPassword" placeholder="请输入密码" />
            </el-form-item>
          </el-form>
        </template>
      </BaseDialog>

      <!-- 编辑用户 -->
      <BaseDialog v-model:visible="EditUserDialog.visible.value" :title="'修改用户信息'" :width="'500px'">
        <template #content>
          <el-form :model="FormData" :rules="rules" ref="EditRuleForm" label-width="120px" style="margin-right:30px;">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="FormData.userName" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item label="密码" prop="userPassword">
              <el-input v-model="FormData.userPassword" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="金额" prop="balance">
              <el-input v-model="FormData.balance" placeholder="请输入密码" />
            </el-form-item>
          </el-form>
        </template>
      </BaseDialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getData, del, updateStatus } from "@/api/system/user";
import BaseDialog from '@/components/dialog/index.vue'
import { ElMessage } from "element-plus";
import Table from "@/components/table/index.vue";
import { Plus, Delete, Search } from '@element-plus/icons'
import useDialog from '@/hooks/useDialog.js'
import { apiGetUserList, apiAddUser } from '@/api/user/List.js'

const FormData = ref({
  userName: '',
  userPassword: '',
  balance: ''
})
const rules = {
  userName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  money: [{ required: true, message: '请输入余额', trigger: 'blur' }],
}


// 添加用户弹出框
const AddUserDialog = useDialog()
const AddRuleForm = ref()
// 添加用户
function handleAdd() {
  AddUserDialog.open()
}
// 添加用户 -确认
function confirm() {
  AddRuleForm.value.validate(async (valid) => {
    if (valid) {
      await apiAddUser(FormData.value)
      AddUserDialog.close()
      await getUserList()
      initFormData()
    }
  })
}
// 添加用户 -取消
function close() {
  initFormData()
}

// 编辑用户弹出框
const EditUserDialog = useDialog()
const EditRuleForm = ref()

// 编辑用户弹窗功能
const handleEdit = (row) => {
  EditUserDialog.open()
  FormData.value = row
  
}



// 存储搜索用的数据
const query = reactive({
  input: "",
});

// 分页参数, 供table使用
const page = reactive({
  index: 1,
  size: 20,
  total: 0,
});
const loading = ref(true);
// 表格数据
const tableData = ref([]);
// 批量删除数据
const chooseData = ref([]);
const handleSelectionChange = (val) => {
  chooseData.value = val;
};

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

async function getUserList() {
  let res = await apiGetUserList()
  tableData.value = res.data
}

function initFormData() {
  FormData.value = {
    userName: '',
    userPassword: '',
    money: ''
  }
}

onMounted(() => {
  getUserList()
})

getTableData(true)

</script>

<style lang="scss" scoped>
.statusName {
  margin-right: 10px;
}
</style>