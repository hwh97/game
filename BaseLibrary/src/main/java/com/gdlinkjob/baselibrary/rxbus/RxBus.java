package com.gdlinkjob.baselibrary.rxbus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;


/**
 * Created by legendmohe on 16/4/14.
 */
public class RxBus {

    // TODO - need concurrent wrapper?
    private static volatile RxBus mDefaultInstance;

    Map<Object, DisposableSubscriber<Object>> mEnumEventHandlerMap = new HashMap<>();
    Map<Object, DisposableSubscriber<Object>> mDefaultEventHandlerMap = new HashMap<>();
    private final Map<Class<?>, Object> mStickyEventMap;

    private final FlowableProcessor<Object> mBus;

    public RxBus() {
        mBus = PublishProcessor.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    public void post(Object o) {
        new SerializedSubscriber<>(mBus).onNext(o);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        //背压处理，防止MissingBackpressureException
        return mBus.ofType(eventType).onBackpressureBuffer();
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Flowable<T> toFlowableUI(Class<T> eventType) {
//        return mBus.ofType(eventType).compose(RxUtils.applyIOToMainThreadSchedulers());
        //背压处理，防止MissingBackpressureException
        return mBus.ofType(eventType).onBackpressureBuffer().observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    public void reset() {
        mDefaultInstance = null;
    }


    public void register(Object host) {
        // default event resolver
        registerEnumEventResolver(host);
        registerDefaultEventResolver(host);
//        register(host, new RxDefaultEventResolver());
    }

    public void unregister(Object host) {
        unregisterEnumEventResolver(host);
        unregisterDefaultEventResolver(host);
    }

    public void registerEnumEventResolver(Object host) {
        if (mEnumEventHandlerMap.containsKey(host))
            throw new IllegalStateException("host has been registered");

        RxEventHandler handler = new RxEventHandler(host, new RxEnumEventResolver());
        mBus.subscribe(handler);
        mEnumEventHandlerMap.put(host, handler);
    }

    public void unregisterEnumEventResolver(Object host) {
        if (!mEnumEventHandlerMap.containsKey(host))
            throw new IllegalArgumentException("host has not been registered");
        mEnumEventHandlerMap.get(host).dispose();
        mEnumEventHandlerMap.remove(host);
    }

    public void registerDefaultEventResolver(Object host) {
        if (mDefaultEventHandlerMap.containsKey(host))
            throw new IllegalStateException("host has been registered");

        RxEventHandler handler = new RxEventHandler(host, new RxDefaultEventResolver());
        mBus.subscribe(handler);
        mDefaultEventHandlerMap.put(host, handler);
    }

    public void unregisterDefaultEventResolver(Object host) {
        if (!mDefaultEventHandlerMap.containsKey(host))
            throw new IllegalArgumentException("host has not been registered");
        mDefaultEventHandlerMap.get(host).dispose();
        mDefaultEventHandlerMap.remove(host);
    }

    /**
     * Stciky 相关
     */

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Flowable<T> toFlowableSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Flowable<T> flowable = mBus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
//                return observable.mergeWith(Observable.create(subscriber -> subscriber.onNext(eventType.cast(event))));
                return flowable.mergeWith(Flowable.create(subscriber -> subscriber.onNext(eventType.cast(event)), BackpressureStrategy.BUFFER));
            } else {
                return flowable;
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }
}
