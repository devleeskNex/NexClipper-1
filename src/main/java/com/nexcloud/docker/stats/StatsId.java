package com.nexcloud.docker.stats;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
public class StatsId implements Serializable {

    String containerId;

    Long time;
}
