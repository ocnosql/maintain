package com.ailk.web;

import com.ailk.service.impl.QueryByNoRowkeyService;
import org.mortbay.log.Log;

public class NoRowkeyThread extends Thread {

    private String sql_str;

    public NoRowkeyThread(String sql_str) {
        this.sql_str = sql_str;
    }

    public String getSql_str() {
        return sql_str;
    }

    public void setSql_str(String sql_str) {
        this.sql_str = sql_str;
    }

    @Override
    public void run() {
        Log.info("NoRowkeyThread start" + this.getName());
        try {
            QueryByNoRowkeyService service = new QueryByNoRowkeyService();
            service.taskSubmit(sql_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.info("NoRowkeyThread end--" + this.getName());
    }


}
