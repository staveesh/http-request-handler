package com.taveeshsharma.requesthandler.dto.documents;

import com.taveeshsharma.requesthandler.dto.JobDescription;
import com.taveeshsharma.requesthandler.dto.JobInterval;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.Parameters;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Document("job_tracker")
public class Job {
    @Id
    private String key;
    private String type;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Integer intervalSec;
    private Long count;
    private Long priority;
    private Parameters parameters;
    private Integer nodeCount;
    private JobInterval jobInterval;
    private ZonedDateTime nextReset;
    private AtomicInteger currentNodeCount;

    public Job() {
    }

    public Job(JobDescription description) {
        this.key = description.getMeasurementDescription().getKey();
        this.type = description.getMeasurementDescription().getType();
        this.startTime = description.getMeasurementDescription().getStartTime();
        this.endTime = description.getMeasurementDescription().getEndTime();
        this.intervalSec = description.getMeasurementDescription().getIntervalSec();
        this.count = description.getMeasurementDescription().getCount();
        this.priority = description.getMeasurementDescription().getPriority();
        this.parameters = description.getMeasurementDescription().getParameters();
        this.nodeCount = description.getNodeCount();
        this.jobInterval = description.getJobInterval();
        if (isRecurring()) {
            this.nextReset = ApiUtils.
                    addInterval(description.getMeasurementDescription().getStartTime(),
                            description.getJobInterval());
        }
        this.currentNodeCount = new AtomicInteger(0);
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public JobInterval getJobInterval() {
        return jobInterval;
    }

    public void setJobInterval(JobInterval jobInterval) {
        this.jobInterval = jobInterval;
    }

    public ZonedDateTime getNextReset() {
        return nextReset;
    }

    public void setNextReset(ZonedDateTime nextReset) {
        this.nextReset = nextReset;
    }

    public AtomicInteger getCurrentNodeCount() {
        return currentNodeCount;
    }

    public void setCurrentNodeCount(AtomicInteger currentNodeCount) {
        this.currentNodeCount = currentNodeCount;
    }

    public boolean nodesReached() {
        return currentNodeCount.get() >= nodeCount;
    }

    private boolean jobElapsed() {
        ZonedDateTime presentTime = ZonedDateTime.now(); //either create once or all the time when checking
        return presentTime.isAfter(endTime);
    }

    public boolean isRecurring() {
        long totalSeconds = this.jobInterval.getHours()*60*60 +
                this.jobInterval.getMinutes()*60 +
                this.getJobInterval().getSeconds();
        return totalSeconds != 0;
    }

    public void addNodeCount() {
        //ensures results of the same job instance
        currentNodeCount.getAndIncrement();
    }

    public boolean isRemovable() {
        if (jobElapsed()) {
            return true;
        }
        if (!isRecurring()) { //means the job is not to be repeated and if the req nodes are reached then can be removed
            return nodesReached();
        }
        return false; //then is recurring and
    }

    public boolean isResettable(ZonedDateTime currentTime) {
        if (!isRecurring()) return false;
        return nodesReached() || currentTime.isAfter(nextReset);
    }

    private void setNextResetTime() {
        //if not recurring creating a new next reset time is useless as wont use this field
        //otherwise
        if (isRecurring()) {
            this.nextReset = ApiUtils.addInterval(startTime, jobInterval);
        }
    }

    public void reset() {
        if (isRecurring()) {
            currentNodeCount.set(0);
            setStartTime(nextReset);
            //this will create a new Date obj thus start and next wont be pointing to the same object
            //will use new start time(obtained from the prev reset time) and interval to create the next Reset time
            setNextResetTime();
        }
    }

    public MeasurementDescription getMeasurementDescription() {
        MeasurementDescription description = new MeasurementDescription();
        description.setType(type);
        description.setKey(key);
        description.setStartTime(startTime);
        description.setEndTime(endTime);
        description.setIntervalSec(intervalSec);
        description.setCount(count);
        description.setPriority(priority);
        description.setParameters(parameters);
        return description;
    }
}
