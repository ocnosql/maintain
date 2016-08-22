package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.service.impl.CreateTableService;
import com.ailk.service.impl.QueryByNoRowkeyService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTableAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(CreateTableAction.class);

    public String createTable() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        CreateTableService service = new CreateTableService();
        Map map = service.createHiveTable(vs);
        boolean flag = (Boolean) map.get("flag");
        String msg = (String) map.get("msg");
        Gson gs = new Gson();
        JsonResult result = new JsonResult();
        if (flag) {
            result.setMessage("创建表成功。");
            LOG.info(" createTable action success");
        } else {
            result.setMessage("创建表失败:" + msg);
            LOG.info(" createTable action failure");
        }
        this.setAjaxStr(gs.toJson(result));
        return AJAXRTN;
    }

    public String createTable2() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        CreateTableService service = new CreateTableService();
        Map map = service.createHiveTable2(vs);
        boolean flag = (Boolean) map.get("flag");
        String msg = (String) map.get("msg");
        Gson gs = new Gson();
        JsonResult result = new JsonResult();
        if (flag) {
            result.setMessage("创建表成功。");
            LOG.info(" createTable action success");
        } else {
            result.setMessage("创建表失败:" + msg);
            LOG.info(" createTable action failure");
        }
        this.setAjaxStr(gs.toJson(result));
        return AJAXRTN;
    }

    public String queryTable() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        CreateTableService service = new CreateTableService();

        ResultDTO dto = service.loadDataByPage(vs);
        List<Map> list = dto.getRecords();
        long totalCountByPage = dto.getTotalCount();
        List dataList = new ArrayList();
        for (Map m : list) {
            Integer id = (Integer) m.get("id");
            String keyId = String.valueOf(id);
            Integer status = (Integer) m.get("status");
            String statusName = "";
            if (status == 1) {
                statusName = "成功";
            } else if (status == 0) {
                statusName = "失败";
            }
            String createDate = (String) m.get("createDate");
            String hiveTableName = (String) m.get("hiveTableName");
            String hbaseTableName = (String) m.get("hbaseTableName");
            dataList.add(new String[]{keyId, hiveTableName, hbaseTableName, statusName, createDate});
        }
        Map<String, String[]> fields = new HashMap<String, String[]>();
        Map<String, List> testData2 = new HashMap<String, List>();
        fields.put("taskList", new String[]{"编号","hive表名","hbase表名","执行状态", "创建时间"});
        testData2.put("taskList", dataList);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("taskList"), testData2.get("taskList"), totalCountByPage));
        return AJAXRTN;
    }

}
