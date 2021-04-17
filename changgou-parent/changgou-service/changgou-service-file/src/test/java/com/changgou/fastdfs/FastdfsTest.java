package com.changgou.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;


public class FastdfsTest {

    //上传图片  client-->tracker server      storage server
    @Test
    public void upload() throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        ClientGlobal.init("D:\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
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
        String[] jpgs = storageClient.upload_file("C:\\Users\\yukino\\Pictures\\Saved Pictures\\bizhi\\029.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    //下载图片
    @Test
    public void download() throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        ClientGlobal.init("D:\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
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
        byte[] group1s = storageClient.download_file("group1", "M00/00/00/wKjThGA49PuASeRqAAVxT06aL2Q095.jpg");
        //写入磁盘
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/1234.jpg"));
        fileOutputStream.write(group1s);
        fileOutputStream.close();
    }

    //删除图片

    @Test
    public void delete() throws Exception {
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        ClientGlobal.init("D:\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //4.根据trackerclient获取到链接对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer对象 设置null

        //6.创建stroageClient --->提供了很多的操作图片的API的代码（上传图片，下载 ，删除）
        StorageClient storageClient = new StorageClient(trackerServer, null);
        int group1 = storageClient.delete_file("group1", "M00/00/00/wKjThGA49PuASeRqAAVxT06aL2Q095.jpg");
        if (group1 == 0) {
            System.out.println("chengg");
        } else {
            System.out.println("不成功");
        }
    }

    //查询 获取文件的信息
    @Test
    public void getInfo() throws Exception{
        //1.创建一个配置文件 用于填写服务端的ip和端口
        //2.加载配置文件 建立链接
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\98\\changgou98\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //4.根据trackerclient获取到链接对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer对象 设置null

        //6.创建stroageClient --->提供了很多的操作图片的API的代码（上传图片，下载 ，删除）
        StorageClient storageClient = new StorageClient(trackerServer, null);
        FileInfo group1 = storageClient.get_file_info("group1", "M00/00/00/wKjThGA18JmALn5YAAVxT06aL2Q928.jpg");
        System.out.println(group1.getCreateTimestamp());
        System.out.println(group1.getFileSize()+group1.getSourceIpAddr());
    }


}
