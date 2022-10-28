<template>
  <div class="mt-5px text-18px font600">欢迎使用B-Recycle电池回收管理平台！当前用户：{{userStore.username}}</div>
</template>
<script setup lang="ts">
import { useUserStore } from '@/store/user'
const userStore = useUserStore()

</script>
