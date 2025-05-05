<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <#if !readOnly><a-button type="primary" @click="onAdd">Add</a-button></#if>
    </a-space>
  </p>
  <a-table :dataSource="${domain}s"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <#if !readOnly>
        <a-space>
          <a-popconfirm
              title="Once deleted, it cannot be recovered. Are you sure you want to delete?"
              @confirm="onDelete(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a style="color: red">Delete</a>
          </a-popconfirm>
          <a @click="onEdit(record)">Edit</a>
        </a-space>
        </#if>
      </template>
      <#list fieldList as field>
        <#if field.enums>
      <template v-else-if="column.dataIndex === '${field.nameHump}'">
        <span v-for="item in ${field.enumsConst}_ARRAY" :key="item.key">
          <span v-if="item.key === record.${field.nameHump}">
            {{item.value}}
          </span>
        </span>
      </template>
        </#if>
      </#list>
    </template>
  </a-table>
  <#if !readOnly>
  <a-modal v-model:visible="visible" title="${tableNameEn}" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="${domain}" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <#list fieldList as field>
        <#if field.name!="id" && field.nameHump!="createTime" && field.nameHump!="updateTime">
      <a-form-item label="${field.nameEn}">
        <#if field.enums>
        <a-select v-model:value="${domain}.${field.nameHump}">
          <a-select-option v-for="item in ${field.enumsConst}_ARRAY" :key="item.key" :value="item.key">
            {{item.value}}
          </a-select-option>
        </a-select>
        <#elseif field.javaType=='Date'>
          <#if field.type=='time'>
        <a-time-picker v-model:value="${domain}.${field.nameHump}" valueFormat="HH:mm:ss" placeholder="Please select time" />
          <#elseif field.type=='date'>
        <a-date-picker v-model:value="${domain}.${field.nameHump}" valueFormat="YYYY-MM-DD" placeholder="Please select date" />
          <#else>
        <a-date-picker v-model:value="${domain}.${field.nameHump}" valueFormat="YYYY-MM-DD HH:mm:ss" show-time placeholder="Please select datetime" />
          </#if>
        <#else>
        <a-input v-model:value="${domain}.${field.nameHump}" />
        </#if>
      </a-form-item>
        </#if>
      </#list>
    </a-form>
  </a-modal>
  </#if>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "${do_main}-view",
  setup() {
    <#list fieldList as field>
    <#if field.enums>
    const ${field.enumsConst}_ARRAY = window.${field.enumsConst}_ARRAY;
    </#if>
    </#list>
    const visible = ref(false);
    let ${domain} = ref({
      <#list fieldList as field>
      ${field.nameHump}: undefined,
      </#list>
    });
    const ${domain}s = ref([]);
    // The name of three fields of pagination is fixed
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    <#list fieldList as field>
      <#if field.name!="id" && field.nameHump!="createTime" && field.nameHump!="updateTime">
    {
      title: '${field.nameEn}',
      dataIndex: '${field.nameHump}',
      key: '${field.nameHump}',
    },
      </#if>
    </#list>
    <#if !readOnly>
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    </#if>
    ];

    <#if !readOnly>
    const onAdd = () => {
      ${domain}.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      ${domain}.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/${module}/${do_main}/delete/" + record.id).then((response) => {
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
      axios.post("/${module}/${do_main}/save", ${domain}.value).then((response) => {
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
    </#if>

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/${module}/${do_main}/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          ${domain}s.value = data.content.list;
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
      <#list fieldList as field>
      <#if field.enums>
      ${field.enumsConst}_ARRAY,
      </#if>
      </#list>
      ${domain},
      visible,
      ${domain}s,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      <#if !readOnly>
      onAdd,
      handleOk,
      onEdit,
      onDelete
      </#if>
    };
  },
});
</script>
