<template>
  <div>{{ dailyTrainTicket }}</div>
</template>

<script>

import {defineComponent} from 'vue';

export default defineComponent({
  name: "order-view",
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    console.log("Order info for selected train number", dailyTrainTicket);

    return {
      dailyTrainTicket
    };
  },
});
</script>
