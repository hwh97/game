package com.gdlinkjob.baselibrary.database;

import java.util.List;

/**
 * Created by legendmohe on 16/8/31.
 */
public interface DBImpl {
    <T> void set(String key, T value);

    <T> T get(String key);

    <T> T get(String key, T defaultValue);

    void remove(String key);

    void clear();

    List<String> allKeys();

    boolean contains(String key);

}
