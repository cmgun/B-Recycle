<!--交易平台 -->
<template>
    <!-- el-scrollbar是滚动条 -->
    <el-scrollbar>
        <!----------------------------- 表单 ------------------------>
        <div>
  <!--  查询条件   -->
  <el-form ref="tableConfig" inline="true" :model="searchParam" label-width="80px">
    <!-- <el-form-item label="账户名">
      <el-input v-model="searchParam.userName" clearable />
    </el-form-item> -->
    <el-form-item label="企业名称">
      <el-input v-model="searchParam.entName" clearable />
    </el-form-item>
    <el-form-item label="企业类型">
      <el-select v-model="searchParam.tag" placeholder="请选择企业类型" >
        <el-option label="车企" value="0" />
        <el-option label="电池生产企业" value="1" />
        <el-option label="电池租赁企业" value="2" />
        <el-option label="电池回收企业" value="3" />
        <el-option label="储能企业" value="4" />
        <el-option label="电池原材料生产企业" value="5" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <!-- <el-button type="success" @click="clearFilter">重置</el-button> -->
    </el-form-item>
  </el-form>
            <el-table ref="tableRef" row-key="date" :data="tableData" style="width: flex">
                <el-table-column prop="entName" label="企业名称" width="flex" />
                <el-table-column prop="Content" label="交易内容"  width="flex" />
                <el-table-column prop="Price" label="交易底价" width="flex" />
                <!-- 筛选 -->
                <el-table-column prop="tag" label="企业类型" width="flex" :filters="[
                  { text: '车企', value: '0' },
                  { text: '电池生产企业', value: '1' },
                  { text: '电池租赁企业', value: '2' },
                  { text: '电池回收企业', value: '3' },
                  { text: '储能企业', value: '4' },
                  { text: '电池原材料生产企业', value: '5' },
                ]" :filter-method="filterTag" filter-placement="bottom-end">
                    <template #default="scope">
                        <!-- 实现功能：表格做判断，后端返回数字值，根据数字写三元表达式 教程网址↓ -->
                        <!-- https://blog.csdn.net/cdd9527/article/details/126501032?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-126501032-blog-125242450.pc_relevant_3mothn_strategy_recovery&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-126501032-blog-125242450.pc_relevant_3mothn_strategy_recovery&utm_relevant_index=1 -->
                        <!-- 可以用于写是否通过质检的状态返回 -->
                        <!-- 要实现不同标签，不同颜色 -->
                        <el-tag
                            :type="scope.row.tag === '电池原材料生产企业' ? 'info' :(scope.row.tag === '储能企业'?'success':(scope.row.tag === '电池回收企业'?'info':(scope.row.tag === '电池租赁企业'?'warning':(scope.row.tag === '电池生产企业'?'danger':'')))) "
                            disable-transitions>{{ scope.row.tag }}</el-tag>
                    </template>
                </el-table-column>
                <!-- 输入报价 -->
                <!-- 参考scope用法，这个实在是看不懂了 -->
                <!-- 参考链接：https://blog.csdn.net/weixin_43753330/article/details/89925161 -->
                <!-- 参考链接：https://blog.csdn.net/weixin_46659028/article/details/106802222 -->
                <!-- element 组件 官网： https://element-plus.gitee.io/zh-CN/component/table.html#固定列 -->
                <el-table-column fixed="right" label="报价" width="120">
                    <template #default="scope2">
                        <!-- <el-button link type="primary" size="small" @click.prevent="deleteRow(scope2.$index)">
                            Remove
                        </el-button> -->
                        <el-input link type="primary" size="small" v-model="scope2.row.Price" placeholder="Please input"
                            clearable />
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label=" " width="flex">
                    <template #default="scope3">
                        <el-button link type="primary" size="small" @click.prevent="submitData(scope3.row)">
                        提交报价
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </el-scrollbar>
  <!----------------------------------------- 分页 --------------------------------------------->
  <!-- 此处需要用axiosReq请求信息，可以参考errorlog.vue 中分页的用法 -->
   <!-----关注 Errorlog 中 v-model的使用 ----------------->
   <el-affix position="bottom" :offset="20">
      <div class="columnCC mt2 ">
          <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
              :background="true" layout="total, sizes, prev, pager, next, jumper" :total="totalPage" :page-count="pageCount"
              @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
  </el-affix>
  <!----------------------------------------- 分页 --------------------------------------------->
