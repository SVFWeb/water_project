<template>
  <div class="layout-container">
    <div class="layout-container-table" v-loading="loading">
      <!-- 添加设备 -->
      <div class="layout-container-form-handle">
        <el-button type="primary" :icon="Plus" @click="onAddMachine">添加设备</el-button>
      </div>

      <!-- 设备列表 -->
      <div class="box" ref="box">
        <el-scrollbar height="100%">
          <el-row :gutter="20">
            <el-col :md="8" v-for="row in 4" :key="row.id">
              <el-card :body-style="{ padding: '0px' }" shadow="hover">
                <img src="http://blog.51weblove.com/wp-content/uploads/2019/03/2019032323331541.jpg" class="image">
                <div style="padding: 14px;">
                  <span>设备名称</span>
                  <div class="bottom clearfix">
                    <div class="online" v-if="true">
                      <el-icon>
                        <CircleCheck />
                      </el-icon>
                      <span>在线</span>
                    </div>
                    <div class="offline" v-else>
                      <el-icon>
                        <WarningFilled />
                      </el-icon>
                      <span>离线</span>
                    </div>
                    <el-button type="text" class="edit-button" @click="showEditor">查看详情</el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-scrollbar>
      </div>

      <!-- 设备详情弹出框 -->
      <el-dialog v-model="dialogVisible" title="设备信息" width="700px" :before-close="handleClose">
        <div class="form-container">
          <!-- 地区选择器 -->
          <div class="form-item full-width">
            <label>地区</label>
            <el-cascader v-model="formData.region" :options="optionsnative_place" />
          </div>

          <!-- 经度 -->
          <div class="form-item">
            <label>经度</label>
            <el-input v-model="formData.longitude" placeholder="请输入经度" />
          </div>

          <!-- 纬度 -->
          <div class="form-item">
            <label>纬度</label>
            <el-input v-model="formData.latitude" placeholder="请输入纬度" />
          </div>

          <!-- 状态开关 -->
          <div class="form-item">
            <label>状态</label>
            <el-switch v-model="formData.status" active-text="开启" inactive-text="关闭" />
          </div>

          <!-- 设备是否启用 -->
          <div class="form-item">
            <label>设备是否启用</label>
            <el-radio-group v-model="formData.deviceEnabled">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </div>

          <!-- 水箱是否加满 -->
          <div class="form-item">
            <label>水箱是否加满</label>
            {{ 0 > 1 ? '是' : '否' }}
          </div>

          <!-- 设备温度 -->
          <div class="form-item">
            <label>设备温度 (°C)</label>
            {{ formData.temperature }}
          </div>

          <!-- 电池电量 -->
          <div class="form-item">
            <label>电池电量 (%)</label>
            {{ '80' }}
          </div>

          <!-- 总加水量 -->
          <div class="form-item">
            <label>总加水量 (ML)</label>
            {{ 600 }}
          </div>


        </div>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 添加设备弹出框 -->
      <el-dialog v-model="AddDialogVisible" title="添加设备" width="700px" :before-close="handleClose">
        <el-form :model="AddFormData" ref="ruleForm" label-width="120px" style="margin-right:30px;">
          <el-form-item label="设备名称" prop="machineId">
            <el-input v-model="AddFormData.machineId" placeholder="请输入名称"></el-input>
          </el-form-item>
          <el-form-item label="设备放置区域" prop="location">
            <el-cascader v-model="AddFormData.location" :options="optionsnative_place" />
          </el-form-item>
        </el-form>
        <div>
          <el-button type="primary" @click="AddConfirm">确认</el-button>
          <el-button @click="AddClose">取消</el-button>
        </div>
      </el-dialog>

    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons'
import { getData } from '@/api/card'
// 级联地址
import { pcaTextArr } from 'element-china-area-data'
import { ElMessage, ElMessageBox } from 'element-plus'

// 地区数据
const optionsnative_place = pcaTextArr
// 弹出框控制
const dialogVisible = ref(false);
// 添加设备弹出框
const AddDialogVisible = ref(false);

// 表单数据
const formData = reactive({
  region: [],
  status: true,
  deviceEnabled: true,
  tankFull: false,
  temperature: 25,
  batteryLevel: 80,
  totalWater: 0,
  longitude: '116.36864881583199',
  latitude: '39.95766339839936'
});

// 
const AddFormData = ref({
  machineId: '',// 设备名称
  location: [] // 地址
})

/*
  const text = '北京市 市辖区 东城区';
  const result = text.split(' ');
  console.log(result);  // ['北京市', '市辖区', '东城区']

  const arr = ['北京市', '市辖区', '东城区'];
  const result = arr.join(' ');
  console.log(result);  // '北京市 市辖区 东城区'
*/


// 处理弹窗关闭
const handleClose = (done) => {
  ElMessageBox.confirm('确定要关闭吗？')
    .then(() => {
      done();
    })
};

// 处理表单提交
const handleSubmit = () => {
  ElMessageBox.confirm('确定要修改吗？')
    .then(() => {
      console.log('表单数据:', formData);
      ElMessage.success('修改成功！');
      dialogVisible.value = false;
    })

};
// 加载状态
const loading = ref(true)
// 设备列表
const list = ref([])
const box = ref()

const getListData = (init) => {
  loading.value = true

  loading.value = false
}

// 查看详情
const showEditor = () => {
  dialogVisible.value = true
}

// 添加设备
function onAddMachine() {
  AddDialogVisible.value = true
}

// 添加设备 -确认
function AddConfirm() {
  ElMessageBox.confirm('确定要添加吗？')
    .then(() => {
      AddDialogVisible.value = false
    })

}

// 添加设备 -取消
function AddClose() {
  ElMessageBox.confirm('确定要取消吗？')
    .then(() => {
      AddDialogVisible.value = false
      AddFormData.value = {
        machineId: '',// 设备名称
        location: [] // 地址
      }
    })

}


onMounted(() => {
  getListData(true)
})
</script>

<style lang="scss" scoped>
* {
  text-align: left;
}

.el-col {
  margin-bottom: 20px;
}

.box {
  height: calc(100% - 50px);

  margin-top: 15px;

  :deep(.is-horizontal) {
    display: none;
  }
}

.online {
  span {
    margin-left: 5px;
    vertical-align: text-top;
  }

  font-size: 13px;
  color: green;
}

.offline {
  span {
    margin-left: 5px;
    vertical-align: text-top;
  }

  font-size: 13px;
  color: red;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.edit-button {
  padding: 0;
  float: right;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.form-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-item {
  margin-bottom: 15px;
}

.form-item label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.dialog-footer {
  text-align: right;
  margin-top: 20px;
}

.full-width {
  grid-column: 1 / -1;
}
</style>