/**
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ailk.service.impl;

import com.ailk.dao.ImportDao;
import com.ailk.oci.ocnosql.common.compress.HbaseNullCompress;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.oci.ocnosql.common.config.OciTableRef;
import com.ailk.oci.ocnosql.common.config.TableConfiguration;
import com.ailk.oci.ocnosql.common.exception.BulkLoadException;
import com.ailk.oci.ocnosql.common.exception.ClientRuntimeException;
import com.ailk.oci.ocnosql.common.exception.ConfigException;
import com.ailk.oci.ocnosql.common.util.CommonConstants;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.PutSortReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Tool to import data from a TSV file.
 *
 * This tool is rather simplistic - it doesn't do any quoting or
 * escaping, but is useful for many data loads.
 *
 */
public class ImportMR {
    static Log LOG = LogFactory.getLog(ImportMR.class);
    public final static String NAME = "mutipleimporttsv";
    public final static String CONF_FILE = "client-runtime.properties";
    //public static HashMap<String, String> results = new HashMap<String, String>();
    public static String FAILED_REASON = "failed_reason";
    private String msg;
    private Map<String, Object> retMap = new HashMap<String, Object>();

    static final String COMPRESSION_CONF_KEY = "hbase.hfileoutputformat.families.compression";
    final static String MAPPER_CONF_KEY = "importtsv.mapper.class";
    final static String TIMESTAMP_CONF_KEY = "importtsv.timestamp";
    final static Class DEFAULT_MAPPER = ImportMapper.class;
    final static String DEFAULT_COMPRESSOR = HbaseNullCompress.class.getName();
    private static HBaseAdmin hbaseAdmin;

    static class TsvParser {
        /**             sd
         * Column families and qualifiers mapped to the TSV columns
         */
        private final byte[][] families;
        private final byte[][] qualifiers;

        private final byte separatorByte;

        public TsvParser(String columnsSpecification, String separatorStr) {
            // Configure separator
            byte[] separator = Bytes.toBytes(separatorStr);
            Preconditions.checkArgument(separator.length == 1,
                    "TsvParser only supports single-byte separators");
            separatorByte = separator[0];

            // Configure columns
            ArrayList<String> columnStrings = Lists.newArrayList(
                    Splitter.on(',').trimResults().split(columnsSpecification));

            families = new byte[columnStrings.size()][];
            qualifiers = new byte[columnStrings.size()][];

            for (int i = 0; i < columnStrings.size(); i++) {
                String str = columnStrings.get(i);
                String[] parts = str.split(":", 2);
                if (parts.length == 1) {
                    families[i] = str.getBytes();
                    qualifiers[i] = HConstants.EMPTY_BYTE_ARRAY;
                } else {
                    families[i] = parts[0].getBytes();
                    qualifiers[i] = parts[1].getBytes();
                }
            }
        }

        public byte[] getFamily(int idx) {
            return families[idx];
        }
        public byte[] getQualifier(int idx) {
            return qualifiers[idx];
        }

        public ParsedLine parse(byte[] lineBytes, int length)
                throws BadTsvLineException {
            // Enumerate separator offsets
            ArrayList<Integer> tabOffsets = new ArrayList<Integer>(families.length);
            for (int i = 0; i < length; i++) {
                if (lineBytes[i] == separatorByte) {
                    tabOffsets.add(i);
                }
            }
            if (tabOffsets.isEmpty()) {
                throw new BadTsvLineException("No delimiter");
            }

            tabOffsets.add(length);

            if (tabOffsets.size() != families.length) {
                throw new BadTsvLineException("Excessive columns");
            }
            return new ParsedLine(tabOffsets, lineBytes);
        }

        class ParsedLine {
            private final ArrayList<Integer> tabOffsets;
            private byte[] lineBytes;

            ParsedLine(ArrayList<Integer> tabOffsets, byte[] lineBytes) {
                this.tabOffsets = tabOffsets;
                this.lineBytes = lineBytes;
            }

            public int getColumnOffset(int idx) {
                if (idx > 0)
                    return tabOffsets.get(idx - 1) + 1;
                else
                    return 0;
            }
            public int getColumnLength(int idx) {
                return tabOffsets.get(idx) - getColumnOffset(idx);
            }
            public int getColumnCount() {
                return tabOffsets.size();
            }
            public byte[] getLineBytes() {
                return lineBytes;
            }
        }

