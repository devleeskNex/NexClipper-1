package com.nexcloud.docker.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.nexcloud.docker.DockerUtil;
import com.nexcloud.docker.controller.DockerController;
import com.sun.javafx.binding.StringFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class StatsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StatsScheduler.class);


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StatsRepository statsRepository;

    @Autowired
    DockerController dockerController;

    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void saveContainersStats() throws Exception {
        String containers = DockerUtil.procDockerApi("/containers/json",
                "GET",
                null,
                null);

        List<Map> list = objectMapper.readValue(containers, List.class);

        if (!list.isEmpty()) {
            long time = new Date().getTime();
            for (Map container : list) {
                String id = container.get("Id").toString();

                String stat = DockerUtil.procDockerApi("/containers/" + id + "/stats?stream=false",
                        "GET",
                        null,
                        null);

                Object json = Configuration.defaultConfiguration().jsonProvider().parse(stat);

                StatsId statsId = new StatsId();
                statsId.setContainerId(id);
                statsId.setTime(time);

                Stats stats = new Stats();
                stats.setTimestamp(time);
                stats.setId(statsId);
                stats.setPer_cpu(this.calCpu(json));
                stats.setLimit_mem(Long.parseLong(JsonPath.read(stat, "$.memory_stats.limit") + ""));
                stats.setUsed_mem(Long.parseLong(JsonPath.read(stat, "$.memory_stats.usage") + ""));

                List<Map> io = (List<Map>) JsonPath.read(stat, "$.blkio_stats.io_service_bytes_recursive");
                for (int i = 0; i < io.size(); i++) {
                    if ("Write".equals(io.get(i).get("op").toString())) {
                        stats.setBlock_io_write(Long.parseLong(io.get(i).get("value") + ""));

                    } else if ("Read".equals(io.get(i).get("op").toString())) {
                        stats.setBlock_io_read(Long.parseLong(io.get(i).get("value") + ""));
                    }
                }

                String[] nts = new String[]{"rx_bytes", "rx_dropped", "rx_errors", "rx_packets"
                        , "tx_bytes", "tx_dropped", "tx_errors", "tx_packets"};

                for (String nt : nts) {
                    Long bytes = 0L;
                    List<Object> bytes_list = (List<Object>) JsonPath.read(stat, "$.networks[*]." + nt);
                    for (int i = 0; i < bytes_list.size(); i++) {
                        bytes += Long.parseLong((bytes_list.get(i) + ""));
                    }
                    switch (nt) {
                        case "rx_bytes":
                            stats.setRx_bytes(bytes);
                            break;
                        case "rx_dropped":
                            stats.setRx_dropped(bytes);
                            break;
                        case "rx_errors":
                            stats.setRx_errors(bytes);
                            break;
                        case "rx_packets":
                            stats.setRx_packets(bytes);
                            break;
                        case "tx_bytes":
                            stats.setTx_bytes(bytes);
                            break;
                        case "tx_dropped":
                            stats.setTx_dropped(bytes);
                            break;
                        case "tx_errors":
                            stats.setTx_errors(bytes);
                            break;
                        case "tx_packets":
                            stats.setTx_packets(bytes);
                            break;
                    }
                }

                statsRepository.save(stats);
                Map map = objectMapper.convertValue(stats, Map.class);

                //send to sse(server sent event)
                map.put("containerId", statsId.getContainerId());
                dockerController.emitterSend(map);
            }
        }

//        Runtime runtime = Runtime.getRuntime();
//
//        NumberFormat format = NumberFormat.getInstance();
//
//        StringBuilder sb = new StringBuilder();
//        long maxMemory = runtime.maxMemory();
//        long allocatedMemory = runtime.totalMemory();
//        long freeMemory = runtime.freeMemory();
//
//        sb.append("free memory: " + format.format(freeMemory / 1024) + "<br/>");
//        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "<br/>");
//        sb.append("max memory: " + format.format(maxMemory / 1024) + "<br/>");
//        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "<br/>");
//        logger.info(sb.toString());
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 20000)
    public void deleteExpiredStats() throws Exception {
        Long expireTime = 600000L; //10 min.
        long l = new Date().getTime() - expireTime;

        statsRepository.deleteExpiredStats(l);
    }


    private Float calCpu(Object stat) {
        float cpuPercent = 0.0f;
        long cpuDelta = (Long) JsonPath.read(stat, "$.cpu_stats.cpu_usage.total_usage")
                - (Long) JsonPath.read(stat, "$.precpu_stats.cpu_usage.total_usage");

        long systemDelta = (Long) JsonPath.read(stat, "$.cpu_stats.system_cpu_usage")
                - (Long) JsonPath.read(stat, "$.precpu_stats.system_cpu_usage");

        int cpuCount = (Integer) JsonPath.read(stat, "$.cpu_stats.online_cpus");

        cpuPercent = (float) cpuDelta / (float) systemDelta * cpuCount * 100;
        return Float.parseFloat(String.format("%.2f", cpuPercent));
    }
}
