package com.ailk.service.impl;

import com.ailk.dao.CommonDao;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by wsh on 2016/8/11.
 */
public class CommonService {

    public List<Map<String, Object>> getColumns(String schema, String table) throws SQLException {
        CommonDao commonDao = new CommonDao();
        return commonDao.queryColumnsByTable(schema, table);
    }

    public List<Map<String, Object>> getTableAllColumns(String schema, String table) throws SQLException {
        CommonDao commonDao = new CommonDao();
        return commonDao.queryAllColumnsByTable(schema, table);
    }

    public Long getTotal(String sql) throws SQLException {
        String sqlx = "select count(*) as TOTAL from (" + sql + ")";
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        List<Map<String, Object>> results = help.executeQuery(sqlx);
        Map<String, Object> map = results.get(0);
        String total = String.valueOf(map.get("TOTAL"));
        return Long.parseLong(total);
    }

    public static void main(String[] args) {
        CommonService commonAction = new CommonService();
        Long total = null;
        try {
            total = commonAction.getTotal("select * from qry_ins_user");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("totalx is " + total);
    }
}
