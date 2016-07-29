package com.ailk.web;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.DataInfo;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.pool.DbPool;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.util.HDFSUtil;
import org.apache.hadoop.conf.Configuration;
import org.mortbay.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.ECField;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by scj on 2016/7/20.
 */
public class RowkeyBatchSubThread extends Thread{

    private String id;
    private String pid;
    private String phone_num;
    private String dst_path;
    private String sql_str;
    //private HbaseJdbcHelper help;

    //public RowkeyBatchSubThread(HbaseJdbcHelper help,String id,String pid,String phone_num,String dst_path,String sql_str){
    public RowkeyBatchSubThread(String id,String pid,String phone_num,String dst_path,String sql_str){
        //this.help = help;
        this.id = id;
        this.pid = pid;
        this.phone_num = phone_num;
        this.dst_path = dst_path;
        this.sql_str = sql_str;
    }

    @Override
    public void run(){
        BaseDao dao = new BaseDao("ocnosql");
        Log.info("RowkeyBatchSubThread start" + this.getName());

        //具体业务部分  根据号码生成文件到指定的目录
        //生成rowkey将sql_str中的rowkey替换掉
        RowKeyGenerator generator = new MD5RowKeyGenerator();
        if(generator!=null){
            String rowkey = (String) generator.generate(this.phone_num);
            this.sql_str = this.sql_str.replace("${rowkey}",rowkey);
        }

        try {
            Log.info("RowkeyBatchSubThread  &&&&&&" + this.getName()+"  SQL="+ this.sql_str );

            //准备就绪  查询后写文件
            Configuration conf = Connection.getInstance().getConf();
            HbaseJdbcHelper help = new PhoenixJdbcHelper(); //会存在连接数问题
            ResultSet rs = help.executeQueryRaw(this.sql_str);
            DataInfo info = HDFSUtil.write(conf, rs, dst_path);
            DbPool.closeConnection();

            //更子任务表中的文件目录
            Object[] paramsPath = new Object[4];
            paramsPath[0] = info.getPath();
            paramsPath[1] = info.getTotalCount();
            paramsPath[2] = this.id;
            paramsPath[3] = this.pid;
            dao.executeUpdate("update subtask_status set dst_path=?,sub_total_count=? where id=? and pid=?",paramsPath);

        }catch (Exception e){
            e.printStackTrace();
        }

        String[] params = new String[3];
        params[0] = "1";
        params[1] = this.id;
        params[2] = this.pid;
        //更新subtask状态为已完成
        dao.executeUpdate("update subtask_status set finish_flag=? where id=? and pid=?",params);

        //查询子任务表中同一Pid下是否还有没有完成的子任务
        StringBuffer sqlCountBuffer = new StringBuffer("select count(*) C from subtask_status where finish_flag='0'and pid='").append(pid).append("'");
        List<Map> result =  dao.query(sqlCountBuffer.toString());
        long count = Long.parseLong(result.get(0).get("C").toString());
        if(count<=0){
            //更新task_status表，任务已经完成
            String[] params1 = new String[2];
            params1[0] = this.pid;
            params1[1] = "1";

            dao.executeUpdate("update task_status a ,(select sum(sub_total_count) as total_count ,pid from subtask_status where pid=?) b set a.finish_flag=? ,a.total_count = b.total_count where a.id = b.pid",params1);
            //dao.executeUpdate("update task_status set finish_flag=? where id=?",params1);
            Log.info("RowkeyBatchSubThread --" + this.getName()+"  更新总任务状态为完成");
        }else{
            Log.info("RowkeyBatchSubThread --" + this.getName()+"  总任务状态待更新");
        }
        Log.info("RowkeyBatchSubThread end" + this.getName());
    }

    public static void main(String args[]){}

}
