package com.ailk.job;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.StringUtils;

/**
 * Created by wangkai8 on 2017/4/14.
 */
public class SubmitJob {

    public static final String PREPARE = "PREPARE";
    public static final String RUNNING = "RUNNING";
    public static final String FINISHED = "FINISHED";
    public static final String UNFINISHED = "UNFINISHED";

    public static final String SUCCESS = "SUCCESSED";
    public static final String FAILED = "FAILED";


    private Job job;
    private String jobId;


    public SubmitJob() {

    }
    public SubmitJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }



    public void setJob(Job job) {
        this.job = job;
    }

    public String getJobId() {
        if(job != null) {
            return job.getJobID().toString();
        }
        return null;
    }

    public String getReportProcess() {
        String report = "";
        if(job != null) {
            try {
                report =
                        (" map " + StringUtils.formatPercent(job.mapProgress(), 0) +
                                " reduce " +
                                StringUtils.formatPercent(job.reduceProgress(), 0));
            } catch (Throwable e) {
                throw new RuntimeException("get MapReduce process report exception", e);
            }
            return report;
        }
        return "";
    }
}
