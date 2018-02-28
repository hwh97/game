package com.gdlinkjob.baselibrary.database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by legendmohe on 16/8/31.
 */
public class NoSQL {

    private static Context mContext;
    private DBImpl mDBImpl;

    private static final ConcurrentHashMap<String, NoSQL> mBookMap = new ConcurrentHashMap<>();

    public  static String DEFAULT_DB_NAME = "com.gdlinkjob.database";

    private NoSQL(Context context, String dbName) {
        mDBImpl = new PaperDBImpl(context, dbName);
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }
    public static void init(Context context,String dataBaseName) {
        mContext = context.getApplicationContext();
        DEFAULT_DB_NAME=dataBaseName;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new NullPointerException("context is null");
        }
        return mContext;
    }

    private DBImpl getDBImpl() {
        if (mDBImpl == null) {
            throw new NullPointerException("DBImpl is null");
        }
        return mDBImpl;
    }


    /**
     * Returns paper book instance with the given name
     *
     * @param name name of new database
     * @return Paper instance
     */
    public static NoSQL db(String name) {
        return getDB(name);
    }

    public static NoSQL db(int name) {
        return getDB(name+"");
    }

    /**
     * Returns default paper book instance
     *
     * @return Book instance
     */
    public static NoSQL db() {
        return getDB(DEFAULT_DB_NAME);
    }

    private static NoSQL getDB(String name) {
        synchronized (mBookMap) {
            NoSQL noSQLDB = mBookMap.get(name);
            if (noSQLDB == null) {
                noSQLDB = new NoSQL(mContext, name);
                mBookMap.put(name, noSQLDB);
            }
            return noSQLDB;
        }
    }

    ///////////////////////////////////function///////////////////////////////////

    public <T> void set(String key, T value) {
        getDBImpl().set(key, value);
    }

    public <T> T get(String key) {
        return getDBImpl().get(key);
    }

    public <T> T get(String key, T defaultValue) {
        return getDBImpl().get(key, defaultValue);
    }

    public void remove(String key) {
        getDBImpl().remove(key);
    }

    public boolean contains(String key) {
        return getDBImpl().contains(key);
    }

    public List<String> allKeys() {
        return getDBImpl().allKeys();
    }

    public void clear() {
        getDBImpl().clear();
    }

}
