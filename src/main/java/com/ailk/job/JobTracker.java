package com.ailk.job;

import org.apache.hadoop.mapreduce.Job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangkai8 on 2017/4/14.
 */
public class JobTracker {

    private static final Map<String, SubmitJob> jobs = new ConcurrentHashMap<String, SubmitJob>();
    private static JobTracker self;
    private static Object lock = new Object();

    private JobTracker() {

    }

    public static JobTracker getInstance() {
        if(self == null) {
            synchronized (lock) {
                if (self == null) {
                    self = new JobTracker();
                }
            }
        }
        return self;
    }

    public void addJob(SubmitJob job) {
        jobs.put(job.getJobId(), job);
    }

    public SubmitJob getJob(String jobId) {
        return jobs.get(jobId);
    }

    public void removeJob(String jobId) {
        if(jobId != null)
            jobs.remove(jobId);
    }

}
