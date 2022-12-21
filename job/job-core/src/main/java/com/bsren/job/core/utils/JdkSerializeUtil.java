package com.bsren.job.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Slf4j
public class JdkSerializeUtil {

    public static byte[] serialize(Object object){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    //TODO deserialize的时候并不需要clazz，但是提供clazz会不会更好呢
    //TODO 研究一个类的存储过程
    public static <T> Object deserialize(byte[] bytes,Class<T> clazz){
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
