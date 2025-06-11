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
           @ok="showFirstImageCodeModal">
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
<!--      <br/>-->
<!--      Final tickets: {{tickets}}-->
<!--      <br/>-->
<!--      Final seats: {{chooseSeatObj}}-->
    </div>
  </a-modal>

  <!-- Second back-end CAPTCHA -->
  <a-modal v-model:visible="imageCodeModalVisible" :title="null" :footer="null" :closable="false"
           style="top: 50px; width: 400px">
    <p style="text-align: center; font-weight: bold; font-size: 18px">
      Use back-end CAPTCHA to mitigate sudden traffic spikes<br/>
      to avoid bots snatching
    </p>
    <p>
      <a-input v-model:value="imageCode" placeholder="CAPTCHA">
        <template #suffix>
          <img v-show="!!imageCodeSrc" :src="imageCodeSrc" alt="CAPTCHA" v-on:click="loadImageCode()"/>
        </template>
      </a-input>
    </p>
    <a-button type="danger" block @click="handleOk">Place order after inputting CAPTCHA</a-button>
  </a-modal>

  <!-- First front-end CAPTCHA -->
  <a-modal v-model:visible="firstImageCodeModalVisible" :title="null" :footer="null" :closable="false"
           style="top: 50px; width: 400px">
    <p style="text-align: center; font-weight: bold; font-size: 18px">
      Use front-end CAPTCHA to mitigate sudden traffic spikes<br/>
      Reduce the load on the back-end CAPTCHA endpoint.
    </p>
    <p>
      <a-input v-model:value="firstImageCodeTarget" placeholder="CAPTCHA">
        <template #suffix>
          {{firstImageCodeSourceA}} + {{firstImageCodeSourceB}}
        </template>
      </a-input>
    </p>
    <a-button type="danger" block @click="validFirstImageCode">Submit CAPTCHA</a-button>
  </a-modal>

  <a-modal v-model:visible="lineModalVisible" title="Queue up for tickets" :footer="null" :maskClosable="false" :closable="false"
           style="top: 50px; width: 400px">
    <div class="book-line">
      <div v-show="confirmOrderLineCount < 0">
        <loading-outlined /> System Processing...
      </div>
      <div v-show="confirmOrderLineCount >= 0">
        <loading-outlined /> There are {{confirmOrderLineCount}} users ahead of you purchasing tickets. You’re in the queue. Please wait a moment.

      </div>
    </div>
    <br/>
    <a-button type="danger" @click="onCancelOrder">Cancel Queueing</a-button>
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
    const lineModalVisible = ref(false);
    const confirmOrderId = ref();
    const confirmOrderLineCount = ref(-1);

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
      if (Tool.isEmpty(imageCode.value)) {
        notification.error({description: 'CAPTCHA cannot be empty'});
        return;
      }

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
        tickets: tickets.value,
        imageCodeToken: imageCodeToken.value,
        imageCode: imageCode.value,
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          // notification.success({description: "Place Order successfully!"});
          visible.value = false;
          imageCodeModalVisible.value = false;
          lineModalVisible.value = true;
          confirmOrderId.value = data.content;
          queryLineCount();
        } else {
          notification.error({description: data.message});
        }
      });
    }

    /* ------------------- 定时查询订单状态 --------------------- */
    // 确认订单后定时查询
    let queryLineCountInterval;

    // 定时查询订单结果/排队数量
    const queryLineCount = () => {
      confirmOrderLineCount.value = -1;
      queryLineCountInterval = setInterval(function () {
        axios.get("/business/confirm-order/query-line-count/" + confirmOrderId.value).then((response) => {
          let data = response.data;
          if (data.success) {
            let result = data.content;
            switch (result) {
              case -1 :
                notification.success({description: "Ticket purchase successful!"});
                lineModalVisible.value = false;
                clearInterval(queryLineCountInterval);
                break;
              case -2:
                notification.error({description: "Ticket purchase failed."});
                lineModalVisible.value = false;
                clearInterval(queryLineCountInterval);
                break;
              case -3:
                notification.error({description: "Sorry, no ticket available."});
                lineModalVisible.value = false;
                clearInterval(queryLineCountInterval);
                break;
              default:
                confirmOrderLineCount.value = result;
            }
          } else {
            notification.error({description: data.message});
          }
        });
      }, 500);
    };

    /* ------------------- Second CAPTCHA --------------------- */
    const imageCodeModalVisible = ref();
    const imageCodeToken = ref();
    const imageCodeSrc = ref();
    const imageCode = ref();

    /**
     * Load CAPTCHA
     */
    const loadImageCode = () => {
      imageCodeToken.value = Tool.uuid(8);
      imageCodeSrc.value = process.env.VUE_APP_SERVER + '/business/kaptcha/image-code/' + imageCodeToken.value;
    };

    const showImageCodeModal = () => {
      loadImageCode();
      imageCodeModalVisible.value = true;
    };

    /* ------------------- First CAPTCHA --------------------- */
    const firstImageCodeSourceA = ref();
    const firstImageCodeSourceB = ref();
    const firstImageCodeTarget = ref();
    const firstImageCodeModalVisible = ref();

    /**
     * Load first CAPCHA
     */
    const loadFirstImageCode = () => {
      // Get a number between 1 and 10: Math.floor(Math.random()*10 + 1)
      firstImageCodeSourceA.value = Math.floor(Math.random() * 10 + 1) + 10;
      firstImageCodeSourceB.value = Math.floor(Math.random() * 10 + 1) + 20;
    };

    /**
     * Display first CAPTCHA pop-up window
     */
    const showFirstImageCodeModal = () => {
      loadFirstImageCode();
      firstImageCodeModalVisible.value = true;
    };

    /**
     * Validate first CAPTCHA
     */
    const validFirstImageCode = () => {
      if (parseInt(firstImageCodeTarget.value) === parseInt(firstImageCodeSourceA.value + firstImageCodeSourceB.value)) {
        // First CAPTCHA validation passed
        firstImageCodeModalVisible.value = false;
        showImageCodeModal();
      } else {
        notification.error({description: 'CAPTCHA does not match'});
      }
    };

    /**
     * Cancel queueing
     */
    const onCancelOrder = () => {
      axios.get("/business/confirm-order/cancel/" + confirmOrderId.value).then((response) => {
        let data = response.data;
        if (data.success) {
          let result = data.content;
          if (result === 1) {
            notification.success({description: "Cacel syccessfully!"});
            // After cancelling queueing, do not query result continuously
            clearInterval(queryLineCountInterval);
            lineModalVisible.value = false;
          } else {
            notification.error({description: "Fail to cancel!"});
          }
        } else {
          notification.error({description: data.message});
        }
      });
    };

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
      handleOk,
      imageCodeToken,
      imageCodeSrc,
      imageCode,
      showImageCodeModal,
      imageCodeModalVisible,
      loadImageCode,
      firstImageCodeSourceA,
      firstImageCodeSourceB,
      firstImageCodeTarget,
      firstImageCodeModalVisible,
      showFirstImageCodeModal,
      validFirstImageCode,
      lineModalVisible,
      confirmOrderId,
      confirmOrderLineCount,
      onCancelOrder
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
