package com.bsren.job.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

//TODO 用nio的方式读写文件
@Slf4j
public class FileUtil {

    public static boolean deleteRecursively(File file){
        if(file!=null && file.exists()){
            if(file.isDirectory()){
                File[] childFiles = file.listFiles();
                if(childFiles!=null){
                    for (File child : childFiles) {
                        deleteRecursively(child);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static void deleteFile(String fileName){
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
    }

    public static void writeFileContent(File file,byte[] data){
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }
        try(FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            log.error("write file "+file.getName()+"fail");
        }
    }

    public static byte[] readFileContent(File file){
        long length = file.length();
        byte[] res = new byte[(int) length];
        try(FileInputStream fis = new FileInputStream(file)) {
            System.out.println(fis.available());
            fis.read(res);
            return res;
        } catch (IOException e) {
            log.error("read file "+file.getName()+"fail");
        }
        return null;
    }

    public static byte[] readOnce(File file) throws IOException {
        checkFileExists(file);
        if (file.length() > Integer.MAX_VALUE) {
            throw new IOException(file.getName() + " is too big to read");
        }
        int bufferSize = (int) file.length();
        //定义buffer缓冲区大小
        byte[] buffer = new byte[bufferSize];
        try(FileInputStream in = new FileInputStream(file)) {
            in.read(buffer);
        }
        return buffer;
    }

    public static byte[] readByByteArrayOutputStream(File file) throws FileNotFoundException {
        checkFileExists(file);
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bin.read(buffer)) > 0) {
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("read file "+file.getName()+"fail");
        }
        return null;
    }

    public static byte[] readByNIO(File file) throws FileNotFoundException {
        checkFileExists(file);
        //1、定义一个File管道，打开文件输入流，并获取该输入流管道。
        //2、定义一个ByteBuffer，并分配指定大小的内存空间
        //3、while循环读取管道数据到byteBuffer，直到管道数据全部读取
        //4、将byteBuffer转换为字节数组返回
        try(FileInputStream in = new FileInputStream(file);
            FileChannel fileChannel = in.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
            while (fileChannel.read(buffer)>0) {
            }
            return buffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //nio的方式复制文件
    public static void nioCopyFile(File src, File dst) throws IOException {
        if(null==src||null==dst)
            throw new NullPointerException("src or dst is null");
        if(!src.exists()||!src.isFile())
            throw new IllegalArgumentException(String.format("INVALID FILE NAME(无效文件名) src=%s",src.getCanonicalPath()));
        if (dst.exists() &&!dst.isFile()) {
            throw new IllegalArgumentException(String.format("INVALID FILE NAME(无效文件名) dst=%s",dst.getCanonicalPath()));
        }
        File folder = dst.getParentFile();
        if (!folder.exists())
            folder.mkdirs();
        if(((src.length()+(1<<10)-1)>>10)>(folder.getFreeSpace()>>10))
            throw new IOException(String.format("DISK ALMOST FULL(磁盘空间不足) %s",folder.getCanonicalPath()));
        try (FileInputStream fin = new FileInputStream(src);
             FileOutputStream fout = new FileOutputStream(dst);
             FileChannel fic = fin.getChannel();
             FileChannel foc = fout.getChannel()) {
            // 从FileInputStream创建用于输入的FileChannel
            // 从FileOutputStream 创建用于输出的FileChannel
            // 16KB缓冲区
            ByteBuffer bb = ByteBuffer.allocate(1024 << 4);
            // 根据 read返回实际读出的字节数 中止循环
            while (fic.read(bb) > 0) {
                // 缓冲区翻转用于输出到foc
                bb.flip();
                foc.write(bb);
                // 清空缓冲区用于下次读取
                bb.clear();
            }
        }
    }

    private static void checkFileExists(File file) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
    }
}
