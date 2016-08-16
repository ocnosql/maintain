package com.ailk.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by lihui on 2016/7/13.
 */
public class HiveDao {

    private static Log log = LogFactory.getLog(HiveDao.class);
    BaseDao hiveDao = new BaseDao("hivesql");

    public long hiveGetRowNums(String tableName) {
        long totalCount = 0;
        String sql = "select b.PARAM_VALUE from TBLS a LEFT JOIN TABLE_PARAMS b on a.TBL_ID=b.TBL_ID where a.TBL_NAME='" + tableName + "' and b.PARAM_KEY='numRows'";
        List<Map> list = hiveDao.query(sql);
        totalCount = Long.parseLong(list.get(0).get("PARAM_VALUE").toString());
        return totalCount;
    }

    public List<Map> hiveGetColumns(String tableName) {
        String sql = "select c.COLUMN_NAME from COLUMNS_V2 c WHERE c.CD_ID=(select b.CD_ID from TBLS a LEFT JOIN SDS b on a.SD_ID=b.SD_ID where TBL_NAME='" + tableName + "');";
        List<Map> list = hiveDao.query(sql);
        return list;
    }


}



