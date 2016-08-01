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
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.struts2.ServletActionContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class NoRowkeyQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(NoRowkeyQueryAction.class);
    private InputStream file;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String taskSubmit() {
        Gson gs = new Gson();
        try {
            ValueSet vs = new ValueSet();
            bindParams(vs, ServletActionContext.getRequest());
            String sql = vs.getString("sql");
//            QueryByNoRowkeyService service = new QueryByNoRowkeyService();
//            service.taskSubmit(sql);
            NoRowkeyThread  noRowkeyThread = new NoRowkeyThread(sql);
            noRowkeyThread.start();
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
            } else if (status == 2) {
                statusName = "失败";
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
        fields.put("taskList", new String[]{"主键", "任务状态", "任务执行开始时间", "任务执行结束时间", "任务时长/秒", "总行数", "查询语句"});
        testData2.put("taskList", dataList);
        this.setAjaxStr(ResultBuild.buildJson(fields.get("taskList"), testData2.get("taskList"), totalCountByPage));
        return AJAXRTN;
    }

    public String dataExport() throws WriteException, IOException {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        QueryByNoRowkeyService service = new QueryByNoRowkeyService();

        String exportType=vs.getString("exportType");
        ResultDTO dto = service.queryExport(vs);
        List<Map> records = dto.getRecords();
        String tableName = (String) dto.getExtInfo().get("tableName");
        String[] fields = ResultBuild.createFieldHeader(records.get(0));

        ByteArrayOutputStream out = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        if (exportType.equals("0")) {//.xls export
            WritableWorkbook workbook = null;
            try {
                out = new ByteArrayOutputStream();
                workbook = Workbook.createWorkbook(out);
                WritableSheet sheet = workbook.createSheet("导出表", 1);

                String[] strNames = fields;
                //head
                for (int i = 0; i < strNames.length; i++) {
                    Label label0 = new Label(i, 0, strNames[i].replace(tableName + ".", ""));
                    sheet.addCell(label0);
                }
                //data
                for (int i = 0; i < records.size(); i++) {
                    Map<String, String> record = (Map) records.get(i);
                    int j = 0;
                    for (Map.Entry<String, String> entry : record.entrySet()) {
//                        System.out.println(entry.getKey() + "--->" + entry.getValue());
                        sheet.addCell(new Label(j, i + 1, entry.getValue()));
                        j++;
                    }
//                    System.out.println("行数i=" + i);
                }
                workbook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workbook.close();
            }
            fileName = "" + sdf.format(new Date()) + ".xls";
        } else {//txt export
            try {
                StringBuffer sb = new StringBuffer();
                out = new ByteArrayOutputStream();
                for (int i = 0; i < records.size(); i++) {
                    Map<String, String> record = (Map) records.get(i);
                    sb.setLength(0);
                    for (Map.Entry<String, String> entry : record.entrySet()) {
                        sb.append(entry.getValue()).append("\t");
//                        System.out.println("ssss:=" + sb.toString());
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append("\n");
                    out.write(sb.toString().getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileName = "" + sdf.format(new Date()) + ".txt";
        }
        byte[] b = out.toByteArray();
        file = new ByteArrayInputStream(b);
        return "success";
    }
}
