<template>
  <!-- 全局设置组件的大小 -->
  <el-config-provider :locale="locale" namespace="el" size="large">
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">
import { setToken } from '@/utils/auth'
import { useAppStore } from '@/store/app'

import zhCn from 'element-plus/lib/locale/lang/zh-cn'
let locale = $ref(zhCn) //中文

// import en from 'element-plus/lib/locale/lang/en'
// let locale = $ref(en) //english

const appStore = useAppStore()
const settings = computed(() => {
  return appStore.settings
})
onBeforeMount(() => {
  //set tmp token when setting isNeedLogin false
  if (!settings.value.isNeedLogin) setToken(settings.value.tmpToken)
})
</script>
