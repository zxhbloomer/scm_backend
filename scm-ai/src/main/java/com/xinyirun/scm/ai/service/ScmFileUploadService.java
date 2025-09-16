package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.entity.AiChatRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * SCM AI文件上传服务类
 * 
 * 负责处理AI聊天中的文件上传功能，包括：
 * 1. 文件类型验证和安全检查
 * 2. 文件存储路径管理
 * 3. 文件名处理和重命名
 * 4. 文件大小限制控制
 * 5. 支持多租户文件隔离
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Slf4j
@Service
public class ScmFileUploadService {

    /**
     * 文件上传根路径
     * 可通过配置文件配置: scm.ai.upload.path
     */
    @Value("${scm.ai.upload.path:/data/scm/ai-chat-files}")
    private String uploadBasePath;

    /**
     * 单个文件最大大小（字节）
     * 默认10MB，可通过配置文件配置: scm.ai.upload.max-file-size
     */
    @Value("${scm.ai.upload.max-file-size:10485760}")
    private long maxFileSize;

    /**
     * 允许上传的文件类型
     * 可通过配置文件配置: scm.ai.upload.allowed-types
     */
    @Value("${scm.ai.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,txt,csv}")
    private String allowedFileTypes;

    /**
     * 日期格式化器，用于生成目录结构
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 危险文件扩展名黑名单
     */
    private static final List<String> DANGEROUS_EXTENSIONS = Arrays.asList(
            "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "war", 
            "php", "asp", "jsp", "sh", "py", "rb", "pl", "go"
    );

    /**
     * 上传聊天文件
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID  
     * @param sessionId 会话ID
     * @param file 上传的文件
     * @return 文件附件信息
     * @throws IOException 文件处理异常
     * @throws IllegalArgumentException 参数验证异常
     */
    public AiChatRecord.AttachmentInfo uploadChatFile(String tenantId, Long userId, 
                                                    String sessionId, MultipartFile file) throws IOException {
        
        log.info("开始上传聊天文件 - 租户: {}, 用户: {}, 会话: {}, 文件: {}", 
                tenantId, userId, sessionId, file.getOriginalFilename());

        // 1. 基础验证
        validateFile(file);
        
        // 2. 安全检查
        performSecurityCheck(file);
        
        // 3. 生成存储路径和文件名
        String storedFileName = generateStoredFileName(file.getOriginalFilename());
        Path relativePath = generateFilePath(tenantId, userId, sessionId, storedFileName);
        Path absolutePath = Paths.get(uploadBasePath).resolve(relativePath);
        
        // 4. 确保目录存在
        Files.createDirectories(absolutePath.getParent());
        
        // 5. 保存文件
        try {
            Files.copy(file.getInputStream(), absolutePath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("文件保存成功: {}", absolutePath);
            
        } catch (IOException e) {
            log.error("文件保存失败: {} - 错误: {}", absolutePath, e.getMessage());
            throw new IOException("文件保存失败: " + e.getMessage(), e);
        }
        
        // 6. 创建附件信息
        AiChatRecord.AttachmentInfo attachmentInfo = new AiChatRecord.AttachmentInfo()
                .setOriginalName(file.getOriginalFilename())
                .setStoredName(storedFileName)
                .setFilePath(relativePath.toString().replace("\\", "/"))  // 统一使用正斜杠
                .setFileSize(file.getSize())
                .setContentType(file.getContentType())
                .setUploadTime(LocalDateTime.now());
        
        log.info("聊天文件上传成功 - 原名: {}, 存储名: {}, 路径: {}, 大小: {} bytes", 
                file.getOriginalFilename(), storedFileName, attachmentInfo.getFilePath(), file.getSize());
        
        return attachmentInfo;
    }

    /**
     * 验证文件基础信息
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                String.format("文件大小超出限制，最大允许 %.1f MB", maxFileSize / 1024.0 / 1024.0));
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        // 验证文件扩展名
        String extension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowedTypes = Arrays.asList(allowedFileTypes.toLowerCase().split(","));
        
        if (!allowedTypes.contains(extension)) {
            throw new IllegalArgumentException(
                String.format("不支持的文件类型: %s，允许的类型: %s", extension, allowedFileTypes));
        }
    }

    /**
     * 执行安全检查
     */
    private void performSecurityCheck(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename).toLowerCase();
        
        // 检查危险文件扩展名
        if (DANGEROUS_EXTENSIONS.contains(extension)) {
            log.warn("检测到危险文件类型上传尝试: {}", originalFilename);
            throw new IllegalArgumentException("出于安全考虑，不允许上传该类型的文件: " + extension);
        }
        
        // 检查文件名中的危险字符
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            log.warn("检测到危险路径字符: {}", originalFilename);
            throw new IllegalArgumentException("文件名包含非法字符");
        }
        
