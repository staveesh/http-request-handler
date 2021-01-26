package com.taveeshsharma.requesthandler.dto.documents;

import com.taveeshsharma.requesthandler.dto.JobDescription;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.Parameters;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Document("job_tracker")
public class Job {
    @Id
    private String key;
    private String type;
    private Date startTime;
    private Date endTime;
    private Integer intervalSec;
    private Long count;
    private Long priority;
    private Parameters parameters;
    private Integer nodeCount;
    private Integer jobInterval;
    private Date nextReset;
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
        if (description.getJobInterval() != 0) {
            this.nextReset = ApiUtils.
                    addHours(description.getMeasurementDescription().getStartTime(),
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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

    public Integer getJobInterval() {
        return jobInterval;
    }

    public void setJobInterval(Integer jobInterval) {
        this.jobInterval = jobInterval;
    }

    public Date getNextReset() {
        return nextReset;
    }

    public void setNextReset(Date nextReset) {
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
        Date presentTime = new Date(); //either create once or all the time when checking
        return presentTime.after(endTime);
    }

    public boolean isRecurring() {
        return jobInterval != 0;
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

    public boolean isResettable(Date currentTime) {
        if (!isRecurring()) return false;
        return nodesReached() || currentTime.after(nextReset);
    }

    private void setNextResetTime() {
        //if not recurring creating a new next reset time is useless as wont use this field
        //otherwise
        if (isRecurring()) {
            this.nextReset = ApiUtils.addHours(startTime, jobInterval);
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
        return new MeasurementDescription(
                type,
                key,
                startTime,
                endTime,
                intervalSec,
                count,
                priority,
                parameters
        );
    }
}
