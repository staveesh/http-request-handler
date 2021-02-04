package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Qualifier("roundRobinAlgorithm")
public class RoundRobinAlgorithm extends SchedulingAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinAlgorithm.class);

    /**
     * Performs scheduling in such a way that jobs that arrive first get scheduled first.
     *
     * @param graph
     * @return
     */
    @Override
    public List<Job> preprocessJobs(ConflictGraph graph, List<String> devices) {
        logger.info("Preprocessing jobs using Round Robin scheme");
        List<Job> jobs = graph.getJobs();
        jobs.sort((j1, j2) -> {
            if (j1.getStartTime().before(j2.getStartTime()))
                return -1;
            else if (j1.getStartTime().equals(j2.getStartTime()))
                return 0;
            else
                return 1;
        });
        return jobs;
    }

}