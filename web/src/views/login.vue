<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><rocket-two-tone />&nbsp;Train Flash Sale Platform</h1>
      <h2 style="text-align: center">&nbsp;ZephyrQiu</h2>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: 'Please input mobile number' }]"
        >
          <a-input v-model:value="loginForm.mobile" placeholder="mobile number"/>
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: 'Please input verification code' }]"
        >
          <a-input v-model:value="loginForm.code" placeholder="verification code">
            <template #addonAfter>
              <a @click="sendCode">Send code</a>
            </template>
          </a-input>
          <!--<a-input v-model:value="loginForm.code" placeholder="验证码"/>-->
        </a-form-item>

        <a-form-item>
          <a-button type="primary" block @click="login">Login / Sign up</a-button>
        </a-form-item>

      </a-form>
    </a-col>
  </a-row>
</template>

<script>
import { defineComponent, reactive } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';

export default defineComponent({
  name: "login-view",
  setup() {
    const loginForm = reactive({
      mobile: '206-000-0000',
      code: '',
    });

    const sendCode = () => {
      axios.post("/member/member/send-code", {
        mobile: loginForm.mobile
      }).then(response => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: 'Send verification successfully!'});
          loginForm.code = "123456";
        } else {
          notification.error({ description: data.message});
        }
      });
    };

    const login = () => {
      axios.post("/member/member/login", loginForm).then(response => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: 'Login successfully!'});
        } else {
          notification.error({ description: data.message});
        }
      });
    };

    return {
      loginForm,
      sendCode,
      login
    };
  },
});
</script>

<style>
.login-main h1 {
  font-size: 25px;
  font-weight: bold;
}
.login-main h2 {
  padding-bottom: 10px;
}
.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}
</style>