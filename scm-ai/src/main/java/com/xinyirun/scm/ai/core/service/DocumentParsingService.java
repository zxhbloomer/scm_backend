package com.xinyirun.scm.ai.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档解析服务
 *
 * <p>功能说明：</p>
 * 从URL加载文档并解析为文本内容
 *
 * <p>支持的文件类型：</p>
 * <ul>
 *   <li>PDF - 使用TikaDocumentReader</li>
 *   <li>TXT - 使用TikaDocumentReader</li>
 *   <li>DOC/DOCX/XLS/XLSX/PPT/PPTX - 使用TikaDocumentReader</li>
 * </ul>
 *
 * <p>实现方式：</p>
 * 使用Spring AI的UrlResource + TikaDocumentReader（基于Apache Tika）
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Service
@Slf4j
public class DocumentParsingService {

    /**
     * 从URL加载文档并解析为文本内容
     *
     * <p>
     * <pre>
     * public Document loadDocument(AdiFile adiFile) {
     *     String url = adiFile.getPath();
     *     String ext = FilenameUtils.getExtension(url).toLowerCase();
     *
     *     if (ext.equalsIgnoreCase("pdf")) {
     *         return UrlDocumentLoader.load(url, new ApachePdfBoxDocumentParser());
     *     } else if (ext.equalsIgnoreCase("txt")) {
     *         return UrlDocumentLoader.load(url, new TextDocumentParser());
     *     } else if (ArrayUtils.contains(POI_DOC_TYPES, ext)) {
     *         return UrlDocumentLoader.load(url, new ApachePoiDocumentParser());
     *     }
     * }
     * </pre>
     *
     * @param fileUrl 文件URL（前端上传到外部存储后返回的URL）
     * @param fileName 文件名（用于判断文件类型）
     * @return 解析后的文档内容
     */
    public String parseDocumentFromUrl(String fileUrl, String fileName) {
        try {
            // 1. 提取文件扩展名
            String ext = getFileExtension(fileName).toLowerCase();
            log.info("开始解析文档，文件名: {}, 扩展名: {}, URL: {}", fileName, ext, fileUrl);

            // 2. 创建UrlResource（处理URL中的中文字符）
            // 对URL进行编码处理，避免中文字符导致URISyntaxException
            String encodedUrl = encodeUrl(fileUrl);
            UrlResource resource = new UrlResource(new URI(encodedUrl).toURL());

            // 3. 根据文件类型选择解析器
            String content;
            if ("pdf".equals(ext)) {
                // PDF文件 - 
                content = parsePdf(resource);
            } else if ("txt".equals(ext)) {
                // 文本文件 - 
                content = parseTxt(resource);
            } else if (isOfficeDocument(ext)) {
                // Office文档 - 
                content = parseOfficeDocument(resource);
            } else {
                throw new IllegalArgumentException("不支持的文件格式: " + ext);
            }

            // 4. 清理文本内容
            content = cleanContent(content);

            log.info("文档解析完成，文件名: {}, 内容长度: {} 字符", fileName, content.length());
            return content;

        } catch (Exception e) {
            log.error("文档解析失败，文件名: {}, URL: {}, 错误: {}", fileName, fileUrl, e.getMessage(), e);
            throw new RuntimeException("文档解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析PDF文件
     * 
     *
     * <p>Spring AI实现：</p>
     * TikaDocumentReader - 使用Apache Tika解析PDF（内部使用PDFBox）
     *
     * <p>注意：Spring AI的PDF专用Reader在某些版本中可能不可用，</p>
     * <p>因此统一使用TikaDocumentReader，它支持包括PDF在内的所有文档格式</p>
     *
     * @param resource PDF文件资源
     * @return 解析后的文本内容
     */
    private String parsePdf(UrlResource resource) {
        // 使用TikaDocumentReader解析PDF
        // Tika内部会自动使用PDFBox来处理PDF文件
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        // 读取所有页面/片段
        List<Document> documents = reader.get();

        // 合并所有内容
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 解析文本文件
     * 
     *
     * @param resource 文本文件资源
     * @return 解析后的文本内容
     */
    private String parseTxt(UrlResource resource) {
        // TikaDocumentReader可以处理纯文本
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();

        return documents.isEmpty() ? "" : documents.get(0).getText();
    }

    /**
     * 解析Office文档（DOC/DOCX/XLS/XLSX/PPT/PPTX）
     * 
     *
     * <p>Spring AI实现：</p>
     * TikaDocumentReader - 内部使用Apache Tika，支持所有Office格式
     *
     * @param resource Office文档资源
     * @return 解析后的文本内容
     */
    private String parseOfficeDocument(UrlResource resource) {
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();

        // 合并所有文档片段
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 判断是否为Office文档
     *
     * <p>支持的格式：doc, docx, xls, xlsx, ppt, pptx</p>
     *
     * @param ext 文件扩展名
     * @return 是否为Office文档
     */
    private boolean isOfficeDocument(String ext) {
        return ext.equals("doc") || ext.equals("docx") ||
               ext.equals("xls") || ext.equals("xlsx") ||
               ext.equals("ppt") || ext.equals("pptx");
    }

    /**
     * 清理文档内容
     *
     * <p>移除空字符（\u0000）</p>
     *
     * @param content 原始内容
     * @return 清理后的内容
     */
    private String cleanContent(String content) {
        if (content == null) {
            return "";
        }

        // 移除空字符
        return content.replace("\u0000", "");
    }

    /**
     * 获取文件扩展名
     *
     *
     * @param fileName 文件名
     * @return 扩展名（不包含点号）
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 对URL进行编码处理
     *
     * <p>处理URL中的中文字符和特殊字符，避免URISyntaxException</p>
     * <p>只对路径部分的文件名进行编码，保留协议、域名和路径分隔符</p>
     *
     * @param url 原始URL
     * @return 编码后的URL
     */
    private String encodeUrl(String url) {
        try {
            // 如果URL已经编码过（包含%），直接返回
            if (url.contains("%")) {
                return url;
            }

            // 分离URL的各个部分：protocol://domain/path/filename
            int protocolEnd = url.indexOf("://");
            if (protocolEnd == -1) {
                // 没有协议，直接编码整个URL
                return URLEncoder.encode(url, StandardCharsets.UTF_8)
                        .replace("+", "%20"); // 空格用%20而不是+
            }

            String protocol = url.substring(0, protocolEnd + 3); // 包含://
            String remaining = url.substring(protocolEnd + 3);

            // 分离域名和路径
            int pathStart = remaining.indexOf("/");
            if (pathStart == -1) {
                // 只有域名，没有路径
                return url;
            }

            String domain = remaining.substring(0, pathStart);
            String path = remaining.substring(pathStart);

            // 对路径中的每个部分进行编码（保留/分隔符）
            String[] pathParts = path.split("/");
            StringBuilder encodedPath = new StringBuilder();
            for (String part : pathParts) {
                if (!part.isEmpty()) {
                    encodedPath.append("/")
                            .append(URLEncoder.encode(part, StandardCharsets.UTF_8)
                                    .replace("+", "%20")); // 空格用%20而不是+
                }
            }

            return protocol + domain + encodedPath.toString();

        } catch (Exception e) {
            log.warn("URL编码失败，使用原始URL: {}, 错误: {}", url, e.getMessage());
            return url;
        }
    }
}
