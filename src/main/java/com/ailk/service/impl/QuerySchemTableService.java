package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.model.DataInfo;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.service.IQueryService;
import com.ailk.util.Cache;
import com.ailk.util.HDFSUtil;
import org.apache.hadoop.conf.Configuration;

import java.sql.ResultSet;
import java.util.*;

/**
 * Created by scj on 2016/7/13.
 */
public class QuerySchemTableService implements IQueryService {

    @Override
    public ResultDTO loadData(ValueSet vs) {

        Configuration conf = Connection.getInstance().getConf();

        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);

        //String sql = "select * from QRY_INS_USER limit 100";
//        String sql = "select distinct column_name,data_type,nullable from system.catalog where table_name='"+vs.get("tablename")+"' and column_name is not null";
        String sql = "select * from system.catalog where table_name='"+vs.get("tablename")+"' and column_name is not null";
        try {
            if(info == null) {
                HbaseJdbcHelper help = new PhoenixJdbcHelper();
                //ResultSet rs = help.executeQueryRaw(vs.getString("sql"));
                ResultSet rs = help.executeQueryRaw(sql);
                info = HDFSUtil.write(conf, rs, "/schemtable_query");
                Cache.put(info.getUuid(), info);
            }

            List<Map> list = HDFSUtil.read(conf, info.getPath(), info.getColumns(), vs.getInt("start"), vs.getInt("limit"));
            //将数据库中的部分数据修改后展现
            for(int i=0;list!=null&&i<list.size();i++){
                Map map = list.get(i);
                String DATA_TYPE = String.valueOf(map.get("DATA_TYPE"));
                if("12".equals(DATA_TYPE)){
                    map.put("DATA_TYPE","varchar");
                }
                list.set(i,map);
            }

            ResultDTO dto = new ResultDTO();
            dto.setRecords(list);
            dto.setTotalCount(info.getTotalCount());
            dto.setHasPaged(true);
            dto.setSuccess(true);
            dto.setExtInfo(Collections.singletonMap("gid", info.getUuid()));
            return dto;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }



    }
}
