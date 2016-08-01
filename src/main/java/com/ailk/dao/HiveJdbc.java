package com.ailk.dao;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.util.DateUtil;
import com.ailk.util.PropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lihui on 2016/7/13.
 */
public class HiveJdbc {
    //    public static final String jdbc_driver = "org.apache.hive.jdbc.HiveDriver";
//    public static final String jdbc_url = "jdbc:hive2://10.15.46.16:10001";
//    public static final String jdbc_username = "hadoop";
//    public static final String jdbc_password = "cloud@2016";
    public static final String jdbc_driver = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.driverClass");
    public static final String jdbc_url = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.url");
    public static final String jdbc_username = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.username");
    public static final String jdbc_password = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.passwd");

    private static Log log = LogFactory.getLog(HiveJdbc.class);

    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName(jdbc_driver);
            c = DriverManager.getConnection(jdbc_url, jdbc_username, jdbc_password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static List<Map> query(String sql) {
        log.info("execute query : " + sql);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map> recordList = new ArrayList<Map>();
        try {
            conn = HiveJdbc.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                int columnCount = meta.getColumnCount();
                Map record = new LinkedHashMap();
                for (int i = 1; i <= columnCount; i++) {
                    if (rs.getObject(i) instanceof java.util.Date) {
                        record.put(meta.getColumnName(i), DateUtil.format(rs.getTimestamp(i)));
                    } else {
                        record.put(meta.getColumnName(i), rs.getObject(i));
                    }
                }
                recordList.add(record);
            }
        } catch (Exception e) {
            throw new AppRuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        log.debug("execute query complete! return " + recordList.size() + " records");
        return recordList;
    }

    /**
     * @param sql
     * @param start
     * @param limit
     * @return
     */
    public static List<Map> queryByPage(String sql,String tableName,int start, int limit) {
        log.info("execute queryByPage : " + sql);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map> recordList = new ArrayList<Map>();
        try {
            conn = HiveJdbc.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int count = 0;
            while (rs.next()) {
                int columnCount = meta.getColumnCount();
                Map record = new LinkedHashMap();
                count++;
                if (count > start) {
                    for (int i = 1; i <= columnCount; i++) {
                        if (rs.getObject(i) instanceof java.util.Date) {
                            record.put(meta.getColumnName(i).replace(tableName+".",""), DateUtil.format(rs.getTimestamp(i)));
                        } else {
                            record.put(meta.getColumnName(i).replace(tableName+".",""), rs.getObject(i));
                        }
                    }
                    recordList.add(record);
                }
            }
        } catch (Exception e) {
            throw new AppRuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        log.debug("execute query complete! return " + recordList.size() + " records");
        return recordList;
    }

    public static long queryTotalCount(String sql) {
        sql = "select count(1) C from " + sql;
        long totalCount = 0;
        List<Map> result = query(sql);
        totalCount = Long.parseLong(result.get(0).get("c").toString());
        return totalCount;
    }

    public static boolean queryCreateTable(String sql, String table) throws AppRuntimeException{
        boolean flag = false;
        String temp_sql = "CREATE TABLE " + table + " row format delimited fields terminated by '\t' as " + sql;
        log.info("execute queryCreateTable : " + temp_sql);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = HiveJdbc.getConnection();
            stmt = conn.createStatement();
            int a = stmt.executeUpdate(temp_sql);
            if (a == 0) {
                flag = true;
            }
        } catch (Exception e) {
//            throw new AppRuntimeException(e);
            log.error("execute queryCreateTable Exception:"+e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        return flag;
    }

    public static void test() {
//        List<Map> lis=HiveJdbc.query("select * from qry_ins_user limit 20");
//        System.out.print("ssss:=" + lis.size());
        long a= HiveJdbc.queryTotalCount("temp_20160720145345");
        System.out.println(a);
    }

    public static void main(String[] args) throws Exception {
        String jdbcdriver = "org.apache.hive.jdbc.HiveDriver";
        //jdbc:hive2://wangkai8:10001
        //jdbc:hive2://10.15.46.16:10002/default
        String jdbcurl = "jdbc:hive2://10.15.46.16:10001";
//        try {
//            Class.forName(jdbcdriver);
//        } catch (ClassNotFoundException e) {
//// TODO Auto-generated catch block
//            e.printStackTrace();
//            System.exit(1);
//        }
//        Connection c = DriverManager.getConnection(jdbcurl, "hadoop", "cloud@2016");
        Connection c = HiveJdbc.getConnection();
        Statement st = c.createStatement();
//        String sql = "insert overwrite directory '/hivetest' ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' select * from test";
//        st.executeUpdate(sql);

//        String sql = "select * from qry_ins_user limit 20";
//        ResultSet rs = st.executeQuery(sql);
//        ResultSetMetaData meta = rs.getMetaData();
//        for(int i = 1; i <= meta.getColumnCount(); i++) {
//            System.out.print(meta.getColumnName(i) + "\t");
//        }
//        System.out.println();
//        System.out.println("-------------------------------------------------");
//        while (rs.next()){
//            for(int i = 1; i <= meta.getColumnCount(); i++) {
//                System.out.print(rs.getObject(i) + "\t");
//            }
//            System.out.println();
//        }
        System.out.println("------------222222-------------------------------------");
        test();
    }
}