        public static class BadTsvLineException extends Exception {
            public BadTsvLineException(String err) {
                super(err);
            }
            private static final long serialVersionUID = 1L;
        }
    }

    /**
     * Sets up the actual job.
     *
     * @param conf  The current configuration.
     * @return The newly created job.
     * @throws IOException When setting up the job fails.
     */
    public static Job createSubmittableJob(Configuration conf, String tableName, String inputPath, String tmpOutputPath)
            throws IOException, ClassNotFoundException {

        // Support non-XML supported characters
        // by re-encoding the passed separator as a Base64 string.
        String actualSeparator = conf.get(CommonConstants.SEPARATOR);
        if (actualSeparator != null) {
            conf.set(CommonConstants.SEPARATOR,
                    Base64.encodeBytes(actualSeparator.getBytes()));
        }
        String tableNameConf = conf.get(CommonConstants.TABLE_NAME);
        if (tableNameConf == null) {
            conf.set(CommonConstants.TABLE_NAME,tableName);
        }

        // See if a non-default Mapper was set
        String mapperClassName = conf.get(MAPPER_CONF_KEY);
        Class mapperClass = mapperClassName != null ?
                Class.forName(mapperClassName) : DEFAULT_MAPPER;
        LOG.info("---bulkload mapper class is ---"+mapperClassName);

        Path inputDir = new Path(inputPath);
        Job job = new Job(conf, NAME + "_" + tableName);
        job.setJarByClass(ImportMR.class);
        FileInputFormat.setInputPaths(job, inputDir);

        //根据参数Dimporttsv.inputFormat设置InputFormat,默认是TextInputFormat
        String inputFmtName = conf.get(CommonConstants.INPUTFORMAT, "org.apache.hadoop.mapreduce.lib.input.TextInputFormat");
        LOG.info(CommonConstants.INPUTFORMAT+" is "+inputFmtName);
        Class inputFmtClass = Class.forName(inputFmtName);
        job.setInputFormatClass(inputFmtClass);
        job.setJar("/wsh/ImportMR.jar");
        job.setJarByClass(ImportMR.class);
        job.setMapperClass(mapperClass);
        job.setReducerClass(ImportReduce.class);

        String hfileOutPath = tmpOutputPath;
        if (hfileOutPath != null) {
            if (!doesTableExist(tableName)) {
                createTable(conf, tableName);
            }
            HTable table = new HTable(conf, tableName);
            Path outputDir = new Path(hfileOutPath);
            FileOutputFormat.setOutputPath(job, outputDir);
            job.setMapOutputKeyClass(ImmutableBytesWritable.class);
            job.setMapOutputValueClass(Put.class);
//      job.setCombinerClass(PutCombiner.class);
            HFileOutputFormat.configureIncrementalLoad(job, table);
        } else {
            // No reducers.  Just write straight to table.  Call initTableReducerJob
            // to set up the TableOutputFormat.
            TableMapReduceUtil.initTableReducerJob(tableName, null, job);
            job.setNumReduceTasks(0);
        }

        TableMapReduceUtil.addDependencyJars(job);
        TableMapReduceUtil.addDependencyJars(job.getConfiguration(),
                com.google.common.base.Function.class /* Guava used by TsvParser */);
        return job;
    }

    private static boolean doesTableExist(String tableName) throws IOException {
        return hbaseAdmin.tableExists(tableName.getBytes());
    }

    private static void createTable(Configuration conf, String tableName)
            throws IOException {
        HTableDescriptor htd = new HTableDescriptor(tableName.getBytes());
        String columns[] = conf.getStrings(CommonConstants.COLUMNS);
        Set<String> cfSet = new HashSet<String>();
        for (String aColumn : columns) {
            // if (TsvParser.ROWKEY_COLUMN_SPEC.equals(aColumn)) continue;
            // we are only concerned with the first one (in case this is a cf:cq)
            cfSet.add(aColumn.split(":", 2)[0]);
        }
        for (String cf : cfSet) {
            HColumnDescriptor hcd = new HColumnDescriptor(Bytes.toBytes(cf));
            htd.addFamily(hcd);
        }
        hbaseAdmin.createTable(htd);
    }

