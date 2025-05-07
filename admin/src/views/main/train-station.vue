<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="trainStations"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
          <a-popconfirm
              title="Once deleted, it cannot be recovered. Are you sure you want to delete?"
              @confirm="onDelete(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a style="color: red">Delete</a>
          </a-popconfirm>
          <a @click="onEdit(record)">Edit</a>
        </a-space>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Train Station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="trainStation" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Train Number">
        <a-input v-model:value="trainStation.trainCode" />
      </a-form-item>
      <a-form-item label="Station Index">
        <a-input v-model:value="trainStation.index" />
      </a-form-item>
      <a-form-item label="Station">
        <a-input v-model:value="trainStation.name" />
      </a-form-item>
      <a-form-item label="Station Alias">
        <a-input v-model:value="trainStation.namePinyin" />
      </a-form-item>
      <a-form-item label="Arrival Time">
        <a-time-picker v-model:value="trainStation.inTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Departure Time">
        <a-time-picker v-model:value="trainStation.outTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Stop Duration">
        <a-time-picker v-model:value="trainStation.stopTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Mileage(km)">
        <a-input v-model:value="trainStation.km" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "train-station-view",
  setup() {
    const visible = ref(false);
    let trainStation = ref({
      id: undefined,
      trainCode: undefined,
      index: undefined,
      name: undefined,
      namePinyin: undefined,
      inTime: undefined,
      outTime: undefined,
      stopTime: undefined,
      km: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const trainStations = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    {
      title: 'Train Number',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: 'Station Index',
      dataIndex: 'index',
      key: 'index',
    },
    {
      title: 'Station',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Station Alias',
      dataIndex: 'namePinyin',
      key: 'namePinyin',
    },
    {
      title: 'Arrival Time',
      dataIndex: 'inTime',
      key: 'inTime',
    },
    {
      title: 'Departure Time',
      dataIndex: 'outTime',
      key: 'outTime',
    },
    {
      title: 'Stop Duration',
      dataIndex: 'stopTime',
      key: 'stopTime',
    },
    {
      title: 'Mileage(km)',
      dataIndex: 'km',
      key: 'km',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      trainStation.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      trainStation.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/train-station/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Delete successfully!"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/train-station/save", trainStation.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Save successfully!"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
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
      axios.get("/business/admin/train-station/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          trainStations.value = data.content.list;
          // Set the value of the pagination component
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
      trainStation,
      visible,
      trainStations,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete
    };
  },
});
</script>
