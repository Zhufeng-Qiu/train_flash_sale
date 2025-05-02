import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import * as Icons from '@ant-design/icons-vue'
import axios from 'axios';

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app');


const icons = Icons;
for (const i in icons) {
  app.component(i, icons[i]);
}

/**
 * axios interceptor for back-end
 */
axios.interceptors.request.use(function (config) {
  console.log('Request parameter：', config);
  const _token = store.state.member.token;
  if (_token) {
    config.headers.token = _token;
    console.log("Add token into request headers: ", _token);
  }
  return config;
}, error => {
  return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
  console.log('Response result：', response);
  return response;
}, error => {
  console.log('Response error：', error);
  const response = error.response;
  const status = response.status;
  if (status === 401) {
     // check if status code is 401, then redirect to login page
    console.log("Not logged in or session time out, redirect to the login page.");
    store.commit("setMember", {});
    notification.error({ description: "Not logged in or session time out" });
    router.push('/login');
  }
  return Promise.reject(error);
});

axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('Environment：', process.env.NODE_ENV);
console.log('Server：', process.env.VUE_APP_SERVER);
