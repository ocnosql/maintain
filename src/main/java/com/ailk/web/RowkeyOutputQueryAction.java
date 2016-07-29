package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.DataInfo;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.QueryByRowkeyOutputService;
import com.ailk.service.impl.QueryByRowkeyService;
import com.ailk.util.Cache;
import com.ailk.util.DateUtil;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.struts2.ServletActionContext;

import java.io.*;
import java.util.Date;

/**
 * Created by scj on 2016/7/14.
 */
public class RowkeyOutputQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(RowkeyOutputQueryAction.class);

    public String query(){
        IQueryService service = new QueryByRowkeyOutputService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        try{
            long startTime = System.currentTimeMillis();
            ResultDTO dto = service.loadData(vs);
            long endTime = System.currentTimeMillis();
            LOG.info("query action complete! token: " + (endTime - startTime) + "ms");
            JsonResult result;
            if(dto.isSuccess()){
                if(!dto.isHasPaged()) {
                    result = ResultBuild.buildResult(dto.getRecords(), Integer.parseInt(start), Integer.parseInt(limit));
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
            //LOG.info("convert records to json token: " + (endTime2 - endTime) + "ms");
        } catch(Throwable ex){
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }
        return AJAXRTN;
    }

    private InputStream file;
    private String fileName;

    public String rowkeyDownload() throws IOException {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);
        if(info!=null){
            //从缓存中获取文件信息
            Configuration conf = Connection.getInstance().getConf();
            FileSystem fs = FileSystem.get(conf);
            FSDataInputStream in = fs.open(new Path(info.getPath()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            in.close();

            String date_str = DateUtil.format(new Date(), "YYYYMMDDHHMMSS");
            fileName = date_str+".txt";
            byte[] b = out.toByteArray();
            file = new ByteArrayInputStream(b);
        }
        return "success";
    }

    public String rowkeyDownload_excel() throws IOException, WriteException {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String gid = vs.getString("gid");
        DataInfo info = Cache.get(gid);
        WritableWorkbook workbook = null;
        if(info!=null){
            //从缓存中获取文件信息
            Configuration conf = Connection.getInstance().getConf();
            FileSystem fs = FileSystem.get(conf);
            FSDataInputStream in = fs.open(new Path(info.getPath()));
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                workbook = Workbook.createWorkbook(out);
                WritableSheet sheet = workbook.createSheet("sheet", 1);

                //列名
                Label labelColumn = null;
                for (int i = 0; i < info.getColumns().length; i++) {
                    labelColumn = new Label(i, 0, info.getColumns()[i]);
                    labelColumn.setCellFormat(WritableWorkbook.NORMAL_STYLE);
                    sheet.addCell(labelColumn);
                }

                //循环文件每一行数据，并分隔成各个列
                String line;
                int index = 1;
                Label labelContent = null;
                while ((line = reader.readLine()) != null) {
                    //切割成数组
                    String[] values = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t");
                    for (int i = 0; i < values.length; i++) {
                        labelContent = new Label(i, index, values[i]);
                        labelContent.setCellFormat(WritableWorkbook.NORMAL_STYLE);
                        sheet.addCell(labelContent);
                    }
                    index++;
                }
                workbook.write();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                workbook.close();
            }

            String date_str = DateUtil.format(new Date(), "YYYYMMDDHHMMSS");
            fileName = date_str+".xls";
            byte[] b = out.toByteArray();
            file = new ByteArrayInputStream(b);
        }
        return "success";
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
