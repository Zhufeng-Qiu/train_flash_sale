<template>
  <div v-if="!param.date">
    Please select a train first in <strong>Ticket Search</strong>,
    <router-link to="/ticket">
      navigate to Ticket Search page
    </router-link>
  </div>
  <div v-else>
    <p style="font-weight: bold;">
      Date: {{param.date}}, Train Number:{{param.trainCode}}, Departure Station: {{param.startPinyin}}, Arrival Station: {{param.endPinyin}}
    </p>
    <table>
      <tr>
        <td style="width: 25px; background: #FF9900;"></td>
        <td>: Sold</td>
        <td style="width: 20px;"></td>
        <td style="width: 25px; background: #999999;"></td>
        <td>: Available</td>
      </tr>
    </table>
    <br>
    <div v-for="(seatObj, carriage) in train" :key="carriage"
         style="border: 3px solid #99CCFF;
                 margin-bottom: 30px;
                 padding: 5px;
                 border-radius: 4px">
      <div style="display:block;
                  width:80px;
                  height:10px;
                  position:relative;
                  top:-15px;
                  text-align: center;
                  background: white;">
        <strong>{{carriage}}</strong>
      </div>
      <div>
        <strong>Seat Type:</strong>
        <template v-if="seatObj['seatType'] === '1'">
          First Class Seat
        </template>
        <template v-else-if="seatObj['seatType'] === '2'">
          Second Class Seat
        </template>
        <template v-if="seatObj['seatType'] === '3'">
          Soft Sleeper
        </template>
        <template v-else-if="seatObj['seatType'] === '4'">
          Hard Seat
        </template>
      </div>
      <table>
        <tr>
          <td v-for="(sell, index) in Object.values(seatObj)[0]" :key="index"
              style="text-align: center">
            {{index + 1}}
          </td>
        </tr>
        <tr v-for="(sellList, col) in seatObj" :key="col">
          <template v-if="col !== 'seatType'">
            <td v-for="(sell, index) in sellList" :key="index"
                style="text-align: center;
                      border: 2px solid white;
                      background: grey;
                      padding: 0 4px;
                      color: white;
                      "
                :style="{background: (sell > 0 ? '#FF9900' : '#999999')}">{{col}}</td>
          </template>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>

import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";
import {useRoute} from "vue-router";

export default defineComponent({
  name: "seat-view",
  setup() {
    const route = useRoute();
    const param = ref({});
    param.value = route.query;
    const list = ref();
    // Using objects makes assembling arrays easier. A 3D-array can only hold the final 0/1 values and cannot store entries like "Carriage 1" or "A"
    // {
    //   "Carriage 1": {
    //      "seatType": "1"
    //      "A" : ["000", "001", "001", "001"],
    //      "B" : ["000", "001", "001", "001"],
    //      "C" : ["000", "001", "001", "001"],
    //      "D" : ["000", "001", "001", "001"]
    //    }, "Carriage 2": {
    //      "seatType": "2"
    //      "A" : ["000", "001", "001", "001"],
    //      "B" : ["000", "001", "001", "001"],
    //      "C" : ["000", "001", "001", "001"],
    //      "D" : ["000", "001", "001", "001"],
    //      "D" : ["000", "001", "001", "001"]
    //    }
    // }
    let train = ref({});

    // Query all station info for selecte train
    const querySeat = () => {
      axios.get("/business/seat-sell/query", {
        params: {
          date: param.value.date,
          trainCode: param.value.trainCode,
        }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          list.value = data.content;
          format();
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * Extract sales info for current station interval, and check whether the seats are available
     */
    const format = () => {
      let _train = {};

      for (let i = 0; i < list.value.length; i++) {
        let item = list.value[i];

        // Calculate whether the seats are available for current interval; station indices start at 0.
        let sellDB = item.sell;

        // Suppose 6 stations: start = 1, end = 3, sellDB = 11111, final result: sell = 111, sold
        // Suppose 6 stations: start = 1, end = 3, sellDB = 11011, final result: sell = 101, sold
        // Suppose 6 stations: start = 1, end = 3, sellDB = 10001, final result: sell = 000, available
        // Test:
        // let sellDB = "123456789";
        // let start = 1;
        // let end = 3;
        // let sell = sellDB.substr(start, end - start)
        // console.log(sell)
        let sell = sellDB.substr(param.value.startIndex, param.value.endIndex - param.value.startIndex);
        // console.log("Whole Sales info: ", sellDB, "Sales info for current interval: ", sell);

        // Transfer sell info into train object
        if (!_train["Carriage" + item.carriageIndex]) {
          _train["Carriage" + item.carriageIndex] = {};
        }
        if (!_train["Carriage" + item.carriageIndex][item.col]) {
          _train["Carriage" + item.carriageIndex][item.col] = [];
        }
        _train["Carriage" + item.carriageIndex][item.col].push(parseInt(sell));
        if (!_train["Carriage" + item.carriageIndex]["seatType"]) {
          _train["Carriage" + item.carriageIndex]["seatType"] = item.seatType;
        }
      }

      train.value = _train;
    }

    onMounted(() => {
      if (param.value.date) {
        querySeat();
      }
    });

    return {
      param,
      train
    };
  },
});
</script>
