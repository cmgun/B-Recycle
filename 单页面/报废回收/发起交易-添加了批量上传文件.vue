<!--suppress ALL -->
<template>
    <div>
        <!-- 使用了container来布局 -->
        <div class="common-layout">
            <el-container>
                <el-header height="100xp">
                    <br />
                    <h2>
                        <!--  class="text-center " -->
                        发起交易
                    </h2>


                </el-header>
                <el-container>
                    <!-- <el-aside width="400px"></el-aside> -->
                    <el-main>
                        <el-row :gutter="20">
                            <el-col :span="2">

                                <div> 批量上传电池编号 </div>
                            </el-col>
                            <el-col :span="1">
                                <el-upload ref="uploadRef" class="upload-demo"
                                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                                    :auto-upload="false">
                                    <el-button type="primary" size=""> 选取文件 </el-button>
                                </el-upload>

                            </el-col>
                        </el-row>
                        <!-- <div class="app-container" style="margin-top:0px;"> -->

                        <!-- </div> -->
                        <el-form ref="ruleFormRef" :model="ruleForm" label-position="top" status-icon :rules="rules"
                            class="demo-ruleForm" style="max-width: 460px">

                            <el-form-item label="电池编号" prop="BatteryNumber">
                                <el-input v-model="ruleForm.BatteryNumber" clearable />
                            </el-form-item>

                            <el-radio-group v-model="diabaled" size="large" @change="change">
                                <el-radio :label="false">指定回收商</el-radio>
                                <el-radio :label="true">不指定回收商</el-radio>

                            </el-radio-group>
                            <el-form-item label="指定回收商" prop="Recycler">

                                <el-input v-model="ruleForm.Recycler" :disabled="diabaled" clearable />


                            </el-form-item>
                            <el-form-item label="最低成交金额/元" prop="MinimumTransactionAmount">
                                <el-input v-model="ruleForm.MinimumTransactionAmount" clearable />
                            </el-form-item>

                            <el-form-item label="竞价时间/天" prop="BiddingDay">
                                <el-input v-model="ruleForm.BiddingDay" clearable />
                            </el-form-item>
                        </el-form>
                        <!-- 选项 参考链接：https://element-plus.gitee.io/zh-CN/component/radio.html#单选框组-->
                        <!-- 通过 change 事件来响应变化，它会传入一个参数 value 来表示改变之后的值。 -->

                        <br />
                        <el-form-item>
                            <el-button round type="primary" @click="submitForm(ruleFormRef)">提交</el-button>
                            <el-button round @click="resetForm(ruleFormRef)">重置</el-button>
                            <!-- <el-button @click.prevent="backLogin">返回</el-button> -->
                        </el-form-item>
                    </el-main>
                </el-container>
            </el-container>
        </div>
    </div>
</template>
  
<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import type { FormInstance } from 'element-plus'
// 提交文件
import { UploadInstance, valueEquals } from 'element-plus'

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




// 选项
const diabaled = ref('2')
const change = (params) => {
    const label = params;
    // change 可以得到label信息，也就是选项信息。
    console.log(label);

}
// handleregister 处理注册的方法：提交数据给后端【可参考登录处理方法】，返回一个弹框：注册信息已提交
const handleregister = (valid) => {
    //注册信息提交弹窗 
    if (valid)
        ElMessage({
            message: '注册信息已成功提交。',
            type: 'success',
        })
    else {
        ElMessage.error('注册信息无法提交，请检查后重新提交。')
    }
}


// ↓ ↓ ======表单涉及的代码=======
const ruleFormRef = ref<FormInstance>()

// 电池编号 
const BatteryNumber = (rule: any, value: any, callback: any) => {
    // if (!value) {
        // return callback(new Error('请输入电池编号'))
    // } else {
        callback()
    // }
}

// 最低成交金额
const MinimumTransactionAmount = (rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请输入最低成交金额'))
    } else {
        callback()
    }
}
// 请输入指定回收商
const Recycler = (rule: any, value: any, callback: any) => {
    if (!value) {
        if (change.parames == "ture") {
            return callback(new Error('请输入指定回收商'))
        }
    } else {
        callback()
    }
}

// 竞价时间
const BiddingDay = (rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请输入竞价时间/天'))
    } else {
        callback()
    }
}

const ruleForm = reactive({
    BatteryNumber: '',
    BiddingDay: '',
    Recycler: '',
    MinimumTransactionAmount: '',
})

const rules = reactive({
    BatteryNumber: [{ validator: BatteryNumber, trigger: 'blur' }],
    BiddingDay: [{ validator: BiddingDay, trigger: 'blur' }],
    Recycler: [{ validator: Recycler, trigger: 'blur' }],
    MinimumTransactionAmount: [{ validator: MinimumTransactionAmount, trigger: 'blur' }],

})

// 提交表单的处理方法，注册信息接口
const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            console.log('submit!');
            // 提交后的信息弹窗方法
            handleregister(valid);
        } else {
            console.log('error submit!');
            handleregister(valid);
            return false
        }

    })
}
// 重置内容
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
}
  // ↑ ↑ ======表单涉及的代码=======
</script>
  
<style lang="scss" scoped>

</style>
  