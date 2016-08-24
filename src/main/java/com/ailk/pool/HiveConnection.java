package com.ailk.pool;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.util.DateUtil;
import com.ailk.util.PropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by wsh on 2016/7/19.
 */

    public class HiveConnection {
        private static Log log = LogFactory.getLog(HiveConnection.class);
        /*public static final String jdbc_driver="org.apache.hive.jdbc.HiveDriver";
        public static final String jdbc_url="jdbc:hive2://10.15.46.16:10001";
        public static final String jdbc_username="hadoop";
        public static final String jdbc_password="cloud@2016"; */
        public static final String jdbc_driver = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.driverClass");
        public static final String jdbc_url = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.url");
        public static final String jdbc_username = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.username");
        public static final String jdbc_password = PropertiesUtil.getProperty("runtime.properties", "hiveJdbc.passwd");

        public static Connection getConnection() {
            Connection connecion = null;
            try {
                Class.forName(jdbc_driver);
                connecion = DriverManager.getConnection(jdbc_url, jdbc_username, jdbc_password);
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }catch (SQLException exception) {
                exception.printStackTrace();
            }
            return connecion;
        }

    public static List<Map> query(String sql){
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            List<Map> recordList = new ArrayList<Map>();
            try{
                conn = HiveConnection.getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                ResultSetMetaData meta = rs.getMetaData();
                while(rs.next()){
                    int columnCount = meta.getColumnCount();
                    Map record = new LinkedHashMap();
                    for(int i = 1; i <= columnCount; i++){
                        if(rs.getObject(i) instanceof java.util.Date){
                            record.put(meta.getColumnName(i).replace("a.",""), DateUtil.format(rs.getTimestamp(i)));
                        }else{
                            record.put(meta.getColumnName(i).replace("a.",""), rs.getObject(i));
                        }
                    }
                    recordList.add(record);
                }
            }catch(Exception e){
                throw new AppRuntimeException(e);
            }finally{
                if(rs != null){
                    try{
                        rs.close();
                    }catch(Exception e){
                       e.printStackTrace();
                    }
                }
                if(stmt != null){
                    try{
                        stmt.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return recordList;
        }

    public static List<Map> queryPage(String sql, int start, int limit) {
            log.info("execute query : " + sql);
            Connection conn = HiveConnection.getConnection();
            Statement stmt = null;
            ResultSet rs = null;
            List<Map> recordList = new ArrayList<Map>();
            try {
                stmt = conn.createStatement();
                String SQL = "select * from (" + sql + ") a limit " + (start + limit);
                rs = stmt.executeQuery(SQL);
                ResultSetMetaData meta = rs.getMetaData();
                int j = 0;
                while (rs.next()) {
                    if (j < start) {
                        j++;
                        continue;
                    }
                    int columnCount = meta.getColumnCount();
                    Map record = new LinkedHashMap();
                    for (int i = 1; i <= columnCount; i++) {
                        if (rs.getObject(i) instanceof java.util.Date) {
                            record.put(meta.getColumnName(i).replace("a.", ""), DateUtil.format(rs.getTimestamp(i)));
                        } else {
                            record.put(meta.getColumnName(i).replace("a.",""), rs.getObject(i));
                        }
                    }
                    j++;
                    recordList.add(record);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if(rs != null)
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                if(stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
                return recordList;
        }

    public static Map createHiveTable (String sql) throws AppRuntimeException{
        boolean flag = false;
        Connection conn = null;
        Statement stmt = null;
        Map map=new HashMap();
        try {
            conn = HiveConnection.getConnection();
            stmt = conn.createStatement();
            int a = stmt.executeUpdate(sql);
            if (a == 0) {
                flag = true;
                map.put("flag",true);
                map.put("msg","success");
            }
        } catch (Exception e) {
            map.put("flag",false);
            map.put("msg",e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                }
            }
        }
        return map;
    }
}