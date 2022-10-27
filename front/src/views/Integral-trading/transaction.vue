<!--suppress ALL -->
<template>
    <div>
        <!-- 使用了container来布局 -->
        <div class="common-layout">
            <!-- <el-container> -->
                <el-container>                    
                    <el-main>
                        <h3>积分交易</h3>
                        <br />
                        <el-form ref="ruleFormRef" :model="ruleForm" label-position="top" status-icon :rules="rules"
                            class="demo-ruleForm" style="max-width: 460px">

                            <el-form-item label="输入需要交易的积分额度" prop="Score">
                                <el-input v-model="ruleForm.Score" clearable />
                            </el-form-item>

                            <el-radio-group v-model="diabaled" size="large" @change="change">
                                <el-radio :label="false">指定买方</el-radio>
                                <el-radio :label="true">不指定买方</el-radio>
                            </el-radio-group>
                        <!-- <br /> -->
                            <el-form-item label="输入指定买方" prop="Buyer">
                                <el-input v-model="ruleForm.Buyer" :disabled="diabaled" clearable />
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
            <!-- </el-container> -->
        </div>
    </div>
</template>
  
<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import type { FormInstance } from 'element-plus'

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

// 积分
const Score = (rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请输入电池编号'))
    } else {
        callback()
    }
}

// 最低成交金额
const MinimumTransactionAmount = (rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请输入最低成交金额'))
    } else {
        callback()
    }
}
// 请输入指定买家
const Buyer = (rule: any, value: any, callback: any) => {
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
    Score: '',
    BiddingDay: '',
    Buyer: '',
    MinimumTransactionAmount: '',
})

const rules = reactive({
    Score: [{ validator: Score, trigger: 'blur' }],
    BiddingDay: [{ validator: BiddingDay, trigger: 'blur' }],
    Buyer: [{ validator: Buyer, trigger: 'blur' }],
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
  