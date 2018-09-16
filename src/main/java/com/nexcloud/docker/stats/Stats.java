package com.nexcloud.docker.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "stats")
@Data
public class Stats {

    @EmbeddedId
    StatsId id;

    private Float per_cpu;

    private Long used_mem;
    private Long limit_mem;

    private Long rx_bytes;
    private Long rx_packets;
    private Long rx_errors;
    private Long rx_dropped;
    private Long tx_bytes;
    private Long tx_packets;
    private Long tx_errors;
    private Long tx_dropped;

    private Long block_io_read;
    private Long block_io_write;

    private Long timestamp;

//    @JsonIgnore
//    @Column(columnDefinition = "TEXT")
//    String dataStr;
//
//    public Map getData() {
//        try {
//            return new ObjectMapper().readValue(this.getDataStr(), Map.class);
//        } catch (Exception ex) {
//            return new HashMap();
//        }
//    }
}
