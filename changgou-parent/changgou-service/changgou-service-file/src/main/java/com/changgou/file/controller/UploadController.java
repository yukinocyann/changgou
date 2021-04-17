package com.changgou.file.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.file.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author yukino
 * @Date 2021/2/24 15:02
 * @Description
 */
@RestController
public class UploadController {
    @PostMapping("/upload")
    public String upload(@RequestParam(name ="file") MultipartFile file){
        try {
            if (!file.isEmpty()) {
                //1.获取字节数组
                byte[] bytes = file.getBytes();
                //2.获取原文件的扩展名
                String extName = StringUtils.getFilenameExtension(file.getOriginalFilename());
                //3.使用工具类调用方法 实现上传图片到fastdfs somewhere
                FastDFSFile filex = new FastDFSFile(
                        file.getOriginalFilename(),
                        bytes,
                        extName
                );
                String[] upload = FastDFSClient.upload(filex);

                String realPath = "http://192.168.211.132:8080/" + upload[0] + "/" + upload[1];
                //4.拼接URL 返回给前端
                return realPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