</template>
  
<script lang="ts" setup>
import { ref } from 'vue'
import { ElTable } from 'element-plus'
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'

import { useEntAccessStore } from '@/store/entAccess'

// 报价
const submitData = (value:any) => {
        alert(JSON.stringify(tableData))
      }

// ===================分页=========================
// 总条数
const totalPage = ref(10)
// 当前页数
const currentPage = ref(1)
// 总的大小
const pageSize = ref(10)
// 总页数
const pageCount = ref(1)

const handleSizeChange = (val: number) => {
  // console.log(`${val} items per page`)
  pageSize.value = val
  search()
}
const handleCurrentChange = (val: number) => {
  // console.log(`current page: ${val}`)
  currentPage.value = val
  search()
}

// ===================分页=========================
// 查询条件
const searchParam = reactive({
  entName: '',
  tag: ''
})
// 添加：
// 调用查询接口
const search = () => {
  let param = {
    pageSize: pageSize.value,
    pageNo: currentPage.value,
    entName: searchParam.entName,
    tag: searchParam.tag,
  }

  entAccessStore.entList(param)
  .then((res: any) => {
      console.log("list success")
      // 填充分页插件
      pageSize.value = res.pageSize
      totalPage.value = res.total
      pageCount.value = res.pageCount

      // let result:Array<User> = new Array<User>()
      // 填充tabledata
      tableData.splice(0, tableData.length)
      for (let i = 0; i < res.data.length; i++) {
        let data = res.data[i]
        tableData.push({
          userName: data.userName,
          entName: data.entName,
          tag: data.tag
        })
      }
      // tableData.value = result
    })
    .catch((res) => {
      console.log("list error")
    })

}
// 列表数据
const tableData = reactive(new Array<User>)

const entAccessStore = useEntAccessStore()



// ===================列表=========================
interface User {
    // date: string
    // name: string
    // license: string
    // tag: string
      // date: string
  userName: string
  entName: string
  tag: string
}
const tableRef = ref<InstanceType<typeof ElTable>>()

// const resetDateFilter = () => {
//     tableRef.value!.clearFilter(['name'])
// }

// TODO: improvement typing when refactor table
const clearFilter = () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-expect-error
    tableRef.value!.clearFilter()
}

// // 交易状态 最终成交价  交易内容
// const formatter1 = (row: User, column: TableColumnCtx<User>) => {
//     return row.license
// }
// const formatter2 = (row: User, column: TableColumnCtx<User>) => {
//     return row.license
// }
// const formatter3 = (row: User, column: TableColumnCtx<User>) => {
//     return row.license
// }
// const formatter4 = (row: User, column: TableColumnCtx<User>) => {
//     return row.license
// }

const filterTag = (value: string, row: User) => {
  return row.tag === value
}

search()
// ===================列表=========================



</script>
<style scoped lang="scss">
.demo-pagination-block+.demo-pagination-block {
    margin-top: 10px;
}

.demo-pagination-block .demonstration {
    margin-bottom: 16px;
}

.detail-container {
    flex-wrap: wrap;
}

.detail-container-item {
    min-width: 40%;
    margin-bottom: 20px;
}

.detailDialog-title {
    margin-bottom: 14px;
    font-weight: bold;
    font-size: 16px;
}

// 滚动条
.scrollbar-demo-item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 50px;
    margin: 10px;
    text-align: center;
    border-radius: 4px;
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
}
</style>