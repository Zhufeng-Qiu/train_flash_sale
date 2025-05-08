<template>
  <a-select v-model:value="name" show-search allowClear
            :filterOption="filterNameOption"
            @change="onChange" placeholder="Please select station"
            :style="'width: ' + localWidth">
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name" :label="item.name + item.namePinyin + item.namePy">
      {{item.name}} | {{item.namePinyin}} | {{item.namePy}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "station-select-view",
  props: ["modelValue", "width"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const name = ref();
    const stations = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }

    // Use watch() to get the value of parent component dynamically.
    // Only work once when placed in onMounted or other methods
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      name.value = props.modelValue;
    }, {immediate: true});

    /**
     * Query all the station number for drop-down box
     */
    const queryAllStation = () => {
      axios.get("/business/admin/station/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Filter for drop-down box
     */
    const filterNameOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    /**
     * Respond the value of component to parent component
     * @param value
     */
    const onChange = (value) => {
      emit('update:modelValue', value);
      let station = stations.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(station)) {
        station = {};
      }
      emit('change', station);
    };

    onMounted(() => {
      queryAllStation();
    });

    return {
      name,
      stations,
      localWidth,
      filterNameOption,
      onChange,
    };
  },
});
</script>
