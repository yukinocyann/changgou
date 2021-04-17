package com.changgou.file.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * @Date 2021/2/24 14:33
 * @param null
 * @return
 * @Description //
 **/
public class FastDFSClient {
    static {
        try {
            ClassPathResource resource = new ClassPathResource("fdfs_client.conf");
            ClientGlobal.init(resource.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    //上传图片
    public static String[] upload(FastDFSFile file) throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        //3.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //4.根据trackerclient获取到链接对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer对象 设置null

        //6.创建stroageClient --->提供了很多的操作图片的API的代码（上传图片，下载 ，删除）
        StorageClient storageClient = new StorageClient(trackerServer, null);
        //参数1 指定要上传图片的本地的图片的绝对路径
        //参数2 指定要上传图片的图片的扩展名（jpg/png）不要带点
        //参数3 指定元数据 指的是 图片的高度 日期，像素......    可以不给，
        NameValuePair[] meta_list = new NameValuePair[]{
                //像素 高度  大小
                new NameValuePair(file.getName())
        };

        String[] jpgs = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        return jpgs;
    }

    //下载图片
    public static byte[] downFile(String groupName, String remoteFileName) throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        // ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\98\\changgou98\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //4.根据trackerclient获取到链接对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer对象 设置null

        //6.创建stroageClient --->提供了很多的操作图片的API的代码（上传图片，下载 ，删除）
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //7.下在图片
        //参数1 指定要下载的组名
        //参数2 指定要下载的远程文件路径
        byte[] group1s = storageClient.download_file(groupName, remoteFileName);
        return group1s;
    }

    //删除图片
    public static boolean deleteFile(String groupName, String remoteFileName) throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        //ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\98\\changgou98\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //4.根据trackerclient获取到链接对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer对象 设置null

        //6.创建stroageClient --->提供了很多的操作图片的API的代码（上传图片，下载 ，删除）
        StorageClient storageClient = new StorageClient(trackerServer, null);
        int group1 = storageClient.delete_file(groupName, remoteFileName);
        if (group1 == 0) {
            return true;
        } else {
            return false;
        }
    }
    //获取图片的信息 //todo
}
