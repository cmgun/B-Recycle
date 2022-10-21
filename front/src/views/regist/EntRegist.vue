<!--suppress ALL -->
<template>
  <div>
    <!-- 使用了container来布局 -->
    <div class="common-layout">
      <el-container>
        <el-header height="100xp">
          <br />
          <h2 class="text-center">
            企业注册信息表
          </h2>
        </el-header>
        <el-container>
          <el-aside width="720px"></el-aside>
          <el-main>
            <!-- X 未实现功能：账号（不允许中文）、密码 = = 再次输入密码、名字（不允许空格）、手机号11位、身份证号18位-->
            <!-- 布局看起来不够好看 -->
            <el-form ref="ruleFormRef" :model="ruleForm" label-position="top" status-icon :rules="rules"
              class="demo-ruleForm" style="max-width: 460px">

              <el-form-item label="账号" prop="AccountNumber">
                <el-input v-model="ruleForm.AccountNumber" clearable />
              </el-form-item>

              <el-form-item label="密码" prop="pass">
                <el-input v-model="ruleForm.pass" type="password" autocomplete="off" clearable />
              </el-form-item>
              <el-form-item label="再次输入密码" prop="checkPass">
                <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" clearable />
              </el-form-item>

              <el-form-item label="企业名称" prop="companyID">
                <el-input v-model="ruleForm.companyID" type="text" clearable />
              </el-form-item>

              <el-form-item label="统一社会信用代码" prop="TrustNumber">
                <el-input v-model.number="ruleForm.TrustNumber" clearable />
              </el-form-item>

              <el-form-item label="电话" prop="PhoneNumber">
                <el-input v-model.number="ruleForm.PhoneNumber" clearable />
              </el-form-item>

              <el-form-item label="地址" prop="Address">
                <el-input v-model="ruleForm.Address" type="text" clearable />
              </el-form-item>
              <!-- 选择器 -->
              <el-form-item label="企业类型" prop="CompanyType">
                <el-select v-model="ruleForm.CompanyType" placeholder="请选择企业类型" >
                  <el-option label="车企" value="CQ" />
                  <el-option label="电池生产企业" value="DCSCS" />
                  <el-option label="电池租赁商" value="DCZLS" />
                  <el-option label="电池回收商" value="DCHSS" />
                  <el-option label="储能企业" value="CNQY" />
                  <el-option label="电池原材料生产企业" value="DCYCLSCQY" />
                </el-select>
              </el-form-item>
              <!-- 空一行！ -->
              <br />
              <el-form-item>
                <el-button type="primary" @click="submitForm(ruleFormRef)">提交</el-button>
                <el-button @click="resetForm(ruleFormRef)">重置</el-button>
                <el-button @click.prevent="backLogin">返回</el-button>
              </el-form-item>
            </el-form>
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

//use the auto import from vite.config.js of AutoImport
const router = useRouter()

//返回 
let backLogin = () => {
  router.push(`/login`)
}


// ↓ ↓ ======表单涉及的代码=======
const ruleFormRef = ref<FormInstance>()

// 用户名
const checkAccountNumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入英文和数字的组合'))
  } else {
    callback()
  }
}
// 电话号码
const checkPhoneNumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入正确的电话号码'))
  }
  setTimeout(() => {
    if (!Number.isInteger(value)) {
      callback(new Error('请输入数字'))
    }
    else {
      callback()
    }
  }, 1000)
}
// 密码
const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (ruleForm.checkPass !== '') {
      if (!ruleFormRef.value) return
      ruleFormRef.value.validateField('checkPass', () => null)
    }
    callback()
  }
}
// 密码匹配
const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== ruleForm.pass) {
    callback(new Error("两次输入的密码不一样!"))
  } else {
    callback()
  }
}

// 地址
const Address = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入企业地址'))
  } else {
    callback()
  }
}

// 社会信用代码
const TrustNumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入统一社会信用代码'))
  } else {
    callback()
  }
}

// 公司名称

const companyID = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入公司名称'))
  } else {
    callback()
  }
}

const ruleForm = reactive({
  AccountNumber: '',
  pass: '',
  checkPass: '',
  TrustNumber: '',
  companyID: '',
  Address: '',
  PhoneNumber: '',
  CompanyType: '',
})

const rules = reactive({
  AccountNumber: [{ validator: checkAccountNumber, trigger: 'blur' }],
  pass: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
  PhoneNumber: [{ validator: checkPhoneNumber, trigger: 'blur' }],
  companyID: [{ validator: companyID, trigger: 'blur' }],
  TrustNumber: [{ validator: TrustNumber, trigger: 'blur' }],
  Address: [{ validator: Address, trigger: 'blur' }],
  CompanyType: [
    {
      // 决定了是否必须填
      required: true, 
      message: '请选择企业类型',
      trigger: 'change',
    },
  ],
})

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!');
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
