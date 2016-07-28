package com.ailk.web;

import com.ailk.dao.BaseDao;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.pool.DbPool;
import org.mortbay.log.Log;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by scj on 2016/7/20.
 */
public class RowkeyBatchMainThread extends Thread{

    private File file;
    private String taskId;
    private String dst_path;
    private String sql_str;

    public RowkeyBatchMainThread(File file,String taskId,String dst_path,String sql_str){
        this.file = file;
        this.taskId = taskId;
        this.dst_path = dst_path;
        this.sql_str = sql_str;
    }

    @Override
    public void run(){
        BaseDao dao = new BaseDao("ocnosql");
        Log.info("RowkeyBatchMainThread start");
        //按行读取文件
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            List<Object[]> paramsList = new ArrayList<Object[]>();
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                //String subtaskid = UUID.randomUUID().toString();
                String[] params = new String[5];
                params[0] = UUID.randomUUID().toString();//GUID
                params[1] = tempString;
                params[2] = "0";
                params[3] = taskId;
                params[4] = "";        //在执行成功后回写

                paramsList.add(params);
                //每500条做一次批量提交
                if(paramsList.size()>=500){
                    Log.info("开始批量提交");
                    dao.executeBatchUpdate("insert into subtask_status(id,phone_num,finish_flag,pid,dst_path) values(?,?,?,?,?)",paramsList);
                    Log.info("结束批量提交");
                    paramsList.clear();
                }
                line++;
            }
            //最后剩余的号码一次提交
            if(paramsList!=null&&paramsList.size()>0){
                Log.info("开始最后剩余部分批量提交");
                dao.executeBatchUpdate("insert into subtask_status(id,phone_num,finish_flag,pid,dst_path) values(?,?,?,?,?)",paramsList);
                Log.info("结束最后剩余部分批量提交");
                paramsList.clear();
            }
            reader.close();

        }catch (FileNotFoundException e1){
            Log.info("文件没有找到");
            e1.printStackTrace();
            //更新任务状态为失败
            Log.info("更新任务状态为失败");
            dao.executeUpdate("update task_status set finish_flag='-1' where id='"+this.taskId+"'", null);
        }catch (IOException e) {
            Log.info("解析文件异常");
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        //创建线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
        //根据taskId查找所有需要进行的任务  分页查询  每次5000
        StringBuffer sqlCountBuffer = new StringBuffer("select count(*) C from subtask_status where pid='").append(taskId).append("'");
        List<Map> result =  dao.query(sqlCountBuffer.toString());
        long count = Long.parseLong(result.get(0).get("C").toString());
        long pageSize = 5000;
        long pageNum = count/pageSize + 1;

        long start = 0;
        long limit = pageSize;
        String subtask_id = "";
        String subtask_pid = "";
        String subtask_phone_num = "";
        String subtask_dst_path = dst_path;
        for(int i=0;i<pageNum;i++){
            start = pageSize*i;
            limit = pageSize;
            StringBuffer sqlBuffer = new StringBuffer("select * from subtask_status where pid='").append(taskId).append("' order by id desc");
            List<Map> list = dao.queryByPageMysql(sqlBuffer.toString(),start,limit);
            List<Map> listTemp = Collections.synchronizedList(list);
            //循环List中的内容丢进线程去跑。。。。
            for(Map map : listTemp){
                subtask_id = map.get("id").toString();
                subtask_pid = map.get("pid").toString();
                subtask_phone_num = map.get("phone_num").toString();
                //subtask_dst_path = map.get("dst_path").toString();
                //根据参数创建线程，并将线程丢到线程池中去跑
                fixedThreadPool.execute( new RowkeyBatchSubThread(subtask_id,subtask_pid,subtask_phone_num,subtask_dst_path,sql_str));
            }
        }

        //DbPool.closeConnection();
        Log.info("RowkeyBatchMainThread end");
    }

}
