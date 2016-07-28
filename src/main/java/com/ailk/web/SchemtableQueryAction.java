package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.QueryByRowkeyService;
import com.ailk.service.impl.QuerySchemTableService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

/**
 * Created by scj on 2016/7/13.
 */
public class SchemtableQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(SchemtableQueryAction.class);

    public String query(){

        System.out.println("parameter  tablename = " + ServletActionContext.getRequest().getParameter("tablename"));
        System.out.println("parameter  tableschem = " + ServletActionContext.getRequest().getParameter("tableschem"));


        IQueryService service = new QuerySchemTableService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());



        Gson gs = new Gson();

        System.out.println("vs = " + gs.toJson(vs));

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
