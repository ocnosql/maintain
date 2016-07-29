package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.DataInfo;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.service.IQueryService;
import com.ailk.util.Cache;
import com.ailk.util.HDFSUtil;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by scj on 2016/7/14.
 * 主键导出查询服务
 */
public class QueryByRowkeyOutputService implements IQueryService {

    public static final Log LOG = LogFactory.getLog(QueryByRowkeyOutputService.class);

    @Override
    public ResultDTO loadData(ValueSet vs) {
        Configuration conf = Connection.getInstance().getConf();

        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);

        try {
            if(info == null) {
                HbaseJdbcHelper help = new PhoenixJdbcHelper();
                String rowkey = String.valueOf(vs.get("phonenum"));
                RowKeyGenerator generator = new MD5RowKeyGenerator();
                if(generator!=null){
                    rowkey = (String) generator.generate(rowkey);
                }

                //sql 需要进行拼接
                StringBuffer sqlBuffer = new StringBuffer();
                sqlBuffer.append("select * from ");
                if(!"DEFAULT".equals(vs.get("table_schem"))){
                    sqlBuffer.append(vs.get("table_schem")).append(".");
                }
                sqlBuffer.append(vs.get("table_name")).append(" ");
                if(StringUtils.isNotEmpty(vs.get("sql")+"")){
                    //因为条件部分很可能带有limit，group by...等  需要将键值的部分插入到where之前
                    StringBuffer sqlwhereBuffer =  new StringBuffer();
                    sqlwhereBuffer.append(String.valueOf(vs.get("sql")));
                    if(sqlwhereBuffer.indexOf("where")>-1||sqlwhereBuffer.indexOf("WHERE")>-1){
                        //记录下where位置 插入rowkey条件
                        int indexwhere = sqlwhereBuffer.indexOf("where");
                        if(indexwhere<=-1) indexwhere = sqlwhereBuffer.indexOf("WHERE");
                        indexwhere = indexwhere + 5;

                        sqlwhereBuffer.insert(indexwhere," id>='" + rowkey + "' and id<='" + rowkey + "g' and ");
                    }else{
                        sqlBuffer.append(" where id>='").append(rowkey).append("' and id<='").append(rowkey).append("g'");
                    }
                    sqlBuffer.append(" ").append(sqlwhereBuffer);
                }else{
                    sqlBuffer.append(" where id>='").append(rowkey).append("' and id<='").append(rowkey).append("g'");
                }
                LOG.info(sqlBuffer.toString());
                //ResultSet rs = help.executeQueryRaw(vs.getString("sql"));
                ResultSet rs = help.executeQueryRaw(sqlBuffer.toString());
                info = HDFSUtil.write(conf, rs, "/rowkeyoutput_query");
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
