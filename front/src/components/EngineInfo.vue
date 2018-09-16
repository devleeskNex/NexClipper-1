<template>
  <v-card>
    <v-card-text>
      <h4>Engine Summary</h4>
      <v-divider></v-divider>

      <codemirror
        style="height: 300px; font-size: 11px"
        ref="info"
        :options="{
              theme: 'dracula',
              mode: 'yaml',
              extraKeys: {'Ctrl-Space': 'autocomplete'},
              lineNumbers: true,
              lineWrapping: true,
              readOnly: true
            }"
        :value="dump">

      </codemirror>
    </v-card-text>
  </v-card>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';

  var YAML = require('js-yaml');

  @Component
  export default class EngineInfo extends Vue {
    data() {
      return {
        loaded: false,
        info: null,
        dump: ''
      }
    }

    mounted() {
      this.load();
    }

    async load() {
      var me = this;
      var res = await axios.get('docker/info');

      this.info = res.data;
      this.dump = YAML.dump(this.info, null, 2);

      console.log(this.info);

      this.loaded = true;
    }

    editorToObject() {

    }
  }
</script>
