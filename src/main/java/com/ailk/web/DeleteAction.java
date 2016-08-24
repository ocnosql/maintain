package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.dao.HiveDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.DeleteMR;
import com.ailk.service.impl.DeleteService;
import com.ailk.service.impl.ImportService;
import com.ailk.service.impl.QueryByNoRowkeyService;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wsh on 2016/7/13.
 */

public class DeleteAction extends BaseAction  {
    private static Log log = LogFactory.getLog(DeleteAction.class);
    private String tablespace;

    public String query(){
        IQueryService service = new DeleteService();
        ValueSet vs = new ValueSet();

        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        try{
            long startTime = System.currentTimeMillis();
            ResultDTO dto = service.loadData(vs);
            long endTime = System.currentTimeMillis();
            LOG.info("query take: " + (endTime - startTime) + "ms");
            JsonResult result;
            if(dto.isSuccess()){
                if(!dto.isHasPaged()) {
                    Integer start = Integer.valueOf(vs.getString("start"));
                    Integer limit = Integer.valueOf(vs.getString("limit"));
                    result = ResultBuild.buildResult(dto.getRecords(), start, limit);
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
            log.info("convert records to json token: " + (endTime2 - endTime) + "ms");
        } catch(Throwable ex){
            ex.printStackTrace();
            LOG.error("查询异常", ex.getMessage());
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

    public String queryData() {
        DeleteService service = new DeleteService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        try {
            long startTime = System.currentTimeMillis();
            ResultDTO dto = service.queryData(vs);
            long endTime = System.currentTimeMillis();
            LOG.info("query action complete! token: " + (endTime - startTime) + "ms");
            JsonResult result;
            if (dto.isSuccess()) {
                if (!dto.isHasPaged()) {
                    result = ResultBuild.buildResult(dto.getRecords(), Integer.parseInt(start), Integer.parseInt(limit));
                } else {
                    result = ResultBuild.buildResult(dto.getRecords(), dto.getTotalCount());
                }
                if (dto.getExtInfo() != null) {
                    result.setExtInfo(dto.getExtInfo());
                }
                this.setAjaxStr(gs.toJson(result));
            } else {
                this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(dto.getMessage())));
            }
            long endTime2 = System.currentTimeMillis();
        } catch (Throwable ex) {
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

    public String submitQuery() {
        DeleteService service = new DeleteService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        service.submitQuery(vs);
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("success", true);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String delete() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        DeleteService ds = new DeleteService();
        ds.deleteDate(vs);
        Gson gs = new Gson();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("success", true);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public void bindParams(ValueSet vs, HttpServletRequest request){
        Enumeration e = request.getParameterNames();
        while(e.hasMoreElements()){
            String name = (String) e.nextElement();
            vs.put(name, request.getParameter(name));
        }
    }

    public String queryHistory() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        DeleteService deleteService = new DeleteService();
        String schema = String.valueOf(vs.get("table_space"));
        String table = String.valueOf(vs.get("tables"));
        List<String[]> datas = new ArrayList<String[]>();
        List<String[]> datasx = deleteService.queryHistory(schema, table);
        if(datasx != null) {
            datas = datasx;
        }

        Map<String, String[]> fields = new HashMap<String, String[]>();
        Map<String, List> testData2 = new HashMap<String, List>();
        fields.put("deleteHistory", new String[]{"id", "表空间", "表", "SQL", "查询日期", "查询状态","查询记录数", "删除日期", "删除状态", "删除总记录数","备份表"});
        testData2.put("deleteHistory", datas);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("deleteHistory"), testData2.get("deleteHistory")));
        return AJAXRTN;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }
}