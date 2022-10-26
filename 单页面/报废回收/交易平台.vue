<!-- tag!!! 是企业名字的标签信息 -->
<!-- json文件返回的信息与prop字段对应 -->
<!-- 分页需要后端信息返回关键字才好写成动态的，所以这里都写成静态的了 -->
<template>
    <!-- el-scrollbar是滚动条 -->
    <el-scrollbar>
        <!----------------------------- 表单 ------------------------>
        <div>

            <el-button type="success" plain size="media" @click="clearFilter">恢复所有筛选项</el-button>
            <el-table ref="tableRef" row-key="date" :data="tableData" style="width: flex">
                <el-table-column prop="name" label="发布交易企业" width="flex" />
                <el-table-column prop="Content" label="交易内容" :formatter="formatter1" width="flex" />
                <el-table-column prop="Price" label="交易底价" :formatter="formatter2" width="flex" />
                <!-- 筛选 -->
                <el-table-column prop="tag" label="交易企业类型" width="flex" :filters="[
                  { text: '车企', value: '车企' },
                  { text: '电池生产企业', value: '电池生产企业' },
                  { text: '电池租赁企业', value: '电池租赁企业' },
                  { text: '电池回收企业', value: '电池回收企业' },
                  { text: '储能企业', value: '储能企业' },
                  { text: '电池原材料生产企业', value: '电池原材料生产企业' },
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
                <el-table-column fixed="right" label=" " width="120">
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
    <el-affix position="bottom" :offset="20">
        <div class="columnCC mt2 ">
            <el-pagination v-model:currentPage="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
                :background="true" layout="total, sizes, prev, pager, next, jumper" :total="totalPage"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </el-affix>
    <!----------------------------------------- 分页 --------------------------------------------->
</template>
  
<script lang="ts" setup>
import { ref } from 'vue'
import { ElTable } from 'element-plus'
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import { User } from '@element-plus/icons-vue';
// 报价
const deleteRow = (index: number) => {
    tableData.values.splice(index, 1)
}

const submitData = (value:any) => {
        alert(JSON.stringify(tableData))
      }

// ===================分页=========================
// 总条数
const totalPage = 40
// 总页数
const pageNum = ref(4)
// 总的大小
const pageSize = ref(10)

const handleSizeChange = (val: number) => {
    console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
    console.log(`current page: ${val}`)
}
// ===================分页=========================

// ===================列表=========================
interface User {
    // date: string
    name: string
    license: string
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

// 交易状态 最终成交价  交易内容
const formatter1 = (row: User, column: TableColumnCtx<User>) => {
    return row.license
}
const formatter2 = (row: User, column: TableColumnCtx<User>) => {
    return row.license
}
const formatter3 = (row: User, column: TableColumnCtx<User>) => {
    return row.license
}
const formatter4 = (row: User, column: TableColumnCtx<User>) => {
    return row.license
}

const filterTag = (value: string, row: User) => {
    return row.tag === value
}

// const filterHandler = (
//     value: string,
//     row: User,
//     column: TableColumnCtx<User>
// ) => {
//     const property = column['property']
//     return row[property] === value
// }

//文件信息   
const tableData: User[] = [
    // const tableData = ref([
    {
        // date: '2016-05-03',
        name: '我爱开车',
        license: 'No',
        tag: '车企',
    },
    {
        // date: '2016-05-02',
        name: '我爱生产电池',
        license: 'No',
        tag: '电池生产企业',
    },
    {
        // date: '2016-05-04',
        name: '我爱发电',
        license: 'No',
        tag: '储能企业',
    },
    {
        // date: '2016-05-01',
        name: '嘿嘿嘿',
        license: 'No',
        tag: '电池原材料生产企业',
    },
    {
        // date: '2016-05-03',
        name: '我爱开车',
        license: 'No',
        tag: '车企',
    },
    {
        // date: '2016-05-02',
        name: '我爱生产电池',
        license: 'No',
        tag: '电池生产企业',
    },
    {
        // date: '2016-05-04',
        name: '我爱发电',
        license: 'No',
        tag: '储能企业',
    },
    {
        // date: '2016-05-01',
        name: '嘿嘿嘿',
        license: 'No',
        tag: '电池原材料生产企业',
    }, {
        // date: '2016-05-03',
        name: '我爱开车',
        license: 'No',
        tag: '车企',
    },
    {
        // date: '2016-05-02',
        name: '我爱生产电池',
        license: 'No',
        tag: '电池生产企业',
    },
    {
        // date: '2016-05-04',
        name: '我爱发电',
        license: 'No',
        tag: '储能企业',
    },
    {
        // date: '2016-05-01',
        name: '嘿嘿嘿',
        license: 'No',
        tag: '电池原材料生产企业',
    }, {
        // date: '2016-05-03',
        name: '我爱开车',
        license: 'No',
        tag: '车企',
    },
    {
        // date: '2016-05-02',
        name: '我爱生产电池',
        license: 'No',
        tag: '电池生产企业',
    },
    {
        // date: '2016-05-04',
        name: '我爱发电',
        license: 'No',
        tag: '储能企业',
    },
    {
        // date: '2016-05-01',
        name: '嘿嘿嘿',
        license: 'No',
        tag: '电池原材料生产企业',
    }, {
        // date: '2016-05-03',
        name: '我爱开车',
        license: 'No',
        tag: '车企',
    },
    {
        // date: '2016-05-02',
        name: '我爱生产电池',
        license: 'No',
        tag: '电池生产企业',
    },
    {
        // date: '2016-05-04',
        name: '我爱发电',
        license: 'No',
        tag: '储能企业',
    },
    {
        // date: '2016-05-01',
        name: '嘿嘿嘿',
        license: 'No',
        tag: '电池原材料生产企业',
    },
]
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