<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainSeats"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'col'">
        <span v-for="item in SEAT_COL_ARRAY" :key="item.code">
          <span v-if="item.code === record.col && item.type === record.seatType">
            {{item.desc}}
          </span>
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'seatType'">
        <span v-for="item in SEAT_TYPE_ARRAY" :key="item.code">
          <span v-if="item.code === record.seatType">
            {{item.desc}}
          </span>
        </span>
      </template>
    </template>
  </a-table>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select";

export default defineComponent({
  name: "daily-train-seat-view",
  components: {TrainSelectView},
  setup() {
    const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
    const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
    const visible = ref(false);
    let dailyTrainSeat = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      carriageIndex: undefined,
      row: undefined,
      col: undefined,
      seatType: undefined,
      carriageSeatIndex: undefined,
      sell: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainSeats = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      trainCode: null,
      date: null
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
        title: 'Carriage Index',
        dataIndex: 'carriageIndex',
        key: 'carriageIndex',
      },
      {
        title: 'Row',
        dataIndex: 'row',
        key: 'row',
      },
      {
        title: 'Column',
        dataIndex: 'col',
        key: 'col',
      },
      {
        title: 'Seat Type',
        dataIndex: 'seatType',
        key: 'seatType',
      },
      {
        title: 'In-carriage Seat Index',
        dataIndex: 'carriageSeatIndex',
        key: 'carriageSeatIndex',
      },
      {
        title: 'Sale Status',
        dataIndex: 'sell',
        key: 'sell',
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
      axios.get("/business/admin/daily-train-seat/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode,
          date: params.value.date
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainSeats.value = data.content.list;
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
        size: page.pageSize,
        trainCode: params.value.trainCode,
        date: params.value.date
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      SEAT_COL_ARRAY,
      SEAT_TYPE_ARRAY,
      dailyTrainSeat,
      visible,
      dailyTrainSeats,
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
