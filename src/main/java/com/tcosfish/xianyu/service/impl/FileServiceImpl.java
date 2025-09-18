package com.tcosfish.xianyu.service.impl;

import com.tcosfish.xianyu.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author tcosfish
 * @apiNote 文件上传相关类, 将各类验证与具体逻辑分离
 */
@Service
public class FileServiceImpl implements FileService {

  @Value("${upload.path}")
  private String uploadBasePath;

  /**
   * 返回给前端的地址前缀 (可配合CDN/Nginx等)
   */
  @Value("${upload.url-prefix}")
  private String urlPrefix;

  /**
   * 允许上传的图片后缀名（小写形式）
   */
  private static final List<String> ALLOWED_IMAGE_EXTENSIONS
          = List.of(".jpg", ".jpeg", ".png", ".webp");

  /**
   * 单个图片最大尺寸 (10MB)
   */
  private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

  // 主要进行验证
  @Override
  public String uploadImage(MultipartFile file) {
    if(file == null || file.isEmpty()) {
      throw new IllegalArgumentException("上传的图片文件为空");
    }

    if(file.getSize() > MAX_IMAGE_SIZE) {
      throw new IllegalArgumentException("图片大小不能超过 10MB");
    }

    String originalFilename = file.getOriginalFilename();
    if(originalFilename == null || !originalFilename.contains(".")) {
      throw new IllegalArgumentException("图片文件名无效");
    }

    String fileExtname = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    if(!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtname)) {
      throw new IllegalArgumentException("只支持"+ALLOWED_IMAGE_EXTENSIONS+"等格式的图片");
    }

    return doUpload(file);
  }

  @Override
  public String uploadFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("文件为空");
    }

    String originalFilename = file.getOriginalFilename();
    if(originalFilename == null || !originalFilename.contains(".")) {
      throw new IllegalArgumentException("文件名不合法");
    }

    return doUpload(file);
  }

  /**
   * 实际执行文件上传的公共方法
   *
   * @param file MultipartFile
   * @return 上传后可访问的URL
   */
  private String doUpload(MultipartFile file) {
    // 当在 Spring MVC 中处理文件上传时，客户端上传的文件会被封装成一个 MultipartFile 对象。
    // 可以通过调用 transferTo 方法将上传的文件保存到服务器的某个位置。

    // 1. 生成新名
    String originalFilename = file.getOriginalFilename();
    String fileExtension = originalFilename
            .substring(originalFilename.lastIndexOf("."))
            .toLowerCase();
    String newFileName = UUID.randomUUID() + fileExtension;

    // 2. 确保目录存在
    File uploadDir = new File(uploadBasePath);
    if (!uploadDir.exists() && !uploadDir.mkdirs()) {
      throw new IllegalStateException("无法创建上传目录: " + uploadBasePath);
    }

    // 3. 保存文件
    File destFile = new File(uploadDir, newFileName);
    try {
      file.transferTo(destFile);
    } catch (IOException e) {
      throw new IllegalStateException("文件保存失败: " + e.getMessage(), e);
    }

    // 4. 返回访问 URL, 这种情况下基本都在 /images/**
    return urlPrefix + "/" + newFileName;
  }
}