    /*
     * @param errorMsg Error message.  Can be null.
     */
    private static void usage(final String errorMsg) {
        if (errorMsg != null && errorMsg.length() > 0) {
            System.err.println("ERROR: " + errorMsg);
        }
        String usage =
                "Usage: " + NAME + " -Dimporttsv.columns=a,b,c <tablename> <inputdir>\n" +
                        "\n" +
                        "Imports the given input directory of TSV data into the specified table.\n" +
                        "\n" +
                        "The column names of the TSV data must be specified using the -Dimporttsv.columns\n" +
                        "option. This option takes the form of comma-separated column names, where each\n" +
                        "column name is either a simple column family, or a columnfamily:qualifier. The special\n" +
                        "column name HBASE_ROW_KEY is used to designate that this column should be used\n" +
                        "as the row key for each imported record. You must specify exactly one column\n" +
                        "to be the row key, and you must specify a column name for every column that exists in the\n" +
                        "input data.\n" +
                        "\n" +
                        "By default importtsv will load data directly into HBase. To instead generate\n" +
                        "HFiles of data to prepare for a bulk data load, pass the option:\n" +
                        "  -D" + CommonConstants.IMPORT_TMP_OUTPUT + "=/path/for/output\n" +
                        "  Note: if you do not use this option, then the target table must already exist in HBase\n" +
                        "\n" +
                        "Other options that may be specified with -D include:\n" +
                        "  -D" + CommonConstants.SKIPBADLINE + "=false - fail if encountering an invalid line\n" +
                        "  '-D" + CommonConstants.SEPARATOR + "=|' - eg separate on pipes instead of tabs\n" +
                        "  -D" + TIMESTAMP_CONF_KEY + "=currentTimeAsLong - use the specified timestamp for the import\n" +
                        "  -D importtsv.bulk.insertvertical=true = - use vertical line to separate rowkey columns\n" +
                        "  -Dimporttsv.indexrowkeycolumn=  - use indexrowkeycolumn to generate index rowkey;default is index column|table rowkey\n"+
                        "  -Dimporttsv.indexcolumns=  - use to specify \n"+
                        "  -D importtsv.indextablename= - use to specify the index table name \n" +
                        "  -D importtsv.bulk.indexoutput    /path/for/index/output/\n" +
                        "  -D" + MAPPER_CONF_KEY + "=my.Mapper - A user-defined Mapper to use instead of " + DEFAULT_MAPPER.getName() + "\n" +
                        "For performance consider the following options:\n" +
                        "  -Dmapred.map.tasks.speculative.execution=false\n" +
                        "  -Dmapred.reduce.tasks.speculative.execution=false";

        System.err.println(usage);
    }

    /**
     * Used only by test method
     * @param conf
     */
    static void createHbaseAdmin(Configuration conf) throws IOException {
        hbaseAdmin = new HBaseAdmin(conf);
    }

