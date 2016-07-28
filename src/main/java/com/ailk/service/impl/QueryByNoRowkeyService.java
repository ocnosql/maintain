package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.dao.HiveJdbc;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;
import com.ailk.util.DateUtil;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QueryByNoRowkeyService implements IQueryService {

    public static final Log LOG = LogFactory.getLog(QueryByNoRowkeyService.class);
    public static final String TEMP_TABLE_PREFIX = "temp_";
    BaseDao dao = new BaseDao("ocnosql");

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
            boolean flag = HiveJdbc.queryCreateTable(sql, tableName);
            String insert_task = "insert into qrytask(status,createDate,updateDate,totalCount,tempTable,querySql) values(?,now(),now(),?,?,?);";
            Object[] a = new Object[4];
            a[0] = 0;
            a[1] = String.valueOf(0);
            a[2] = tableName;
            a[3] = sql;
            dao.executeUpdate(insert_task, a);
            LOG.info("insert qrytask success");
//            boolean flag=false;
            if (flag) {
                LOG.info("create table success");
                totalCount = HiveJdbc.queryTotalCount(tableName);
                String update_task = "update qrytask set status=?,updateDate=now(),timeDiff=TIMESTAMPDIFF(SECOND,createDate,updateDate),totalCount=? where tempTable=?;";
                Object[] a2 = new Object[3];
                a2[0] = 1;
                a2[1] = String.valueOf(totalCount);
                a2[2] = tableName;
                dao.executeUpdate(update_task, a2);
                LOG.info("update qrytask success");
            } else {
                LOG.info("create table failure");
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
        int start = Integer.parseInt(vs.getString("start"));
        int limit = Integer.parseInt(vs.getString("limit"));
        long totalCount = dao.queryTotalCount2("qrytask");
        long pageNow = start == 0 ? 0 : start / limit;
        List<Map> list = dao.query("select * from qrytask limit " + limit * pageNow + "," + limit + "");
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
                List<Map> list = HiveJdbc.queryByPage(tempsql, start, limit);
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
}
