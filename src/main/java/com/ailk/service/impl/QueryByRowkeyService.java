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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wangkai8 on 16/7/8.
 */
public class QueryByRowkeyService implements IQueryService {

    @Override
    public ResultDTO loadData(ValueSet vs) {
        Configuration conf = Connection.getInstance().getConf();

        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);

        try {
            if(info == null) {
                HbaseJdbcHelper help = new PhoenixJdbcHelper();
                ResultSet rs = help.executeQueryRaw(vs.getString("sql"));
                info = HDFSUtil.write(conf, rs, "/rowkey_query");
                Cache.put(info.getUuid(), info);
            }

            List<Map> list = HDFSUtil.read(conf, info.getPath(), info.getColumns(), vs.getInt("start"), vs.getInt("limit"));

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
