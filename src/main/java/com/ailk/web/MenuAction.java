package com.ailk.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.core.base.action.BaseAction;
import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.common.config.Connection;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.*;

import com.ailk.model.ext.Menu;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

@ParentPackage("cloud-default")
@Namespace("/menu")
public class MenuAction extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String AJAXRTN = "ajax_rtn";
	private String ajaxStr;

    BaseDao dao = new BaseDao("ocnosql");
	
	public String test(){
		try {
			System.out.println("recevie message.");
//			while(true){
//				Thread.currentThread().sleep(2000);
//			}
			synchronized(this){
				this.wait();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AJAXRTN;
	}


//    @Action(value="menuList")
//	public String menuList(){
//		List<Menu> menuList = new ArrayList<Menu>();
//
//		Menu taskList = new Menu();
//		taskList.setId(0);
//		taskList.setText("任务列表");
//		taskList.setMurl("check/taskList.jsp");
//		taskList.setLeaf(true);
//		menuList.add(taskList);
//
//		Menu drquery = new Menu();
//		drquery.setId(1);
//		drquery.setText("详单条数稽核");
//		drquery.setMurl("check/tableCount.jsp");
//		drquery.setLeaf(true);
//
//
//		Menu ocnosql = new Menu();
//		ocnosql.setId(2);
//		ocnosql.setText("一致性稽核");
//		ocnosql.setMurl("check/consistencyCheck.jsp");
//		ocnosql.setLeaf(true);
//
//		Menu ocnosql_zd = new Menu();
//		ocnosql_zd.setId(100);
//		ocnosql_zd.setText("主键查询");
//		ocnosql_zd.setMurl("check/rowkeyQuery.jsp");
//		ocnosql_zd.setLeaf(true);
//
//		Menu switchDB = new Menu();
//		switchDB.setId(101);
//		switchDB.setText("统计分析");
//		switchDB.setMurl("check/sql.jsp");
//		switchDB.setLeaf(true);
//
//		Menu redis = new Menu();
//		redis.setId(3);
//		redis.setText("数据修改");
//		redis.setMurl("check/updateRows.jsp");
//		redis.setLeaf(true);
//
//        Menu refreshCache = new Menu();
//        refreshCache.setId(6);
//        refreshCache.setText("数据删除");
//        refreshCache.setMurl("check/deleteRows.jsp");
//        refreshCache.setLeaf(false);
//
////****************************************************************
//
////***********************************************************
//
//		menuList.add(drquery);
//		//menuList.add(drquery_zd);
//		menuList.add(ocnosql);
//		menuList.add(ocnosql_zd);
//		menuList.add(redis);
//        menuList.add(refreshCache);
//		menuList.add(switchDB);
//		//menuList.add(redisStatus);
//
//		Gson gs = new Gson();
//		this.setAjaxStr(gs.toJson(menuList));
//
//		return AJAXRTN;
//	}

	
	
	public String getAjaxStr() {
		return ajaxStr;
	}

	public void setAjaxStr(String ajaxStr) {
		this.ajaxStr = ajaxStr;
	}

    public String query() {
        List<Map> list = dao.query("select * from menu");
        List<Menu> menuList = new ArrayList<Menu>();
        Map<String, Menu> indexMap = new HashMap<String, Menu>();
        for(Map m : list) {
            Menu menu = new Menu();
            menu.setId((String)m.get("id"));
            menu.setText((String) m.get("label"));
            menu.setMurl((String) m.get("url"));
            menu.setPid((String)m.get("pid"));
            menu.setLeaf(true);
            menuList.add(menu);
            indexMap.put(menu.getId(), menu);
        }

        List<Menu> menuListReturn = new ArrayList<Menu>();
        for(Menu menu : menuList) {
            if(StringUtils.isNotEmpty(menu.getPid())) {
                Menu parent = indexMap.get(menu.getPid());
                parent.addChild(menu);
                parent.setLeaf(false);
            }else if("tableschem".equals(menu.getMurl())){
                menu.setLeaf(false);
                menuListReturn.add(menu);
            } else {
                menuListReturn.add(menu);
            }
        }

        Gson gs = new Gson();
        this.setAjaxStr(gs.toJson(menuListReturn));
        return AJAXRTN;
    }


    public String getTableSchemTree(){

        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        int id = vs.getInt("id");

        List<Map> list = dao.query("select * from menu");
        List<Menu> menuList = new ArrayList<Menu>();
        Map<String, Menu> indexMap = new HashMap<String, Menu>();

        //添加根节点为查询表空间和表的节点
        Menu menuRoot = new Menu();
        menuRoot.setId(String.valueOf(id));
        menuRoot.setText("可查询表空间和表");
        menuRoot.setMurl("");
        menuRoot.setPid("");
        menuRoot.setLeaf(false);
        menuList.add(menuRoot);
        indexMap.put(menuRoot.getId(), menuRoot);

        //Id = 6 为可查询表空间和表
        //查询所有的表空间
        int schemtableRootIndex = id ;
        Configuration conf = Connection.getInstance().getConf();
        try {
            HbaseJdbcHelper help = new PhoenixJdbcHelper();
            String sqlStr = "select distinct table_schem,table_name from system.catalog where table_schem is null or table_schem!='SYSTEM'";
            //待改进
            List<Map<String, Object>> rs =  help.executeQuery(sqlStr);
            List<Map> records = new ArrayList<Map>();
            records.addAll(rs);

            int schemIndex = 0;
            int tableIndex = 1;
            List<String> table_schemList = new ArrayList<String>();
            //将查询出的节点都添加进menuListReturn
            for(Map mapTmp : records) {
                //判断tmp的table_schem在
                boolean isContain = table_schemList.contains((String) mapTmp.get("TABLE_SCHEM"));

                if(isContain){
                    //表空间节点已经添加，不需要在增加，直接增加表节点
                    Menu menu = new Menu();
                    menu.setId(schemtableRootIndex+"."+schemIndex+"."+tableIndex);
                    menu.setText((String) mapTmp.get("TABLE_NAME"));
                    menu.setMurl("check/schemtableTree.jsp?tableschem=" + (String) mapTmp.get("TABLE_SCHEM") + "&tablename=" + menu.getText());
                    menu.setPid(schemtableRootIndex + "." + schemIndex);
                    menu.setLeaf(true);
                    menuList.add(menu);
                    indexMap.put(menu.getId(), menu);
                    tableIndex++;
                }else{
                    tableIndex = 1;
                    schemIndex++;
                    //添加表空间节点
                    table_schemList.add((String) mapTmp.get("TABLE_SCHEM"));
                    Menu menu = new Menu();
                    menu.setId(schemtableRootIndex+"."+schemIndex);
                    menu.setText((String) mapTmp.get("TABLE_SCHEM"));
                    menu.setMurl("");
                    menu.setPid(schemtableRootIndex+"");
                    menu.setLeaf(false);
                    menuList.add(menu);
                    indexMap.put(menu.getId(), menu);

                    //添加表节点
                    menu = new Menu();
                    menu.setId(schemtableRootIndex+"."+schemIndex+"."+tableIndex);
                    menu.setText((String) mapTmp.get("TABLE_NAME"));
                    menu.setMurl("check/schemtableTree.jsp?tableschem=" + (String) mapTmp.get("TABLE_SCHEM") + "&tablename=" + menu.getText());
                    menu.setPid(schemtableRootIndex+"."+schemIndex);
                    menu.setLeaf(true);
                    menuList.add(menu);
                    indexMap.put(menu.getId(), menu);

                    tableIndex++;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        List<Menu> menuListReturn = new ArrayList<Menu>();
        for(Menu menu : menuList) {
            //默认表空间时 表空间名用DEFAULT
            if(menu.getText()==null){
                menu.setText("DEFAULT");
            }
            if(StringUtils.isNotEmpty(menu.getPid())) {
                Menu parent = indexMap.get(menu.getPid());
                parent.addChild(menu);
                parent.setLeaf(false);
            }else if(String.valueOf(id).equals(menu.getId())){
                menu.setLeaf(false);
                menuListReturn.add(menu);
            } else {
                menuListReturn.add(menu);
            }
        }

        Gson gs = new Gson();
        this.setAjaxStr(gs.toJson(menuListReturn));
        return AJAXRTN;
    }

	
}