        // 检查MIME类型与文件扩展名是否匹配（简单检查）
        String contentType = file.getContentType();
        if (contentType != null && !isMimeTypeMatched(extension, contentType)) {
            log.warn("文件扩展名与MIME类型不匹配: {} - {}", extension, contentType);
            // 这里只记录警告，不阻止上传，因为某些客户端可能发送不准确的MIME类型
        }
    }

    /**
     * 检查MIME类型与文件扩展名是否匹配
     */
    private boolean isMimeTypeMatched(String extension, String contentType) {
        // 简单的MIME类型匹配检查
        switch (extension) {
            case "jpg":
            case "jpeg":
                return contentType.startsWith("image/jpeg");
            case "png":
                return contentType.startsWith("image/png");
            case "gif":
                return contentType.startsWith("image/gif");
            case "pdf":
                return contentType.equals("application/pdf");
            case "txt":
                return contentType.startsWith("text/");
            case "csv":
                return contentType.contains("csv") || contentType.startsWith("text/");
            default:
                return true; // 对于其他类型，暂时不做严格检查
        }
    }

    /**
     * 生成存储文件名
     * 格式: UUID_原文件名
     */
    private String generateStoredFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String nameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        
        // 清理文件名中的特殊字符
        nameWithoutExt = nameWithoutExt.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5._-]", "_");
        
        return UUID.randomUUID().toString() + "_" + nameWithoutExt + "." + extension;
    }

    /**
     * 生成文件存储路径
     * 路径结构: tenant/{tenantId}/chat/{yyyy}/{MM}/{dd}/{userId}/{sessionId}/
     */
    private Path generateFilePath(String tenantId, Long userId, String sessionId, String storedFileName) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DATE_FORMATTER);
        
        return Paths.get("tenant", tenantId, "chat", datePath, userId.toString(), sessionId, storedFileName);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * 获取文件的完整路径
     * 
     * @param relativePath 相对路径
     * @return 绝对路径
     */
    public Path getAbsolutePath(String relativePath) {
        return Paths.get(uploadBasePath).resolve(relativePath);
    }

    /**
     * 检查文件是否存在
     * 
     * @param relativePath 相对路径
     * @return 文件是否存在
     */
    public boolean fileExists(String relativePath) {
        Path absolutePath = getAbsolutePath(relativePath);
        return Files.exists(absolutePath) && Files.isRegularFile(absolutePath);
    }

    /**
     * 删除文件
     * 
     * @param relativePath 相对路径
     * @return 是否删除成功
     */
    public boolean deleteFile(String relativePath) {
        try {
            Path absolutePath = getAbsolutePath(relativePath);
            if (Files.exists(absolutePath)) {
                Files.delete(absolutePath);
                log.info("文件删除成功: {}", relativePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("文件删除失败: {} - 错误: {}", relativePath, e.getMessage());
            return false;
        }
    }

    /**
     * 获取文件大小
     * 
     * @param relativePath 相对路径
     * @return 文件大小（字节），文件不存在返回-1
     */
    public long getFileSize(String relativePath) {
        try {
            Path absolutePath = getAbsolutePath(relativePath);
            if (Files.exists(absolutePath)) {
                return Files.size(absolutePath);
            }
            return -1;
        } catch (IOException e) {
            log.error("获取文件大小失败: {} - 错误: {}", relativePath, e.getMessage());
            return -1;
        }
    }

    /**
     * 清理过期文件
     * 
     * @param daysToKeep 保留天数
     * @return 清理的文件数量
     */
    public int cleanupExpiredFiles(int daysToKeep) {
        // TODO: 实现文件清理逻辑
        log.info("开始清理{}天前的过期文件", daysToKeep);
        
        // 这里可以实现遍历目录，删除过期文件的逻辑
        // 建议通过定时任务调用此方法
        
        return 0;
    }
}