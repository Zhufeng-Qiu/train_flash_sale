<template>
  <a-select v-model:value="trainCode" show-search allowClear
            :filterOption="filterTrainCodeOption"
            @change="onChange" placeholder="Please select train number"
            :style="'width: ' + localWidth">
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code" :label="item.code + item.startPinyin + item.endPinyin">
      {{item.code}} | {{item.startPinyin}} ~ {{item.endPinyin}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "train-select-view",
  props: ["modelValue", "width"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const trainCode = ref();
    const trains = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }

    // Use watch() to get the value of parent component dynamically.
    // Only work once when placed in onMounted or other methods
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      trainCode.value = props.modelValue;
    }, {immediate: true});

    /**
     * Query all the train number for drop-down box
     */
    const queryAllTrain = () => {
      axios.get("/business/admin/train/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          trains.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Filter for drop-down box
     */
    const filterTrainCodeOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    /**
     * Respond the value of component to parent component
     * @param value
     */
    const onChange = (value) => {
      emit('update:modelValue', value);
      let train = trains.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      emit('change', train);
    };

    onMounted(() => {
      queryAllTrain();
    });

    return {
      trainCode,
      trains,
      localWidth,
      filterTrainCodeOption,
      onChange,
    };
  },
});
</script>
