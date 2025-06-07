<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
<!--      <a-button type="primary" @click="onAdd">Add</a-button>-->
    </a-space>
  </p>
  <a-table :dataSource="skTokens"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
<!--          <a-popconfirm-->
<!--              title="Once deleted, it cannot be recovered. Confirm to delete?"-->
<!--              @confirm="onDelete(record)"-->
<!--              ok-text="Confirm" cancel-text="Cancel">-->
<!--            <a style="color: red">Delete</a>-->
<!--          </a-popconfirm>-->
          <a @click="onEdit(record)">Modify Remaining Tokens</a>
        </a-space>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Flash Sale Token" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="skToken" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Date">
        <a-date-picker v-model:value="skToken.date" valueFormat="YYYY-MM-DD" placeholder="Please select date" disabled/>
      </a-form-item>
      <a-form-item label="Train Number">
        <a-input v-model:value="skToken.trainCode" disabled/>
      </a-form-item>
      <a-form-item label="Remaining Tokens">
        <a-input v-model:value="skToken.count" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "sk-token-view",
  setup() {
    const visible = ref(false);
    let skToken = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      count: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const skTokens = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
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
      title: 'Remaining Tokens',
      dataIndex: 'count',
      key: 'count',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      skToken.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      skToken.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/sk-token/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/sk-token/save", skToken.value).then((response) => {
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
      axios.get("/business/admin/sk-token/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          skTokens.value = data.content.list;
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
      skToken,
      visible,
      skTokens,
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
