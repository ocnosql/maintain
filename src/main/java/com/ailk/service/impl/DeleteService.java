package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.dao.DeleteDao;
import com.ailk.dao.HiveDao;
import com.ailk.dao.HiveJdbc;
import com.ailk.model.DataInfo;
import com.ailk.model.DeleteLog;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.common.util.DateUtil;
import com.ailk.pool.HiveConnection;
import com.ailk.service.IQueryService;
import com.ailk.util.Cache;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeleteService implements IQueryService {
    BaseDao dao = new BaseDao("ocnosql");
    @Override
    public ResultDTO loadData(ValueSet vs) {
        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);
        String sql = vs.getString("sql");
        int start = Integer.valueOf(vs.getString("start"));
        int limit = Integer.valueOf(vs.getString("limit"));
        List<Map> list = null;

        if(info == null) {
            info = new DataInfo();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        info.setUuid(sdf.format(new Date()));
        Cache.put(info.getUuid(), info);

        list = HiveConnection.queryPage(sql, start, limit);
        ResultDTO dto = new ResultDTO();
        dto.setRecords(list);

        dto.setTotalCount(10000);
        dto.setHasPaged(true);
        dto.setSuccess(true);
        dto.setExtInfo(Collections.singletonMap("gid", info.getUuid()));
        return dto;
    }

    public ResultDTO queryData(ValueSet vs) {
        int start = Integer.parseInt(vs.getString("start"));
        int limit = Integer.parseInt(vs.getString("limit"));
        String id = vs.getString("id");
        ResultDTO dto = dto = new ResultDTO();
        try {
            String sql = "select * from deleteLog where id = '" + id + "'";
            List<Map> resultList = dao.query(sql);
            if (resultList.size() > 0) {
                Object obj = resultList.get(0).get("bup");
                if(obj == null || "".equals(obj)) {
                    throw new Exception("此次查询异常!");
                }
                String tableName = String.valueOf(obj);
                long qtotal = (resultList.get(0).get("qtotal") == null)? 0 : Long.parseLong(String.valueOf(resultList.get(0).get("qtotal")));
                long end = 0;
                int page = 0;

                if(qtotal == 0) {
                    end = 0;
                } else {
                    if (start == 0) {
                        end = start + limit;
                    } else {
                        page = start / limit + 1;
                        end = page * limit;
                    }
                    if(end > qtotal) end = qtotal;
                }
                String tempsql = "select * from " + tableName + " limit " + end;
                List<Map> list = HiveJdbc.queryByPage(tempsql,tableName, start, limit);
                dto.setRecords(list);
                dto.setTotalCount(qtotal);
                dto.setHasPaged(true);
                dto.setSuccess(true);
            }
            return dto;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }

    public void submitQuery(ValueSet vs) {
        String id = DateUtil.dateToString(new Date(), "yyyyMMddHHmmssSSS");
        String sql = (String)vs.get("sql");
        String hive_table = "query_delete_" + id;

        DeleteDao deleteDao = new DeleteDao("ocnosql");
        DeleteLog log = new DeleteLog();
        String table = (String)vs.get("table");
        String table_space = String.valueOf(vs.get("table_space"));

        log.setId(id);
        log.setSchemax(table_space);
        log.setTablex(table);
        log.setBup(hive_table);
        log.setQtotal(String.valueOf(0));
        log.setQdate(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        log.setQstatus("查询中");
        log.setSqlx(sql);
        deleteDao.log(log);
        backup(sql, hive_table);
        HiveDao hiveDao = new HiveDao();
        long count = hiveDao.hiveGetRowNums(hive_table);
        deleteDao.updateQInfo(String.valueOf(count), "查询成功", id);
    }

    public void backup(String sql, String table)  {
        queryCreateTable(sql, table);
    }

    public static void deleteRow(String tablename, String rowkey) {
        try {
            Configuration conf = HBaseConfiguration.create();
            HTable table = new HTable(conf, tablename);
            Delete d1 = new Delete(rowkey.getBytes());
            table.delete(d1);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryCreateTable(String sql, String table){
        String temp_sql = "CREATE TABLE " + table + " row format delimited fields terminated by ',' as " + sql;
        java.sql.Connection conn = null;
        Statement stmt = null;
        try{
            conn = HiveConnection.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(temp_sql);
        }catch(Exception e){
            throw new AppRuntimeException(e);
        }finally{
            if(stmt != null){
                try{
                    stmt.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String[]> queryHistory(String schema, String table) {
        DeleteDao deleteDao = new DeleteDao("ocnosql");
        List<Map> list = deleteDao.queryHistory(schema, table);
        List<String[]> datas = new ArrayList<String[]>();
        if(list != null) {
            for(Map map : list) {
                HashMap m = (HashMap) map;
                String[] data = {String.valueOf(
                        m.get("id")), String.valueOf(m.get("schemax")),String.valueOf(m.get("tablex")), String.valueOf(m.get("sqlx")),
                        String.valueOf(m.get("qdate")), String.valueOf(m.get("qstatus")),String.valueOf(m.get("qtotal")),
                        String.valueOf(m.get("ddate")), String.valueOf(m.get("dstatus")),String.valueOf(m.get("dtotal")),
                        String.valueOf(m.get("bup"))
                };
                datas.add(data);
            }
        }
        return datas;
    }

    public void deleteDate(ValueSet vs) {
        String id = (String)vs.get("id");

        String sql = "select * from deleteLog where id = '" + id + "'";
        List<Map> resultList = dao.query(sql);
        if (resultList.size() > 0) {
            Object obj = resultList.get(0).get("bup");
            Object otable = resultList.get(0).get("tablex");
            if(obj == null || "".equals(obj)) {
                //throw new Exception("此次查询异常!");
                return;
            }
            String hive_tablename = String.valueOf(obj);
            String table = String.valueOf(otable);

            String date = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
            DeleteDao deleteDao = new DeleteDao("ocnosql");
            deleteDao.updateDInfo("删除中", date, id);

            if(hive_tablename != null){
                new Thread(new DeleteRunner(table, hive_tablename, id)).start();
            }
        }
    }

    private class DeleteRunner implements  Runnable {

        private String table;
        private String hive_tablename;
        private String id;

        public DeleteRunner(String table, String hive_tablename, String id) {
            this.table = table;
            this.hive_tablename = hive_tablename;
            this.id = id;
        }

        @Override
        public void run() {
            String[] args = {table, hive_tablename, id};
            DeleteDao deleteDao = new DeleteDao("ocnosql");
            try {
                DeleteMR.main(args);
                //deleteDao.updateDstatus("删除成功", "0" , id);
            } catch (Exception e) {
                deleteDao.updateDstatus("删除失败", "0",  id);
                e.printStackTrace();
            }
        }
    }
}