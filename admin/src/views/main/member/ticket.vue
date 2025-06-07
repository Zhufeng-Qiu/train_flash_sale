<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="tickets"
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
      <template v-else-if="column.dataIndex === 'seatCol'">
        <span v-for="item in SEAT_COL_ARRAY" :key="item.code">
          <span v-if="item.code === record.seatCol">
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
  <a-modal v-model:visible="visible" title="Ticket" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="ticket" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Member Id">
        <a-input v-model:value="ticket.memberId" />
      </a-form-item>
      <a-form-item label="Passenger Id">
        <a-input v-model:value="ticket.passengerId" />
      </a-form-item>
      <a-form-item label="Passenger Name">
        <a-input v-model:value="ticket.passengerName" />
      </a-form-item>
      <a-form-item label="Date">
        <a-date-picker v-model:value="ticket.trainDate" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
      </a-form-item>
      <a-form-item label="Train Number">
        <a-input v-model:value="ticket.trainCode" />
      </a-form-item>
      <a-form-item label="Carriage Index">
        <a-input v-model:value="ticket.carriageIndex" />
      </a-form-item>
      <a-form-item label="Row">
        <a-input v-model:value="ticket.seatRow" />
      </a-form-item>
      <a-form-item label="Column">
        <a-select v-model:value="ticket.seatCol">
          <a-select-option v-for="item in SEAT_COL_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Departure Station">
        <a-input v-model:value="ticket.startStation" />
      </a-form-item>
      <a-form-item label="Departure Station Alias">
        <a-input v-model:value="ticket.startPinyin" />
      </a-form-item>
      <a-form-item label="Start Time">
        <a-time-picker v-model:value="ticket.startTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Arrival Station">
        <a-input v-model:value="ticket.endStation" />
      </a-form-item>
      <a-form-item label="Arrival Station Alias">
        <a-input v-model:value="ticket.endPinyin" />
      </a-form-item>
      <a-form-item label="End Time">
        <a-time-picker v-model:value="ticket.endTime" valueFormat="HH:mm:ss" placeholder="Please select time" />
      </a-form-item>
      <a-form-item label="Seat Type">
        <a-select v-model:value="ticket.seatType">
          <a-select-option v-for="item in SEAT_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "ticket-view",
  setup() {
    const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
    const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
    const visible = ref(false);
    let ticket = ref({
      id: undefined,
      memberId: undefined,
      passengerId: undefined,
      passengerName: undefined,
      trainDate: undefined,
      trainCode: undefined,
      carriageIndex: undefined,
      seatRow: undefined,
      seatCol: undefined,
      startStation: undefined,
      startPinyin: undefined,
      startTime: undefined,
      endStation: undefined,
      endPinyin: undefined,
      endTime: undefined,
      seatType: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const tickets = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    {
      title: 'Member Id',
      dataIndex: 'memberId',
      key: 'memberId',
    },
    {
      title: 'Passenger Id',
      dataIndex: 'passengerId',
      key: 'passengerId',
    },
    {
      title: 'Passenger Name',
      dataIndex: 'passengerName',
      key: 'passengerName',
    },
    {
      title: 'Date',
      dataIndex: 'trainDate',
      key: 'trainDate',
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
      dataIndex: 'seatRow',
      key: 'seatRow',
    },
    {
      title: 'Column',
      dataIndex: 'seatCol',
      key: 'seatCol',
    },
    {
      title: 'Departure Station',
      dataIndex: 'startStation',
      key: 'startStation',
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
      dataIndex: 'endStation',
      key: 'endStation',
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
      title: 'Seat Type',
      dataIndex: 'seatType',
      key: 'seatType',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      ticket.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      ticket.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/member/admin/ticket/delete/" + record.id).then((response) => {
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
      axios.post("/member/admin/ticket/save", ticket.value).then((response) => {
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
      axios.get("/member/admin/ticket/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          tickets.value = data.content.list;
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
      SEAT_COL_ARRAY,
      SEAT_TYPE_ARRAY,
      ticket,
      visible,
      tickets,
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
