<template>
  <p>
    <a-space>
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="Please select date"></a-date-picker>
      <station-select-view v-model="params.start" width="200px"></station-select-view>
      <station-select-view v-model="params.end" width="200px"></station-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
    </template>
  </a-table>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";

export default defineComponent({
  name: "daily-train-ticket-view",
  components: {StationSelectView, TrainSelectView},
  setup() {
    const visible = ref(false);
    let dailyTrainTicket = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      start: undefined,
      startPinyin: undefined,
      startTime: undefined,
      startIndex: undefined,
      end: undefined,
      endPinyin: undefined,
      endTime: undefined,
      endIndex: undefined,
      ydz: undefined,
      ydzPrice: undefined,
      edz: undefined,
      edzPrice: undefined,
      rw: undefined,
      rwPrice: undefined,
      yw: undefined,
      ywPrice: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainTickets = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      trainCode: null,
      date: null,
      start: null,
      end: null
    });
    const columns = [
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Train Number',
      dataIndex: 'trainCode',
      key: 'trainCode',
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
      title: 'Departure Station Index',
      dataIndex: 'startIndex',
      key: 'startIndex',
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
      title: 'Arrival Station Index',
      dataIndex: 'endIndex',
      key: 'endIndex',
    },
    {
      title: 'First Class Remaining Tickets',
      dataIndex: 'ydz',
      key: 'ydz',
    },
    {
      title: 'First Class Ticket Price',
      dataIndex: 'ydzPrice',
      key: 'ydzPrice',
    },
    {
      title: 'Second Class Remaining Tickets',
      dataIndex: 'edz',
      key: 'edz',
    },
    {
      title: 'Second Class Ticket Price',
      dataIndex: 'edzPrice',
      key: 'edzPrice',
    },
    {
      title: 'Soft Sleeper Remaining Tickets',
      dataIndex: 'rw',
      key: 'rw',
    },
    {
      title: 'Soft Sleeper Ticket Price',
      dataIndex: 'rwPrice',
      key: 'rwPrice',
    },
    {
      title: 'Hard Seat Remaining Tickets',
      dataIndex: 'yw',
      key: 'yw',
    },
    {
      title: 'Hard Seat Ticket Price',
      dataIndex: 'ywPrice',
      key: 'ywPrice',
    },
    ];


    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode,
          date: params.value.date,
          start: params.value.start,
          end: params.value.end
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainTickets.value = data.content.list;
          // Set the value of the pagination component
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange = (page) => {
      pagination.value.pageSize = page.pageSize;
      handleQuery({
        page: page.current,
        size: page.pageSize
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      params
    };
  },
});
</script>
