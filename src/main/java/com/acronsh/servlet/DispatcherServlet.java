/*
* Copyright (c) 2017 Hinew. All Rights Reserved.
 * ============================================================================
 * 版权所有 海牛(上海)电子商务有限公司，并保留所有权利。
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 * 官方网站：http://www.hinew.com.cn
 * ============================================================================
*/
package com.acronsh.servlet;

import com.acronsh.annotation.Controller;
import com.acronsh.annotation.Qualify;
import com.acronsh.annotation.RequestMapping;
import com.acronsh.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangyakun
 * @email yakun0622@gmail.com
 * @date 2018/1/28 21:01
 */
public class DispatcherServlet extends HttpServlet {

    private List<String> classNames = new ArrayList<String>();

    private Map<String, Object> beans = new HashMap<String, Object>();

    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
//        1.扫描相关包
        System.out.println("开始扫描包....");
        scanPackage("com.acronsh");
        for (String className : classNames) {
            System.out.println(className);
        }
//        2.@Controller 和 @Service类的实例化
        System.out.println("开始实例化相关类....");
        instanceClass();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

//        3.依赖注入
        System.out.println("开始依赖注入....");
        ioc();

//        4.建立URL与controller中method的映射关系
        System.out.println("开始关联映射....");
        handlerMapping();
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void handlerMapping() {
        if (beans.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();

            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
                String classUrl = rm.value();
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodRm = method.getAnnotation(RequestMapping.class);
                        // key: /acronsh/eat
                        handlerMap.put(classUrl + methodRm.value(), method);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    private void ioc() {
        if (beans.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            //  通过反射,获取每个bean中的属性, 再将依赖@Qualify注解的属性注入进去
            Object instance = entry.getValue();
            Class clazz = instance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Qualify.class)) {
                    Qualify qualify = field.getAnnotation(Qualify.class);
                    String value = qualify.value();
                    field.setAccessible(true);
                    try {
                        field.set(instance, beans.get(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void instanceClass() {
        if (classNames.size() <= 0) {
            return;
        }
        for (String className : classNames) {
            className = className.replace(".class", "");
            try {
                Class clazz = Class.forName(className);
                // 只处理Controller和Service类
                // Controller类
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // 实例化
                    Object instance = clazz.newInstance();
                    RequestMapping rm = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    String rmValue = rm.value();
                    // 加入bean缓存
                    beans.put(rmValue, instance);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    // Service类
                    Object instance = clazz.newInstance();
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    beans.put(service.value(), instance);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void scanPackage(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(replaceTo(packageName));
        //  url 示例: file:D:/workspace/com/acronsh
        String fileStr = url.getFile();
        System.out.println("fileStr = " + fileStr);
        File file = new File(fileStr);
        // 文件下 所有的文件名
        String[] filesStr = file.list();
        for (String path : filesStr) {
            File filePath = new File(fileStr + path);
            if (filePath.isDirectory()) {
                scanPackage(packageName + "." + path);
            } else {
                // 将文件放入内存中
                // packageName + "." + filePath.getName()  com.acronsh.service.AcronshServiceImpl.class
                classNames.add(packageName + "." + filePath.getName());
            }
        }
    }

    private String replaceTo(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取URI
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        // 对uri进行处理,获取映射的方法
        String path = uri.replace(contextPath, "");
        Method method = (Method) handlerMap.get(path);
        Object obj = beans.get("/" + path.split("/")[1]);
        try {
            method.invoke(obj, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}