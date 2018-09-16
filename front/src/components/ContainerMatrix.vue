<template>
  <div>
    <v-card>
      <v-card-text>
        <h4>Cpu Usage</h4>
        <v-divider></v-divider>
        <highcharts v-if="matrixLoaded" :options="cpuOpt"></highcharts>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-text>
        <h4>Mem Usage</h4>
        <v-divider></v-divider>
        <highcharts v-if="matrixLoaded" :options="memOpt"></highcharts>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-text>
        <h4>Block IO Usage</h4>
        <v-divider></v-divider>
        <highcharts v-if="matrixLoaded" :options="ioOpt"></highcharts>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-text>
        <h4>Network Throughput</h4>
        <v-divider></v-divider>
        <highcharts v-if="matrixLoaded" :options="ntbOpt"></highcharts>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-text>
        <h4>Network Error</h4>
        <v-divider></v-divider>
        <highcharts v-if="matrixLoaded" :options="nteOpt"></highcharts>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';
  import Highcharts from 'highcharts';

  Highcharts.setOptions({
    global: {
      useUTC: false
    }
  });

  @Component
  export default class ContainerMatrix extends Vue {
    @Prop(String) id;

    data() {
      var cpuOpt = this.baseOption();
      cpuOpt.yAxis.title.text = 'Cpu Usage';
      cpuOpt.series = [{
        type: 'area',
        name: 'Cpu Usage',
        data: []
      }];

      var memOpt = this.baseOption();
      memOpt.yAxis.title.text = 'Mem Usage';
      memOpt.series = [{
        type: 'area',
        name: 'used_mem',
        data: []
      }, {
        type: 'area',
        name: 'limit_mem',
        data: []
      }];

      var ioOpt = this.baseOption();
      ioOpt.yAxis.title.text = 'Block IO Usage';
      ioOpt.series = [{
        type: 'area',
        name: 'block_io_read',
        data: []
      }, {
        type: 'area',
        name: 'block_io_write',
        data: []
      }];

      var ntbOpt = this.baseOption();
      ntbOpt.yAxis.title.text = 'Network Throughput';
      ntbOpt.series = [{
        type: 'area',
        name: 'tx_bytes',
        data: []
      }, {
        type: 'area',
        name: 'rx_bytes',
        data: []
      }];

      var nteOpt = this.baseOption();
      nteOpt.yAxis.title.text = 'Network Error';
      nteOpt.series = [{
        type: 'area',
        name: 'tx_errors',
        data: []
      }, {
        type: 'area',
        name: 'rx_errors',
        data: []
      }];

      return {
        matrixLoaded: false,
        cpuOpt: cpuOpt,
        memOpt: memOpt,
        ioOpt: ioOpt,
        ntbOpt: ntbOpt,
        nteOpt: nteOpt
      }
    }

    mounted() {
      var me = this;
      me.load();
      window.busVue.$on('container', function (message) {
        if (me.id == message.containerId) {
          me.cpuOpt.series[0].data.push(
            [message.timestamp, message.per_cpu]
          );
          me.memOpt.series[0].data.push(
            [message.timestamp, message.used_mem]
          );
          me.memOpt.series[1].data.push(
            [message.timestamp, message.limit_mem]
          );
          me.ioOpt.series[0].data.push(
            [message.timestamp, message.block_io_read]
          );
          me.ioOpt.series[1].data.push(
            [message.timestamp, message.block_io_write]
          );
          me.ntbOpt.series[0].data.push(
            [message.timestamp, message.tx_bytes]
          );
          me.ntbOpt.series[1].data.push(
            [message.timestamp, message.rx_bytes]
          );
          me.nteOpt.series[0].data.push(
            [message.timestamp, message.tx_errors]
          );
          me.nteOpt.series[1].data.push(
            [message.timestamp, message.rx_errors]
          );
        }
      });
    }

    baseOption() {
      return {
        exporting: {enabled: false},
        title: {
          text: null
        },
        chart: {
          zoomType: 'x'
        },
        xAxis: {
          type: 'datetime'
        },
        yAxis: {
          title: {
            text: ''
          }
        },
        legend: {
          enabled: false
        },
        plotOptions: {
          area: {
            fillColor: {
              linearGradient: {
                x1: 0,
                y1: 0,
                x2: 0,
                y2: 1
              },
              stops: [
                [0, Highcharts.getOptions().colors[0]],
                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
              ]
            },
            marker: {
              radius: 2
            },
            lineWidth: 1,
            states: {
              hover: {
                lineWidth: 1
              }
            },
            threshold: null
          }
        }
      }
    }

    @Watch('id')
    async load() {
      var me = this;
      var res = await axios.get('stats/search/findById_ContainerId?containerId=' + this.id + '&size=100000&sort=Id_time,desc');

      var per_cpu = [];

      var used_mem = [];
      var limit_mem = [];

      var block_io_read = [];
      var block_io_write = [];

      var tx_bytes = [];
      var rx_bytes = [];

      var tx_errors = [];
      var rx_errors = [];
      if (res.data._embedded.stats) {
        res.data._embedded.stats.forEach(function (stat, i) {
          per_cpu.push([stat.timestamp, stat.per_cpu]);
          used_mem.push([stat.timestamp, stat.used_mem]);
          limit_mem.push([stat.timestamp, stat.limit_mem]);
          block_io_read.push([stat.timestamp, stat.block_io_read]);
          block_io_write.push([stat.timestamp, stat.block_io_write]);
          tx_bytes.push([stat.timestamp, stat.tx_bytes]);
          rx_bytes.push([stat.timestamp, stat.rx_bytes]);
          tx_errors.push([stat.timestamp, stat.tx_errors]);
          rx_errors.push([stat.timestamp, stat.rx_errors]);
        })
      }
      per_cpu.reverse();
      used_mem.reverse();
      limit_mem.reverse();
      block_io_read.reverse();
      block_io_write.reverse();
      tx_bytes.reverse();
      rx_bytes.reverse();
      tx_errors.reverse();
      rx_errors.reverse();

      me.cpuOpt.series[0].data = per_cpu;

      me.memOpt.series[0].data = used_mem;
      me.memOpt.series[1].data = limit_mem;

      me.ioOpt.series[0].data = block_io_read;
      me.ioOpt.series[1].data = block_io_write;

      me.ntbOpt.series[0].data = tx_bytes;
      me.ntbOpt.series[1].data = rx_bytes;

      me.nteOpt.series[0].data = tx_errors;
      me.nteOpt.series[1].data = rx_errors;

      me.matrixLoaded = true;
    }
  }
</script>
