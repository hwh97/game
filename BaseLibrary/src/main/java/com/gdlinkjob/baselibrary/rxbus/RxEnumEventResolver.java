package com.gdlinkjob.baselibrary.rxbus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by legendmohe on 16/4/15.
 */
public class RxEnumEventResolver implements RxEventHandler.SubscriptionResolver {
    @Override
    public void invokeSubscribeMethod(Object host, Method method, Object event) throws InvocationTargetException, IllegalAccessException {
        RxEvent.EnumEvent enumEvent = (RxEvent.EnumEvent) event;
        RxSubscribeEnum rxSubscribeEnum = method.getAnnotation(RxSubscribeEnum.class);
        if (rxSubscribeEnum.value().equals("") || rxSubscribeEnum.value().equals(enumEvent.tag)) {
            if (enumEvent.data == null || method.getParameterTypes()[1].isAssignableFrom(enumEvent.data.getClass())) {
                method.invoke(host, enumEvent.type, enumEvent.data);
            }
        }
    }

    @Override
    public Class resolveSubscriptionClassFromSourceEvent(Object rawEvent) {
        if (!rawEvent.getClass().equals(RxEvent.EnumEvent.class))
            return null;
        return ((RxEvent.EnumEvent) rawEvent).type.getClass();
    }

    @Override
    public Class<? extends Annotation> resolveAnnotationClass() {
        return RxSubscribeEnum.class;
    }

    @Override
    public int resolveParamNum() {
        return 2;
    }
}
