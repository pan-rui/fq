package com.hy.core;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2015/9/10.
 */
public class ParamsMap<K,V> extends LinkedHashMap<K, V> {
    public static ParamsMap newMap(String key, Object value) {
        return new ParamsMap().addParams(key, value);
    }
    public ParamsMap addParams(K key, V value) {
        put(key, value);
        return this;
    }
}
