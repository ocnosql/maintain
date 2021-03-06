package com.ailk.dao;

import com.ailk.model.ImportConfig;
import com.ailk.model.ImportLog;

import java.util.List;
import java.util.Map;

/**
 * Created by wsh on 2016/8/9.
 */
public class ImportDao extends BaseDao{
    public ImportDao(String poolName) {
        super(poolName);
    }

    public void insertConfig(ImportConfig ic) {
        String sql = "insert importconfig(cname, separatorx, inputPath, rowkey, generator, outputPath, algocolumn, schemax, tablex, callback) values(?,?,?,?,?,?,?,?,?,?)";
        String[] params = {ic.getCname(), ic.getSeparator(), ic.getInputPath(), ic.getRowkey(), ic.getGenerator(), ic.getOutputPath(), ic.getAlgocolumn(), ic.getSchema(), ic.getTable(), ic.getCallback()};
        executeUpdate(sql, params);
    }

    public void updateConfig(ImportConfig ic) {
        String sql = "update importconfig set cname = ?, separatorx = ?, inputPath = ?, rowkey = ?, generator = ?, outputPath = ?, algocolumn = ?, schemax = ?, tablex = ? , callback = ? where id = ?";
        Object[] params = {ic.getCname(), ic.getSeparator(), ic.getInputPath(), ic.getRowkey(), ic.getGenerator(), ic.getOutputPath(), ic.getAlgocolumn(), ic.getSchema(), ic.getTable(), ic.getCallback(), ic.getId()};
        super.executeUpdate(sql, params);
    }

    public void deleteConfig(int id) {
        String sql = "delete from importconfig where id = ?";
        Object[] params = {id};
        super.executeUpdate(sql, params);
    }

    public List<Map> queryConfigList(String schema, String table) {
        String sql = "select * from importconfig where schemax = '" + schema + "' and tablex = '" + table + "'";
        List<Map> datas =  super.query(sql);
        return datas;
    }

    public List<Map> queryConfigById(int id) {
        String sql = "select * from importconfig where id = '" + id + "'";
        return query(sql);
    }

    public List<Map> queryConfigBySchemaAndTable(String schema, String table) {
        String sql = "select cname, id from importconfig where schemax = '" + schema + "' and tablex = '" + table + "'";
        return query(sql);
    }

    public List<Map> queryIcBySchemaAndTable(String schema, String table, String cname) {
        String sql = "select * from importconfig where schemax = '" + schema + "' and tablex = '" + table + "' and cname = '" + cname + "'";
        System.out.println("sql is : " + sql);
        return query(sql);
    }

    public void log(ImportLog importLog) {
        String sql = "insert into importLog(id, tablex, schemax, status, final_status, importdate, input, output, mr_status, completebulkload_status) values(?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {
                importLog.getId(),
                importLog.getTablex(),
                importLog.getSchemax(),
                importLog.getStatus(),
                importLog.getFinalStatus(),
                importLog.getImportdate(),
                importLog.getInput(),
                importLog.getOutput(),
                importLog.getMrStatus(),
                importLog.getCompleteBulkloadStatus()
        };
        super.executeUpdate(sql, params);
    }

    public void updateLogStatus(String status, String id) {
        String sql = "update importLog set status = ? where id = ?";
        Object[] params = {status, id};
        super.executeUpdate(sql, params);
    }

    public List<Map> queryImportHistory(String schema, String table) {
        StringBuffer sql = new StringBuffer("select * from importLog where schemax = '" + schema + "'");
        if(table != null && !"".equals(table)) {
            sql.append(" and tablex = '" + table +"'");
        }
        sql.append(" order by importdate desc");
        return super.query(sql.toString());
    }

    public void updateCounter(String id, String status, String final_status, String total , String success_total, String fail_total) {
        String sql = "update importLog set  total = ? , success_total = ? , fail_total = ? , status = ? , final_status =? where id = ?";
        Object[] params = {total, success_total, fail_total, status, final_status, id};
        super.executeUpdate(sql, params);
    }

    public void updateCounter(String id, String status, String final_status, String mr_status, String completebkl_status, String total , String success_total, String fail_total) {
        String sql = "update importLog set  total = ? , success_total = ? , fail_total = ? , status = ? , final_status =?, mr_status=?, completebulkload_status = ? where id = ?";
        Object[] params = {total, success_total, fail_total, status, final_status, mr_status, completebkl_status, id};
        super.executeUpdate(sql, params);
    }

    public void updateMRJobStatus(String id, String jobId, String mr_phase_status) {
        String sql = "update importLog set job_id = ?, mr_status = ?  where id = ?";
        Object[] params = {jobId, mr_phase_status, id};
        super.executeUpdate(sql, params);
    }

    public void updateMRJobStatus(String id, String mr_phase_status) {
        String sql = "update importLog set mr_status = ?  where id = ?";
        Object[] params = {mr_phase_status, id};
        super.executeUpdate(sql, params);
    }

    public void updateCompletebulkloadStatus(String id, String completebulkload_phase_status) {
        String sql = "update importLog set completebulkload_status = ?  where id = ?";
        Object[] params = {completebulkload_phase_status, id};
        super.executeUpdate(sql, params);
    }
}