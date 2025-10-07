package com.xinyirun.scm.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档解析服务
 *
 * <p>功能说明：</p>
 * 严格对应aideepin的FileOperatorContext.loadDocument()逻辑
 * 从URL加载文档并解析为文本内容
 *
 * <p>支持的文件类型：</p>
 * <ul>
 *   <li>PDF - 使用PagePdfDocumentReader（对应aideepin的ApachePdfBoxDocumentParser）</li>
 *   <li>TXT - 使用TikaDocumentReader（对应aideepin的TextDocumentParser）</li>
 *   <li>DOC/DOCX/XLS/XLSX/PPT/PPTX - 使用TikaDocumentReader（对应aideepin的ApachePoiDocumentParser）</li>
 * </ul>
 *
 * <p>实现原理：</p>
 * aideepin使用LangChain4j的UrlDocumentLoader.load(url, parser)
 * scm-ai使用Spring AI的UrlResource + DocumentReader
 *
 * <p>参考代码：</p>
 * aideepin: AliyunOssFileOperator.loadDocument()
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\file\AliyunOssFileOperator.java
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
     * <p>对应aideepin代码：</p>
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

            // 2. 创建UrlResource（使用URI避免过时警告）
            UrlResource resource = new UrlResource(new URI(fileUrl).toURL());

            // 3. 根据文件类型选择解析器
            String content;
            if ("pdf".equals(ext)) {
                // PDF文件 - 对应aideepin的ApachePdfBoxDocumentParser
                content = parsePdf(resource);
            } else if ("txt".equals(ext)) {
                // 文本文件 - 对应aideepin的TextDocumentParser
                content = parseTxt(resource);
            } else if (isOfficeDocument(ext)) {
                // Office文档 - 对应aideepin的ApachePoiDocumentParser
                content = parseOfficeDocument(resource);
            } else {
                throw new IllegalArgumentException("不支持的文件格式: " + ext);
            }

            // 4. 清理文本内容（对应aideepin的.replace("\u0000", "")）
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
     * 对应aideepin的ApachePdfBoxDocumentParser
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

        // 合并所有内容（对应aideepin返回的单个Document）
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 解析文本文件
     * 对应aideepin的TextDocumentParser
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
     * 对应aideepin的ApachePoiDocumentParser
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
     * 对应aideepin的ArrayUtils.contains(POI_DOC_TYPES, ext)
     *
     * <p>aideepin支持的格式：</p>
     * private static final String[] POI_DOC_TYPES = new String[]{"doc", "docx", "xls", "xlsx", "ppt", "pptx"};
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
     * 对应aideepin的content.replace("\u0000", "")
     *
     * <p>aideepin代码：</p>
     * <pre>
     * String content = document.text().replace("\u0000", "");
     * </pre>
     *
     * @param content 原始内容
     * @return 清理后的内容
     */
    private String cleanContent(String content) {
        if (content == null) {
            return "";
        }

        // 移除空字符（对应aideepin的replace("\u0000", "")）
        return content.replace("\u0000", "");
    }

    /**
     * 获取文件扩展名
     * 对应aideepin的FilenameUtils.getExtension()
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
}
