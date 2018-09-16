<template>
  <div>
    <v-layout row wrap>
      <v-flex xs6>
        <container-status></container-status>
      </v-flex>
      <v-flex xs6>
        <engine-info></engine-info>
      </v-flex>
    </v-layout>

  </div>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';
  import ContainerStatus from '@/components/ContainerStatus'
  import EngineInfo from '@/components/EngineInfo'

  @Component({
    components: {
      'container-status': ContainerStatus,
      'engine-info': EngineInfo
    }
  })
  export default class Home extends Vue {
    data() {
      return {}
    }

    mounted() {

    }

    move(routerPath) {
      alert(routerPath);
      if (routerPath) {
        this.$router.push(routerPath)
      }
    }
  }
</script>
