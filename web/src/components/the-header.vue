<template>
  <a-layout-header class="header">
    <div class="logo">
      <router-link to="/welcome" style="color: white; font-size: 18px">
        <div style="display: flex; align-items: center;">
          <img src="@/assets/logo.png" alt="logo" style="height: 35px; margin-right: 10px;" />
          <span>Train Flash Sale</span>
        </div>
      </router-link>
    </div>
    <div style="float: right; color: white;">
      Hello, {{member.mobile}} &nbsp;&nbsp; / &nbsp;&nbsp;
      <router-link to="/login" style="color: white;">
        Sign out
      </router-link>
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; Welcome
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined /> &nbsp; Passenger
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <user-outlined /> &nbsp; Ticket Search
        </router-link>
      </a-menu-item>
      <a-menu-item key="/my-ticket">
        <router-link to="/my-ticket">
          <idcard-outlined /> &nbsp; My Tickets
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import router from '@/router'

export default defineComponent({
  name: "the-header-view",
  setup() {
    let member = store.state.member;
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      member,
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  float: left;
  height: 64px;
  width: 235px;
  color: white;
  font-size: 20px;
}
</style>