    public boolean execute(Connection conn, OciTableRef table) {
        if(conn == null){
            msg = "Connection object must not be null";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ClientRuntimeException(msg);
        }
        Configuration conf = conn.getConf();
        if (table == null) {
            msg = "table must not be null";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ClientRuntimeException(msg);
        }

        String tableName = table.getName();
        String column = table.getColumns();
        String seperator = table.getSeperator();
        String inputPath = table.getInputPath();
        String tmpOutPut = table.getImportTmpOutputPath();
        String skipBadLine = table.getSkipBadLine();
        String compressor = table.getCompressor();
        String rowkeyUnique = table.getRowKeyUnique();
        String algoColumn = table.getAlgoColumn();
        String rowkeyGenerator = table.getRowkeyGenerator();
        String rowkeyColumn = table.getRowkeyColumn();
        String callback = table.getCallback();

        if(StringUtils.isEmpty(tableName)){
            msg = "No " + CommonConstants.TABLE_NAME + " specified. Please check config,then try again after refreshing cache";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ConfigException(msg);
        }
        conf.set(CommonConstants.TABLE_NAME, tableName);

        if(StringUtils.isEmpty(seperator)){
            msg = "No " + CommonConstants.SEPARATOR + " specified. Please check config,then try again after refreshing cache";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ConfigException(msg);
        }
        conf.set(CommonConstants.SEPARATOR, seperator);

        // Make sure columns are specified
        String columns[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(column, ",");
        if (columns == null) {
            msg = "No " + CommonConstants.COLUMNS + " specified. Please check config,then try again after refreshing cache";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ConfigException(msg);
        }
        conf.set(CommonConstants.COLUMNS, column);

//		int rowkeysFound = 0;
//		for (String col : columns) {
//			if (col.equals(CommonConstants.ROW_KEY))
//				rowkeysFound++;
//		}
//		if (rowkeysFound != 1) {
//			msg = "Must specify exactly one column as " + CommonConstants.ROW_KEY + ". Please check config,then again after refreshing cache";
//			retMap.put(FAILED_REASON, msg);
//			LOG.error(msg);
//			throw new ConfigException(msg);
//		}

        if (columns.length < 2) {
            msg = "One or more columns in addition to the row key are required. Please check config,then try again after refreshing cache";
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ConfigException(msg);
        }

        String[] columnTmp = null;
        for (int i = 0; i < columns.length; i++) {
            columnTmp = columns[i].split(":");
            if (columnTmp != null && columnTmp.length == 2) {
                break;
            }
        }
        conf.set(CommonConstants.SINGLE_FAMILY, columnTmp[0]);
        if(!StringUtils.isEmpty(skipBadLine)){
            conf.set(CommonConstants.SKIPBADLINE, skipBadLine);
        }
        //压缩方式
        conf.set(CommonConstants.COMPRESSOR, (compressor == null)?DEFAULT_COMPRESSOR:compressor);
        conf.set(CommonConstants.ALGOCOLUMN, algoColumn);
        conf.set(CommonConstants.ROWKEY_GENERATOR, rowkeyGenerator);
        conf.set(CommonConstants.ROWKEYCOLUMN, rowkeyColumn);
        conf.set(CommonConstants.ROWKEYCALLBACK, callback);

        boolean ret = false;
        Counter failCounter = null;
        try {
            hbaseAdmin = new HBaseAdmin(conf);
            TableConfiguration.getInstance().writeTableConfiguration(tableName, column, seperator, conf);
            conf.set(CommonConstants.TABLE_NAME,tableName);
            String hdfs_url = conf.get(CommonConstants.HDFS_URL);
            FileSystem fs =  FileSystem.get(URI.create(hdfs_url),conf);
            FileStatus[] fileStatusArr = fs.listStatus(new Path(hdfs_url + inputPath));
            if(fileStatusArr != null && fileStatusArr.length > 0){
                if(fileStatusArr[0].isFile()){
                    ret = (Boolean)runJob(conf, tableName, inputPath, tmpOutPut)[0];
                }
                int inputPathNum = 0;
                for(FileStatus everyInputPath : fileStatusArr){
                    Path inputPathStr = everyInputPath.getPath();
                    String absoluteInputPathStr =  inputPath + "/"+ inputPathStr.getName();
                    boolean retCode = (Boolean)runJob(conf, tableName, absoluteInputPathStr, tmpOutPut+"/"+ inputPathStr.getName())[0];
                    if(retCode){
                        String base64Seperator = conf.get(CommonConstants.SEPARATOR);
                        conf.set(CommonConstants.SEPARATOR,new String(Base64.decode(base64Seperator))); //重置separator
                        if(inputPathNum == fileStatusArr.length-1){
                            ret = true;
                        }
                        inputPathNum++;
                        continue;
                    }else{ //出现错误
                        ret = false;
                        inputPathNum++;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            msg = "job execute failed,nested exception is " + e;
            retMap.put(FAILED_REASON, msg);
            LOG.error(msg);
            throw new ClientRuntimeException(msg);
        }

        if(!ret){
            msg = "execute job failed,please check map/reduce log in jobtracker page";
            retMap.put(FAILED_REASON, msg);
            return false;
        }
        return true;
    }

    /**
     * Main entry point.
     *
     * @param args  The command line parameters.
     * @throws Exception When running the job fails.
     */
    public static void main(String[] args) throws Exception {
        long inputLineNum  = 0L;
        long badLineNum = 0L;
        long outputLineNum = 0L;

        String id = args[0];
        String[] params = new String[args.length-1];
        for(int i=1; i<args.length; i++) {
            params[i-1] = args[i];
        }
        Configuration conf = HBaseConfiguration.create();
        String[] otherArgs = new GenericOptionsParser(conf, params).getRemainingArgs();
        if (otherArgs.length < 2) {
            usage("Wrong number of arguments: " + otherArgs.length);
            return;
        }
        LOG.info("------------INDEXALGOCOLUMN:" +  conf.get(CommonConstants.INDEXALGOCOLUMN));
        // Make sure columns are specified
        String columns = conf.get(CommonConstants.COLUMNS);
        if (columns == null) {
            usage("No columns specified. Please specify with -D" + CommonConstants.COLUMNS+"=...");
            return;
        }
        String seperator = conf.get(CommonConstants.SEPARATOR);
        if(StringUtils.isEmpty(seperator)){
            conf.set(CommonConstants.SEPARATOR, CommonConstants.DEFAULT_SEPARATOR);
            seperator = CommonConstants.DEFAULT_SEPARATOR;
        }
        // Make sure one or more columns are specified
        if (columns.split(",").length < 2) {
            usage("One or more columns in addition to the row key are required");
            return;
        }
        //make sure tableName and columns are upper to used by phoenix.
        columns = columns.toUpperCase();
        String notNeedLoadColumnsStr = conf.get(CommonConstants.NOTNEEDLOADCOLUMNS);
        String notNeedLoadColumns = null;
        if(!StringUtils.isEmpty(notNeedLoadColumnsStr)){
            notNeedLoadColumns = notNeedLoadColumnsStr.toUpperCase();
            conf.set(CommonConstants.NOTNEEDLOADCOLUMNS,notNeedLoadColumns);
        }

        String writeTableConfigColumns = getWriteConfigColumn(columns,notNeedLoadColumns);
        hbaseAdmin = new HBaseAdmin(conf);
        String tableName = otherArgs[0].toUpperCase();
        String inputPath = otherArgs[1];
        String tmpOutputPath = conf.get(CommonConstants.IMPORT_TMP_OUTPUT);
        conf.set(CommonConstants.TABLE_NAME,tableName);
        conf.set(CommonConstants.COLUMNS,columns);
        String pathStr = conf.get(CommonConstants.HDFS_URL) + inputPath;
        FileSystem fs =  FileSystem.get(URI.create(conf.get(CommonConstants.HDFS_URL)),conf);
        FileStatus[] fileStatusArr = fs.listStatus(new Path(pathStr));
        TableConfiguration.getInstance().writeTableConfiguration(tableName, writeTableConfigColumns,seperator,conf);//写配置文件
        if(fileStatusArr != null && fileStatusArr.length > 0){
            if(fileStatusArr[0].isFile()){
                Object[] resObjs = runJob(conf, tableName, inputPath, tmpOutputPath);
                inputLineNum = (Long)resObjs[1];
                outputLineNum = (Long)resObjs[2];
                badLineNum = (Long)resObjs[3];
                ImportDao importDao = new ImportDao("ocnosql");
                importDao.updateCounter(id, "导入成功", String.valueOf(inputLineNum), String.valueOf(outputLineNum), String.valueOf(badLineNum));
                //importDao.updateCounter("20160815080159655", "1000", "1000", "100");

                //results.put(id+"_total", String.valueOf(inputLineNum));
                //results.put(id+"_success_total", String.valueOf(outputLineNum));
                //results.put(id+"_fail_total", String.valueOf(badLineNum));
                LOG.info("Bulkloadx Result={inputLine:"+inputLineNum+",outputLine:"+outputLineNum+",badLine:"+badLineNum+"}");
                boolean result = (Boolean)resObjs[0];
                if(result){
                    return;
                }
               return;
            }
            for(FileStatus everyInputPath : fileStatusArr){
                Path inputPathStr = everyInputPath.getPath();
                String absoluteInputPathStr =  inputPath + "/"+ inputPathStr.getName();
                FileStatus[] subFileStatusArr = fs.listStatus(new Path(conf.get(CommonConstants.HDFS_URL) + absoluteInputPathStr));
                if(subFileStatusArr == null || subFileStatusArr.length==0)//如果目录为空则不用起job
                    continue;
                Object[] resObjs =  runJob(conf, tableName, absoluteInputPathStr, tmpOutputPath+"/"+ inputPathStr.getName());
                boolean ret = (Boolean)resObjs[0];
                if(ret){
                    inputLineNum += (Long)resObjs[1];
                    outputLineNum += (Long)resObjs[2];
                    badLineNum += (Long)resObjs[3];
                    String seperatorStr = conf.get(CommonConstants.SEPARATOR);
                    conf.set(CommonConstants.SEPARATOR,new String(Base64.decode(seperatorStr))); //重置separator
                    continue;
                }else{ //出现错误
                    LOG.error("Bulkload Result={inputLine:"+inputLineNum+",outputLine:"+outputLineNum+",badLine:"+badLineNum+"}");
                    return;
                }
            }
            LOG.info("Bulkload1 Result={inputLine:"+inputLineNum+",outputLine:"+outputLineNum+",badLine:"+badLineNum+"}");
        }
        LOG.info("Bulkload2 Result={inputLine:"+inputLineNum+",outputLine:"+outputLineNum+",badLine:"+badLineNum+"}");
    }

    private static Object[] runJob(Configuration conf, String tableName, String inputPath, String tmpOutputPath) throws Exception {
        Long everyJobInputLine = 0L;
        Long everyJobOutputLine = 0L;
        Long everyJobBadLine = 0L;
        Job job = null;
        try{
            job = createSubmittableJob(conf, tableName, inputPath, tmpOutputPath);
        }catch (Exception e){
            System.err.println("ERROR:mutiplecolumn bulkload when create submittableJob error is :"
                    + e.fillInStackTrace());
            return new Object[]{false,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
        }
        boolean completion = false;
        try{
            if(job == null)  return new Object[]{false,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
            completion = job.waitForCompletion(true);
            everyJobBadLine = job.getCounters().getGroup("ImportTsv").findCounter("Bad Lines").getValue();
            everyJobInputLine = job.getCounters().getGroup("ImportTsv").findCounter("total Lines").getValue();
            everyJobOutputLine = everyJobInputLine - everyJobBadLine;

        }catch (Exception e){
            System.err.println("ERROR:mutiplecolumn bulkload when execute Job error is :"+
                    e.fillInStackTrace());
            return new Object[]{false,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
        }
        try{
            if(completion && !StringUtils.isEmpty(tmpOutputPath)){
                String[] toolRunnerArgs = new String[]{tmpOutputPath,tableName};
                int ret = ToolRunner.run(new LoadIncrementalHFiles(conf), toolRunnerArgs);
                return new Object[]{ret==0,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
            }else{
                return new Object[]{false,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
            }
        }catch (Exception e){
            System.err.println("ERROR:mutiplecolumn bulkload when LoadIncrementalHFiles error is :"
                    + e.fillInStackTrace());
            return new Object[]{false,everyJobInputLine,everyJobOutputLine,everyJobBadLine};
        }
    }

    public Map<String, Object> getReturnMap() {
        return retMap;
    }

    public static String getWriteConfigColumn(String columns,String notNeedLoadColumns) throws BulkLoadException{
        String writeTableConfigColumns;
        if(!StringUtils.isEmpty(notNeedLoadColumns)){
            String[] columnArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(columns,",");
            String[] writeTableConfigColumnArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(notNeedLoadColumns,",");
            StringBuilder bud = new StringBuilder();
            for(String column:columnArr){
                boolean isWrite = true;
                for(String writeTableConfigColumn : writeTableConfigColumnArr){
                    if(column.equals(writeTableConfigColumn)){
                        isWrite = false;
                        break;
                    }
                }
                if(isWrite){
                    bud.append(column).append(",");
                }
            }
            writeTableConfigColumns = bud.substring(0,bud.length()-1);
        }else{
            writeTableConfigColumns = columns;
        }
        return writeTableConfigColumns;
    }
}