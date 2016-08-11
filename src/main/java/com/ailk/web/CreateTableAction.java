package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.service.impl.CreateTableService;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.Map;

public class CreateTableAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(CreateTableAction.class);

    public String createTable() {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        CreateTableService service = new CreateTableService();
        Map map = service.createHiveTable(vs);
        boolean flag=(Boolean)map.get("flag");
        String msg=(String)map.get("msg");
        Gson gs = new Gson();
        JsonResult result = new JsonResult();
        if (flag) {
            result.setMessage("创建表成功。");
            LOG.info(" createTable action success");
        } else {
            result.setMessage("创建表失败:"+msg);
            LOG.info(" createTable action failure");
        }
        this.setAjaxStr(gs.toJson(result));
        return AJAXRTN;
    }
}
