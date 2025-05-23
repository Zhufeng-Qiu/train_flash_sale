<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      <train-select-view v-model="params.code" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
      <a-button type="danger" @click="onClickGenDaily">Generate train info manually</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrains"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
          <a-popconfirm
              title="Once deleted, it cannot be recovered. Confirm to delete?"
              @confirm="onDelete(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a style="color: red">Delete</a>
          </a-popconfirm>
          <a @click="onEdit(record)">Edit</a>
        </a-space>
      </template>
      <template v-else-if="column.dataIndex === 'type'">
        <span v-for="item in TRAIN_TYPE_ARRAY" :key="item.code">
          <span v-if="item.code === record.type">
            {{item.desc}}
          </span>
        </span>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Daily Train" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="dailyTrain" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Date">
        <a-date-picker v-model:value="dailyTrain.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      </a-form-item>
      <a-form-item label="Train Number">
        <train-select-view v-model="dailyTrain.code" @change="onChangeCode"></train-select-view>
      </a-form-item>
      <a-form-item label="Train Type">
        <a-select v-model:value="dailyTrain.type">
          <a-select-option v-for="item in TRAIN_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Departure Station">
        <a-input v-model:value="dailyTrain.start" />
      </a-form-item>
      <a-form-item label="Departure Station Alias">
        <a-input v-model:value="dailyTrain.startPinyin" />
      </a-form-item>
      <a-form-item label="Start Time">
        <a-time-picker v-model:value="dailyTrain.startTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Arrival Station">
        <a-input v-model:value="dailyTrain.end" />
      </a-form-item>
      <a-form-item label="Arrival Station Alias">
        <a-input v-model:value="dailyTrain.endPinyin" />
      </a-form-item>
      <a-form-item label="End Time">
        <a-time-picker v-model:value="dailyTrain.endTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal v-model:visible="genDailyVisible" title="Generate Train Info" @ok="handleGenDailyOk"
           :confirm-loading="genDailyLoading" ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="genDaily" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Date">
        <a-date-picker v-model:value="genDaily.date" placeholder="Please select date"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import dayjs from 'dayjs';

export default defineComponent({
  name: "daily-train-view",
  components: {TrainSelectView},
  setup() {
    const TRAIN_TYPE_ARRAY = window.TRAIN_TYPE_ARRAY;
    const visible = ref(false);
    let dailyTrain = ref({
      id: undefined,
      date: undefined,
      code: undefined,
      type: undefined,
      start: undefined,
      startPinyin: undefined,
      startTime: undefined,
      end: undefined,
      endPinyin: undefined,
      endTime: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrains = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      code: null
    });
    const genDaily = ref({
      date: null
    });
    const genDailyVisible = ref(false);
    const genDailyLoading = ref(false);
    const columns = [
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Train Number',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: 'Train Type',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: 'Departure Station',
      dataIndex: 'start',
      key: 'start',
    },
    {
      title: 'Departure Station Alias',
      dataIndex: 'startPinyin',
      key: 'startPinyin',
    },
    {
      title: 'Start Time',
      dataIndex: 'startTime',
      key: 'startTime',
    },
    {
      title: 'Arrival Station',
      dataIndex: 'end',
      key: 'end',
    },
    {
      title: 'Arrival Station Alias',
      dataIndex: 'endPinyin',
      key: 'endPinyin',
    },
    {
      title: 'End Time',
      dataIndex: 'endTime',
      key: 'endTime',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      dailyTrain.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrain.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Delete successfully!"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
            code: params.value.code,
            date: params.value.date
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/daily-train/save", dailyTrain.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Save successfully!"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
            code: params.value.code,
            date: params.value.date
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
      axios.get("/business/admin/daily-train/query-list", {
        params: {
          page: param.page,
          size: param.size,
          code: params.value.code,
          date: params.value.date
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrains.value = data.content.list;
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
        size: pagination.pageSize,
        code: params.value.code,
        date: params.value.date
      });
    };

    const onChangeCode = (train) => {
      console.log("Train dop-down box selected: ", train);
      let t = Tool.copy(train);
      delete t.id;
      // Combine using assign
      dailyTrain.value = Object.assign(dailyTrain.value, t);
    };

    const onClickGenDaily = () => {
      genDailyVisible.value = true;
    };

    const handleGenDailyOk = () => {
      let date = dayjs(genDaily.value.date).format("YYYY-MM-DD");
      genDailyLoading.value = true;
      axios.get("/business/admin/daily-train/gen-daily/" + date).then((response) => {
        genDailyLoading.value = false;
        let data = response.data;
        if (data.success) {
          notification.success({description: "Generate successfully!"});
          genDailyVisible.value = false;
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
            code: params.value.code,
            date: params.value.date
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      TRAIN_TYPE_ARRAY,
      dailyTrain,
      visible,
      dailyTrains,
      pagination,
      columns,
      params,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      onChangeCode,
      genDaily,
      genDailyVisible,
      handleGenDailyOk,
      onClickGenDaily,
      genDailyLoading
    };
  },
});
</script>
