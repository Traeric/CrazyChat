package com.crazychat.common.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class FileResource {
    public static String getUploadPath(String filePath) {
        // 获取根目录
        File path;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (!path.exists()) {     // 判断目录是否存在
            path = new File("");
        }
        // 如果上传的目录为 /static/images
        File upload = new File(path.getAbsolutePath(), "static/" + filePath);
        if (!upload.exists()) {     // 判断目录是否存在，如果不存在就创建
            upload.mkdirs();
        }
        //在开发测试模式时，得到地址为：{项目根目录}/target/static/images/
        //在打成jar正式发布时，得到的地址为:{发布jar包目录}/static/images/
        return upload.getAbsolutePath();
    }
}
