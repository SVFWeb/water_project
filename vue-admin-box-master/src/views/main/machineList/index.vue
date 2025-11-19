<template>
  <div class="layout-container">
    <div class="layout-container-table" v-loading="loading">
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
                      <el-icon><WarningFilled /></el-icon>
                      <span>离线</span>
                    </div>
                    <el-button type="text" class="edit-button" @click="showEditor">查看详情</el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty description="空空如也~" style="height: 500px;" v-show="list.length === 0"></el-empty>
        </el-scrollbar>
      </div>

      <el-pagination v-model:current-page="page.index" class="system-page" background
        layout="total, sizes, prev, pager, next, jumper" :total="page.total" :page-size="page.size"
        :page-sizes="[10, 20, 50, 100]" @current-change="handleCurrentChange" @size-change="handleSizeChange">
      </el-pagination>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getData } from '@/api/card'

const loading = ref(true)
const list = ref([])
const box = ref()
const page = reactive({
  index: 1,
  size: 20,
  total: 0
})

const getListData = (init) => {
  loading.value = true
  const params = {
    page: page.index,
    pageSize: page.size
  }
  getData(params)
    .then(res => {
      page.total = res.data.pager.total
      list.value = res.data.list
    })
    .catch(err => {
      list.value = []
      page.index = 1
      page.total = 0
    })
    .finally(() => {
      loading.value = false
    })
}

// 分页相关：监听页码切换事件
const handleCurrentChange = (val) => {
  page.index = val
  getListData(false)
}

// 分页相关：监听单页显示数量切换事件
const handleSizeChange = (val) => {
  page.size = val
  page.index = 1
  getListData(false)
}

const showEditor = () => {
  // 编辑逻辑
}

onMounted(() => {
  box.value.addEventListener('resize', (e) => {
    console.log(12)
  })
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

  margin-bottom: 15px;

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

.offline{
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
</style>