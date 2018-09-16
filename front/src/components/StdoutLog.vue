<template>
  <v-card>
    <v-card-text>
      <h4>STDOUT Log</h4>
      <v-divider></v-divider>

      <codemirror
        ref="info"
        :options="{
              theme: 'dracula',
              mode: 'log',
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
  export default class StdoutLog extends Vue {
    @Prop(String) id;

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

    @Watch('id')
    async load() {
      var me = this;
      var res = await axios.get('docker/containers/' + this.id + '/logs?stdout=true&tail=500');

      this.dump = res.data;

      $(this.$el).find('.CodeMirror').height(600).css('font-size', '11px');
      this.loaded = true;
    }
  }
</script>
