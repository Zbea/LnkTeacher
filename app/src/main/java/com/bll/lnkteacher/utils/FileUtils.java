package com.bll.lnkteacher.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static void mkdirs(String path){
        if (!FileUtils.isExist(path)){
            File file=new File(path);
            file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    /**
     * 读取文本内容String
     *
     * @param inputStream
     * @return 读取文本内容String
     */
    public static String readFileContent(InputStream inputStream) {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(inputStreamReader);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 将file转换为inputStream
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream file2InputStream(File file) throws IOException {
        return Files.newInputStream(file.toPath());
    }

    public static List<String> getDirectorys(String path){
        List<String> names = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return names;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                names.add(tempList[i].getName());
            }
        }
        return names;
    }

    /**
     * 获取目录下文件对象  不包含文件目录下的子文件目录 （升序）
     * @param path
     * @return
     */
    public static List<File> getFiles(String path){
        List<File> files = new ArrayList<>();
        if(path.isEmpty()){
            return files;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return files;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
        }
        files.sort( new FileNumberComparator());

        return files;
    }

    //按数字排序
    static class FileNumberComparator implements Comparator<File> {
        private  final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

        @Override
        public int compare(File f1, File f2) {
            String name1 = f1.getName();
            String name2 = f2.getName();
            Matcher matcher1 = NUMBER_PATTERN.matcher(name1);
            Matcher matcher2 = NUMBER_PATTERN.matcher(name2);

            if (matcher1.find() && matcher2.find()) {
                int num1 = Integer.parseInt(matcher1.group(1));
                int num2 = Integer.parseInt(matcher2.group(1));
                return Integer.compare(num1, num2);
            } else {
                // 如果文件名中没有数字，可以按文件名直接比较或者抛出异常，视情况而定
                return name1.compareTo(name2);
            }
        }
    }

    /**
     * 获取目录下文件对象  不包含文件目录下的子文件目录 （升序）
     * @param path
     * @return
     */
    public static List<File> getAscFiles(String path){
        List<File> files = new ArrayList<>();
        if(path.isEmpty()){
            return files;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return files;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
        }
        sortAscFiles(files);
        return files;
    }

    /**
     * 获取目录下文件对象  不包含文件目录下的子文件目录 （降序）
     * @param path
     * @return
     */
    public static List<File> getDescFiles(String path,int pageIndex,int pageSize){
        List<File> files = new ArrayList<>();
        if(path.isEmpty()){
            return files;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return files;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
        }
        sortDescFiles(files);
        if (files.size()<pageSize){
            return files;
        }
        else {
            List<File> pageFiles = new ArrayList<>();
            for (int i = (pageIndex-1)*pageSize; i < files.size() ; i++) {
                pageFiles.add(files.get(i));
            }
            return pageFiles;
        }
    }

    /**
     * 获取目录下文件对象  不包含文件目录下的子文件目录 (降序)
     * @param path
     * @return
     */
    public static List<File> getDescFiles(String path){
        List<File> files = new ArrayList<>();
        if(path.isEmpty()){
            return files;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return files;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
        }
        sortDescFiles(files);
        return files;
    }

    /**
     * 删除指定文件夹里的 指定文件
     * @param path 文件夹路径
     * @param name 文件名
     */
    public static void deleteFile(String path,String name){
        List<File> files= getAscFiles(path);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (getFileName(file.getName()).equals(name)) {
                deleteFile(file);
            }
        }
    }

    /**
     * 删除文件夹
     * @param path
     */
    public static void delete(String path){
        File file=new File(path);
        deleteFile(file);
    }

    /**
     * 删除文件夹
     * @param file
     */
    public static void deleteFile(File file){
        if (file == null || !file.exists() )
            return;
        // 判断传递进来的是文件还是文件夹,如果是文件,直接删除,如果是文件夹,则判断文件夹里面有没有东西
        if (file.isDirectory()) {
            // 如果是目录,就删除目录下所有的文件和文件夹
            File[] files = file.listFiles();
            // 遍历目录下的文件和文件夹
            for (File f : files) {
                // 如果是文件,就删除
                if (f.isFile()) {
                    // 删除文件
                    f.delete();
                } else{
                    // 如果是文件夹,就递归调用文件夹的方法
                    deleteFile(f);
                }
            }
            file.delete();
        }
        else {
            file.delete();
        }
    }

    /**
     * 文件夹排序 按照自然升序
     * @param files
     */
    public static void sortAscFiles(List<File> files) {
        if (files==null){
            return;
        }
        files.sort(Comparator.comparingLong(File::lastModified));
    }

    /**
     * 文件夹排序 自认降序
     * @param files
     */
    public static void sortDescFiles(List<File> files) {
        if (files==null){
            return;
        }
        files.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
    }

    /**
     * 复制文件到指定文件夹
     * @param oldPathName
     * @param newPathName
     * @return
     */
    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.d("debug", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.d("debug", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.d("debug", "copyFile:  oldFile cannot read.");
                return false;
            }
            Files.copy(Paths.get(oldPathName) ,Paths.get(newPathName) , StandardCopyOption.REPLACE_EXISTING);
            oldFile.delete();
//            File newFile = new File(newPathName);
//            if (!newFile.exists()){
//                newFile.getParentFile().mkdirs();
//                newFile.createNewFile();
//            }
//            FileInputStream fileInputStream = new FileInputStream(oldPathName);
//            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
//            byte[] buffer = new byte[1024];
//            int byteRead;
//            while (-1 != (byteRead = fileInputStream.read(buffer))) {
//                fileOutputStream.write(buffer, 0, byteRead);
//            }
//            fileInputStream.close();
//            fileOutputStream.flush();
//            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * name1.txt   -> name1
     * @param fileNameStr
     * @return
     */
    public static String getFileName(String fileNameStr){
        return fileNameStr.substring(0,fileNameStr.lastIndexOf("."));
    }

    /**
     * url   -> name1
     * @param url
     * @return
     */
    public static String getUrlName(String url){
        File file=new File(url);
        String fileNameStr=file.getName();
        return fileNameStr.substring(0,fileNameStr.lastIndexOf("."));
    }

    /**
     * 获取url的格式后缀
     * @param url
     * @return
     */
    public static String getUrlFormat(String url){
        return url.substring(url.lastIndexOf("."));
    }

    public static boolean isExist(String path){
        return new File(path).exists();
    }

    /**
     * 判断文件夹是否存在内容
     * @param path
     * @return
     */
    public static boolean isExistContent(String path){
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        // 遍历文件夹中的文件
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            return true;
        }
        return false;
    }

}
