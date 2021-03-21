package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.dto.JobDescription;
import com.taveeshsharma.requesthandler.dto.JobInterval;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.Parameters;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.*;

@SpringBootTest
class RequestHandlerApplicationTests {

	private static final List<String> TARGET_SERVERS = new ArrayList<String>(){{
		add("www.google.com");
		add("www.facebook.com");
		add("www.amazon.com");
		add("www.youtube.com");
		add("www.netflix.com");
		add("www.takealot.com");
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
		md.setType("ping");
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

	@Test
	void producesValidScheduleForRandomJobs(){
		for(int i = 1; i <= 300; i++){
			schedulerService.addMeasurement(buildRandomJob(i));
		}
		Map<Job, Assignment> schedule = schedulerService.getJobSchedule();
		// Conflicting jobs shouldn't be scheduled together
		List<Job> allJobs = new ArrayList<>(schedule.keySet());
		for(Job j1 : allJobs){
			for(Job j2 : allJobs){
				if(!j1.getKey().equals(j2.getKey())){
					if(j1.getParameters().getTarget().equalsIgnoreCase(j2.getParameters().getTarget())){
						Assertions.assertFalse(
								schedule.get(j1).getDeviceKey().equals(schedule.get(j2).getDeviceKey()) &&
								schedule.get(j1).getDispatchTime().equals(schedule.get(j2).getDispatchTime())
						);
					}
				}
			}
		}
	}

}
