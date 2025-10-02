<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        :openKeys="['batch', 'base', 'business', 'member']"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; Welcome
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          <InfoCircleOutlined /> &nbsp; About
        </router-link>
      </a-menu-item>
      <a-sub-menu key="member">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Member Management
          </span>
        </template>
        <a-menu-item key="/member/ticket">
          <router-link to="/member/ticket">
            <MenuUnfoldOutlined /> Member Tickets
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="business">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Business Management
          </span>
        </template>
        <a-menu-item key="/business/confirm-order">
          <router-link to="/business/confirm-order">
            <MenuUnfoldOutlined /> &nbsp; Order Management
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/sk-token">
          <router-link to="/business/sk-token">
            <MenuUnfoldOutlined /> &nbsp; Token Management
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-ticket">
          <router-link to="/business/daily-train-ticket">
            <MenuUnfoldOutlined /> &nbsp; Daily Remaining Ticket
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <MenuUnfoldOutlined /> &nbsp; Daily Train Management
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-station">
          <router-link to="/business/daily-train-station">
            <MenuUnfoldOutlined /> &nbsp; Daily Train-Station
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-carriage">
          <router-link to="/business/daily-train-carriage">
            <MenuUnfoldOutlined /> &nbsp; Daily Train-Carriage
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-seat">
          <router-link to="/business/daily-train-seat">
            <MenuUnfoldOutlined /> &nbsp; Daily Train-Seat
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="base">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Basic Data
          </span>
        </template>
        <a-menu-item key="/base/station">
          <router-link to="/base/station">
            <MenuUnfoldOutlined /> &nbsp; Station Management
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train">
          <router-link to="/base/train">
            <MenuUnfoldOutlined /> &nbsp; Train Management
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-station">
          <router-link to="/base/train-station">
            <MenuUnfoldOutlined /> &nbsp; Train-Station
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-carriage">
          <router-link to="/base/train-carriage">
            <MenuUnfoldOutlined /> &nbsp; Train-Carriage
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-seat">
          <router-link to="/base/train-seat">
            <MenuUnfoldOutlined /> &nbsp; Train-Seat
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="batch">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Batch Management
          </span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined /> &nbsp; Task Management
          </router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";

export default defineComponent({
  name: "the-sider-view",
  setup() {
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>