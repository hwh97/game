package com.gdlinkjob.baselibrary.database;

import android.content.Context;

import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * Created by legendmohe on 16/8/31.
 */
public class PaperDBImpl implements DBImpl {

    private Book book;

    public PaperDBImpl(Context context, String dbName) {
        Paper.init(context);
        this.book = Paper.book(dbName);
    }

    @Override
    public <T> void set(String key, T value) {
        book.write(key, value);
    }

    @Override
    public <T> T get(String key) {
        return book.read(key);
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        return book.read(key, defaultValue);
    }

    @Override
    public void remove(String key) {
        book.delete(key);
    }

    @Override
    public void clear() {
        book.destroy();
    }

    @Override
    public List<String> allKeys() {
        return book.getAllKeys();
    }

    @Override
    public boolean contains(String key) {
        return book.exist(key);
    }
}
