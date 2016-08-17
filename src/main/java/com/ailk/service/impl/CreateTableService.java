package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.HiveJdbc;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CreateTableService {
    public static final Log LOG = LogFactory.getLog(CreateTableService.class);

    /**
     * 创建表
     *
     * @return
     */
    public Map createHiveTable(ValueSet vs) {
        try {
            String org_field_tableName = vs.getString("org_field_tableName");
            String split_str = vs.getString("split_str");
            System.out.println(vs);
            String cloumns = "";//列名字段
            Set<Map> set = vs.entrySet();
            Iterator item = set.iterator();
            Map columns_Str = new HashMap();//字段columns的Map集合
            List part = new ArrayList();//选中的分区字段数字集合
            while (item.hasNext()) {
                Map.Entry map = (Map.Entry) item.next();
                String key = (String) map.getKey();
                String value = (String) map.getValue();
                if (key.indexOf("_colName_") > 0) {
                    cloumns = cloumns + value + " string,";
                    columns_Str.put(key, value);
                }
                if (key.indexOf("_partName_") > 0) {
                    String index = key.substring(key.lastIndexOf("_") + 1, key.length());
//                    System.out.println("index:="+index);
                    part.add(index);
                }
            }

            String part_str = "";//分区字段的字符串
            String columns_dif = "";//有分区字段选中时去掉重复的字段
            Set<Map> set2 = columns_Str.entrySet();
            Iterator item2 = set2.iterator();
            while (item2.hasNext()) {
                Map.Entry map = (Map.Entry) item2.next();
                String key = (String) map.getKey();
                String value = (String) map.getValue();
                String a = key.substring(key.lastIndexOf("_") + 1, key.length());
                //根据数字找到对应的分区字段
                for (int i = 0; i < part.size(); i++) {
                    if (a.equals(part.get(i))) {
                        part_str = part_str + value + " string,";
                    }
                }
                if (!part.contains(a)) {
                    columns_dif = columns_dif + value + " string,";
                }
            }
            cloumns = cloumns.substring(0, cloumns.length() - 1);

            if (StringUtils.isNotBlank(columns_dif)) {
                columns_dif = columns_dif.substring(0, columns_dif.length() - 1);
            }
            if (StringUtils.isNotBlank(part_str)) {
                part_str = part_str.substring(0, part_str.length() - 1);
                //分区的字段不能和表的字段重复
                cloumns = columns_dif;
                System.out.println("columns_dif：=" + cloumns);
            }

            //sql组装
            StringBuffer sb = new StringBuffer();
            sb.append("create table " + org_field_tableName + "(" + cloumns + ")");
            if (StringUtils.isNotBlank(part_str)) {
                sb.append("PARTITIONED BY (" + part_str + ")");
            }
            sb.append("row format delimited fields terminated by '" + split_str + "'");
            String sql = sb.toString();
            System.out.println("sql:=" + sql);
            Map map = HiveJdbc.createHiveTable(sql);
//            Map map=new HashMap();
            boolean flag = (Boolean) map.get("flag");
            if (flag) {
                LOG.info("createHiveTable service success");
            } else {
                LOG.info("createHiveTable service failure");
            }
            return map;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }


    public Map createHiveTable2(ValueSet vs) {
        String table_schem = vs.getString("table_schem");
        String table_name = vs.getString("table_name");
//        System.out.println(vs);
        String cloumns = "";
        String cloumnsMapping = "";
        CommonService coms = new CommonService();
        String rowKey = "";
        Map map = null;
        try {
            List<Map<String, Object>> list = coms.getTableAllColumns(table_schem, table_name);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> colMap = list.get(i);
                String column = (String) colMap.get("COLUMN_NAME");
                String COLUMN_FAMILY = (String) colMap.get("COLUMN_FAMILY");
                if ("null".equals(COLUMN_FAMILY) || COLUMN_FAMILY == null) {
                    rowKey = "key";
                } else {
                    cloumns = cloumns + column + " string,";
                    cloumnsMapping = cloumnsMapping + "F:" + column + ",";
                }
            }
            cloumns = cloumns.substring(0, cloumns.length() - 1);
            cloumnsMapping = cloumnsMapping.substring(0, cloumnsMapping.length() - 1);
//            System.out.println("cloumns:=" + cloumns);
            cloumns = rowKey + " string," + cloumns;
            cloumnsMapping = ":" + rowKey + "," + cloumnsMapping;
            //sql组装
            StringBuffer sb = new StringBuffer();
            sb.append(" CREATE EXTERNAL TABLE " + table_name + "(" + cloumns + ")");
            sb.append(" STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'");
            sb.append(" WITH SERDEPROPERTIES ('hbase.columns.mapping' ='" + cloumnsMapping + "')");
            sb.append(" TBLPROPERTIES ('hbase.table.name' = '" + table_name + "')");
            LOG.info("sql:=" + sb.toString());
//            Map map=new HashMap();
            map = HiveJdbc.createHiveTable(sb.toString());
            boolean flag = (Boolean) map.get("flag");
            if (flag) {
                LOG.info("createHiveTable service success");
            } else {
                LOG.info("createHiveTable service failure");
            }
        } catch (Exception e) {

        }
        return map;
    }


}
