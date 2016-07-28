package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;

import java.util.List;
import java.util.Map;

/**
 * Created by scj on 2016/7/14.
 * 主键批量导出查询服务
 */
public class QueryByRowkeyBatchOutputService implements IQueryService {

    @Override
    public ResultDTO loadData(ValueSet vs) {
        BaseDao dao = new BaseDao("ocnosql");
        try {
            StringBuffer sqlCountBuffer = new StringBuffer("select count(*) C from task_status");
            List<Map> result =  dao.query(sqlCountBuffer.toString());
            long count = Long.parseLong(result.get(0).get("C").toString());

            StringBuffer sqlBuffer = new StringBuffer("select * from task_status order by create_time desc");
            List<Map> list = dao.queryByPageMysql(sqlBuffer.toString(),vs.getInt("start"),vs.getInt("limit"));
            //将数据库中的部分数据修改后展现
            for(int i=0;list!=null&&i<list.size();i++){
                Map map = list.get(i);
                String finish_flag = String.valueOf(map.get("finish_flag"));
                if("-1".equals(finish_flag)){
                    map.put("finish_flag","失败");
                }else if("1".equals(finish_flag)){
                    map.put("finish_flag","成功");
                }else{
                    map.put("finish_flag","进行中");
                }
                list.set(i,map);
            }
            ResultDTO dto = new ResultDTO();
            dto.setRecords(list);
            dto.setTotalCount(count);
            dto.setHasPaged(true);
            dto.setSuccess(true);
            dto.setExtInfo(null);
            return dto;
        } catch (Throwable e) {
            throw new AppRuntimeException(e);
        }
    }

}
