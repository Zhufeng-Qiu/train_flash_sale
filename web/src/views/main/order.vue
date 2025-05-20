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
  <a-divider></a-divider>
  <b>Select the passengers for whom you want to purchase tickets: </b>&nbsp;
  <a-checkbox-group v-model:value="passengerChecks" :options="passengerOptions" />
  <div class="order-tickets">
    <a-row class="order-tickets-header" v-if="tickets.length > 0">
      <a-col :span="5">Passenger</a-col>
      <a-col :span="6">ID Number</a-col>
      <a-col :span="4">Passenger Type</a-col>
      <a-col :span="4">Seat Type</a-col>
    </a-row>
    <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.passengerId">
      <a-col :span="5">{{ticket.passengerName}}</a-col>
      <a-col :span="6">{{ticket.passengerIdCard}}</a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.passengerType" style="width: 100%">
          <a-select-option v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.seatTypeCode" style="width: 100%">
          <a-select-option v-for="item in seatTypes" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
    </a-row>
  </div>
  <div v-if="tickets.length > 0">
    <a-button type="primary" size="large" @click="finishCheckPassenger">Submit</a-button>
  </div>

  <a-modal v-model:visible="visible" title="Please check the following infomation"
           style="top: 50px; width: 800px"
           ok-text="Confirm" cancel-text="Cancel"
           @ok="handleOk">
    <div class="order-tickets">
      <a-row class="order-tickets-header" v-if="tickets.length > 0">
        <a-col :span="5">Passenger</a-col>
        <a-col :span="10">ID Number</a-col>
        <a-col :span="4">Passenger Type</a-col>
        <a-col :span="4">Seat Type</a-col>
      </a-row>
      <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.passengerId">
        <a-col :span="5">{{ticket.passengerName}}</a-col>
        <a-col :span="10">{{ticket.passengerIdCard}}</a-col>
        <a-col :span="4">
          <span v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code">
            <span v-if="item.code === ticket.passengerType">
              {{item.desc}}
            </span>
          </span>
        </a-col>
        <a-col :span="4">
          <span v-for="item in seatTypes" :key="item.code">
            <span v-if="item.code === ticket.seatTypeCode">
              {{item.desc}}
            </span>
          </span>
        </a-col>
      </a-row>
      <br>
      <div v-if="chooseSeatType === 0" style="color: red;">
        Your order is not supported to seat selection
        <div>Platform rule: Seat selection is only supported if all seats are in first class or all are in second class</div>
        <div>Platform rule: seat selection is not supported when remaining tickets are under 20</div>
      </div>
      <div v-else style="text-align: center">
        <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                  v-model:checked="chooseSeatObj[item.code + '1']" :checked-children="item.desc" :un-checked-children="item.desc" />
        <div v-if="tickets.length > 1">
          <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                    v-model:checked="chooseSeatObj[item.code + '2']" :checked-children="item.desc" :un-checked-children="item.desc" />
        </div>
        <div style="color: #999999">Note: you can choose {{tickets.length}} seats</div>
      </div>
      <br/>
      最终购票：{{tickets}}
      <br/>
      最终选座：{{chooseSeatObj}}
    </div>
  </a-modal>
</template>

<script>

import {defineComponent, ref, onMounted, watch, computed} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";


