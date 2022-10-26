<!-- tag!!! 是企业名字的标签信息 -->
<!-- json文件返回的信息与prop字段对应 -->
<!-- 分页需要后端信息返回关键字才好写成动态的，所以这里都写成静态的了 -->
<template>
    <!-- el-scrollbar是滚动条 -->
    <el-scrollbar>
        <!----------------------------- 表单 ------------------------>
        <div>
            <el-row>
                <el-col :span="4">
                    <el-descriptions title="我的积分交易" :column="2" size="default" border>
                        <el-descriptions-item label="我的总积分 "><span>{{ info.Socre }}</span></el-descriptions-item>
                    </el-descriptions>
                </el-col>
            </el-row>
            <el-table ref="tableRef" row-key="date" :data="tableData" style="width: flex">
                <el-table-column prop="Time" label="时间" :formatter="formatter2" width="flex" />
                <el-table-column prop="name" label="交易企业名称" width="flex" />
                <el-table-column prop="Type" label="交易类型" :formatter="formatter1" width="flex" />
                <el-table-column prop="ScoreChange" label="积分明细" :formatter="formatter2" width="flex" />
                <el-table-column prop="Surplus " label="积分余额" :formatter="formatter2" width="flex" />
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

// 我的总积分---此处应该有数据请求
let info = reactive({
    Socre: 50,
    age: 18,
    id: 1
})

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



//文件信息   
const tableData: User[] = [
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
.el-row {
    margin-bottom: 20px;
}

.el-row:last-child {
    margin-bottom: 0;
}

.el-col {
    border-radius: 4px;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}

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