<template>
     <el-row class="mb-4">
        <el-button type="primary" round size="large" @click="open">点击此处查询审批状态</el-button>
        <!-- <el-alert class=el-alert title="你已通过审批" type="success" description="" center effect="dark" show-icon />
        <el-alert class=el-alert title="未通过审批" type="error" description="请重新提交文档" center effect="dark" show-icon /> -->
    </el-row>
</template>

<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
// import type { Action } from 'element-plus'

const open = () => {
    // 查询调用请求后，弹窗，弹结果

    // 此处为弹窗，不知道这个会不会有用
  ElMessageBox.confirm(
    '查询',
    'Warning',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(() => {
      ElMessage({
        type: 'success',
        message: '审批通过',
      })
    })
    .catch(() => {
      ElMessage({
        type: 'error',
        message: '审批没有通过，请重新提交审批文件',
      })
    }) 
// 参考： 注册页面
//     if (valid)
//     ElMessage({
//       message: '审批通过。',
//       type: 'success',
//     })
//   else {
//     ElMessage.error('审批没有通过，请重新提交审批文件')
//   }



}
</script>

<style scoped>
.el-alert {
    margin: 20px 0 0;
}

.el-alert:first-child {
    margin: 0;
}
</style>