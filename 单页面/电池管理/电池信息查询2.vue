
<!-- json文件返回的信息与prop字段对应 -->
<template>
    <div class="app-container">
        <!------------------------------- 查询【需要请求数据】---------------------------------------------->
        
        <div class="demo-input-size">
            <!-- 查询搜索过程和v-model 字段息息相关！关注errorlog页面中的searchForm -->
            <el-input v-model="input.BatteryNumber" class="w-50 m-2" size="large" placeholder="请输入电池编号"
                :prefix-icon="Search" clearable />
            <!-- 在searchBtnClick处调用axiosReq，请求电池编号数据,参考Errorlog 中 的serchBthClick的用法-->
            <el-button type="primary" size="media" @click="searchBtnClick">筛选信息</el-button>
            <!-- 电池溯源信息查询 -->
            <el-button  type="success" size="media" @click="searchBtnClick2">电池溯源信息查询</el-button>
        </div>
        <!----------------------------- 溯源记录弹窗 -------------------------------------------------------->
        <el-drawer v-model="table" title="电池溯源信息" direction="rtl" size="50%">
            <el-table :data="gridData" style="width: auto">
                <el-table-column property="BatteryNumber" label="电池编号" width="150" />
                <el-table-column property="BatterBackground" label="溯源信息" width="200" />
            </el-table>
        </el-drawer>
        <!----------------------------- 溯源记录弹窗 -------------------------------------------------------->
        <!----------------------------- 表单 --------------------------------------------------------->
        <!-- el-scrollbar是滚动条 -->
        <el-scrollbar> 
            <div>
                <el-table ref="tableRef" :data="tableData" style="width: auto">
                    <el-table-column prop="BatteryNumber" label="电池编号" width="auto" />
                    <el-table-column prop="BatteryHistory" label="历史流转记录" width="auto" />
                    <el-table-column prop="BatteryStatus" label="状态" width="auto" />
                </el-table>
            </div>
        </el-scrollbar>

        <!----------------------------------------- 分页【需要请求数据】-------------------------------------------->
        <!-----关注 Errorlog 中 v-model的使用 ----------------->
        <!-- 此处需要用axiosReq请求信息，可以参考errorlog.vue 中分页的用法 -->
        <el-affix position="bottom" :offset="20">
            <div class="columnCC mt2 ">
                <el-pagination v-model:currentPage="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
                    :background="true" layout="total, sizes, prev, pager, next, jumper" :total="totalPage"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-affix>
        <!----------------------------------------- 分页 --------------------------------------------->
    </div>
</template>
  
<script lang="ts" setup>
import { ElTable } from 'element-plus'
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { reactive, } from 'vue'
import { ElDrawer, ElMessageBox } from 'element-plus'

// ===================溯源信息弹窗相关===========================
const table = ref(false)
const dialog = ref(false)
const loading = ref(false)
let timer

// 关闭按钮
const cancelForm = () => {
    loading.value = false
    dialog.value = false
    clearTimeout(timer)
}

const handleClose = (done) => {
    if (loading.value) {
        return
    }
    ElMessageBox.confirm('Do you want to submit?')
        .then(() => {
            loading.value = true
            timer = setTimeout(() => {
                done()
                // 动画关闭需要一定的时间
                setTimeout(() => {
                    loading.value = false
                }, 400)
            }, 2000)
        })
        .catch(() => {
            // catch error
        })
}

const gridData = [
    {
        BatteryNumber: '2016-05-02',
        BatterBackground: 'Peter Parker',

    },
    {
        BatteryNumber: '2016-05-04',
        BatterBackground: 'Peter Parker',

    },
    {
        BatteryNumber: '2016-05-01',
        BatterBackground: 'Peter Parker',

    },

]
// ===================溯源信息弹窗相关===========================

// ===================搜索相关===========================
const searchBtnClick = () => {
    // 写请求，或者直接读列表的元素

}
const searchBtnClick2 = () => {
// 让弹窗弹出来
   table.value=true
// 写请求，或者直接读列表的元素,并将数据渲染到弹窗上的表单上

}
// 输入的数据与查询的数据进行绑定
const input = reactive({
    BatteryNumber: '',
})
// ===================搜索相关============================

// =================== 分页 =========================
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
// =================== 分页 =========================


// =================== 数据列表 =========================
//文件信息

const tableData: User[] = [
    {
        // date: '2016-05-03',
        BatteryNumber: '121212',
        BatteryHistory: '用户回收',
        BatteryStatus: '回收企业中',
        BatterBackground:''
    },
    {
        // date: '2016-05-03',
        BatteryNumber: '121212',
        BatteryHistory: '用户回收',
        BatteryStatus: '回收企业中',
    }, {
        // date: '2016-05-03',
        BatteryNumber: '121212',
        BatteryHistory: '用户回收',
        BatteryStatus: '回收企业中',
    }, {
        // date: '2016-05-03',
        BatteryNumber: '121212',
        BatteryHistory: '用户回收',
        BatteryStatus: '回收企业中',
    }, {
        // date: '2016-05-03',
        BatteryNumber: '121212',
        BatteryHistory: '用户回收',
        BatteryStatus: '回收企业中',
    },
]
// ===================数据列表=========================



</script>
<style scoped lang="scss">
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