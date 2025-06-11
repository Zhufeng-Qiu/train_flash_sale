import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
  {
    path: '/login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/',
    component: () => import('../views/main.vue'),
    meta: {
      loginRequire: true
    },
    children: [{
      path: 'welcome',
      component: () => import('../views/main/welcome.vue'),
    }, {
      path: 'passenger',
      component: () => import('../views/main/passenger.vue'),
    }, {
      path: 'ticket',
      component: () => import('../views/main/ticket.vue'),
    }, {
      path: 'order',
      component: () => import('../views/main/order.vue'),
    }, {
      path: 'my-ticket',
      component: () => import('../views/main/my-ticket.vue'),
    }, {
      path: 'seat',
      component: () => import('../views/main/seat.vue')
    }]
  },
  {
    path: '',
    redirect: '/welcome'
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// axios interceptor for front-end
router.beforeEach((to, from, next) => {
  // Whether meta.loginRequire property needs be intercepted
  if (to.matched.some(function (item) {
    console.log(item, "Whether login verification is required: ", item.meta.loginRequire || false);
    return item.meta.loginRequire
  })) {
    const _member = store.state.member;
    console.log("Start the login verification: ", _member);
    if (!_member.token) {
      console.log("Not logged in or session time out");
      notification.error({ description: "Not logged in or session time out" });
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router
