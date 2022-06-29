package com.bsren.javaStd.reflect;

import com.bsren.common.Student;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class reflectTest {

    @Test
    public void test1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class aClass = Class.forName("com.bsren.common.Student");

        Constructor constructor = aClass.getConstructor(String.class, int.class);

        Student rsb = (Student) constructor.newInstance("rsb", 23);

        System.out.println(rsb);
    }

    @Test
    public void test2() throws ClassNotFoundException {
        Class aClass = Class.forName("com.bsren.common.Student");

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField);
        }
    }

    //获得在该类中声明的方法，并不会把父类的方法也写出来
    @Test
    public void test3() throws ClassNotFoundException {
        Class aClass = Class.forName("com.bsren.common.Student");

        Method[] methods = aClass.getDeclaredMethods();
        for (Method m : methods) {
            System.out.println(m);
        }
    }

    //获取所有的方法
    @Test
    public void test4() throws ClassNotFoundException {
        Class aClass = Class.forName("com.bsren.common.Student");

        Method[] methods = aClass.getMethods();
        for (Method m : methods) {
            System.out.println(m);
        }
    }

    //执行指定方法
    @Test
    public void test5() throws Exception{
        Class aClass = Class.forName("com.bsren.common.Student");

        Student instance = (Student) aClass.newInstance();
        Method method = aClass.getMethod("study");

        method.invoke(instance);
    }
}
