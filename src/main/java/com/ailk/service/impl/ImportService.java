package com.ailk.service.impl;

import com.ailk.dao.CommonDao;
import com.ailk.dao.ImportDao;
import com.ailk.model.ImportConfig;
import com.ailk.model.ImportLog;
import com.ailk.model.ValueSet;
import com.ailk.oci.ocnosql.common.util.DateUtil;
import com.ailk.oci.ocnosql.tools.load.mutiple.MutipleColumnImportTsv;

import java.util.*;

public class ImportService {

    public List<String[]> queryConfigList(ValueSet vs) {
        String schema = vs.getString("schema");
        String table = vs.getString("table");

        ImportDao importDao = new ImportDao("ocnosql");
        List<Map> list = importDao.queryConfigList(schema, table);
        List<String[]> datas = new ArrayList<String[]>();
        if(list != null) {
            for(Map map : list) {
                HashMap m = (HashMap) map;
                String[] data = {String.valueOf(m.get("id")), String.valueOf(m.get("cname")),String.valueOf(m.get("separatorx")), /*String.valueOf(m.get("loadType")), */
                         String.valueOf(m.get("rowkey")), String.valueOf(m.get("generator")),
                        String.valueOf(m.get("algocolumn"))/*, String.valueOf(m.get("inputPath")), String.valueOf(m.get("outputPath")), */
                };
                datas.add(data);
            }
        }
        return datas;
    }

    public boolean insertConfig(ValueSet vs)  {
        //String outputPath = String.valueOf(vs.get("outputPath"));
        //String inputPath = String.valueOf(vs.get("inputPath"));
        boolean result = true;
        String cname = String.valueOf(vs.get("cname"));
        String separator = String.valueOf(vs.get("separator"));
        String loadType = String.valueOf(vs.get("loadType"));
        String rowkey = String.valueOf(vs.get("rowkey"));
        String generator = String.valueOf(vs.get("generator"));
        String algocolumn = String.valueOf(vs.get("algocolumn"));
        String schema = String.valueOf(vs.get("schema"));
        String table = String.valueOf(vs.get("table"));
        ImportDao importDao = new ImportDao("ocnosql");
        List<Map> map = importDao.queryIcBySchemaAndTable(schema, table, cname);
        if(map != null && map.size() > 0) {
            result = false;
            return result;
        }

        ImportConfig importConfig = new ImportConfig();
        importConfig.setCname(cname);
        importConfig.setSeparator(separator);
        importConfig.setLoadType(loadType);
        //importConfig.setInputPath(inputPath);
        importConfig.setRowkey(rowkey);
        importConfig.setGenerator(generator);
        //importConfig.setOutputPath(outputPath);
        importConfig.setAlgocolumn(algocolumn);
        importConfig.setSchema(schema);
        importConfig.setTable(table);
        importDao.insertConfig(importConfig);
        return result;
    }

    public void updateConfig(ValueSet vs) {
        Integer id = Integer.parseInt(String.valueOf(vs.get("id")));
        String cname = String.valueOf(vs.get("cname"));
        String separator = String.valueOf(vs.get("separator"));
        //String loadType = String.valueOf(vs.get("loadType"));
       // String inputPath = String.valueOf(vs.get("inputPath"));
        String rowkey = String.valueOf(vs.get("rowkey"));
        String generator = String.valueOf(vs.get("generator"));
        //String outputPath = String.valueOf(vs.get("outputPath"));
        String algocolumn = String.valueOf(vs.get("algocolumn"));
        String schema = String.valueOf(vs.get("schema"));
        String table = String.valueOf(vs.get("table"));
        ImportConfig importConfig = new ImportConfig();
        importConfig.setId(id);
        importConfig.setCname(cname);
        importConfig.setSeparator(separator);
        //importConfig.setLoadType(loadType);
       // importConfig.setInputPath(inputPath);
        importConfig.setRowkey(rowkey);
        importConfig.setGenerator(generator);
        //importConfig.setOutputPath(outputPath);
        importConfig.setAlgocolumn(algocolumn);
        importConfig.setSchema(schema);
        importConfig.setTable(table);
        ImportDao importDao = new ImportDao("ocnosql");
        importDao.updateConfig(importConfig);
    }

    public void deleteConfig(ValueSet vs) {
        Integer id = Integer.parseInt(String.valueOf(vs.get("id")));
        ImportDao importDao = new ImportDao("ocnosql");
        importDao.deleteConfig(id);
    }

    public List<Map> queryConfigById(ValueSet vs) {
        Integer id = Integer.parseInt(String.valueOf(vs.get("id")));
        ImportDao importDao = new ImportDao("ocnosql");
        return importDao.queryConfigById(id);
    }