export default defineComponent({
  name: "order-view",
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    const passengers = ref([]);
    const passengerOptions = ref([]);
    const passengerChecks = ref([]);
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

    // Tickets details for UI display and send to back-end API to indicate which passenger is buying a ticket for which seat.
    // {
    //   passengerId: 123,
    //   passengerType: "1",
    //   passengerName: "John Smith",
    //   passengerIdCard: "12323132132",
    //   seatTypeCode: "1",
    //   seat: "C1"
    // }
    const tickets = ref([]);
    const PASSENGER_TYPE_ARRAY = window.PASSENGER_TYPE_ARRAY;
    const visible = ref(false);

    // Check or uncheck a passenger then add or remove a record
    watch(() => passengerChecks.value, (newVal, oldVal)=>{
      console.log("Selected passengers changed", newVal, oldVal)
      // Clean and re-construct the tickets list when changing
      tickets.value = [];
      passengerChecks.value.forEach((item) => tickets.value.push({
        passengerId: item.id,
        passengerType: item.type,
        seatTypeCode: seatTypes[0].code,
        passengerName: item.name,
        passengerIdCard: item.idCard
      }))
    }, {immediate: true});

    // 0: Seat selection is not supported；1: Select first class seat；2: Select second class seat
    const chooseSeatType = ref(0);
    // Get corresponding columns according selected seat type
    // First class seat: ACDF
    // Second class seat: ABCDF
    const SEAT_COL_ARRAY = computed(() => {
      return window.SEAT_COL_ARRAY.filter(item => item.type === chooseSeatType.value);
    });
    // Selected seats
    // {
    //   A1: false, C1: true，D1: false, F1: false，
    //   A2: false, C2: false，D2: true, F2: false
    // }
    const chooseSeatObj = ref({});
    watch(() => SEAT_COL_ARRAY.value, () => {
      chooseSeatObj.value = {};
      for (let i = 1; i <= 2; i++) {
        SEAT_COL_ARRAY.value.forEach((item) => {
          chooseSeatObj.value[item.code + i] = false;
        })
      }
      console.log("Initialize two rows of unselected seats: ", chooseSeatObj.value);
    }, {immediate: true});

    const handleQueryPassenger = () => {
      axios.get("/member/passenger/query-mine").then((response) => {
        let data = response.data;
        if (data.success) {
          passengers.value = data.content;
          passengers.value.forEach((item) => passengerOptions.value.push({
            label: item.name,
            value: item
          }))
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const finishCheckPassenger = () => {
      console.log("Tickets List: ", tickets.value);

      if (tickets.value.length > 5) {
        notification.error({description: 'You can only purchase up to five tickets.'});
        return;
      }

      // Validate that ticket availability is sufficient: for each seat type in the purchase list, check the train’s remaining-seat info to see if there are enough tickets.
      // Front-end validation may not be completely accurate, but it helps to offload much of the work from the back end.
      // Note: this step only simulates deduction—be sure to copy the `seatTypesTemp` variable before subtracting. Deducting directly from the original `seatTypes` would affect real inventory.
      let seatTypesTemp = Tool.copy(seatTypes);
      for (let i = 0; i < tickets.value.length; i++) {
        let ticket = tickets.value[i];
        for (let j = 0; j < seatTypesTemp.length; j++) {
          let seatType = seatTypesTemp[j];
          // Decrement the remaining count for this seat type by 1. This uses a temporary copy of the inventory for validation, not the real one.
          if (ticket.seatTypeCode === seatType.code) {
            seatType.count--;
            if (seatType.count < 0) {
              notification.error({description: seatType.desc + 'is insufficient'});
              return;
            }
          }
        }
      }
      console.log("Front-end remaining tickets validation passed");

      // Check whether seat selection is supported, only pure first class and pure second class seats allow seat selection.
      // First filter out all seat types from records, e.g. [1, 1, 2, 2]
      let ticketSeatTypeCodes = [];
      for (let i = 0; i < tickets.value.length; i++) {
        let ticket = tickets.value[i];
        ticketSeatTypeCodes.push(ticket.seatTypeCode);
      }
      // Deduplicate the seat types: [1, 2]
      const ticketSeatTypeCodesSet = Array.from(new Set(ticketSeatTypeCodes));
      console.log("Selected seat types: ", ticketSeatTypeCodesSet);
      if (ticketSeatTypeCodesSet.length !== 1) {
        console.log("Multiple seat types are not supported to seat selection");
        chooseSeatType.value = 0;
      } else {
        // ticketSeatTypeCodesSet.length === 1, that is only one seat type selection is selected
        if (ticketSeatTypeCodesSet[0] === SEAT_TYPE.YDZ.code) {
          console.log("First class seat selection");
          chooseSeatType.value = SEAT_TYPE.YDZ.code;
        } else if (ticketSeatTypeCodesSet[0] === SEAT_TYPE.EDZ.code) {
          console.log("Second class seat selection");
          chooseSeatType.value = SEAT_TYPE.EDZ.code;
        } else {
          console.log("Not first class seat nor second class seat, not supported to seat selection");
          chooseSeatType.value = 0;
        }

        // Seat selection is not supported when remaining tickets are under 20; otherwise the success rate of ticket selection will not that high and affect ticket issuance
        if (chooseSeatType.value !== 0) {
          for (let i = 0; i < seatTypes.length; i++) {
            let seatType = seatTypes[i];
            // Find same seat type
            if (ticketSeatTypeCodesSet[0] === seatType.code) {
              // check remaining tickets, seat selection is not supported when remaining tickets are under 20;
              if (seatType.count < 20) {
                console.log("Seat selection is not supported when remaining tickets are under 20")
                chooseSeatType.value = 0;
                break;
              }
            }
          }
        }
      }

      // Pop-up ticket info window
      visible.value = true;
    };

    const handleOk = () => {
      console.log("Selected seats: ", chooseSeatObj.value);

      // Set seat for each ticket
      // Clean then set
      for (let i = 0; i < tickets.value.length; i++) {
        tickets.value[i].seat = null;
      }
      let i = -1;
      // Either do not select, or selected seats should be equal to purchased seats, i === (tickets.value.length - 1)
      for (let key in chooseSeatObj.value) {
        if (chooseSeatObj.value[key]) {
          i++;
          if (i > tickets.value.length - 1) {
            notification.error({description: 'Selected seats are more than purchased seats'});
            return;
          }
          tickets.value[i].seat = key;
        }
      }
      if (i > -1 && i < (tickets.value.length - 1)) {
        notification.error({description: 'Selected seats are lesss than purchased seats'});
        return;
      }

      console.log("Final tickets; ", tickets.value);

      axios.post("/business/confirm-order/do", {
        dailyTrainTicketId: dailyTrainTicket.id,
        date: dailyTrainTicket.date,
        trainCode: dailyTrainTicket.trainCode,
        start: dailyTrainTicket.start,
        end: dailyTrainTicket.end,
        tickets: tickets.value
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Place Order successfully!"});
        } else {
          notification.error({description: data.message});
        }
      });
    }

    onMounted(() => {
      handleQueryPassenger();
    });

    return {
      dailyTrainTicket,
      seatTypes,
      passengers,
      passengerOptions,
      passengerChecks,
      tickets,
      PASSENGER_TYPE_ARRAY,
      visible,
      finishCheckPassenger,
      chooseSeatType,
      chooseSeatObj,
      SEAT_COL_ARRAY,
      handleOk
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

.order-tickets {
  margin: 10px 0;
}
.order-tickets .ant-col {
  padding: 5px 10px;
}
.order-tickets .order-tickets-header {
  background-color: cornflowerblue;
  border: solid 1px cornflowerblue;
  color: white;
  font-size: 16px;
  padding: 5px 0;
}
.order-tickets .order-tickets-row {
  border: solid 1px cornflowerblue;
  border-top: none;
  vertical-align: middle;
  line-height: 30px;
}
.order-tickets .choose-seat-item {
  margin: 5px 5px;
}
</style>
