package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.QueryByRowkeyBatchDetailService;
import com.ailk.service.impl.QueryByRowkeyBatchOutputService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

;


/**
 * Created by scj on 2016/7/14.
 */
public class RowkeyBatchDeatilQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(RowkeyBatchDeatilQueryAction.class);

    public String query(){
        IQueryService service = new QueryByRowkeyBatchDetailService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        LOG.info(gs.toJson(vs));
        BaseDao dao = new BaseDao("ocnosql");
        try{
            long startTime = System.currentTimeMillis();
            ResultDTO dto = service.loadData(vs);
            long endTime = System.currentTimeMillis();
            LOG.info("query action complete! token: " + (endTime - startTime) + "ms");
            JsonResult result;
            if(dto.isSuccess()){
                if(!dto.isHasPaged()) {
                    result = ResultBuild.buildResult(dto.getRecords(), Integer.parseInt(start), Integer.parseInt(limit));
                }else{
                    result = ResultBuild.buildResult(dto.getRecords(), dto.getTotalCount());
                }
                if(dto.getExtInfo() != null){
                    result.setExtInfo(dto.getExtInfo());
                }
                this.setAjaxStr(gs.toJson(result));
            }else{
                this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(dto.getMessage())));
            }
            long endTime2 = System.currentTimeMillis();
            LOG.info("convert records to json token: " + (endTime2 - endTime) + "ms");
        } catch(Throwable ex){
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

}
