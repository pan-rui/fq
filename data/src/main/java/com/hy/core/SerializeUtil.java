package com.hy.core;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2015/9/2.
 */
public class SerializeUtil {
    public static byte[] serialize(Object object) {
        if (!(object instanceof Serializable))
            throw new IllegalArgumentException(" requires a Serializable payload " + "but received an object of type [" + object.getClass().getName() + "]");
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
//序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            oos.flush();
            return bytes;
        } catch (Exception e) {

        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
//反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("对象反序列化发生错误。。。");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class getServerClass(String serverName) {
        Class serverClass = null;
        try {
            serverClass = Class.forName(serverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final Class finalServerClass = serverClass;
        Object proxy = Proxy.newProxyInstance(serverClass.getClassLoader(), new Class[]{serverClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(finalServerClass.newInstance(), args);
            }
        });
        return proxy.getClass();
    }

}