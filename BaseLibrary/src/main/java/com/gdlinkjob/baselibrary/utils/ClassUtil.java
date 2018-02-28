package com.gdlinkjob.baselibrary.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by MYFLY on 2017/7/21.
 * email:coohoobin@gmail.com
 */

/**
 * 查找指定路径下面实现指定接口的全部类
 *
 * @author longyin
 * @author 博客地址：http://blog.csdn.net/u010156024
 *         如果大家有什么问题或疑问，欢迎留言或评论，谢谢!!
 */
public class ClassUtil {

    /**
     * @param c 接口
     * @return List<Class>    实现接口的所有类
     * @Description: 根据一个接口返回该接口的所有类
     */
    public static List<Class<?>> getAllClassByInterface(Class c, String packName) {
        List returnClassList = new ArrayList<Class>();
        //判断是不是接口,不是接口不作处理
        if (c.isInterface()) {
            try {
                List<Class<?>> allClass = getClasses(packName);//获得当前包以及子包下的所有类
                //判断是否是一个接口
                for (int i = 0; i < allClass.size(); i++) {
                    if (c.isAssignableFrom(allClass.get(i))) {
                        if (!c.equals(allClass.get(i))) {
                            returnClassList.add(allClass.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return returnClassList;
    }


    /**
     * @return List<Class>    包下所有类
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     * 获得当前包以及子包下的所有类(android中用)
     */
    private static List<Class<?>> getClasses(String packName) throws ClassNotFoundException,
            IOException {

        ArrayList<Class<?>> classes = new ArrayList<>();
        List<DexFile> dexFiles = new ArrayList<>();
        try {
            BaseDexClassLoader classLoader = ((BaseDexClassLoader) Thread.currentThread().getContextClassLoader());
            Field pathListField = classLoader.getClass().getSuperclass().getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(classLoader);

            Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object dexElements = dexElementsField.get(pathList);
            int dexLength = Array.getLength(dexElements);
            Field dexFileField = null;

            for (int i = 0; i < dexLength; i++) {
                Object dexElement = Array.get(dexElements, i);
                if (dexFileField == null) {
                    dexFileField = dexElement.getClass().getDeclaredField("dexFile");
                    dexFileField.setAccessible(true);
                }
                DexFile dexFile = (DexFile) dexFileField.get(dexElement);
                if (dexFile != null) {
                    dexFiles.add(dexFile);
                }
            }

            for (DexFile file : dexFiles) {
                for (Enumeration<String> entries = file.entries(); entries.hasMoreElements(); ) {
                    final String s1 = entries.nextElement();
                    if (Pattern.compile(packName).matcher(s1).matches()) {
                        classes.add(Class.forName(s1));
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return classes;
    }


    /**
     * @param context 包名
     * @return List<Class>    包下所有类
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     */
    private static List<Class> getClasses(Context context) throws ClassNotFoundException, IOException {

        DexFile df = new DexFile(context.getPackageCodePath());//通過DexFile查找當前的APK中可執行文件
        Enumeration<String> enumeration = df.entries();//獲取df中的元素  這裏包含了所有可執行的類名 該類名包含了包名+類名的方式

        ArrayList<Class> classes = new ArrayList<Class>();
        while (enumeration.hasMoreElements()) {

            String className = (String) enumeration.nextElement();
            Class c = Class.forName(className);
            classes.add(c);
        }
        return classes;
    }

    private static List<Class> findClass(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }


//    @SuppressWarnings("unchecked")
//    public static List<Class> getAllClassByAnnotation(Class annotationClass) {
//        List returnClassList = new ArrayList<Class>();
//        //判断是不是注解
//        if (annotationClass.isAnnotation()) {
//            String packageName = annotationClass.getPackage().getName();    //获得当前包名
//            try {
//                List<Class> allClass = getClasses(packageName);//获得当前包以及子包下的所有类
//
//                for (int i = 0; i < allClass.size(); i++) {
//                    if (allClass.get(i).isAnnotationPresent(annotationClass)) {
//                        returnClassList.add(allClass.get(i));
//                    }
//                }
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//        }
//        return returnClassList;
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<Class> getAllSubclassOfTestInterface(Class c) {
//        Field field = null;
//        Vector v = null;
//        List<Class> allSubclass = new ArrayList<Class>();
//
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        Class<?> classOfClassLoader = classLoader.getClass();
//
////        try {
////            testInterfaceClass = (Class<TestInterface>) Class.forName("com.xxx.xxx.xxx.TestInterface");
////        } catch (ClassNotFoundException e) {
////            throw new RuntimeException(
////                    "无法获取到TestInterface的Class对象!查看包名,路径是否正确");
////        }
//        while (classOfClassLoader != ClassLoader.class) {
//            classOfClassLoader = classOfClassLoader.getSuperclass();
//        }
//        try {
//            field = classOfClassLoader.getDeclaredField("classes");
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(
//                    "无法获取到当前线程的类加载器的classes域!");
//        }
//        field.setAccessible(true);
//        try {
//            v = (Vector) field.get(classLoader);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(
//                    "无法从类加载器中获取到类属性!");
//        }
//        for (int i = 0; i < v.size(); ++i) {
//            Class<?> d = (Class<?>) v.get(i);
//            if (scmJobClass.isAssignableFrom(d) && !scmJobClass.equals(d) && !abstractScmJobClass.equals(d)) {
//                allSubclass.add((Class) d);
//            }
//        }
//        return allSubclass;
//    }
}