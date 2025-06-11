<template>
  <p>
    <a-space>
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
        <a-space>
          <a-button type="primary" @click="toOrder(record)">Order</a-button>
          <a-button type="primary" @click="showStation(record)">En-route stations</a-button>
        </a-space>
      </template>
      <template v-else-if="column.dataIndex === 'station'">
        DEP: {{record.startPinyin}}<br/>
        ARR: {{record.endPinyin}}
      </template>
      <template v-else-if="column.dataIndex === 'time'">
        DEP: {{record.startTime}} <br/>
        ARR: {{record.endTime}}
      </template>
      <template v-else-if="column.dataIndex === 'duration'">
        {{calDuration(record.startTime, record.endTime)}}<br/>
        <div v-if="record.startTime.replaceAll(':', '') >= record.endTime.replaceAll(':', '')">
          Next-day Arrival
        </div>
        <div v-else>
          Same-day Arrival
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'ydz'">
        <div v-if="record.ydz >= 0">
          ${{record.ydzPrice}}<br/>
          {{record.ydz}} seats left<br/>
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'edz'">
        <div v-if="record.edz >= 0">
          ${{record.edzPrice}}<br/>
          {{record.edz}} seats left<br/>
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'rw'">
        <div v-if="record.rw >= 0">
          {{record.rw}} seats left<br/>
          ${{record.rwPrice}}
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'yw'">
        <div v-if="record.yw >= 0">
          {{record.yw}} seats left<br/>
          ${{record.ywPrice}}
        </div>
        <div v-else>
          --
        </div>
      </template>
    </template>
  </a-table>

  <!-- Stations en route -->
  <a-modal style="top: 30px" v-model:visible="visible" :title="null" :footer="null" :closable="false">
    <a-table :data-source="stations" :pagination="false">
      <a-table-column key="index" title="Station Index" data-index="index" />
      <a-table-column key="name" title="Station Name" data-index="name" />
      <a-table-column key="inTime" title="Arrival Time" data-index="inTime">
        <template #default="{ record }">
          {{record.index === 0 ? '-' : record.inTime}}
        </template>
      </a-table-column>
      <a-table-column key="outTime" title="Departure Time" data-index="outTime">
        <template #default="{ record }">
          {{record.index === (stations.length - 1) ? '-' : record.outTime}}
        </template>
      </a-table-column>
      <a-table-column key="stopTime" title="Dwell Duration" data-index="stopTime">
        <template #default="{ record }">
          {{record.index === 0 || record.index === (stations.length - 1) ? '-' : record.stopTime}}
        </template>
      </a-table-column>
    </a-table>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";
import router from "@/router";

export default defineComponent({
  name: "ticket-view",
  components: {StationSelectView},
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
      date: null,
      start: null,
      end: null
    });
    const columns = [
      {
        title: 'Train Number',
        dataIndex: 'trainCode',
        key: 'trainCode',
      },
      {
        title: 'Station',
        dataIndex: 'station',
      },
      {
        title: 'Time',
        dataIndex: 'time',
      },
      {
        title: 'Duration',
        dataIndex: 'duration',
      },
      {
        title: 'First Class Ticket',
        dataIndex: 'ydz',
        key: 'ydz',
      },
      {
        title: 'Second Class Ticket',
        dataIndex: 'edz',
        key: 'edz',
      },
      {
        title: 'Soft Sleeper Ticket',
        dataIndex: 'rw',
        key: 'rw',
      },
      {
        title: 'Hard Seat Ticket',
        dataIndex: 'yw',
        key: 'yw',
      },
      {
        title: 'Operation',
        dataIndex: 'operation',
      },
    ];


    const handleQuery = (param) => {
      if (Tool.isEmpty(params.value.date)) {
        notification.error({description: "Please select date"});
        return;
      }
      if (Tool.isEmpty(params.value.start)) {
        notification.error({description: "Please select departure station"});
        return;
      }
      if (Tool.isEmpty(params.value.end)) {
        notification.error({description: "Please select arrival station"});
        return;
      }
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }

      // Store query parameters
      SessionStorage.set(SESSION_TICKET_PARAMS, params.value);

      loading.value = true;
      axios.get("/business/daily-train-ticket/query-list", {
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

    const calDuration = (startTime, endTime) => {
      let diff = dayjs(endTime, 'HH:mm:ss').diff(dayjs(startTime, 'HH:mm:ss'), 'seconds');
      return dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    };

    const toOrder = (record) => {
      dailyTrainTicket.value = Tool.copy(record);
      SessionStorage.set(SESSION_ORDER, dailyTrainTicket.value);
      router.push("/order")
    };

    // ---------------------- Stations en route ----------------------
    const stations = ref([]);
    const showStation = record => {
      visible.value = true;
      axios.get("/business/daily-train-station/query-by-train-code", {
        params: {
          date: record.date,
          trainCode: record.trainCode
        }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    onMounted(() => {
      params.value = SessionStorage.get(SESSION_TICKET_PARAMS) || {};
      if (Tool.isNotEmpty(params.value)) {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize
        });
      }
    });

    return {
      // dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      params,
      calDuration,
      toOrder,
      showStation,
      stations
    };
  },
});
</script>
