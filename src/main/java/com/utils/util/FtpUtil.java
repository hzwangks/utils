package com.utils.util;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author hzwangks
 * @Description ftp工具类
 * @date 2022年10月25日 下午16:16
 */
public class FtpUtil {

    @Value("${ftp.host}")
    public static String host;

    @Value("${ftp.port}")
    public static int port;

    @Value("${ftp.username}")
    public static String username;

    @Value("${ftp.password}")
    public static String password;

    @Value("${ftp.basePath}")
    public static String basePath;

    @Value("${ftp.downloadPath}")
    public static String downloadPath;


    /**
     *
     * @param filePath 基础路径下的文件所在目录的相对路径
     * @param filename 文件名
     * @param content 文件每一行的内容
     * @return
     */
    public static boolean uploadFile(String filePath, String filename, String[] content) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            if(StrUtil.isNotEmpty(basePath) && !basePath.startsWith("/")){
                basePath = "/" + basePath;
            }
            if(StrUtil.isNotEmpty(basePath) && !basePath.endsWith("/")){
                basePath += "/";
            }
            while(StrUtil.isNotEmpty(filePath) && filePath.startsWith("/")){
                filePath = filePath.substring(1);
            }
            if(StrUtil.isNotEmpty(filePath) && !filePath.endsWith("/")){
                filePath += "/";
            }
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(basePath+filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = basePath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {  //进不去目录，说明该目录不存在
                        if (!ftp.makeDirectory(tempPath)) { //创建目录
                            //如果创建文件目录失败，则返回
                            System.out.println("创建文件目录"+tempPath+"失败");
                            return result;
                        } else {
                            //目录存在，则直接进入该目录
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
//            if (!ftp.storeFile(filename, is)) {
//                return result;
//            }
//            is.close();
            OutputStream os = ftp.storeFileStream(filename);
            for (String str:content) {
                os.write(str.getBytes());
                os.write(System.getProperty("line.separator").getBytes());
            }
            os.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }


    /**
     *
     * @param filePath
     * @return
     */
    public static List<String> getFileList(String filePath) {
        List<String> filenameList = new ArrayList<>();
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return filenameList;
            }
            //切换到指定目录
            if (!ftp.changeWorkingDirectory(filePath)) {
                return filenameList;
            }
            FTPFile[] files = ftp.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        String filename = new String(files[i].getName().getBytes("gbk"), "utf-8");
                        filenameList.add(filename);
                    }
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return filenameList;
    }


    /**
     * @Description: 从FTP服务器下载文件
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @return
     */
    public static boolean downloadFile(String remotePath, String fileName) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            File localFile = new File(downloadPath + File.separator + fileName);
            OutputStream os = new FileOutputStream(localFile);
//            ftp.retrieveFile(new String(fileName.getBytes("UTF-8"), "ISO-8859-1"), os);
            ftp.retrieveFile(fileName, os);
            os.flush();
            os.close();

            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }


    /**
     * @description 读取ftp上txt文件内容
     * @param remotePath 路径
     * @param filename 文件名
     * @return 文件内容每行以字符串列表返回
     */
    public static List<String> readFile(String remotePath, String filename){
        List<String> content = new ArrayList<>();
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return content;
            }
            //切换到指定目录
            if (!ftp.changeWorkingDirectory(remotePath)) {
                return content;
            }
            InputStream is = ftp.retrieveFileStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String row;
            while((row = br.readLine())!=null){
                content.add(row);
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return content;
    }


    /**
     * @description 从下载路径解压zip文件
     * @param filename 文件名
     * @return
     */
    public static boolean unzip(String filename){
        File file = new File(downloadPath+filename);
        try {
            ZipFileUtil.unZip(file, downloadPath + filename.substring(0, filename.lastIndexOf(".")));
            return true;
        }catch (IOException e){
            return false;
        }

    }

    /**
     * @description
     * @param filename
     * @return
     */
    public static List<String> readLocalFile(String filename){
        List<String> content = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String row;
            while((row = br.readLine())!=null){
                content.add(row);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }

}
