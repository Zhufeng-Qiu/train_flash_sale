<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainStations"
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
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Daily Train Station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="dailyTrainStation" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Date">
        <a-date-picker v-model:value="dailyTrainStation.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      </a-form-item>
      <a-form-item label="Train Number">
        <train-select-view v-model="dailyTrainStation.trainCode"></train-select-view>
      </a-form-item>
      <a-form-item label="Station Index">
        <a-input v-model:value="dailyTrainStation.index" />
      </a-form-item>
      <a-form-item label="Station">
        <a-input v-model:value="dailyTrainStation.name" />
      </a-form-item>
      <a-form-item label="Station Alias">
        <a-input v-model:value="dailyTrainStation.namePinyin" />
      </a-form-item>
      <a-form-item label="Arrival Time">
        <a-time-picker v-model:value="dailyTrainStation.inTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Departure Time">
        <a-time-picker v-model:value="dailyTrainStation.outTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Stop Duration">
        <a-time-picker v-model:value="dailyTrainStation.stopTime" valueFormat="HH:mm:ss" placeholder="Please select time" disabled/>
      </a-form-item>
      <a-form-item label="Mileage(km)">
        <a-input v-model:value="dailyTrainStation.km" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import {pinyin} from "pinyin-pro";
import dayjs from "dayjs";

export default defineComponent({
  name: "daily-train-station-view",
  components: {TrainSelectView},
  setup() {
    const visible = ref(false);
    let dailyTrainStation = ref({
      id: undefined,
      date: undefined,
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
    const dailyTrainStations = ref([]);
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

    function containsChinese(str) {
      return /[\u4e00-\u9fa5]/.test(str);
    }

    function transferEnglishCityName(trainStation, origin, relate) {
      const words = trainStation.value[origin].trim().split(/\s+/);
      let res;

      if (words.length > 1) {
        res = words.map(word => word.charAt(0).toUpperCase()).join('');
      } else {
        res =  trainStation.value[origin].slice(0, 3).toUpperCase();
      }
      trainStation.value[relate] = res;
    }

    function transferChineseCityName(trainStation, origin, relate) {
      trainStation.value[relate] = pinyin(trainStation.value[origin], { toneType: 'none'}).replaceAll(" ", "");
    }

    watch(() => dailyTrainStation.value.name, ()=>{
      if (Tool.isNotEmpty(dailyTrainStation.value.name)) {
        if (containsChinese(dailyTrainStation.value.name)) {
          transferChineseCityName(dailyTrainStation, "name", "namePinyin");
        } else {
          transferEnglishCityName(dailyTrainStation, "name", "namePinyin");
        }
      } else {
        dailyTrainStation.value.namePinyin = "";
      }
    }, {immediate: true});

    // Calculate stop duration automatically
    watch(() => dailyTrainStation.value.inTime, ()=>{
      let diff = dayjs(dailyTrainStation.value.outTime, 'HH:mm:ss').diff(dayjs(dailyTrainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      dailyTrainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});

    // Calculate stop duration automatically
    watch(() => dailyTrainStation.value.outTime, ()=>{
      let diff = dayjs(dailyTrainStation.value.outTime, 'HH:mm:ss').diff(dayjs(dailyTrainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      dailyTrainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});


    const onAdd = () => {
      dailyTrainStation.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrainStation.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train-station/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Delete successfully!"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
            trainCode: params.value.trainCode,
            date: params.value.date
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/daily-train-station/save", dailyTrainStation.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Save successfully!"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
            trainCode: params.value.trainCode,
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
      axios.get("/business/admin/daily-train-station/query-list", {
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
          dailyTrainStations.value = data.content.list;
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
      dailyTrainStation,
      visible,
      dailyTrainStations,
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
    };
  },
});
</script>
