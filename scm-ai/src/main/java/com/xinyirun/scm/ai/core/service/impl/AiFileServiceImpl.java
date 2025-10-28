package com.xinyirun.scm.ai.core.service.impl;

import com.xinyirun.scm.ai.bean.vo.common.AiFileVo;
import com.xinyirun.scm.ai.core.service.AiFileService;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.core.system.service.sys.file.ISFileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI文件服务实现
 * 参考: aideepin FileService.java saveFile方法
 * 使用SCM现有的文件服务(ISFileInfoService)来存储文件
 *
 * @author SCM AI Team
 * @since 2025-10-28
 */
@Slf4j
@Service
public class AiFileServiceImpl implements AiFileService {

    @Autowired
    private ISFileInfoService fileInfoService;

    @Value("${file.base-url:http://localhost:8088}")
    private String fileBaseUrl;

    @Value("${file.upload-dir:./uploads/ai-files}")
    private String uploadDir;

    /**
     * 保存上传的文件
     * 参考: aideepin FileService.java第115-122行
     */
    @Override
    public AiFileVo saveFile(MultipartFile file) throws IOException {
        log.info("[AiFile] Saving file: {}, size: {}", file.getOriginalFilename(), file.getSize());

        // 验证文件
        validateFile(file);

        // 获取文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        String name = originalFilename;

        if (originalFilename != null && originalFilename.contains(".")) {
            int dotIndex = originalFilename.lastIndexOf(".");
            name = originalFilename.substring(0, dotIndex);
            ext = originalFilename.substring(dotIndex + 1);
        }

        // 生成唯一文件名
        String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件到本地文件系统
        Path filePath = uploadPath.resolve(uniqueFileName);
        file.transferTo(filePath.toFile());

        log.info("[AiFile] File saved to: {}", filePath.toAbsolutePath());

        // 保存文件元数据到数据库，使用SFileInfoVo的实际字段
        SFileInfoVo fileInfoVo = new SFileInfoVo();
        fileInfoVo.setFile_name(originalFilename);
        fileInfoVo.setFile_size(BigDecimal.valueOf(file.getSize()));
        fileInfoVo.setTimestamp(LocalDateTime.now());

        // 构造文件访问URL
        String fileUrl = fileBaseUrl + "/scm/api/v1/ai/file/download/" + uniqueFileName;
        fileInfoVo.setUrl(fileUrl);

        List<SFileInfoVo> fileList = new ArrayList<>();
        fileList.add(fileInfoVo);

        try {
            fileInfoService.insert(fileList);
        } catch (Exception e) {
            // 如果数据库保存失败,删除已保存的文件
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                log.error("[AiFile] Failed to delete file after DB error", ex);
            }
            log.error("[AiFile] Failed to save file metadata", e);
            throw new IOException("文件保存失败: " + e.getMessage(), e);
        }

        // 获取插入后的f_id作为uuid
        Integer fileId = fileInfoVo.getF_id();
        String uuid = fileId != null ? fileId.toString() : uniqueFileName;

        // 构造返回的VO
        AiFileVo result = new AiFileVo();
        result.setUuid(uuid);
        result.setName(originalFilename);
        result.setExt(ext);
        result.setSize(file.getSize());
        result.setUrl(fileUrl);

        log.info("[AiFile] File saved successfully, uuid: {}, f_id: {}", uuid, fileId);

        return result;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        // 文件不能为空
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 文件大小限制: 10MB
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IOException("文件大小不能超过10MB");
        }

        // 文件类型白名单
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IOException("文件必须有扩展名");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExts = List.of("txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx");

        if (!allowedExts.contains(ext)) {
            throw new IOException("不支持的文件格式: " + ext + "。支持格式: " + String.join(", ", allowedExts));
        }
    }

    /**
     * 删除文件
     */
    @Override
    public boolean deleteFile(String uuid) {
        log.info("[AiFile] Deleting file: {}", uuid);

        try {
            // 尝试将uuid解析为f_id
            Integer fId = null;
            try {
                fId = Integer.parseInt(uuid);
            } catch (NumberFormatException e) {
                log.warn("[AiFile] UUID is not a valid f_id, treating as filename: {}", uuid);
            }

            // 查询文件信息
            SFileInfoVo searchCondition = new SFileInfoVo();
            if (fId != null) {
                searchCondition.setF_id(fId);
            }
            List<SFileInfoVo> fileList = fileInfoService.selectList(searchCondition);

            if (fileList == null || fileList.isEmpty()) {
                log.warn("[AiFile] File not found: {}", uuid);
                return false;
            }

            SFileInfoVo fileInfo = fileList.get(0);

            // 从URL中提取文件名，删除物理文件
            String url = fileInfo.getUrl();
            if (url != null && url.contains("/")) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                try {
                    Path uploadPath = Paths.get(uploadDir);
                    Path filePath = uploadPath.resolve(fileName);
                    Files.deleteIfExists(filePath);
                    log.info("[AiFile] Physical file deleted: {}", filePath);
                } catch (IOException e) {
                    log.error("[AiFile] Failed to delete physical file: {}", fileName, e);
                }
            }

            // 删除数据库记录
            fileInfoService.realDeleteByIdsIn(fileList);

            log.info("[AiFile] File deleted successfully, uuid: {}", uuid);
            return true;
        } catch (Exception e) {
            log.error("[AiFile] Failed to delete file: {}", uuid, e);
            return false;
        }
    }
}
