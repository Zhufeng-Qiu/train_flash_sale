<template>
  <div style="border: 4px solid orange;
            border-radius: 4px;
            padding: 10px; ">
    <h3>
      <b>Introduction of Train Flash Sale Admin Panel</b>
    </h3>
    The Admin Panel consists of four main sections:
    <ul>
      <li>Membership Management: Manages members, passengers, and issued tickets.</li>
      <li>Business Data: Manages daily train schedules and remaining ticket information, sets ticket-purchase tokens, and allows viewing of ticket orders.</li>
      <li>Basic Data: Maintains all fundamental train data, including train numbers, stations, carriages, and seats.</li>
      <li>Batch Job Management: Manages all system batch jobs, including creating, deleting, pausing, restarting, and rerunning them.</li>
    </ul>
  </div>
</template>

<script>

import { defineComponent } from 'vue';

export default defineComponent({
  name: "admin-view",
  setup() {
    return {
    };
  },
});
</script>

