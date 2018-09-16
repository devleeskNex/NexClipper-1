<template>
  <v-card>
    <v-card-text>
      <h4>Containers Status</h4>
      <v-divider></v-divider>
      <highcharts v-bind:style="styles" v-if="loaded" :options="containerOptions"></highcharts>
    </v-card-text>
  </v-card>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';

  @Component
  export default class ContainerStatus extends Vue {
    @Prop({type: Number, default: 300}) height;

    data() {
      return {
        styles: {
          height: this.height + 'px'
        },
        loaded: false,
        containerOptions: {
          exporting: {enabled: false},
          chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
          },
          title: {
            text: null
          },
          tooltip: {
            pointFormat: '{series.name}: <b>{point.y}</b>'
          },
          plotOptions: {
            pie: {
              allowPointSelect: true,
              cursor: 'pointer',
              dataLabels: {
                enabled: false
              },
              showInLegend: true
              //size: '75%'
            },
            series: {
              animation: false
            }
          },
          legend: {
            align: 'right',
            layout: 'vertical',
            verticalAlign: 'middle',
            labelFormat: '{name}: <b>{y}</b>'
          },
          series: []
        }
      }
    }

    mounted() {
      this.load();
    }

    async load() {
      var me = this;
      var res = await axios.get('docker/containers/json?all=true');

      var counts = {
        total: 0,
        exited: 0,
        running: 0,
        paused: 0,
        created: 0,
        restarting: 0,
        dead: 0
      };
      res.data.forEach(function (container, i) {
        counts[container['State']]++;
        counts.total++;
      });

      this.containerOptions.series.push({
        name: 'Count',
        colorByPoint: true,
        data: [{
          name: 'Total',
          y: counts.total,
          color: '#01579b'
        }, {
          name: 'Running',
          y: counts.running,
          color: '#0288d1'
        }, {
          name: 'Created',
          y: counts.created,
          color: '#03a9f4',
        }, {
          name: 'Restarting',
          y: counts.restarting,
          color: '#4fc3f7'
        }, {
          name: 'Dead',
          y: counts.dead,
          color: '#b3e5fc'
        }, {
          name: 'Exited',
          y: counts.exited,
          color: '#e1f5fe'
        }, {
          name: 'Paused',
          y: counts.paused,
          color: '#afc2cb',
        },]
      });

      this.loaded = true;
    }
  }
</script>
