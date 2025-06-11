<template>
  <p>
    <a-space>
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
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
              title="Once deleted, it cannot be recovered. Confirm to delete?"
              @confirm="onDelete(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a style="color: red">Delete</a>
          </a-popconfirm>
          |
          <a @click="onEdit(record)">Edit</a>
        </a-space>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Train Station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="trainStation" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Train Number">
        <train-select-view v-model="trainStation.trainCode"></train-select-view>
      </a-form-item>
      <a-form-item label="Station Index">
        <a-input v-model:value="trainStation.index" />
        <span style="color: red">IMPORTANT: the index of first station must be 0; otherwise, the Seating chart feature would be affected</span>
      </a-form-item>
      <a-form-item label="Station">
        <station-select-view v-model="trainStation.name"></station-select-view>
      </a-form-item>
      <a-form-item label="Station Alias">
        <a-input v-model:value="trainStation.namePinyin" disabled/>
      </a-form-item>
      <a-form-item label="Arrival Time">
        <a-time-picker v-model:value="trainStation.inTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Departure Time">
        <a-time-picker v-model:value="trainStation.outTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Stop Duration">
        <a-time-picker v-model:value="trainStation.stopTime" valueFormat="HH:mm:ss" placeholder="Please select time" disabled/>
      </a-form-item>
      <a-form-item label="Mileage(km)">
        <a-input v-model:value="trainStation.km" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import {pinyin} from "pinyin-pro";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";
import dayjs from 'dayjs';

export default defineComponent({
  name: "train-station-view",
  components: {StationSelectView, TrainSelectView},
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
    let params = ref({
      trainCode: null
    });
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

    watch(() => trainStation.value.name, ()=>{
      if (Tool.isNotEmpty(trainStation.value.name)) {
        if (containsChinese(trainStation.value.name)) {
          transferChineseCityName(trainStation, "name", "namePinyin");
        } else {
          transferEnglishCityName(trainStation, "name", "namePinyin");
        }
      } else {
        trainStation.value.namePinyin = "";
      }
    }, {immediate: true});

    // Calculate stop duration automatically
    watch(() => trainStation.value.inTime, ()=>{
      let diff = dayjs(trainStation.value.outTime, 'HH:mm:ss').diff(dayjs(trainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      trainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});

    // Calculate stop duration automatically
    watch(() => trainStation.value.outTime, ()=>{
      let diff = dayjs(trainStation.value.outTime, 'HH:mm:ss').diff(dayjs(trainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      trainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});

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
            trainCode: params.value.trainCode
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
            size: pagination.value.pageSize,
            trainCode: params.value.trainCode
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
          size: pagination.value.pageSize,
        };
      }
      loading.value = true;
      axios.get("/business/admin/train-station/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode
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
        size: pagination.pageSize,
        trainCode: params.value.trainCode
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
        trainCode: params.value.trainCode
      });
    });

    return {
      trainStation,
      visible,
      trainStations,
      pagination,
      columns,
      loading,
      params,
      handleTableChange,
      handleQuery,
      onAdd,
      handleOk,
      onEdit,
      onDelete
    };
  },
});
</script>
