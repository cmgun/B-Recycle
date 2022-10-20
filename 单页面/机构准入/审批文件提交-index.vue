<!-- 审批文件提交页面 -->
<template>
    <div class="app-container" style="margin-top:10px;">

        <el-upload ref="uploadRef" class="upload-demo"
            action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" :auto-upload="false">
            <template #trigger>
                <el-button type="primary" size="large">选取文件</el-button>
            </template>
            <!-- 上传到服务器 -->
            <el-button class="ml-3" type="success" size="large" @click="submitUpload">
                提交审批（上传到服务器）
            </el-button>
            <template #tip>
                <!-- front-size 设置字体大小 margin-top设置顶部占位 -->
                <div style="margin-top:10px; font-size: large;">请上传资质文件</div>
                <div class="el-upload__tip">文件不能超过500kb</div>
            </template>
        </el-upload>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { UploadInstance, valueEquals } from 'element-plus'
import { ElMessage } from 'element-plus'

const uploadRef = ref<UploadInstance>()

const submitUpload = () => {
  uploadRef.value!.submit();
  open1()
}

// 提交审批通知
const open1 = () => {
    ElMessage({
    // title: 'Success',
    message: '您已提交审批',
    type: 'success',
  })
}
</script>
