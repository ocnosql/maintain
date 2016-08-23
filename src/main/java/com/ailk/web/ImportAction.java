package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.impl.ImportService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(ImportAction.class);

    public String queryConfigList() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        List<String[]> datas = new ArrayList<String[]>();
        List<String[]> datasx = service.queryConfigList(vs);
        if(datasx != null) {
            datas = datasx;
        }

        Map<String, String[]> fields = new HashMap<String, String[]>();
        Map<String, List> testData2 = new HashMap<String, List>();
        fields.put("configList", new String[]{"id", "配置名称", "分隔符", "rowkey", "generator", "algocolumn"});
        testData2.put("configList", datas);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("configList"), testData2.get("configList")));
        return AJAXRTN;
    }

    public String saveConfig() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String id = String.valueOf(vs.get("id"));
        JsonResult result = new JsonResult();
        result.setSuccess(false);
        Gson gs = new Gson();
        if(id == null || "".equals(id)) {
            boolean success = insertConfig();
            result.setSuccess(true);
            if(!success) {
                result.setMessage("配置名称已存在!");
                result.setSuccess(false);
            }
            this.setAjaxStr(gs.toJson(result));
        } else {
            result.setSuccess(true);
            this.setAjaxStr(gs.toJson(result));
            updateConfig();
        }
        return AJAXRTN;
    }

    private boolean insertConfig() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        return service.insertConfig(vs);
    }

    private void updateConfig() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        service.updateConfig(vs);
    }

    public String deleteConfig() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        service.deleteConfig(vs);
        return AJAXRTN;
    }

    public String queryConfigById() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        List<Map> data = service.queryConfigById(vs);
        JsonResult result = new JsonResult();
        result.setRecords(data);
        result.setSuccess(true);
        Gson gs = new Gson();
        this.setAjaxStr(gs.toJson(result));
        return AJAXRTN;
    }

    public String queryConfigBySchemaAndTable() {
        ValueSet vs = new ValueSet();
        Gson gs = new Gson();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        List<Map> results = service.queryConfigBySchemaAndTable(vs);
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", results);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String queryIcBySchemaAndTable() {
        ValueSet vs = new ValueSet();
        Gson gs = new Gson();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        List<Map> results = service.queryIcBySchemaAndTable(vs);
        this.setAjaxStr(gs.toJson(results));
        return AJAXRTN;
    }

    public String importData() throws Exception {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        ImportService service = new ImportService();
        service.importData(vs);
        Gson gs = new Gson();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("success", true);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String queryImportHistory() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String table_space = String.valueOf(vs.get("table_space"));
        String tables = String.valueOf(vs.get("tables"));
        ImportService service = new ImportService();
        List<String[]> results = service.queryImportHistory(table_space, tables);
       // HashMap<String, Object> json = new HashMap<String, Object>();
       // json.put("root", results);
       // Gson gs = new Gson();
       // this.setAjaxStr(gs.toJson(json));


        Map<String, String[]> fields = new HashMap<String, String[]>();
        Map<String, List> testData2 = new HashMap<String, List>();
        fields.put("configHistory", new String[]{"id", "表名", "表空间", "状态", "导入时间", "输入路径", "输出路径", "导入数据总量","成功数据量", "失败数据量"});
        testData2.put("configHistory", results);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("configHistory"), testData2.get("configHistory")));
        return AJAXRTN;
    }
}