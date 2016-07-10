package com.ailk.web;

import java.util.ArrayList;
import java.util.List;

import com.ailk.core.base.action.BaseAction;
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


    @Action(value="menuList")
	public String menuList(){
		List<Menu> menuList = new ArrayList<Menu>();
		
		Menu taskList = new Menu();
		taskList.setId(0);
		taskList.setText("任务列表");
		taskList.setMurl("check/taskList.jsp");
		taskList.setLeaf(true);
		menuList.add(taskList);
		
		Menu drquery = new Menu();
		drquery.setId(1);
		drquery.setText("详单条数稽核");
		drquery.setMurl("check/tableCount.jsp");
		drquery.setLeaf(true);
		

//		Menu drquery_zd = new Menu();
//		drquery_zd.setId(7);
//		drquery_zd.setText("详单条数稽核");
//		drquery_zd.setMurl("list/drqueryZd.jsp");
//		drquery_zd.setLeaf(true);
		
		Menu ocnosql = new Menu();
		ocnosql.setId(2);
		ocnosql.setText("一致性稽核");
		ocnosql.setMurl("check/consistencyCheck.jsp");
		ocnosql.setLeaf(true);
		
		Menu ocnosql_zd = new Menu();
		ocnosql_zd.setId(100);
		ocnosql_zd.setText("主键查询");
		ocnosql_zd.setMurl("check/rowkeyQuery.jsp");
		ocnosql_zd.setLeaf(true);
		
		Menu switchDB = new Menu();
		switchDB.setId(101);
		switchDB.setText("统计分析");
		switchDB.setMurl("check/sql.jsp");
		switchDB.setLeaf(true);
		
		Menu redis = new Menu();
		redis.setId(3);
		redis.setText("数据修改");
		redis.setMurl("check/updateRows.jsp");
		redis.setLeaf(true);

        Menu refreshCache = new Menu();
        refreshCache.setId(6);
        refreshCache.setText("数据删除");
        refreshCache.setMurl("check/deleteRows.jsp");
        refreshCache.setLeaf(true);
		
//****************************************************************		
		
//***********************************************************		
		
		menuList.add(drquery);
		//menuList.add(drquery_zd);
		menuList.add(ocnosql);
		menuList.add(ocnosql_zd);
		menuList.add(redis);
        menuList.add(refreshCache);
		menuList.add(switchDB);
		//menuList.add(redisStatus);

		
		

		
		

		

		

		
		Gson gs = new Gson();
		this.setAjaxStr(gs.toJson(menuList));
		
		return AJAXRTN;
	}

	
	
	public String getAjaxStr() {
		return ajaxStr;
	}

	public void setAjaxStr(String ajaxStr) {
		this.ajaxStr = ajaxStr;
	}
	
	
	
}
