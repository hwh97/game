package com.gdlinkjob.baselibrary.rxbus;

/**
 * Created by legendmohe on 16/4/15.
 */
public class RxEvent {

    public static EnumEvent createEnumEvent(String tag, Enum type, Object data) {
        EnumEvent enumEvent = new EnumEvent();
        enumEvent.type = type;
        enumEvent.data = data;
        enumEvent.tag = tag;
        return enumEvent;
    }

    public static EnumEvent createEnumEvent(Enum type, Object data) {
        EnumEvent enumEvent = new EnumEvent();
        enumEvent.type = type;
        enumEvent.data = data;
        enumEvent.tag = "";
        return enumEvent;
    }

    public static EnumEvent createEnumEvent(Enum type) {
        EnumEvent enumEvent = new EnumEvent();
        enumEvent.type = type;
        enumEvent.data = null;
        enumEvent.tag = "";
        return enumEvent;
    }

    public static class EnumEvent {
        public String tag;
        public Enum type;
        public Object data;
    }
}
