<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="passengers"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
          <a @click="onEdit(record)">Edit</a>
        </a-space>
      </template>
    </template>
  </a-table>
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
import { defineComponent, ref, reactive, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  setup() {
    const visible = ref(false);

    let passenger = ref({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });

    const passengers = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 2,
    });
    let loading = ref(false);
    const columns = [{
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    }, {
      title: 'ID number',
      dataIndex: 'idCard',
      key: 'idCard',
    }, {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
    }, {
      title: 'Operation',
      dataIndex: 'operation'
    }];

    const onAdd = () => {
      passenger.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      passenger.value = window.Tool.copy(record);
      visible.value = true;
    };

    const handleOk = () => {
      axios.post("/member/passenger/save", passenger.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Save successfully！"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current, // stay at current page after adding
            // page: 1, // return to first page after adding
            size: pagination.value.pageSize
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/member/passenger/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          passengers.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange = (pagination) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      passenger,
      visible,
      passengers,
      pagination,
      columns,
      loading,
      onAdd,
      onEdit,
      handleOk,
      handleTableChange,
      handleQuery
    };
  },
});
</script>

<style>
</style>
