package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.dto.JobDescription;
import com.taveeshsharma.requesthandler.dto.JobInterval;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.Parameters;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.ZonedDateTime;
import java.util.*;

@SpringBootTest
class JobSchedulingTests {

	private static final Logger logger = LoggerFactory.getLogger(JobSchedulingTests.class);

	private static final List<String> TARGET_SERVERS = new ArrayList<String>(){{
		add("www.google.com");
		add("www.facebook.com");
		add("www.amazon.com");
		add("www.youtube.com");
		add("www.netflix.com");
		add("www.takealot.com");
	}};

	private static final List<String> JOB_TYPES = new ArrayList<String>(){{
		add(Constants.TCP_TYPE);
		add(Constants.PING_TYPE);
		add(Constants.DNS_TYPE);
		add(Constants.HTTP_TYPE);
		add(Constants.TRACERT_TYPE);
	}};

	@Autowired
	private SchedulerService schedulerService;

	@Test
	void contextLoads() {
	}

	Job buildRandomJob(int i){
		MeasurementDescription md = new MeasurementDescription();
		long minutesAfter = (long)(Math.random()*6+5);
		long intervalMin = (long)(Math.random()*6+5);
		int serverIndex = (int)(Math.random()*(TARGET_SERVERS.size()-1));
		int jobTypeIndex = (int)(Math.random()*(JOB_TYPES.size()-1));
		md.setType(JOB_TYPES.get(jobTypeIndex));
		md.setKey("J"+i);
		md.setStartTime(ZonedDateTime.now().plusMinutes(minutesAfter));
		md.setEndTime(ZonedDateTime.now().plusDays(1L));
		md.setIntervalSec(1);
		md.setCount(1L);
		md.setPriority(10L);
		md.setInstanceNumber(1);
		Parameters params = new Parameters();
		params.setTarget(TARGET_SERVERS.get(serverIndex));
		params.setServer(null);
		params.setDirUp(false);
		params.setExperiment(true);
		md.setParameters(params);
		JobDescription jd = new JobDescription();
		jd.setMeasurementDescription(md);
		jd.setNodeCount(1);
		JobInterval interval = new JobInterval();
		interval.setHours(0L);
		interval.setMinutes(intervalMin);
		interval.setSeconds(0L);
		jd.setJobInterval(interval);
		return new Job(jd);
	}

	Job buildFixedJob(int i, int jobTypeIndex){
		MeasurementDescription md = new MeasurementDescription();
		long minutesAfter = 5L;
		long intervalMin = 5L;
		int serverIndex = (int)(Math.random()*(TARGET_SERVERS.size()-1));
		md.setType(JOB_TYPES.get(jobTypeIndex));
		md.setKey("J"+i);
		md.setStartTime(ZonedDateTime.now().plusMinutes(minutesAfter));
		md.setEndTime(ZonedDateTime.now().plusDays(1L));
		md.setIntervalSec(1);
		md.setCount(1L);
		md.setPriority(10L);
		Parameters params = new Parameters();
		params.setTarget(TARGET_SERVERS.get(serverIndex));
		params.setServer(null);
		params.setDirUp(false);
		params.setExperiment(true);
		md.setParameters(params);
		md.setInstanceNumber(1);
		JobDescription jd = new JobDescription();
		jd.setMeasurementDescription(md);
		jd.setNodeCount(1);
		JobInterval interval = new JobInterval();
		interval.setHours(0L);
		interval.setMinutes(intervalMin);
		interval.setSeconds(0L);
		jd.setJobInterval(interval);
		return new Job(jd);
	}

	void checkValidity(Schedule schedule){
		Map<Job, Assignment> jobAssignments = schedule.getJobAssignments();
		// Conflicting jobs shouldn't be scheduled together
		List<Job> allJobs = new ArrayList<>(jobAssignments.keySet());
		for (Job j1 : allJobs) {
			for (Job j2 : allJobs) {
				if (!j1.getKey().equals(j2.getKey())) {
					if (j1.getParameters().getTarget().equalsIgnoreCase(j2.getParameters().getTarget())) {
						Assertions.assertFalse(
								jobAssignments.get(j1).getDeviceKey().equals(jobAssignments.get(j2).getDeviceKey()) &&
										jobAssignments.get(j1).getDispatchTime().equals(jobAssignments.get(j2).getDispatchTime())
						);
					}
				}
			}
		}
	}

//	@Test
//	void producesValidScheduleForRandomJobs() {
//		for (int i = 1; i <= 10; i++) {
//			schedulerService.addMeasurement(buildRandomJob(i));
//		}
//		List<String> devices = new ArrayList<>();
//		for(int i = 1; i <= 6; i++)
//			devices.add("D"+i);
//		schedulerService.requestScheduling(null, devices);
//		Map<Job, Assignment> schedule = schedulerService.getJobSchedule();
//		checkValidity(schedule);
//	}

	@Test
	void producesValidScheduleForFixedJobs(){
		List<Job> jobs = new ArrayList<Job>(){{
			add(buildFixedJob(1, 1)); //ping : 1
			add(buildFixedJob(2, 2)); // dns : 2
			add(buildFixedJob(3, 3)); // http : 3
			add(buildFixedJob(4, 0)); // tcp : 4
		}};
		ConflictGraph graph = new ConflictGraph(jobs);
		graph.addEdge(jobs.get(0), jobs.get(2));
		graph.addEdge(jobs.get(2), jobs.get(1));
		graph.addEdge(jobs.get(1), jobs.get(3));
		List<String> devices = new ArrayList<>();
		for(int i = 1; i <= 6; i++)
			devices.add("D"+i);
		for(Job job : jobs){
			schedulerService.addMeasurement(job);
		}
		schedulerService.requestScheduling(graph, devices);
//		Schedule schedule = schedulerService.getJobSchedule();
//		checkValidity(schedule);
	}
}
