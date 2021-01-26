package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.documents.Job;
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

    public static boolean addMeasurement(Job job) {
        acquireWriteLock();
        if (job == null) return false;
        activeJobs.add(job);
        releaseWriteLock();
        return true;
    }

    public static List<MeasurementDescription> getActiveJobs() {
        acquireReadLock();
        List<MeasurementDescription> sentJobs = new ArrayList<>();
        Date currentTime = new Date();
        for (Job job : activeJobs) {
            if (currentTime.after(job.getStartTime())
                    && !job.isRemovable() // Job is removable
                    && !job.isResettable(currentTime)) // Job is resettable
            {
                sentJobs.add(job.getMeasurementDescription());
            }
        }
        logger.info("Sent Jobs size is "+sentJobs.size());
        releaseReadLock();
        return sentJobs;
    }

    public static JSONArray getAllJobs(){
        acquireReadLock();
        JSONArray sentJobs= new JSONArray();
        for(Job job:activeJobs){
            sentJobs.put(job.getMeasurementDescription());
        }
        releaseReadLock();
        return sentJobs;
    }

    public static Job recordSuccessfulJob(JSONObject jobDesc) {
        //assuming the JsonObj has key field mapping which measurement failed
        String key = jobDesc.getString("taskKey");
        //int instance = jobDesc.getInt("instance");
        for (Job job : activeJobs) {
            String currKey = job.getKey();
            if (currKey.equals(key) && jobDesc.getBoolean("success")){
                logger.info("Job with key : "+currKey+"has been incremented by one");
                job.addNodeCount();
                if(job.nodesReached()) logger.info("\nJobs with Key "+key+" has Reached its Req Node count\n");
                return job;
            }
        }
        //false means the object is already removed since the count is reached
        return null;
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