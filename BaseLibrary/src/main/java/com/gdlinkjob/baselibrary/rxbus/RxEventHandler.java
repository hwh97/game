package com.gdlinkjob.baselibrary.rxbus;

import com.gdlinkjob.baselibrary.utils.LogUtil;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by legendmohe on 16/4/15.
 */
public class RxEventHandler extends DisposableSubscriber<Object> {

    Map<Class<?>, Set<Subscription>> mEventMap = new ConcurrentHashMap<>();

    WeakReference<Object> mHost;

    SubscriptionResolver mResolver;

    public RxEventHandler() {
        super();
    }

    public RxEventHandler(Object host) {
        super();
        mResolver = new RxDefaultEventResolver();
        bind(host);
    }

    public RxEventHandler(Object host, SubscriptionResolver resolver) {
        super();
        mResolver = resolver;
        bind(host);
    }

    public void bind(Object host) {
        if (host == null)
            throw new NullPointerException("cannot bind null object");

        mHost = new WeakReference<Object>(host);
        Set<Method> methods = findAnnotatedMethods(host.getClass());
        if (methods.size() != 0) {
            for (Method method : methods) {
                registerMethod(method);
            }
        }
    }

    protected void registerMethod(Method method) {
        Class<?> eventType = method.getParameterTypes()[0];
        if (!mEventMap.containsKey(eventType)) {
            mEventMap.put(eventType, new HashSet<Subscription>());
        }
        mEventMap.get(eventType).add(new Subscription(method));
    }

    public Set<Method> findAnnotatedMethods(final Class<?> type) {
        Class<? extends Annotation> annotationClass = mResolver.resolveAnnotationClass();
        int paramNum = mResolver.resolveParamNum();
        Class<?> clazz = type;
        final Set<Method> methods = new HashSet<Method>();
        while (!shouldSkipClass(clazz)) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            for (final Method method : allMethods) {
                if (filterMethod(method, annotationClass, paramNum)) {
                    methods.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    private boolean filterMethod(Method method, Class<? extends Annotation> annotation, int paramNum) {
        if (!method.isAnnotationPresent(annotation)) {
            return false;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (Modifier.isVolatile(method.getModifiers())) {
            return false;
        }
        if (method.getParameterTypes().length != paramNum) {
            return false;
        }
        return true;
    }

    private static boolean shouldSkipClass(final Class<?> clazz) {
        final String clsName = clazz.getName();
        return Object.class.equals(clazz)
                || clsName.startsWith("java.")
                || clsName.startsWith("javax.")
                || clsName.startsWith("android.")
                || clsName.startsWith("com.android.");
    }

    public void unbind() {
        mEventMap.clear();
        mHost = null;

        cancel();
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Object o) {
        if (mHost == null || mHost.get() == null)
            return;

        Class eventClass = mResolver.resolveSubscriptionClassFromSourceEvent(o);
        if (eventClass != null && mEventMap.containsKey(eventClass)) {
            Set<Subscription> subscriptions = mEventMap.get(eventClass);
            for (Subscription wrapper : subscriptions) {
                try {
                    mResolver.invokeSubscribeMethod(mHost.get(), wrapper.mMethod, o);
                } catch (Exception e) {
                    LogUtil.e(e);
                }
            }

        }
    }

    protected static class Subscription {
        public Method mMethod;

        public Subscription(Method method) {
            mMethod = method;
        }
    }

    public interface SubscriptionResolver {

        void invokeSubscribeMethod(Object host, Method method, Object event) throws InvocationTargetException, IllegalAccessException;

        Class resolveSubscriptionClassFromSourceEvent(Object rawEvent);

        Class<? extends Annotation> resolveAnnotationClass();

        int resolveParamNum();
    }
}