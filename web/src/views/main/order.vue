<template>
  <div class="order-train">
    <span class="order-train-main">{{dailyTrainTicket.date}}</span>&nbsp;
    <span class="order-train-main">Train Number: {{dailyTrainTicket.trainCode}}</span>
    <br>
    <span class="order-train-main">Departure: {{dailyTrainTicket.startPinyin}}</span>
    <span class="order-train-main">({{dailyTrainTicket.startTime}})</span>&nbsp;
    <span class="order-train-main">——</span>&nbsp;
    <span class="order-train-main">Arrival: {{dailyTrainTicket.endPinyin}}</span>
    <span class="order-train-main">({{dailyTrainTicket.endTime}})</span>&nbsp;

    <div class="order-train-ticket">
      <span v-for="item in seatTypes" :key="item.type">
        <span>{{item.desc}}</span>：
        <span class="order-train-ticket-main">${{item.price}}</span>&nbsp;
        <span class="order-train-ticket-main">{{item.count}}</span>&nbsp;seats left&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </span>
    </div>
  </div>
</template>

<script>

import {defineComponent} from 'vue';

export default defineComponent({
  name: "order-view",
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    console.log("Order info for selected order", dailyTrainTicket);

    const SEAT_TYPE = window.SEAT_TYPE;
    console.log(SEAT_TYPE)
    // Provided seat types of current train number, including price, remaining tickets, etc. For example:
    // {
    //   type: "YDZ",
    //   code: "1",
    //   desc: "First Class Seat",
    //   count: "100",
    //   price: "50",
    // }
    // For SEAT_TYPE[KEY]: when knowing a specific property name(xxx), use obj.xxx; when the property name is a variable, use obj[xxx].
    const seatTypes = [];
    for (let KEY in SEAT_TYPE) {
      let key = KEY.toLowerCase();
      if (dailyTrainTicket[key] >= 0) {
        seatTypes.push({
          type: KEY,
          code: SEAT_TYPE[KEY]["code"],
          desc: SEAT_TYPE[KEY]["desc"],
          count: dailyTrainTicket[key],
          price: dailyTrainTicket[key + 'Price'],
        })
      }
    }
    console.log("Provided seat types: ", seatTypes)

    return {
      dailyTrainTicket,
      seatTypes
    };
  },
});
</script>

<style>
.order-train .order-train-main {
  font-size: 18px;
  font-weight: bold;
}
.order-train .order-train-ticket {
  margin-top: 15px;
}
.order-train .order-train-ticket .order-train-ticket-main {
  color: green;
  font-size: 18px;
}
</style>
