package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.dao.HiveDao;
import com.ailk.dao.HiveJdbc;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.service.IQueryService;
import com.ailk.util.DateUtil;
import com.ailk.util.HDFSUtil;
import com.ailk.util.PropertiesUtil;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.*;

public class QueryByNoRowkeyService implements IQueryService {
    public static final Log LOG = LogFactory.getLog(QueryByNoRowkeyService.class);
    public static final String TEMP_TABLE_PREFIX = "temp_";
    public static final String HADOOP_FILE_PATH = PropertiesUtil.getProperty("runtime.properties", "hadoopPath.tempTablePath");
    BaseDao dao = new BaseDao("ocnosql");
    private HiveDao hiveDao = new HiveDao();
    //"/cloud/hive/warehouse/";
    /**
     * 任务提交
     *
     * @return
     */
    public ResultDTO taskSubmit(String sql) {
        try {
            String table_Name = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String tableName = TEMP_TABLE_PREFIX + table_Name;
            long totalCount = 0;
            String insert_task = "insert into qrytask(status,createDate,updateDate,totalCount,tempTable,querySql) values(?,now(),now(),?,?,?);";
            Object[] a = new Object[4];
            a[0] = 0;
            a[1] = String.valueOf(0);
            a[2] = tableName;
            a[3] = sql;
            dao.executeUpdate(insert_task, a);
            boolean flag = HiveJdbc.queryCreateTable(sql, tableName);
            LOG.info("insert qrytask success");
//            boolean flag=false;
            if (flag) {
                LOG.info("create table success");
                List<Map> cloumns = HiveJdbc.query("select * from " + tableName + " limit 1");
                Map record = cloumns.get(0);
                Iterator it = record.keySet().iterator();
                String cloumns_table = "";
                while (it.hasNext()) {
                    String columnName = (String) it.next();
                    cloumns_table = cloumns_table + columnName.replace(tableName + ".", "") + ",";
                }
                cloumns_table = cloumns_table.substring(0, cloumns_table.length() - 1);

                totalCount=hiveDao.hiveGetRowNums(tableName);
                //totalCount = HiveJdbc.queryTotalCount(tableName);
                String update_task = "update qrytask set status=?,updateDate=now(),timeDiff=TIMESTAMPDIFF(SECOND,createDate,updateDate),totalCount=?,cloumnsSql=? where tempTable=?;";
                Object[] a2 = new Object[4];
                a2[0] = 1;//成功
                a2[1] = String.valueOf(totalCount);
                a2[2] = cloumns_table;
                a2[3] = tableName;
                dao.executeUpdate(update_task, a2);
                LOG.info("update qrytask success");
            } else {
                LOG.info("create table failure");
                String update_task = "update qrytask set status=?,updateDate=now(),timeDiff=TIMESTAMPDIFF(SECOND,createDate,updateDate) where tempTable=?;";
                Object[] a3 = new Object[2];
                a3[0] = 2;//失败
                a3[1] = tableName;
                dao.executeUpdate(update_task, a3);
            }
            return null;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }

    /**
     * 任务分页
     *
     * @param vs
     * @return
     */
    public ResultDTO loadDataTask(ValueSet vs) {
        String startDate=vs.getString("startDate");
        String endDate=vs.getString("endDate");
        String status=vs.getString("status");
        int start = Integer.parseInt(vs.getString("start"));
        int limit = Integer.parseInt(vs.getString("limit"));
        long pageNow = start == 0 ? 0 : start / limit;
        String sqlcon=" where 1=1 ";
        if(StringUtils.isNotBlank(startDate)&&!StringUtils.isNotBlank(endDate)){
            sqlcon=sqlcon+" and createDate>='"+startDate+"'";
        }
        if(StringUtils.isNotBlank(endDate)&&!StringUtils.isNotBlank(startDate)){
            sqlcon=sqlcon+" and updateDate<='"+endDate+"'";
        }
        if(StringUtils.isNotBlank(endDate)&&StringUtils.isNotBlank(startDate)){
            sqlcon=sqlcon+" and (updateDate>='"+startDate+"' and updateDate<='"+endDate+"')";
        }
        if(StringUtils.isNotBlank(status)){
            sqlcon=sqlcon+" and status="+status;
        }
        String sql="select * from qrytask "+sqlcon+" limit " + limit * pageNow + "," + limit + "";
        String sql2="select count(*) as C from qrytask "+sqlcon;
        long totalCount =0;
        List<Map> list = dao.query(sql);
        List<Map> listCount=dao.query(sql2);
        totalCount = Long.parseLong(listCount.get(0).get("C").toString());

        ResultDTO dto = new ResultDTO();
        dto.setRecords(list);
        dto.setTotalCount(totalCount);
        dto.setHasPaged(true);
        dto.setSuccess(true);
        return dto;
    }

    /**
     * 分页查询
     *
     * @param vs
     * @return
     */
    public ResultDTO loadData(ValueSet vs) {
        int start = Integer.parseInt(vs.getString("start"));
        int limit = Integer.parseInt(vs.getString("limit"));
        String taskId = vs.getString("taskId");
        ResultDTO dto = null;
        try {
            String mysql_select = "select totalCount,tempTable,querySql from qrytask where id=" + taskId;
            List<Map> resultList = dao.query(mysql_select);
            if (resultList.size() > 0) {
                String totalCount = resultList.get(0).get("totalCount").toString();
                String tableName = resultList.get(0).get("tempTable").toString();
                String querySql = resultList.get(0).get("querySql").toString();
                int end = 0;
                int page = 0;
                boolean flag = false;
                if (start == 0) {
                    end = start + limit;
                } else {
                    page = start / limit + 1;
                    end = page * limit;
                }
                String tempsql = "select * from " + tableName + " limit " + end;
                List<Map> list = HiveJdbc.queryByPage(tempsql,tableName, start, limit);
                dto = new ResultDTO();
                dto.setRecords(list);
                dto.setTotalCount(Long.parseLong(totalCount));
                dto.setHasPaged(true);
                dto.setSuccess(true);
                dto.setExtInfo(Collections.singletonMap("gid", querySql));
            }
            return dto;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }

    public ResultDTO queryExport(ValueSet vs) {
//        String exportType = "0";
        List<Map> exlist = null;
        ResultDTO dto = null;
        try {
            String taskId = vs.getString("taskId");
            String mysql_select = "select totalCount,tempTable,querySql,cloumnsSql from qrytask where id=" + taskId;
            List<Map> resultList = dao.query(mysql_select);
            if (resultList.size() > 0) {
                String totalCount = resultList.get(0).get("totalCount").toString();
                String tableName = resultList.get(0).get("tempTable").toString();
                String querySql = resultList.get(0).get("querySql").toString();
                String columns_sql = resultList.get(0).get("cloumnsSql").toString();
//                if (exportType.equals("0")) {
                Configuration conf = Connection.getInstance().getConf();
                String path = HADOOP_FILE_PATH + tableName;
                String[] columns = columns_sql.split(",");
                exlist = HDFSUtil.readFiles(conf, path, columns, 0, Integer.parseInt(totalCount));
//                } else if (exportType.equals("1")) {
//                    exlist = HiveJdbc.query("select * from " + tableName);
//                }

                dto = new ResultDTO();
                dto.setRecords(exlist);
                dto.setHasPaged(false);
                dto.setSuccess(true);
                Map<String, String> other = new HashMap<String, String>();
                other.put("tableName", tableName);
                dto.setExtInfo(other);
            }
            return dto;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }

}
