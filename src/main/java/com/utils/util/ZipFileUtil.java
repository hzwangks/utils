package com.utils.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtil {

    /**
     * 解压压缩包文件
     *
     * @param srcFile     压缩包文件
     * @param destDirPath 解压路径
     */
    public static void unZip(File srcFile, String destDirPath) throws IOException {
        long start = System.currentTimeMillis();

        String srcFilePath = srcFile.getPath();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            System.out.println(srcFilePath + "所指文件不存在");
        }

        // 开始解压
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();

                    }
                    targetFile.createNewFile();

                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[512];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }

                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }

            long end = System.currentTimeMillis();
        } catch (IOException e) {
            throw e;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
