package com.gdlinkjob.baselibrary.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by legendmohe on 16/4/27.
 */
public abstract class DataManager<K, V> {

    private final int MSG_ITEM_ADD = 0;
    private final int MSG_ITEM_REMOVE = 1;
    private final int MSG_ITEM_UPDATE = 2;

    private Context mContext;

    private Handler mNotifyThreadHandler;

    private ConcurrentHashMap<K, V> mDataMap = new ConcurrentHashMap<>();

    private DataObservable<V> mDataObservable = new DataObservableImpl<>();

    public void init(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalArgumentException("should init in mainthread");
        mContext = context;
        mNotifyThreadHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ITEM_ADD:
                        mDataObservable.notifyDataAdded((V) msg.obj);
                        break;
                    case MSG_ITEM_REMOVE:
                        mDataObservable.notifyDataRemove((V) msg.obj);
                        break;
                    case MSG_ITEM_UPDATE:
                        mDataObservable.notifyDataUpdated((V) msg.obj);
                        break;
                }
            }
        };
    }

    public Context getContext() {
        if (mContext == null)
            throw new NullPointerException("context is null");
        return mContext;
    }

    public boolean contains(V item) {
        return mDataMap.containsKey(getIdFromItem(item));
    }

    public boolean containsItemId(K itemId) {
        return mDataMap.containsKey(itemId);
    }

    public void addItem(V item) {
        mDataMap.put(getIdFromItem(item), item);

        if (mNotifyThreadHandler != null) {
            Message msg = Message.obtain();
            msg.what = MSG_ITEM_ADD;
            msg.obj = item;
            mNotifyThreadHandler.sendMessage(msg);
        }
    }

    public void addItem(K itemId, V item) {
        mDataMap.put(itemId, item);

        if (mNotifyThreadHandler != null) {
            Message msg = Message.obtain();
            msg.what = MSG_ITEM_ADD;
            msg.obj = item;
            mNotifyThreadHandler.sendMessage(msg);
        }
    }

    public void addItems(Collection<V> items) {
        if (isEmpty(items))
            return;

        for (V item :
                items) {
            mDataMap.put(getIdFromItem(item), item);

            if (mNotifyThreadHandler != null) {
                Message msg = Message.obtain();
                msg.what = MSG_ITEM_ADD;
                msg.obj = item;
                mNotifyThreadHandler.sendMessage(msg);
            }
        }
    }

    public void removeItem(V item) {
        if (item != null) {
            removeItemById(getIdFromItem(item));
        }
    }

    public void removeItemById(K itemId) {
        V item = mDataMap.get(itemId);
        if (item != null) {
            mDataMap.remove(itemId);

            if (mNotifyThreadHandler != null) {
                Message msg = Message.obtain();
                msg.what = MSG_ITEM_REMOVE;
                msg.obj = item;
                mNotifyThreadHandler.sendMessage(msg);
            }
        }
    }

    public void removeItems(Collection<K> itemIds) {
        if (isEmpty(itemIds))
            return;

        List<V> items = new ArrayList<>();
        for (K itemId :
                itemIds) {
            V item = mDataMap.get(itemId);
            if (item != null) {
                items.add(item);
                mDataMap.remove(itemId);

                if (mNotifyThreadHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_ITEM_REMOVE;
                    msg.obj = item;
                    mNotifyThreadHandler.sendMessage(msg);
                }
            }
        }
    }

    public void clear() {
        if (mDataMap.size() == 0) {
            return;
        }
        Collection<V> items = mDataMap.values();
        if (items.size() != 0) {
            mDataMap.clear();
            for (V item :
                    items) {
                if (mNotifyThreadHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_ITEM_REMOVE;
                    msg.obj = item;
                    mNotifyThreadHandler.sendMessage(msg);
                }
            }
        }
    }

    public void updateItem(V item) {
        updateItem(getIdFromItem(item), item);
    }

    public void updateItem(K itemId, V item) {
        mDataMap.put(itemId, item);

        if (mNotifyThreadHandler != null) {
            Message msg = Message.obtain();
            msg.what = MSG_ITEM_UPDATE;
            msg.obj = item;
            mNotifyThreadHandler.sendMessage(msg);
        }
    }

    public Collection<V> getAllItems() {
        return mDataMap.values();
    }

    public Set<K> getAllItemIds() {
        return mDataMap.keySet();
    }

    public V getItem(K itemId) {
        return mDataMap.get(itemId);
    }

    public int size() {
        return size();
    }

    public void registerDataObserver(DataObserver<V> observer) {
        mDataObservable.register(observer);
    }

    public void unregisterDataObserver(DataObserver<V> observer) {
        mDataObservable.unregister(observer);
    }

    public void postRunnableInNotificationThread(Runnable runnable) {
        if (mNotifyThreadHandler != null)
            mNotifyThreadHandler.post(runnable);
    }

    private  boolean isEmpty(Collection collection) {
        return !(collection != null && collection.size() != 0);
    }


    abstract public K getIdFromItem(V item);
}
