package com.gdlinkjob.baselibrary.utils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by legendmohe on 16/6/10.
 */
public class ObjectUtil {

    public static void updateFieldValueIfNotSet(Object srcObject, Object refObject) {
        if (!srcObject.getClass().equals(refObject.getClass())) {
            return;
        }

        Field[] fields = srcObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible())
                field.setAccessible(true);
            try {
                boolean shouldSetValue = false;
                Object srcValue = field.get(srcObject);
                if (srcValue == null) {
                    shouldSetValue = true;
                } else if (srcValue instanceof Integer) {
                    shouldSetValue = (Integer) srcValue <= 0;
                } else if (srcValue instanceof Byte) {
                    shouldSetValue = (Byte) srcValue == 0x00;
                } else if (srcValue instanceof Short) {
                    shouldSetValue = (Short) srcValue <= 0;
                } else if (srcValue instanceof Long) {
                    shouldSetValue = (Long) srcValue <= 0;
                } else if (srcValue instanceof Float) {
                    shouldSetValue = (Float) srcValue <= 0;
                } else if (srcValue instanceof Double) {
                    shouldSetValue = (Double) srcValue <= 0;
                } else if (srcValue instanceof Character) {
                    shouldSetValue = (Character) srcValue == 0;
                } else if (srcValue instanceof Boolean) {
                    shouldSetValue = (Boolean) srcValue == false;
                }
                if (shouldSetValue) {
                    field.set(srcObject, field.get(refObject));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Iterable<Field> getFieldsUpTo(@NonNull Class<?> startClass,
                                                @Nullable Class<?> exclusiveParent) {

        List<Field> currentClassFields = Arrays.asList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                    (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    public static boolean hasDifferentFieldValue(Object src, Object other) {
        if (src == other)
            return false;
        if (!src.getClass().equals(other.getClass())) {
            return true;
        }

        Field[] fields = src.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible())
                field.setAccessible(true);
            try {
                Object srcValue = field.get(src);
                Object otherValue = field.get(other);
                if (srcValue != null && !srcValue.equals(otherValue)) {
                    return true;
                } else if (srcValue == null && otherValue != null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
