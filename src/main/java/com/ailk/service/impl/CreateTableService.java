package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.HiveJdbc;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

import java.util.*;

public class CreateTableService{
    public static final Log LOG = LogFactory.getLog(CreateTableService.class);
    /**
     * 创建表
     *
     * @return
     */
    public Map createHiveTable(ValueSet vs) {
        try {
            String org_field_tableName=vs.getString("org_field_tableName");
            String org_field_partition=vs.getString("org_field_partition");
            String cloumns="";
            Set<Map> set=vs.entrySet();
            Iterator item=set.iterator();
            while (item.hasNext()){
                Map.Entry map=(Map.Entry)item.next();
                String key=(String)map.getKey();
                String value=(String)map.getValue();
                if(key.indexOf("_colName_")>0){
                    cloumns=cloumns+value+" string,";
                }
            }
            cloumns=cloumns.substring(0,cloumns.length()-1);

            String sql="create table "+org_field_tableName+"("+cloumns+")row format delimited fields terminated by '\t'";
            Map map=HiveJdbc.createHiveTable(sql);
            boolean flag=(Boolean)map.get("flag");
            if(flag){
                LOG.info("createHiveTable service success");
            }else{
                LOG.info("createHiveTable service failure");
            }
            return map;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }


}
