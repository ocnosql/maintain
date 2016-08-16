package com.ailk.dao;

import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by wsh on 2016/8/11.
 */
public class CommonDao {

    public List<Map<String, Object>> queryColumnsByTable(String schema, String table) throws SQLException{
        String sql = "select COLUMN_NAME from system.catalog where table_name = '" + table +"' and COLUMN_NAME is not null";
        if(schema != null && "DEFAULT".equals(schema)) {
            sql = sql + " and table_schem is null";
        } else {
            sql = sql + " and table_schem = '" + schema +"'";
        }
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        List<Map<String, Object>> results = help.executeQuery(sql);
        return results;
    }

    public List<Map<String, Object>> queryAllColumnsByTable(String schema, String table) throws SQLException{
        String sql = "select COLUMN_NAME, COLUMN_FAMILY from system.catalog where table_name = '" + table +"' ";
        if(schema != null && "DEFAULT".equals(schema)) {
            sql = sql + " and table_schem is null";
        } else {
            sql = sql + " and table_schem = '" + schema +"'";
        }
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        List<Map<String, Object>> results = help.executeQuery(sql);
        return results;
    }
}
