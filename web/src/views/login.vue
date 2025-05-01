<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><rocket-two-tone />&nbsp;Train Flash Sale Platform</h1>
      <h2 style="text-align: center">&nbsp;ZephyrQiu</h2>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
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
          <a-button type="primary" block html-type="submit">Login / Sign up</a-button>
        </a-form-item>

      </a-form>
    </a-col>
  </a-row>
</template>

<script>
import { defineComponent, reactive } from 'vue';
import axios from 'axios';
export default defineComponent({
  name: "login-view",
  setup() {
    const loginForm = reactive({
      mobile: '206-000-0000',
      code: '',
    });

    const onFinish = values => {
      console.log('Success:', values);
    };

    const onFinishFailed = errorInfo => {
      console.log('Failed:', errorInfo);
    };

    const sendCode = () => {
      axios.post("http://localhost:8000/member/member/send-code", {
        mobile: loginForm.mobile
      }).then(response => {
        console.log(response);
      });
    };

    return {
      loginForm,
      onFinish,
      onFinishFailed,
      sendCode,
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