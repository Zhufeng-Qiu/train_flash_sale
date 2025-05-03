<template>
  <a-button type="primary" @click="showModal">Add</a-button>
  <a-modal v-model:visible="visible" title="Passenger Info" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="passenger" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Name">
        <a-input v-model:value="passenger.name" />
      </a-form-item>
      <a-form-item label="ID number">
        <a-input v-model:value="passenger.idCard" />
      </a-form-item>
      <a-form-item label="Type">
        <a-select v-model:value="passenger.type">
          <a-select-option value="1">Adult</a-select-option>
          <a-select-option value="2">Child</a-select-option>
          <a-select-option value="3">Student</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, reactive } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  setup() {
    const visible = ref(false);

    const passenger = reactive({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });

    const showModal = () => {
      visible.value = true;
    };

    const handleOk = () => {
      axios.post("/member/passenger/save", passenger).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Save successfully！"});
          visible.value = false;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    return {
      passenger,
      visible,
      showModal,
      handleOk,
    };
  },
});
</script>

<style>
</style>
