package com.ailk.dao;

import com.ailk.model.DeleteLog;

import java.util.List;
import java.util.Map;

/**
 * Created by wsh on 2016/8/12.
 */
public class DeleteDao extends BaseDao {

    public DeleteDao(String poolName) {
        super(poolName);
    }

    public List<Map> queryHistory(String schema, String table) {
        if(schema == null || "".equals(schema)) {
            schema = "DEFAULT";
        }
        StringBuffer sql = new StringBuffer("select * from deleteLog where schemax = '" + schema + "'");
        if(table != null && !"".equals(table)) {
            sql.append("and tablex = '" + table + "'");
        }
        sql.append(" order by qdate desc");
        List<Map> datas =  super.query(sql.toString());
        return datas;
    }

    public void log(DeleteLog dl) {
        String sql = "insert into deleteLog(id, schemax, tablex, qdate, sqlx, qstatus, qtotal, bup) values(?,?,?,?,?,?,?, ?)";
        String[] params = {dl.getId(), dl.getSchemax(), dl.getTablex(), dl.getQdate(), dl.getSqlx(), dl.getQstatus(), dl.getQtotal(), dl.getBup()};
        executeUpdate(sql, params);
    }

    public void updateQInfo(String qtotal, String status, String id) {
        String sql = "update deleteLog set qstatus = ?, qtotal = ? where id = ?";
        String[] params = {status, qtotal, id};
        executeUpdate(sql, params);
    }

    public void updateDInfo(String status, String ddate, String id) {
        String sql = "update deleteLog set dstatus = ?,  ddate = ? where id = ?";
        String[] params = {status, ddate, id};
        executeUpdate(sql, params);
    }

    public void updateDstatus(String status, String dtotal, String id) {
        String sql = "update deleteLog set dstatus = ? , dtotal = ? where id = ?";
        String[] params = {status, dtotal, id};
        executeUpdate(sql, params);
    }
}