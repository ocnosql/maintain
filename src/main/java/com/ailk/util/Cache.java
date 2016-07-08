package com.ailk.util;

import com.ailk.model.DataInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangkai8 on 16/7/8.
 */
public class Cache {

    public static final Map<String, DataInfo> cache = new HashMap<String, DataInfo>();

    public static void put(String key, DataInfo dataInfo) {
        cache.put(key, dataInfo);
    }

    public static DataInfo get(String key) {
        return cache.get(key);
    }
}
