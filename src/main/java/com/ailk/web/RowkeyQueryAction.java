package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.QueryByRowkeyService;
import com.ailk.util.GeneratorMD5;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

/**
 * Created by wangkai8 on 16/7/8.
 */
public class RowkeyQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(RowkeyQueryAction.class);

    public String query(){
        IQueryService service = new QueryByRowkeyService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
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

    public String getmd5(){
        String rowkey = ServletActionContext.getRequest().getParameter("phoneNum");
        RowKeyGenerator generator = new GeneratorMD5();
        try{
            if(generator!=null){
                rowkey = (String) generator.generate(rowkey);
            }
            this.setAjaxStr("{\"success\": true, \"rowkey\": \""+ rowkey +"\"}");
        }catch(Exception e){
            e.printStackTrace();
            this.setAjaxStr("{\"success\": false}");
        }
        return AJAXRTN;
    }
}
