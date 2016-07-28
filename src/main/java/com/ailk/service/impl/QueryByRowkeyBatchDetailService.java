package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by scj on 2016/7/14.
 * 主键批量导出查询服务
 */
public class QueryByRowkeyBatchDetailService implements IQueryService {

    @Override
    public ResultDTO loadData(ValueSet vs) {
        Configuration conf = Connection.getInstance().getConf();

        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);
        try {
            if(info==null){
                info = new DataInfo();
                info.setPath(vs.getString("dst_path"));
                info.setColumns(vs.getString("columns_str").split(","));
                info.setUuid(UUID.randomUUID().toString());
                info.setTotalCount(vs.getLong("total_count"));
                Cache.put(info.getUuid(), info);
            }
            List<Map> list = HDFSUtil.readFiles(conf, info.getPath(), info.getColumns(), vs.getInt("start"), vs.getInt("limit"));
            //ResultDTO dto = HDFSUtil.readFiles(conf, info.getPath(), info.getColumns(), vs.getInt("start"), vs.getInt("limit"));
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
