package com.ailk.service.impl;

import com.ailk.dao.DeleteDao;
import com.ailk.model.DeleteLog;
import com.ailk.util.PropertiesUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class DeleteMR {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String table = args[0];
        String path = args[1];
        String id = args[2];
        conf.set("tablename",  table);
        conf.set("path", path);
        Job job = new Job(conf,"maintain delete data");
        job.setJar("DeleteMR.jar");
        job.setJarByClass(DeleteMR.class);
        job.setMapperClass(DeteleMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setReducerClass(DeleteReducer.class);
        String input_dir = PropertiesUtil.getProperty("runtime.properties", "input_dir");
        String output_dir = PropertiesUtil.getProperty("runtime.properties", "output_dir");
        FileInputFormat.addInputPath(job, new Path(input_dir  + path));
        FileOutputFormat.setOutputPath(job, new Path(output_dir + path));
        job.submit();
        job.waitForCompletion(true);
        long total = job.getCounters().getGroup("delete").findCounter("counter").getValue();
        DeleteDao deleteDao = new DeleteDao("ocnosql");
        deleteDao.updateDstatus("删除成功", String.valueOf(total), id);
    }

    private static class DeteleMapper extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, IntWritable> {
        private Counter counter;
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String table = context.getConfiguration().get("tablename");
            String str=value.toString();
            String [] strArray = str.split(",");
            if(strArray != null) {
               String rowkey = strArray[0];
               DeleteService.deleteRow(table, rowkey);
               counter = context.getCounter("delete","counter");
               counter.increment(1);
               context.write(new Text(), new IntWritable(1));
            }
        }
    }

    private static class DeleteReducer extends org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum=0;
            for(IntWritable count:values){
                sum += count.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }
}
