<template>
  <div class="scroll-y">
    <div>KeepAlive.vue</div>
    <el-form ref="refsearchForm" :inline="true" class="mt2">
      <el-form-item label-width="0px" label="" prop="errorLog" label-position="left">
        <el-input v-model="searchForm.errorLog" class="w-150px" placeholder="input to test keepAlive" />
      </el-form-item>
      <el-form-item label-width="0px" label="" prop="pageUrl" label-position="left">
        <el-input v-model="searchForm.pageUrl" class="w-150px" placeholder="input to test keepAlive" />
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="routerDemoF">to routerDemoF.vue</el-button>
  </div>
</template>

<!--
使用keep-alive
1.设置name（必须）
2.在路由配置处设置cachePage：即可缓存
-->
<script setup lang="ts">
defineOptions({ name: 'KeepAlive' })

import { useAppStore } from '@/store/app'

let { searchForm } = useCommon()
//$ref(experimental)
//let testRef = $ref(1)
let testRef = ref(1)
//赋值
testRef.value = 2
console.log(testRef.value)

onActivated(() => {
  console.log('onActivated')
})
onDeactivated(() => {
  console.log('onDeactivated')
})

const $route = useRoute()
// cacheGroup为缓存分组  KeepAlive->routerDemoF->routerDemoS
let cacheGroup = ['KeepAlive', 'routerDemoF', 'routerDemoS']
const appStore = useAppStore()
const unWatch = watch(
  () => $route.name,
  () => {
    if (!cacheGroup.includes($route.name as string)) {
      useCommon()
        .sleep(300)
        .then(() => {
          cacheGroup.forEach((fItem) => appStore.M_DEL_CACHED_VIEW(fItem))
        })
      //remove watch
      unWatch()
    }
  },
  //deep: true
  { immediate: true }
)

const cancelWatch = () => {
  unWatch()
}

const routerDemoF = () => {
  //推荐路由跳转根据router的name,这样在你修改路径时，只要不修改name，就没有影响。
  //推荐传递的是query参数，好处是刷新时可以回显，传入的obj对象会反序列化。
  useVueRouter().routerPush('routerDemoF', { name: 'routerDemoF' })
}
</script>

<style scoped lang="scss"></style>
