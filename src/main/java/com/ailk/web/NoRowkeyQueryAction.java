package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.impl.QueryByNoRowkeyService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoRowkeyQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(NoRowkeyQueryAction.class);

    public String taskSubmit() {
        Gson gs = new Gson();
        try {
            ValueSet vs = new ValueSet();
            bindParams(vs, ServletActionContext.getRequest());
            String sql = vs.getString("sql");
            QueryByNoRowkeyService service = new QueryByNoRowkeyService();
            service.taskSubmit(sql);
            JsonResult result = new JsonResult();
            result.setSuccess(true);
            result.setMessage("任务提交中,请到任务清单查找最新信息。");
            this.setAjaxStr(gs.toJson(result));
        } catch (Throwable ex) {
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

    public String query() {
        QueryByNoRowkeyService service = new QueryByNoRowkeyService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        try {
            long startTime = System.currentTimeMillis();
            ResultDTO dto = service.loadData(vs);
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
//            LOG.info("convert records to json token: " + (endTime2 - endTime) + "ms");
        } catch (Throwable ex) {
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

    public String queryTask() {
//        System.out.println(start);
//        System.out.println(limit);
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        QueryByNoRowkeyService service = new QueryByNoRowkeyService();

        ResultDTO dto = service.loadDataTask(vs);
        List<Map> list = dto.getRecords();
        long totalCountByPage = dto.getTotalCount();
        List dataList = new ArrayList();
        for (Map m : list) {
            Integer id = (Integer) m.get("id");
            String keyId = String.valueOf(id);
            Integer status = (Integer) m.get("status");
            String statusName = "";
            if (status == 0) {
                statusName = "进行中";
            } else if (status == 1) {
                statusName = "已完成";
            }
            String createDate = (String) m.get("createDate");
            String updateDate = (String) m.get("updateDate");
            String timeDiff = (String) m.get("timeDiff");
            String totalCount = (String) m.get("totalCount");
//            String tempTable=(String) m.get("tempTable");
            String querySql = (String) m.get("querySql");
            dataList.add(new String[]{keyId, statusName, createDate, updateDate, timeDiff, totalCount, querySql});
        }
        Map<String, String[]> fields = new HashMap<String, String[]>();
        Map<String, List> testData2 = new HashMap<String, List>();
        fields.put("taskList", new String[]{"主键", "任务类型", "任务执行开始时间", "任务执行结束时间", "任务时长/秒", "总行数", "查询语句"});
        testData2.put("taskList", dataList);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("taskList"), testData2.get("taskList"), totalCountByPage));
        return AJAXRTN;
    }
}
