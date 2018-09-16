<template>
  <div>
    <v-layout row wrap>
      <v-flex xs12>
        <container-status :height="200"></container-status>
      </v-flex>
      <v-flex xs12>
        <container-list></container-list>
      </v-flex>
    </v-layout>
  </div>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';
  import ContainerList from '@/components/ContainerList'
  import ContainerStatus from '@/components/ContainerStatus'

  @Component({
    components: {
      'container-list': ContainerList,
      'container-status': ContainerStatus,
    }
  })
  export default class Home extends Vue {
    data() {
      return {}
    }

    mounted() {

    }
  }
</script>
