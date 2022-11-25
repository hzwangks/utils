package com.utils.util;

import com.aliyun.oss.*;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

@Slf4j
public class OssOperation {

    private static final String endpoint = "endpoint";
    private static final String accessKeyId = "accessKeyId";
    private static final String accessKeySecret = "accessKeySecret";
    private static final String bucketName = "bucketName";
    private static final String uploadPath = "uploadPath";


    public static void uploadFile(String filePath,String objectName){

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadPath+objectName, new File(filePath));
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } catch (Exception e){
            log.error("Exception",e);
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    public static void uploadUrl(String url,String objectName){

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = new URL(url).openStream();
            log.info("进行上传oss操作：bucketName:"+bucketName+", objectName:"+uploadPath+objectName);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, uploadPath+objectName, inputStream);

        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } catch (MalformedURLException e) {
            log.error("MalformedURLException",e);
        } catch (IOException e) {
            log.error("IOException",e);
        } catch (Exception e) {
            log.error("Exception",e);
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public static URL getUrl(String filename){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 设置URL过期时间为15分钟
        Date expiration = new Date(new Date().getTime() + 900 * 10000);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, uploadPath+filename, expiration);
        return url;
    }


    /**
     * 获取文件的全部元信息
     * @param objectName 填写不包含Bucket名称在内的Object完整路径，例如testfolder/exampleobject.txt。
     * @return
     */
    public static ObjectMetadata getFileData(String objectName){

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        //初始化文件的全部元信息
        ObjectMetadata metadata = new ObjectMetadata();

        try {
            // 依次填写Bucket名称以及Object的完整路径。
            // 获取文件的部分元信息。
//                SimplifiedObjectMeta objectMeta = ossClient.getSimplifiedObjectMeta(bucketName, objectName);
            // 获取文件的全部元信息。
            metadata = ossClient.getObjectMetadata(bucketName, uploadPath+objectName);
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return metadata;
    }



}
