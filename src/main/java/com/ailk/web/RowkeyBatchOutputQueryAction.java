package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;
import com.ailk.oci.ocnosql.client.jdbc.phoenix.PhoenixJdbcHelper;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.service.IQueryService;
import com.ailk.service.impl.QueryByRowkeyBatchOutputService;
import com.ailk.util.DateUtil;
import com.ailk.util.HDFSUtil;
import com.google.gson.Gson;

;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


import com.sun.org.apache.commons.logging.impl.NoOpLog;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by scj on 2016/7/14.
 */
public class RowkeyBatchOutputQueryAction extends BaseAction {

    public static final Log LOG = LogFactory.getLog(RowkeyBatchOutputQueryAction.class);

    public String query(){
        IQueryService service = new QueryByRowkeyBatchOutputService();
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        Gson gs = new Gson();
        BaseDao dao = new BaseDao("ocnosql");
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
            LOG.info("convert records to json token: " + (endTime2 - endTime) + "ms");
        } catch(Throwable ex){
            LOG.error("查询出现异常", ex);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
        }

        return AJAXRTN;
    }


    public String generatetask(){
        {
            ValueSet vs = new ValueSet();
            bindParams(vs, ServletActionContext.getRequest());
            BaseDao dao = new BaseDao("ocnosql");
            try {
                HttpServletRequest httpServletRequest = ServletActionContext.getRequest();
                String filePath = vs.get("filepath_server").toString();
                LOG.info("filePath="+filePath);
                //MultiPartRequestWrapper request = (MultiPartRequestWrapper) ServletActionContext.getRequest();
                //File[] files = request.getFiles("uploadfileid");
                File file = new File(filePath);
                //if(files!=null&&files.length>0) {
                if(file.exists()&&!file.isDirectory()) {
                    //生成任务并插入任务表
                    String[] params = new String[6];
                    params[0] = UUID.randomUUID().toString();             //id
                    params[1] = "0";                                     //task_type
                    params[2] = "0";                                     //finish_type
                    //params[3] = "";                                      //src_path
                    params[3] = "/rowkeyBatchQuery/"+params[0];   //dst_path

                    StringBuffer sqlBuffer = new StringBuffer();
                    sqlBuffer.append("select * from  ");
                    if(!"DEFAULT".equals(vs.get("table_schem"))){
                        sqlBuffer.append(vs.get("table_schem")).append(".");
                    }
                    sqlBuffer.append(vs.get("table_name"));
                    if(StringUtils.isNotEmpty(vs.get("sql") + "")){
                        //因为条件部分很可能带有limit，group by...等  需要将键值的部分插入到where之前
                        StringBuffer sqlwhereBuffer =  new StringBuffer();
                        sqlwhereBuffer.append(String.valueOf(vs.get("sql")));
                        if(sqlwhereBuffer.indexOf("where")>-1||sqlwhereBuffer.indexOf("WHERE")>-1){
                            //记录下where位置 插入rowkey条件
                            int indexwhere = sqlwhereBuffer.indexOf("where");
                            if(indexwhere<=-1) indexwhere = sqlwhereBuffer.indexOf("WHERE");
                            indexwhere = indexwhere + 5;

                            sqlwhereBuffer.insert(indexwhere," id>='${rowkey}' and id<='${rowkey}g' and ");
                        }else{
                            sqlBuffer.append(" where id>='${rowkey}' and id<='${rowkey}g' ");
                        }
                        sqlBuffer.append(" ").append(sqlwhereBuffer);
                    }else{
                        sqlBuffer.append(" where id>='${rowkey}' and id<='${rowkey}g'");
                    }
                    params[4] = sqlBuffer.toString();                                      //sql

                    //获取列名插入到任务表中
                    StringBuffer sqlBufferTemp = new StringBuffer();
                    sqlBufferTemp.append("select * from ");
                    if(!"DEFAULT".equals(vs.get("table_schem"))){
                        sqlBufferTemp.append(vs.get("table_schem")).append(".");
                    }
                    sqlBufferTemp.append(vs.get("table_name")).append(" limit 1");
                    HbaseJdbcHelper help = new PhoenixJdbcHelper();
                    LOG.info(sqlBufferTemp.toString());
                    ResultSet rs = help.executeQueryRaw(sqlBufferTemp.toString());

                    int count = rs.getMetaData().getColumnCount();
                    String[] colums = new String[count];
                    StringBuffer columsBuffer  = new StringBuffer();
                    for(int i = 0; i < count; i++) {
                        colums[i] = rs.getMetaData().getColumnName(i + 1);
                        columsBuffer.append( rs.getMetaData().getColumnName(i + 1));
                        if((i+1)!=count){
                            columsBuffer.append(",");
                        }
                    }
                    params[5] = columsBuffer.toString();

                    dao.executeUpdate("insert into task_status(id,task_type,finish_flag,dst_path,sql_str,columns_str,create_time) values(?,?,?,?,?,?,now())", params);

                    RowkeyBatchMainThread  rowkeyBatchMainThread = new RowkeyBatchMainThread(file,params[0],params[3],params[4]);
                    rowkeyBatchMainThread.start();
                    this.setAjaxStr("{success:true}");
                }else{
                    this.setAjaxStr("{success:false}");
                }
            }catch (Exception e){
                //文件上传失败
                e.printStackTrace();
                this.setAjaxStr("{success:false}");
            }
            return AJAXRTN;
        }
    }



    private File uploadfileid; //与前台保持一致
    private String uploadfileidContentType;
    private String uploadfileidFileName;
    private String types; //存放允许上传的后缀名

    public File getUploadfileid() {
        return uploadfileid;
    }

    public void setUploadfileid(File uploadfileid) {
        this.uploadfileid = uploadfileid;
    }

    public String getUploadfileidContentType() {
        return uploadfileidContentType;
    }

    public void setUploadfileidContentType(String uploadfileidContentType) {
        this.uploadfileidContentType = uploadfileidContentType;
    }

    public String getUploadfileidFileName() {
        return uploadfileidFileName;
    }

    public void setUploadfileidFileName(String uploadfileidFileName) {
        this.uploadfileidFileName = uploadfileidFileName;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String upload(){
        Gson gs = new Gson();
        JsonResult result = new JsonResult();
        try{
            String sPath = ServletActionContext.getRequest().getRealPath("/uploadtemp");
            LOG.info(sPath);
            if(uploadfileid==null){
                result.setSuccess(false);
                result.setMessage("");
            }else{
                File fileDir =new File(sPath);
                //如果文件夹不存在则创建
                if  (!fileDir .exists()  && !fileDir .isDirectory()) {
                    LOG.info("目录不存在，创建目录");
                    fileDir .mkdir();
                } else {
                    LOG.info("目录不存在，创建目录");
                }
                //获取文件名称
                String fileName = uploadfileid.getName();
                //复制文件到指定目录
                InputStream inStream = new FileInputStream(uploadfileid); //读入原文件
                String savePath = sPath+"\\"+uploadfileidFileName;
                File fileDst = new File(savePath);
                if(fileDst.exists()&&!fileDst.isDirectory()){
                    fileDst.delete();
                }
                FileOutputStream fs = new FileOutputStream(savePath);
                byte[] buffer = new byte[1000];
                int byteread ;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                result.setSuccess(true);
                result.setExtInfo(savePath);
                inStream.close();
                fs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("异常");
            result.setExtInfo("");
        }finally {
            this.setAjaxStr(gs.toJson(result));
        }
        return AJAXRTN;
    }


    public String getTableSpaces() {
        String sql = "select distinct table_schem from system.catalog";
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        Gson gs = new Gson();
        List<Map<String, Object>> results = null;
        try {
            results = help.executeQuery(sql);
            if(results != null && results.size() > 0) {
                for(Map<String, Object> map : results) {
                    if(map.get("TABLE_SCHEM") == null) {
                        map.put("TABLE_SCHEM","DEFAULT");
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
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String tablespace = vs.get("tablespace")==null?null:vs.get("tablespace").toString();
        String whereis = " = '" + tablespace + "'";
        if(tablespace == null || "".equals(tablespace)) {
            tablespace = "______________";
        } else if("DEFAULT".equals(tablespace)) {
            whereis = " is null";
        }

        String SQL = sql + whereis;
        HbaseJdbcHelper help = new PhoenixJdbcHelper();
        Gson gs = new Gson();
        List<Map<String, Object>> results = null;
        try {
            results = help.executeQuery(SQL);
        } catch (SQLException e) {
            LOG.error("查询出现异常", e);
            this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(e.getMessage())));
            return AJAXRTN;
        }
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("root", results);
        this.setAjaxStr(gs.toJson(json));
        return AJAXRTN;
    }

    private InputStream file;
    private String fileName;

    public String batchDownload() throws IOException {
        ValueSet vs = new ValueSet();
        bindParams(vs, ServletActionContext.getRequest());
        String dst_path = vs.get("dst_path").toString();

        Configuration conf = Connection.getInstance().getConf();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] files = fs.listStatus(new Path(dst_path));
        FSDataInputStream in;
        //InputStreamReader inputStreamReader;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(int i=0;i<files.length;i++){
            //流方式下载文件
            String fileName = files[i].getPath().getName();//客户端保存的文件名
            in =  fs.open(files[i].getPath());
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            in.close();
        }

        String date_str = DateUtil.format(new Date(),"YYYYMMDDHHMMSS");
        fileName = dst_path.split("/")[2].substring(0,2)+ "_"+date_str+".txt";
        byte[] b = out.toByteArray();
        file = new ByteArrayInputStream(b);

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
