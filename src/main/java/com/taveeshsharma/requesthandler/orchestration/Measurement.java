package com.taveeshsharma.requesthandler.orchestration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//this class will hold jobs
public class Measurement {

    private static final Logger logger = LoggerFactory.getLogger(Measurement.class);

    private static List<Job> activeJobs = new ArrayList<>();
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static boolean addMeasurement(JSONObject jobRequest) {
        acquireWriteLock();
        if (jobRequest == null) return false;
        //TODO there is need for error checking for valid jobRequest structure before addition
        logger.info(jobRequest.toString());
        JSONObject jobDesc = jobRequest.getJSONObject("jobDescription");
        Job jobTobeScheduled = new Job(jobDesc);
        activeJobs.add(jobTobeScheduled);
        releaseWriteLock();
        logger.info("job added:" + jobTobeScheduled);
        return true;
    }

    public static JSONArray getActiveJobs(){
        acquireReadLock();
        JSONArray sentJobs = new JSONArray();
        Date currentTime = new Date();
        for (Job job : activeJobs) {
            if (job.canStart(currentTime) && !job.isRemovable() && !job.isResettable(currentTime)) {
                sentJobs.put(job.getMeasurementDesc());
            }
        }
        logger.info("Sent Jobs size is "+sentJobs.length());
        releaseReadLock();
        return sentJobs;
    }

    public static JSONArray getAllJobs(){
        acquireReadLock();
        JSONArray sentJobs= new JSONArray();
        for(Job job:activeJobs){
            sentJobs.put(job.getMeasurementDesc());
        }
        releaseReadLock();
        return sentJobs;
    }

    public static boolean recordSuccessfulJob(JSONObject jobDesc) {
        //assuming the JsonObj has key field mapping which measurement failed
        String key = jobDesc.getString("taskKey");
        //int instance = jobDesc.getInt("instance");
        for (Job job : activeJobs) {
            String currKey = (String) job.getMeasurementDesc().get("key");
            if (currKey.equals(key) && jobDesc.getBoolean("success")){
                logger.info("Job with key : "+currKey+"has been incremented by one");
                job.addNodeCount();
                if(job.nodesReached()) logger.info("\nJobs with Key "+key+" has Reached its Req Node count\n");
                return true;
            }
        }
        //false means the object is already removed since the count is reached
        return false;
    }

    public static void acquireReadLock() {
        readWriteLock.readLock().lock();
    }

    public static void releaseReadLock() {
        readWriteLock.readLock().unlock();
    }

    public static void acquireWriteLock() {
        readWriteLock.writeLock().lock();
    }

    public static void releaseWriteLock() {
        readWriteLock.writeLock().unlock();
    }

    public static List<Job> getJobs() {
        return activeJobs;
    }
}