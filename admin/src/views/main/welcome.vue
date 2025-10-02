<template>
  <h1>Welcome to ZephyrQ Train Flash Sale Admin Panel</h1>
  <div style="border: 4px solid orange;
            border-radius: 4px;
            padding: 10px; ">
    <h3>
      <b>Introduction of Train Flash Sale platform</b>
    </h3>
    This project contains 15 business tables and uses a self-developed universal code generator to quickly generate CRUD operations and corresponding interfaces.<br>
    The ticket purchasing process may seem simple, but it uses many invisible high-concurrency technologies, such as 100,000 people competing for 1,000 tickets:<br>
    <ul>
      <li>Using <b>CDN</b> to improve user page access speed</li>
      <li>Using <b>distributed caching</b> to provide high-performance ticket availability queries before flash sales begin, while considering cache breakdown, penetration, and avalanche issues</li>
      <li>Using <b>first-layer captcha</b>, pure frontend captcha to weaken instantaneous peaks on the frontend, dispersing 100,000 requests within 100 milliseconds into 100,000 requests within 1-2 seconds</li>
      <li>Using <b>second-layer captcha</b>, backend captcha to further disperse requests while preventing bots from grabbing tickets</li>
      <li>Using <b>rate limiting technology</b> to reduce unnecessary requests while providing users with quick failure responses (informing them that tickets are sold out), quickly failing 90,000 requests and turning them into 10,000 requests competing for 1,000 tickets</li>
      <li>Using <b>token distribution technology</b> to control ticket grabbing volume while preventing bots from brushing tickets, such as releasing 2,000 tokens, turning 8,000 requests into quick failures and 2,000 requests competing for 1,000 tickets</li>
      <li>Using <b>distributed lock technology</b> to prevent overselling, meaning 2,000 people competing for 1,000 tickets can only sell 1,000 tickets, not 1,001 tickets</li>
      <li>Using <b>asynchronous peak shaving + queuing mechanism</b> to solve throughput issues and provide user feedback in the shortest time, with 1,000 requests informed that tickets are sold out and 1,000 requests informed they are in queue</li>
      <li>Using <b>distributed transactions</b> to ensure eventual data consistency, preventing inventory reduction without ticket generation.</li>
    </ul>
  </div>
</template>
<script>
import { defineComponent } from 'vue';

export default defineComponent({
  setup() {
    return {
    };
  },
});
</script>
<style>
</style>
