<template>
  <v-card>
    <v-card-title>
      <v-layout row>
        Containers
        <v-spacer></v-spacer>
        <v-text-field
          v-model="search"
          append-icon="search"
          label="Search"
          single-line
          hide-details
        ></v-text-field>
      </v-layout>
      <v-layout row>
        <v-btn small outline color="deep-orange">+ Add</v-btn>
        <v-spacer></v-spacer>
        <v-btn small outline color="indigo">Start</v-btn>
        <v-btn small outline color="indigo">Stop</v-btn>
        <v-btn small outline color="indigo">Kill</v-btn>
        <v-btn small outline color="indigo">Restart</v-btn>
        <v-btn small outline color="indigo">Pause</v-btn>
        <v-btn small outline color="indigo">Resume</v-btn>
        <v-btn small outline color="indigo">Remove</v-btn>
      </v-layout>
    </v-card-title>

    <v-data-table
      :pagination.sync="pagination"
      :headers="headers"
      :items="containers"
      :search="search"
      v-model="selected"
      item-key="Id"
      select-all
      class="elevation-1"
    >
      <template slot="headerCell" slot-scope="props">
        <v-tooltip bottom>
        <span slot="activator">
          {{ props.header.text }}
        </span>
          <span>
          {{ props.header.text }}
        </span>
        </v-tooltip>
      </template>
      <template slot="items" slot-scope="props">
        <td>
          <v-checkbox
            v-model="props.selected"
            primary
            hide-details
          ></v-checkbox>
        </td>
        <td><a v-on:click="move(props.item.Id)">{{ props.item.Name }}</a></td>
        <td class="text-xs-right">{{ props.item.Id.substring(0,11)}}..</td>
        <td class="text-xs-right">{{ props.item.Image }}</td>
        <td class="text-xs-right">{{ props.item.Command }}</td>
        <td class="text-xs-right">{{ new Date(props.item.Created * 1000) }}</td>
        <td class="text-xs-right">{{ props.item.State }}</td>
        <td class="text-xs-right">{{ props.item.Status }}</td>
      </template>
      <v-alert slot="no-results" :value="true" color="error" icon="warning">
        Your search for "{{ search }}" found no results.
      </v-alert>
    </v-data-table>
  </v-card>
</template>

<script>
  import {Vue, Component, Prop, Watch} from 'vue-property-decorator';

  @Component
  export default class ContainerList extends Vue {
    data() {
      return {
        pagination: {
          rowsPerPage: 10
        },
        containers: [],
        search: '',
        selected: [],
        headers: [
          {text: 'Name', value: 'Name'},
          {text: 'Id', value: 'Id'},
          {text: 'Image', value: 'Image'},
          {text: 'Command', value: 'Command'},
          {text: 'Created', value: 'Created'},
          {text: 'State', value: 'State'},
          {text: 'Status', value: 'Status'}
        ]
      }
    }

    mounted() {
      this.load();
    }

    async load() {
      var me = this;
      var res = await axios.get('docker/containers/json?all=true');
      res.data.forEach(function (container, i) {
        if (container.Names.length) {
          container.Name = container.Names[0];
          //container.CreatedTime = new Date(container.Created).toLocaleDateString();
        }
      });
      this.containers = res.data;
    }

    move(id) {
      this.$router.push('/containers/' + id + '/matrix');
    }
  }
</script>