    public List<String[]> queryImportHistory(String schema, String table) {
        ImportDao importDao = new ImportDao("ocnosql");
        List<Map> list = importDao.queryImportHistory(schema, table);
        List<String[]> datas = new ArrayList<String[]>();
        if(list != null) {
            for(Map map : list) {
                HashMap m = (HashMap) map;
                String[] data = {String.valueOf(m.get("id")), String.valueOf(m.get("tablex")),String.valueOf(m.get("schemax")), /*String.valueOf(m.get("loadType")), */
                        String.valueOf(m.get("status")), String.valueOf(m.get("importdate")),
                        String.valueOf(m.get("input")),String.valueOf(m.get("output")), String.valueOf(m.get("total")), String.valueOf(m.get("success_total")), String.valueOf(m.get("fail_total"))
                };
                datas.add(data);
            }
        }
        return datas;
    }

    public List<Map> queryIcBySchemaAndTable(ValueSet vs) {
        String schema = String.valueOf(vs.get("schema"));
        String table = String.valueOf(vs.get("table"));
        String cname = String.valueOf(vs.get("cname"));
        ImportDao importDao = new ImportDao("ocnosql");
        return importDao.queryIcBySchemaAndTable(schema, table, cname);
    }

    public List<Map> queryConfigBySchemaAndTable(ValueSet vs) {
        String schema = String.valueOf(vs.get("schema"));
        String table = String.valueOf(vs.get("table"));
        ImportDao importDao = new ImportDao("ocnosql");
        return importDao.queryConfigBySchemaAndTable(schema, table);
    }

    public boolean importData(ValueSet vs) throws Exception {

        boolean result = false;
        String schema = String.valueOf(vs.get("table_space"));
        String table = String.valueOf(vs.get("tables"));
        String config = String.valueOf(vs.get("config"));
        String inputPath = String.valueOf(vs.get("inputPath"));

        ImportDao importDao = new ImportDao("ocnosql");

        String now = DateUtil.dateToString(new Date(), "yyyyMMddHHmmssSSS");
        List<Map> ic = importDao.queryIcBySchemaAndTable(schema, table, config);
        if(ic != null) {
            HashMap<String, String> mapx = (HashMap) ic.get(0);
            //String loadType = mapx.get("loadType");
            String rowkeycolumn = "-Dimporttsv.rowkeycolumn=" + mapx.get("rowkey");
            String rowkeyGenerator = "-Dimporttsv.rowkeyGenerator=" + mapx.get("generator");
            String algocolumn = "-Dimporttsv.algocolumn=" + mapx.get("algocolumn");
            String outputPath = "-Dimporttsv.bulk.output=/" + now;
            //com.ailk.oci.ocnosql.common.rowkeygenerator.GenRKCallBackDefaultImpl
            String callback = "-Dimporttsv.callback=" + mapx.get("callback");
            String columns = "-Dimporttsv.columns=";
            CommonDao commonDao = new CommonDao();
            List<Map<String, Object>> results = commonDao.queryColumnsByTable(schema, table);
            StringBuffer sb = new StringBuffer();
            if(results != null && results.size() > 0) {
                for(Map<String, Object> map : results) {
                    String column = String.valueOf(map.get("COLUMN_NAME"));
                    if(column != null) {
                        sb.append("F:");
                        sb.append(column);
                        sb.append(",");
                    }
                }
            }
            if(sb.length() != 0) {
                columns = columns + sb.toString().substring(0, sb.toString().length()-1);
            }
            //写入日志
            ImportLog importLog = new ImportLog();
            importLog.setId(now);
            importLog.setImportdate(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            importLog.setInput(inputPath);
            importLog.setOutput(outputPath);
            importLog.setSchemax(schema);
            importLog.setTablex(table);
            importLog.setStatus("导入中");
            importDao.log(importLog);

            String[] args = {now, callback, "-Dimporttsv.bulk.erroroutput=true", columns, outputPath, rowkeycolumn, algocolumn, rowkeyGenerator,table, inputPath};
            new Thread(new runImport(args, now)).start();
        }
        return result;
    }

    private class runImport implements Runnable {
        private String[] args;
        private String logId;
        public runImport(String[] args, String logId) {
            this.args = args;
            this.logId = logId;
        }

        @Override
        public void run() {
            try {
                ImportMR.main(args);
                //importDao.updateLogStatus("导入成功", logId);
            } catch (Exception e) {
                ImportDao importDao = new ImportDao("ocnosql");
                importDao.updateCounter(logId, "导入失败", "0", "0", "0");
                e.printStackTrace();
            }
        }
    }
}