<template>
  <div>
    <v-layout row>
      Container Detail
      <v-spacer></v-spacer>
      <v-btn small :outline="submenu != 'matrix'" @click="move('matrix')" color="primary">Matrix</v-btn>
      <v-btn small :outline="submenu != 'inspect'" @click="move('inspect')" color="primary">Inspect</v-btn>
      <v-btn small :outline="submenu != 'stdout'" @click="move('stdout')" color="primary">STDOUT Log</v-btn>
      <v-btn small :outline="submenu != 'stderr'" @click="move('stderr')" color="primary">STDERR Log</v-btn>
      <v-btn small :outline="submenu != 'console'" @click="move('console')" color="primary">Console</v-btn>
    </v-layout>

    <container-matrix v-if="submenu == 'matrix'" :id="id"></container-matrix>
    <container-inspect v-if="submenu == 'inspect'" :id="id"></container-inspect>
    <stdout-log v-if="submenu == 'stdout'" :id="id"></stdout-log>
    <stderr-log v-if="submenu == 'stderr'" :id="id"></stderr-log>
  </div>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';
  import ContainerMatrix from '@/components/ContainerMatrix'
  import ContainerInspect from '@/components/ContainerInspect'
  import StdoutLog from '@/components/StdoutLog'
  import StderrLog from '@/components/StderrLog'

  @Component({
    components: {
      'container-matrix': ContainerMatrix,
      'container-inspect': ContainerInspect,
      'stdout-log': StdoutLog,
      'stderr-log': StderrLog
    }
  })
  export default class Home extends Vue {
    @Prop(String) id;
    @Prop(String) submenu;

    move(submenu) {
      this.$router.push('/containers/' + this.id + '/' + submenu);
    }
  }
</script>
