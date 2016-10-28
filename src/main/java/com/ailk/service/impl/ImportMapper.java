/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ailk.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ailk.oci.ocnosql.tools.load.mutiple.MutipleColumnImportTsv;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import com.ailk.oci.ocnosql.common.config.TableConfiguration;
import com.ailk.oci.ocnosql.common.rowkeygenerator.GenRKStep;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGeneratorException;
import com.ailk.oci.ocnosql.common.rowkeygenerator.TableRowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.TableRowKeyGeneratorIndex;
import com.ailk.oci.ocnosql.common.util.CommonConstants;

/**
 * Write table content out to files in hdfs.
 */
public class ImportMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

//   Logger LOG = LoggerFactory.getLogger(MutipleColumnImporterMapper.class);
//   Log log =  LogFactory.getLog(MutipleColumnImporterMapper.class);
//   org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MutipleColumnImporterMapper.class);

    private static Log log = LogFactory.getLog(ImportMapper.class);

    /** Timestamp for all inserted rows */
    private long ts;

    /** Column seperator */
    private String separator;

    private Counter totalLineCount;

    /** Should skip bad lines */
    private boolean skipBadLines;
    private Counter badLineCount;
    private TableRowKeyGenerator rowkeyGenerator;
    private TableRowKeyGeneratorIndex rowkeyGeneratorIndex;
    private boolean isIndex = false;
    private long index;

    private String tableName;

    private ImportMR.TsvParser parser;

    private Set<String> notNeedLoadColumnQulifiers = new HashSet<String>();

    public long getTs() {
        return ts;
    }

    public boolean getSkipBadLines() {
        return skipBadLines;
    }

    public Counter getBadLineCount() {
        return badLineCount;
    }

    public void incrementBadLineCount(int count) {
        this.badLineCount.increment(count);
    }

    public Counter getTotalLineCount() {
        return totalLineCount;
    }

    public void incrementTotalLineCount(int count) {
        this.totalLineCount.increment(count);
    }

    /**
     * Handles initializing this class with objects specific to it (i.e., the parser).
     * Common initialization that might be leveraged by a subsclass is done in
     * <code>doSetup</code>. Hence a subclass may choose to override this method
     * and call <code>doSetup</code> as well before handling it's own custom params.
     *
     * @param context
     */
    @Override
    protected void setup(Context context) {
        doSetup(context);
        Configuration conf = context.getConfiguration();
        String columnsDefi = conf.get(CommonConstants.COLUMNS);
        parser = new ImportMR.TsvParser(columnsDefi, separator);
        String notNeedLoadColumns = conf.get(CommonConstants.NOTNEEDLOADCOLUMNS);
        if (!StringUtils.isEmpty(notNeedLoadColumns)) {
            String[] notNeedLoadColumnArr = notNeedLoadColumns.split(",");
            for (int i = 0; i < notNeedLoadColumnArr.length; i++) {
                if (notNeedLoadColumnArr[i].contains("F:")) {
                    notNeedLoadColumnQulifiers.add(notNeedLoadColumnArr[i].substring(2));
                } else {
                    notNeedLoadColumnQulifiers.add(notNeedLoadColumnArr[i]);
                }
            }
        }
    }

    /**
     * Handles common parameter initialization that a subclass might want to leverage.
     * @param context
     */
    protected void doSetup(Context context) {
        Configuration conf = context.getConfiguration();

        // If a custom separator has been used,
        // decode it back from Base64 encoding.
        separator = conf.get(CommonConstants.SEPARATOR);
        if (separator == null) {
            separator = CommonConstants.DEFAULT_SEPARATOR;
        } else {
            separator = new String(Base64.decode(separator));
        }

//    ts = conf.getLong(ImportTsv.TIMESTAMP_CONF_KEY, System.nanoTime());//currentTimeMillis());

        skipBadLines = context.getConfiguration().getBoolean(
                CommonConstants.SKIPBADLINE, true);
        badLineCount = context.getCounter("ImportTsv", "Bad Lines");
        totalLineCount = context.getCounter("ImportTsv", "total Lines");
    /* //rowkey的生成规则现在由 tableRowKeyGenerator 来控制它的生成
    String rowkeyGennerator = context.getConfiguration().get(CommonConstants.ROWKEY_GENERATOR);
    if(RowKeyGeneratorHolder.TYPE.md5.name().equalsIgnoreCase(rowkeyGennerator)){
    	rowkeyGenerator = new MD5RowKeyGenerator();
    }
    */
        tableName = conf.get(CommonConstants.TABLE_NAME);
        List<GenRKStep> genRKStepList = TableConfiguration.getInstance().getTableGenRKSteps(tableName, conf);
        rowkeyGenerator = new TableRowKeyGenerator(conf, genRKStepList);
        if (!StringUtils.isEmpty(conf.get(CommonConstants.INDEXROWKEYCOLUMN))) {
            isIndex = true;
            rowkeyGeneratorIndex = new TableRowKeyGeneratorIndex(conf, genRKStepList);
        }

        index = 0;
    }

    /**
     * Convert a line of TSV text into an HBase table row.
     */
    @Override
    public void map(LongWritable offset, Text value, Context context) throws IOException {
        byte[] lineBytes = value.getBytes();
        ts = System.currentTimeMillis();

        try {
            ImportMR.TsvParser.ParsedLine parsed = parser.parse(lineBytes, value.getLength());
            String newRowKey = rowkeyGenerator.generateByGenRKStep(value.toString(), false);//由配置文件的规则来直接生成rowkey
            if (isIndex) {
                Set<String> indexRowkeys = rowkeyGeneratorIndex.generateByGenRKStep(value.toString(), false);
                log.info("-----------------indexRowkeys.size:" + indexRowkeys.size());

                for (String indexRowkey : indexRowkeys) {
                    Put put = null;
                    if("true".equals(context.getConfiguration().get(CommonConstants.INSERTVERTICAL))) {
                        put = new Put((indexRowkey + "|" + newRowKey).getBytes());
                    } else {
                        put = new Put((indexRowkey + newRowKey).getBytes());
                    }
                    for (int i = 0; i < parsed.getColumnCount(); i++) {
                        String columnQualifierStr = new String(parser.getQualifier(i));
                        String rowStr = newRowKey + new String(parser.getFamily(i) + columnQualifierStr);
                        if (notNeedLoadColumnQulifiers.contains(columnQualifierStr)) {
                            continue;
                        }
                        KeyValue kv = new KeyValue(
                                rowStr.getBytes(), 0, newRowKey.getBytes().length,   //roffset,rofflength
                                parser.getFamily(i), 0, parser.getFamily(i).length,
                                parser.getQualifier(i), 0, parser.getQualifier(i).length,
                                ts,
                                KeyValue.Type.Put,
                                lineBytes, parsed.getColumnOffset(i), parsed.getColumnLength(i));

                        KeyValue newKv = null;
                        if("true".equals(context.getConfiguration().get(CommonConstants.INSERTVERTICAL))) {
                            newKv = new KeyValue((indexRowkey + "|" + newRowKey).getBytes(), kv.getFamily(), kv.getQualifier(), ts, new byte[0]);
                        } else {
                            newKv = new KeyValue((indexRowkey + newRowKey).getBytes(), kv.getFamily(), kv.getQualifier(), ts, new byte[0]);
                        }
                        kv = null;
                        put.add(newKv);
                    }

                    if("true".equals(context.getConfiguration().get(CommonConstants.INSERTVERTICAL))) {
                        context.write(new ImmutableBytesWritable((indexRowkey + "|" + newRowKey).getBytes()), put);
                    } else {
                        context.write(new ImmutableBytesWritable((indexRowkey + newRowKey).getBytes()), put);
                    }
                }
            } else {
                Put put = new Put(newRowKey.getBytes());
                for (int i = 0; i < parsed.getColumnCount(); i++) {
                    String columnQualifierStr = new String(parser.getQualifier(i));
                    String rowStr = newRowKey + new String(parser.getFamily(i) + columnQualifierStr);
                    if (notNeedLoadColumnQulifiers.contains(columnQualifierStr)) {
                        continue;
                    }
                    KeyValue kv = new KeyValue(
                            rowStr.getBytes(), 0, newRowKey.getBytes().length,   //roffset,rofflength
                            parser.getFamily(i), 0, parser.getFamily(i).length,
                            parser.getQualifier(i), 0, parser.getQualifier(i).length,
                            ts,
                            KeyValue.Type.Put,
                            lineBytes, parsed.getColumnOffset(i), parsed.getColumnLength(i));

                    KeyValue newKv = new KeyValue(newRowKey.getBytes(), kv.getFamily(), kv.getQualifier(), ts, kv.getValue());
                    kv = null;
                    put.add(newKv);
                }
                context.write(new ImmutableBytesWritable(newRowKey.getBytes()), put);
            }
        } catch (ImportMR.TsvParser.BadTsvLineException badLine) {
            if (skipBadLines) {
                System.err.println(
                        "Bad line at offset: " + offset.get() + ":\n" +
                                badLine.getMessage());
                incrementBadLineCount(1);
                return;
            } else {
                throw new IOException(badLine);
            }
        } catch (IllegalArgumentException e) {
            if (skipBadLines) {
                System.err.println(
                        "Bad line at offset: " + offset.get() + ":\n" + e.getMessage());
                incrementBadLineCount(1);
                return;
            } else {
                throw new IOException(e);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RowKeyGeneratorException e) {
            System.err.println(
                    "gen rowkey error, please check config in the ocnosqlTab.xml." + e.getMessage());
            throw new IOException(e);
        } finally {
            totalLineCount.increment(1);
        }
    }
}
