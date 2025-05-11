<template>
  <div class="job">
    <p>
      <a-button type="primary" @click="handleAdd()">
        Add
      </a-button>&nbsp;
      <a-button type="primary" @click="handleQuery()">
        Refresh
      </a-button>
    </p>
    <a-table :dataSource="jobs"
             :columns="columns"
             :loading="loading">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-space>
            <a-popconfirm
                title="This task will be executed once immediately. Confirm to proceed?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleRun(record)"
            >
              <a-button type="primary" size="small">
                Manually execution
              </a-button>
            </a-popconfirm>
            <a-popconfirm
                title="Confirm to restart?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleResume(record)"
            >
              <a-button v-show="record.state === 'PAUSED' || record.state === 'ERROR'" type="primary" size="small">
                Restart
              </a-button>
            </a-popconfirm>
            <a-popconfirm
                title="Conofirm to pause?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handlePause(record)"
            >
              <a-button v-show="record.state === 'NORMAL' || record.state === 'BLOCKED'" type="primary" size="small">
                Pause
              </a-button>
            </a-popconfirm>
            <a-button type="primary" @click="handleEdit(record)" size="small">
              Edit
            </a-button>
            <a-popconfirm
                title="Once deleted, it cannot be recovered. Confirm to delete?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record)"
            >
              <a-button type="danger" size="small">
                Delete
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
        title="User"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="handleModalOk"
    >
      <a-form :model="job" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="Class Name">
          <a-input v-model:value="job.name" />
        </a-form-item>
        <a-form-item label="Description">
          <a-input v-model:value="job.description" />
        </a-form-item>
        <a-form-item label="Group">
          <a-input v-model:value="job.group" :disabled="!!job.state"/>
        </a-form-item>
        <a-form-item label="Cron Expression">
          <a-input v-model:value="job.cronExpression" />
          <div class="ant-alert ant-alert-success">
            Execute every 5 seconds: 0/5 * * * * ?
            <br>
            Execute every 5 minutes: * 0/5 * * * ?
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';

export default defineComponent({
  name: 'batch-job-view',
  setup () {
    const jobs = ref();
    const loading = ref();

    const columns = [{
      title: 'Group',
      dataIndex: 'group',
    }, {
      title: 'Name',
      dataIndex: 'name',
    }, {
      title: 'Description',
      dataIndex: 'description',
    }, {
      title: 'State',
      dataIndex: 'state',
    }, {
      title: 'Cron Expression',
      dataIndex: 'cronExpression',
    }, {
      title: 'Previous Fire Time',
      dataIndex: 'preFireTime',
    }, {
      title: 'Next Fire Time',
      dataIndex: 'nextFireTime',
    }, {
      title: 'Operation',
      dataIndex: 'operation'
    }];

    const handleQuery = () => {
      loading.value = true;
      jobs.value = [];
      axios.get('/batch/admin/job/query').then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          jobs.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    // -------- Form ---------
    const job = ref();
    job.value = {};
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      let url = "add";
      if (job.value.state) {
        url = "reschedule";
      }
      axios.post('/batch/admin/job/' + url, job.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          notification.success({description: "Save successfully!"});
          handleQuery();
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Add
     */
    const handleAdd = () => {
      modalVisible.value = true;
      job.value = {
        group: 'DEFAULT'
      };
    };

    /**
     * Edit
     */
    const handleEdit = (record) => {
      modalVisible.value = true;
      job.value = Tool.copy(record);
    };

    /**
     * Delete
     */
    const handleDelete = (record) => {
      axios.post('/batch/admin/job/delete', {
        name: record.name,
        group: record.group
      }).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Delete successfully!"});
          handleQuery();
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Pause
     */
    const handlePause = (record) => {
      axios.post('/batch/admin/job/pause', {
        name: record.name,
        group: record.group
      }).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Pause successfully!"});
          handleQuery();
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Restart
     */
    const handleResume = (record) => {
      axios.post('/batch/admin/job/reschedule', record).then((response) => {
        modalLoading.value = false;
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          notification.success({description: "Restart successfully!"});
          handleQuery();
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Manually execution
     */
    const handleRun = (record) => {
      axios.post('/batch/admin/job/run', record).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Manually execution!"});
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const getEnumValue = (key, obj) => {
      return Tool.getEnumValue(key, obj);
    };

    onMounted(() => {
      console.log('index mounted!');
      handleQuery();
    });

    return {
      columns,
      jobs,
      loading,
      handleQuery,

      handleAdd,
      handleEdit,
      handleDelete,
      handleResume,
      handlePause,

      job,
      modalVisible,
      modalLoading,
      handleModalOk,
      handleRun,

      getEnumValue
    };
  }
})
</script>

<style scoped>
</style>
