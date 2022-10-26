<!--suppress ALL -->
<template>
  <div>
    <!-- 使用了container来布局 -->
    <div class="common-layout">
      <el-container>
        <el-header height="100xp">
          <br />
          <h2 class="text-center " >
            消费者信息注册表
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

              <el-form-item label="姓名" prop="ID">
                <el-input v-model="ruleForm.ID" type="text" clearable />
              </el-form-item>

              <el-form-item label="身份证号" prop="IDnumber">
                <el-input v-model.number="ruleForm.IDnumber" clearable />
              </el-form-item>
              <el-form-item label="电话号码" prop="PhoneNumber">
                <el-input v-model.number="ruleForm.PhoneNumber" clearable />
              </el-form-item>
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
import { useUserStore } from '@/store/user'
import { Md5 } from 'ts-md5'


// handleregister 处理注册的方法：提交数据给后端【可参考登录处理方法】，返回一个弹框：注册信息已提交
const handleregister = (valid) => {
  let params = {
    userName: ruleForm.AccountNumber,
    password: ruleForm.pass,
    name: ruleForm.ID,
    phone: ruleForm.PhoneNumber,
    idno: ruleForm.IDnumber
  }
  params.password = Md5.hashStr(params.password)

  const userStore = useUserStore()
  userStore
    .customerRegist(params)
    .then(() => {
      console.log("customerRegist success")
      ElMessage({message: '注册成功。', type: 'success',})
      useCommon()
        .sleep(3)
        .then(() => {
          router.push('/login')
        })
    })
    .catch((res) => {
      console.log("customerRegist error")
      ElMessage.error(res.msg)
    })
}

//use the auto import from vite.config.js of AutoImport
const router = useRouter()

//返回
let backLogin = () => {
  router.push(`/login`)
}


// ↓ ↓ ======表单涉及的代码=======
const ruleFormRef = ref<FormInstance>()

// 名字 没改好
const checkID = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入身份证姓名'))
  }

  setTimeout(() => {
    if (Number.isInteger(value)) {
      callback(new Error('请输入正确的身份证姓名'))
    } else {
      callback()
    }
  }, 1000)
}
// 身份证号码
const checkIDnumber = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入正确的身份证号码'))
  }
  setTimeout(() => {
    if (!Number.isInteger(value)) {
      callback(new Error('请输入数字'))
    } else {
      callback()
    }
  }, 1000)
}
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

const ruleForm = reactive({
  AccountNumber: '',
  pass: '',
  checkPass: '',
  PhoneNumber: '',
  IDnumber: '',
  ID: '',
})

const rules = reactive({
  AccountNumber: [{ validator: checkAccountNumber, trigger: 'blur' }],
  pass: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
  PhoneNumber: [{ validator: checkPhoneNumber, trigger: 'blur' }],
  IDnumber: [{ validator: checkIDnumber, trigger: 'blur' }],
  ID: [{ validator: checkID, trigger: 'blur' }]
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
