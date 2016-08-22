package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.ResultBuild;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.service.impl.CommonService;
import com.ailk.service.impl.CreateTableService;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsh on 2016/7/13.
 */

public class CommonAction extends BaseAction {
    private static Log log = LogFactory.getLog(CommonAction.class);
    private String tablespace;

    public String getTableSpaces() {
        String sql = "select distinct table_schem from system.catalog where table_schem <> 'SYSTEM' or table_schem is null";
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        Gson gs = new Gson();
        List<Map<String, Object>> results = null;
        try {
            results = help.executeQuery(sql);
            if (results != null && results.size() > 0) {
                for (Map<String, Object> map : results) {
                    if (map.get("TABLE_SCHEM") == null) {
                        map.put("TABLE_SCHEM", "DEFAULT");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error("查询异常", e);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(e.getMessage())));
        }
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", results);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String getTables() {
        String sql = "select distinct table_name from system.catalog where table_schem";
        String whereis = " = '" + tablespace + "'";
        if (tablespace == null || "".equals(tablespace)) {
            tablespace = "______________";
        } else if ("DEFAULT".equals(tablespace)) {
            whereis = " is null";
        }

        String SQL = sql + whereis;
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        Gson gs = new Gson();
        List<Map<String, Object>> results = null;
        try {
            results = help.executeQuery(SQL);
        } catch (SQLException e) {
            LOG.error("查询异常", e.getMessage());
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(e.getMessage())));
        }
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", results);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String getTablesHive() {
        String sql = "select distinct table_name from system.catalog where table_schem";
        String whereis = " = '" + tablespace + "'";
        if (tablespace == null || "".equals(tablespace)) {
            tablespace = "______________";
        } else if ("DEFAULT".equals(tablespace)) {
            whereis = " is null";
        }

        String SQL = sql + whereis;
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        Gson gs = new Gson();
        List<Map<String, Object>> results = null;
        try {
            results = help.executeQuery(SQL);
        } catch (SQLException e) {
            LOG.error("查询异常", e.getMessage());
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(e.getMessage())));
        }

        CreateTableService c = new CreateTableService();
        List hiveList = c.getTableName();
        if (hiveList.size() > 0) {
            List<Map<String, Object>> margeList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < results.size(); i++) {
                Map map = results.get(i);
                String tableName = (String) map.get("TABLE_NAME");
                if (!hiveList.contains(tableName)) {
                    Map margeMap = new HashMap();
                    margeMap.put("TABLE_NAME", tableName);
                    margeList.add(margeMap);
                }
            }
            results = margeList;
        }
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", results);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public String getColmns() throws SQLException {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        CommonService commonService = new CommonService();
        String schema = String.valueOf(vs.get("schema"));
        String table = String.valueOf(vs.get("table"));
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", commonService.getColumns(schema, table));
        Gson gs = new Gson();
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }
}