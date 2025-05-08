<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="stations"
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
          |
          <a @click="onEdit(record)">Edit</a>
        </a-space>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="station" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Station">
        <a-input v-model:value="station.name" />
      </a-form-item>
      <a-form-item label="Station Alias">
        <a-input v-model:value="station.namePinyin" disabled/>
      </a-form-item>
      <a-form-item label="Station Initial">
        <a-input v-model:value="station.namePy" disabled/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import {pinyin} from "pinyin-pro";

export default defineComponent({
  name: "station-view",
  setup() {
    const visible = ref(false);
    let station = ref({
      id: undefined,
      name: undefined,
      namePinyin: undefined,
      namePy: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const stations = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
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
      title: 'Station Initial',
      dataIndex: 'namePy',
      key: 'namePy',
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

    function transferChineseCityName(station) {
      station.value.namePinyin = pinyin(station.value.name, { toneType: 'none'}).replaceAll(" ", "");
      station.value.namePy = pinyin(station.value.name, { pattern: 'first', toneType: 'none'}).replaceAll(" ", "");
    }

    // http://pinyin-pro.cn/
    watch(() => station.value.name, ()=>{
      if (Tool.isNotEmpty(station.value.name)) {
        if (containsChinese(station.value.name)) {
          transferChineseCityName(station);
        } else {
          transferEnglishCityName(station, "name", "namePinyin");
          transferEnglishCityName(station, "name", "namePy");
        }
      } else {
        station.value.namePinyin = "";
        station.value.namePy = "";
      }
    }, {immediate: true});

    const onAdd = () => {
      station.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      station.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/station/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/station/save", station.value).then((response) => {
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
      axios.get("/business/admin/station/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          stations.value = data.content.list;
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
      station,
      visible,
      stations,
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
