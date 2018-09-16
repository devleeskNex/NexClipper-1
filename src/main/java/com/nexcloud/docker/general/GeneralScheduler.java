package com.nexcloud.docker.general;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class GeneralScheduler {
    @Scheduled(initialDelay = 1000, fixedDelay = 10000)
    public void saveContainersStats() throws Exception {

    }
}
