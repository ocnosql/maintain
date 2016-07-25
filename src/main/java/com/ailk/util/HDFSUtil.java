package com.ailk.util;

import com.ailk.model.DataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by wangkai8 on 16/7/8.
 */
public class HDFSUtil {

    static String defaultSeparator = "\t";

    public static DataInfo write(Configuration conf, ResultSet rs, String path) throws IOException {
        String uuid = UUID.randomUUID().toString();
        path = path + "/" + uuid;
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = null;
        try {
            out = fs.create(new Path(path));
            int count = rs.getMetaData().getColumnCount();
            String[] colums = new String[count];
            for(int i = 0; i < count; i++) {
                colums[i] = rs.getMetaData().getColumnName(i + 1);
            }
            StringBuffer sb = new StringBuffer();
            long totalCount = 0;
            while(rs.next()) {
                sb.setLength(0);
                for(int i = 1; i <= count; i++) {
                    sb.append(rs.getObject(i)).append(defaultSeparator);
                }
                sb.deleteCharAt(sb.length() -1);
                sb.append("\n");
                out.write(sb.toString().getBytes());
                totalCount ++;
            }
            DataInfo dataInfo = new DataInfo();
            dataInfo.setPath(path);
            dataInfo.setColumns(colums);
            dataInfo.setTotalCount(totalCount);
            dataInfo.setUuid(uuid);
            return dataInfo;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (Throwable e) {

                }
            }
            try {
                rs.close();
            } catch (Throwable e) {

            }
        }
    }


    public static List<Map> read(Configuration conf, String path, String[] columns, int start, int offset) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(new Path(path));
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        int index = 0;
        List<Map> list = new ArrayList<Map>();
        String line;
        while((line = reader.readLine()) != null) {
            if(index < start) {
                index ++;
                continue;
            }
            if(index >= start + offset) {
                break;
            }
            Map record = new LinkedHashMap<String, String>(columns.length);
            String[] values = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, defaultSeparator);
            for(int i = 0; i < columns.length; i++) {
                record.put(columns[i], values[i]);
            }
            list.add(record);
            index ++;
        }
        return list;
    }

    public static List<Map> readFiles(Configuration conf, String path, String[] columns, int start, int offset) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] files = fs.listStatus(new Path(path));
        int index = 0;
        List<Map> list = new ArrayList<Map>();
        String line;
        FSDataInputStream in;
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        //先默认是已经按照一定顺序排定的
        boolean isNextFile = true;
        for(int i=0;i<files.length;i++){
            if(files[i].isFile()) {
                in = fs.open(files[i].getPath());
                inputStreamReader = new InputStreamReader(in);
                reader = new BufferedReader(inputStreamReader);
                while((line = reader.readLine()) != null) {
                    if(index<start){
                        index ++;
                        continue;
                    }
                    if(index >= start + offset){
                        isNextFile = false;
                        break;
                    }
                    Map record = new LinkedHashMap<String, String>(columns.length);
                    String[] values = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, defaultSeparator);
                    for(int j = 0; j < columns.length; j++) {
                        record.put(columns[j], values[j]);
                    }
                    list.add(record);
                    index ++;
                }
                if(!isNextFile) break;
            }
        }
        return list;
    }


}
