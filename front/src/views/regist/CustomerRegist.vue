<!--suppress ALL -->
<template>
  <div>
    <!-- 使用了container来布局 -->
    <div class="common-layout"> 
      <el-container>
        <el-header></el-header>
        <el-container>
          <el-aside width="600px"></el-aside>
          <el-main>
            <!-- X 未实现功能：账号（不允许中文）、密码 = = 再次输入密码、名字（不允许空格）、手机号11位、身份证号18位-->
            <!-- 布局看起来不够好看 -->
            <el-form ref="ruleFormRef" :model="ruleForm" label-position="top" status-icon :rules="rules"
              class="demo-ruleForm" style="max-width: 460px">
              <el-form-item label="账号" prop="AccountNumber">
                <el-input v-model="ruleForm.AccountNumber" />
              </el-form-item>

              <el-form-item label="密码" prop="pass">
                <el-input v-model="ruleForm.pass" type="password" autocomplete="off" />
              </el-form-item>
              <el-form-item label="再次输入密码" prop="checkPass">
                <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" />
              </el-form-item>

              <el-form-item label="身份证号" prop="IDnumber">
                <el-input v-model.number="ruleForm.IDnumber" />
              </el-form-item>
              <el-form-item label="电话号码" prop="PhoneNumber">
                <el-input v-model.number="ruleForm.PhoneNumber" />
              </el-form-item>

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

const checkIDnumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入正确的身份证号码'))
  }
  setTimeout(() => {
    if (!Number.isInteger(value)) {
      callback(new Error('请输入数字'))
    }else{
      callback()
    }
  }, 1000)
}

const checkAccountNumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入英文和数字的组合'))
  }else{
    callback()
  }
}

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

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== ruleForm.pass) {
    callback(new Error("两次输入的密码不一样!"))
  } else {
    callback()
  }
}

const ruleForm = reactive({
  AccountNumber: '',
  pass: '',
  checkPass: '',
  PhoneNumber: '',
  IDnumber: '',
})

const rules = reactive({
  AccountNumber: [{ validator: checkAccountNumber, trigger: 'blur' }],
  pass: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
  PhoneNumber: [{ validator: checkPhoneNumber, trigger: 'blur' }],
  IDnumber: [{ validator: checkIDnumber, trigger: 'blur' }]
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
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  height: 100vh;
  width: 100%;
  background-color: #2d3a4b;

  .login-form {
    margin-bottom: 20vh;
    width: 360px;
  }

  .title-container {
    .title {
      font-size: 22px;
      color: #eee;
      margin: 0px auto 25px auto;
      text-align: center;
      font-weight: bold;
    }
  }
}

.svg-container {
  padding-left: 6px;
  color: $dark_gray;
  text-align: center;
  width: 30px;
}

//错误提示信息
.tip-message {
  color: #e4393c;
  height: 30px;
  margin-top: -12px;
  font-size: 12px;
}

//登录按钮
.login-btn {
  width: 100%;
  margin-bottom: 30px;
  float: none;
}

.show-pwd {
  width: 50px;
  font-size: 16px;
  color: $dark_gray;
  cursor: pointer;
  text-align: center;
}

.left {
  float: left;
}

.right {
  float: right;
}
</style>

<style lang="scss">
//css 样式重置 增加个前缀避免全局污染
.login-container {
  .el-input__wrapper {
    background-color: transparent;
    box-shadow: none;
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }

  .el-input input {
    background: transparent;
    border: 0px;
    -webkit-appearance: none;
    border-radius: 0px;
    padding: 10px 5px 10px 15px;
    color: #fff;
    height: 42px; //此处调整item的高度
    caret-color: #fff;
  }

  //hiden the input border
  .el-input__inner {
    box-shadow: none !important;
  }
}
</style>
