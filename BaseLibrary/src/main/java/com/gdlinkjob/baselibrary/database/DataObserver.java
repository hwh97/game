package com.gdlinkjob.baselibrary.database;

/**
 * Created by legendmohe on 16/4/27.
 */
public interface DataObserver<T> {
    void onDataUpdated(T item);

    void onDataAdded(T item);

    void onDataRemoved(T item);
}
