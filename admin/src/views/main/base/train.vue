<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="trains"
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
          |
          <a-popconfirm
              title="Existing seat records will be deleted. Confirm to generate new seats?"
              @confirm="genSeat(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a>Generate seat</a>
          </a-popconfirm>
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
  <a-modal v-model:visible="visible" title="Train" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="train" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Train Number">
        <a-input v-model:value="train.code" :disabled="!!train.id"/>
      </a-form-item>
      <a-form-item label="Train Type">
        <a-select v-model:value="train.type">
          <a-select-option v-for="item in TRAIN_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Departure Station">
        <station-select-view v-model="train.start"></station-select-view>
      </a-form-item>
      <a-form-item label="Departure Station Alias">
        <a-input v-model:value="train.startPinyin" disabled/>
      </a-form-item>
      <a-form-item label="Start Time">
        <a-time-picker v-model:value="train.startTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Arrival Station">
        <station-select-view v-model="train.end"></station-select-view>
      </a-form-item>
      <a-form-item label="Arrival Station Alias">
        <a-input v-model:value="train.endPinyin" disabled/>
      </a-form-item>
      <a-form-item label="End Time">
        <a-time-picker v-model:value="train.endTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import {pinyin} from "pinyin-pro";
import StationSelectView from "@/components/station-select.vue";

export default defineComponent({
  name: "train-view",
  components: {StationSelectView},
  setup() {
    const TRAIN_TYPE_ARRAY = window.TRAIN_TYPE_ARRAY;
    const visible = ref(false);
    let train = ref({
      id: undefined,
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
    const trains = ref([]);
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

    function containsChinese(str) {
      return /[\u4e00-\u9fa5]/.test(str);
    }

    function transferEnglishCityName(station, origin, relate) {
      const words = station.value[origin].trim().split(/\s+/);
      let res;

      if (words.length > 1) {
        res = words.map(word => word.charAt(0).toUpperCase()).join('');
      } else {
        res =  station.value[origin].slice(0, 3).toUpperCase();
      }
      station.value[relate] = res;
    }

    function transferChineseCityName(station, origin, relate) {
      train.value[relate] = pinyin(train.value[origin], { toneType: 'none'}).replaceAll(" ", "");
    }

    watch(() => train.value.start, ()=>{
      if (Tool.isNotEmpty(train.value.start)) {
        if (containsChinese(train.value.start)) {
          transferChineseCityName(train, "start", "startPinyin");
        } else {
          transferEnglishCityName(train, "start", "startPinyin");
        }
      } else {
        train.value.startPinyin = "";
      }
    }, {immediate: true});

    watch(() => train.value.end, ()=>{
      if (Tool.isNotEmpty(train.value.end)) {
        if (containsChinese(train.value.end)) {
          transferChineseCityName(train, "end", "endPinyin");
        } else {
          transferEnglishCityName(train, "end", "endPinyin");
        }
      } else {
        train.value.endPinyin = "";
      }
    }, {immediate: true});

    const onAdd = () => {
      train.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      train.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/train/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/train/save", train.value).then((response) => {
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
      axios.get("/business/admin/train/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          trains.value = data.content.list;
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

    const genSeat = (record) => {
      loading.value = true;
      axios.get("/business/admin/train/gen-seat/" + record.code).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          notification.success({description: "Generate successfully！"});
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
      train,
      visible,
      trains,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      genSeat
    };
  },
});
</script>
