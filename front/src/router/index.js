import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/Home'
import Dashboard from '@/views/Dashboard'
import Containers from '@/views/Containers'
import ContainerDetail from '@/views/ContainerDetail'

Vue.use(Router);

/**
 * axios & backendUrl
 */

var NODE_ENV = process.env.NODE_ENV;
if (NODE_ENV == 'development') {
  window.baseUrl = 'http://localhost:8080/';
} else {
  window.baseUrl = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
}

const _axios = require('axios');
var axios = _axios.create({
  baseURL: window.baseUrl,
  timeout: 3000
});
window.axios = axios;


export default new Router({
  base: '/',
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
      name: 'home',
      component: Home,
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: Dashboard,
        },
        {
          path: 'containers',
          name: 'containers',
          component: Containers,
        },
        {
          path: 'containers/:id/:submenu',
          name: 'containerDetail',
          component: ContainerDetail,
          props: function (route) {
            return {
              id: route.params.id,
              submenu: route.params.submenu
            }
          }
        }
      ]
    }
  ]
})
