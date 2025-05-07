<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
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
          <user-outlined /> &nbsp; About
        </router-link>
      </a-menu-item>
      <a-menu-item key="/station">
        <router-link to="/station">
          <user-outlined /> &nbsp; Station Management
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train">
        <router-link to="/train">
          <user-outlined /> &nbsp; Train Management
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-station">
        <router-link to="/train-station">
          <user-outlined /> &nbsp; Train-Station
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-carriage">
        <router-link to="/train-carriage">
          <user-outlined /> &nbsp; Train-Carriage
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-seat">
        <router-link to="/train-seat">
          <user-outlined /> &nbsp; Train-Seat
        </router-link>
      </a-menu-item>
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