package com.bsren.javaStd.Serialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Arrays;

public class JdkSerializeTest {

    @Test
    void test1() throws IOException, ClassNotFoundException {
        Person person = new Person("r",23);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        ObjectOutputStream stream = new ObjectOutputStream(outputStream);
        stream.writeObject(person);
        byte[] bytes = outputStream.toByteArray();
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Person object = (Person) inputStream.readObject();
        System.out.println(object);
    }

    @Test
    void test2() throws IOException, ClassNotFoundException {
        P p = new P("r",23);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        ObjectOutputStream stream = new ObjectOutputStream(outputStream);
        stream.writeObject(p);
        byte[] bytes = outputStream.toByteArray();
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        P object = (P) inputStream.readObject();
        System.out.println(object);
    }

    @Test
    void test3() throws IOException, ClassNotFoundException {
        K k = new K("r",23);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        ObjectOutputStream stream = new ObjectOutputStream(outputStream);
        stream.writeObject(k);
        byte[] bytes = outputStream.toByteArray();
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        K object = (K) inputStream.readObject();
        System.out.println(object);
    }

    @Test
    void test4(){
        Constructor<?>[] constructors = K.class.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(Arrays.toString(constructor.getParameterTypes()));
        }
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Person implements Serializable{
    String name;
    int age;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class P implements Serializable{
    String name;
    int age;

    private void writeObject(ObjectOutputStream out) {
        try {
            ObjectOutputStream.PutField putField = out.putFields();
            putField.put("name",this.name);
            putField.put("age",this.age+1);
            out.writeFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in){
        try {
            ObjectInputStream.GetField getField = in.readFields();
            this.name = (String) getField.get("name",new byte[0]);
            this.age = getField.get("age",0)-1;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
class K implements Externalizable{

    volatile String name;
    int age;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.name);
        out.writeObject(this.age);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name = (String) in.readObject();
        this.age = (int) in.readObject();
    }
}