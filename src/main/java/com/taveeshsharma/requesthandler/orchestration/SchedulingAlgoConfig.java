package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.orchestration.algorithms.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingAlgoConfig {

    @Value("${scheduling.algo.name}")
    private String algorithm;

    @Bean
    public SchedulingAlgorithm schedulingAlgorithm(){
        switch (algorithm){
            case "rr":
                return new RoundRobinAlgorithm();
            case "edf":
                return new EDFCEAlgorithm();
            case "dosd":
                return new DOSDAlgorithm();
            case "aosd":
                return new AOSDAlgorithm();
        }
        return null;
    }
}
