package com.utils.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
public class FileOperation {

    //根据url下载文件到指定路径
    public static File saveUrlAs(String url, String filePath){
        //创建不同的文件夹目录
        File file=new File(filePath);
        //如果文件夹不存在，则创建新的的文件夹
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            // 建立链接
            URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            //以Post方式提交表单，默认get方式
            //conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream=conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            //写入到文件（注意文件保存路径的后面一定要加上文件的名称）
            fileOut = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            //保存文件
            while(length != -1)
            {
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            conn.disconnect();
        } catch (Exception e) {
            log.error("下载错误，抛出异常！");
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 根据模板生成pdf
     * @param map 模板中的表单数据 key-表单属性值 value-值
     * @param templatePath 模板路径 src/main/resources/voucher.pdf
     * @param newPdfPath 生成的pdf文件存放的地址
     * @return 返回生成的pdf文件路径
     */
    public static String createPdfByTemplate(Map<String,Object> map, String templatePath, String newPdfPath){
        PdfReader reader;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        //生成pdf文件存放的地址
//        String newPdfPath = "src/main/resources/temp/"+pdfName+".pdf";

        try{
            //设置字体，不然没法向模板写值
            BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);

            //读取pdf模板
            reader = new PdfReader(templatePath);
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader,bos);

            //拿到pdf模板中的表单属性
            AcroFields form = stamper.getAcroFields();
            //设置字体
            form.addSubstitutionFont(bfChinese);
            java.util.Iterator<String> it = form.getFields().keySet().iterator();
            //遍历表单属性，对每个属性赋值
            while(it.hasNext()){
                String name = it.next().toString();
                String value = map.get(name)!=null?map.get(name).toString():null;
                form.setField(name,value);
            }
            //如果为false，生成的pdf还能编辑
            stamper.setFormFlattening(true);
            stamper.close();
            Document doc = new Document();
            File file = new File(newPdfPath);
            PdfCopy copy = new PdfCopy(doc,new FileOutputStream(file));
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()),1);
            copy.addPage(importPage);
            doc.close();

        }catch (IOException e){
            log.error("IOException",e);
        }catch (DocumentException e){
            log.error("DocumentException",e);
        }
        return newPdfPath;
    }


    /**
     * 合并pdf
     * @param files  待合并的文件  src/main/resources/temp/voucherTest.pdf
     * @param newfile 合并后的文件 src/main/resources/temp/merge.pdf
     * @return
     */
    public static boolean mergePdfFiles(String[] files, String newfile) {
        boolean retValue = false;
        Document document = null;
        PdfCopy copy = null;
        PdfReader reader = null;
        try {
            document = new Document(new PdfReader(files[0]).getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(newfile));
            document.open();
            for (int i = 0; i < files.length; i++) {
                reader = new PdfReader(files[i]);
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
                reader.close();
            }
            retValue = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (copy != null) {
                copy.close();
            }
            if (document != null) {
                document.close();
            }
        }
        return retValue;
    }


}
