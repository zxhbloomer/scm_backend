package com.xinyirun.scm.ai.controller.rag;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.core.service.KnowledgeBaseService;
import com.xinyirun.scm.ai.core.service.DocumentProcessingService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * AI知识库管理控制器
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Tag(name = "AI知识库管理")
@RestController
@RequestMapping("/api/v1/ai/knowledge-base")
@Validated
public class KnowledgeBaseController {

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    @Resource
    private DocumentProcessingService documentProcessingService;

    /**
     * 创建或更新知识库
     */
    @PostMapping("/saveOrUpdate")
    @Operation(summary = "创建或更新知识库")
    @SysLogAnnotion("创建或更新知识库")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseVo>> saveOrUpdate(
            @Valid @RequestBody AiKnowledgeBaseVo vo) {
        AiKnowledgeBaseVo result = knowledgeBaseService.saveOrUpdate(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 单文件上传
     */
    @PostMapping(value = "/upload/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "上传文档")
    @SysLogAnnotion("上传文档")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseItemVo>> upload(
            @PathVariable String uuid,
            @RequestParam(value = "indexAfterUpload", defaultValue = "true") Boolean indexAfterUpload,
            @RequestParam(defaultValue = "") String indexTypes,
            @RequestParam("file") MultipartFile file) throws IOException {

        List<String> indexTypeList = Arrays.asList(indexTypes.split(","));
        AiKnowledgeBaseItemVo result = documentProcessingService.uploadDoc(uuid, indexAfterUpload, file, indexTypeList);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 从URL创建文档
     */
    @PostMapping("/uploadFromUrl/{uuid}")
    @Operation(summary = "从URL创建文档")
    @SysLogAnnotion("从URL创建文档")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseItemVo>> uploadFromUrl(
            @PathVariable String uuid,
            @Valid @RequestBody UploadFromUrlRequestVo request) {

        List<String> indexTypeList = Arrays.asList(request.getIndexTypes().split(","));

        AiKnowledgeBaseItemVo result = documentProcessingService.uploadDocFromUrl(
                uuid,
                request.getFileUrl(),
                request.getFileName(),
                request.getFileSize(),
                request.getIndexAfterUpload(),
                indexTypeList
        );

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 搜索我的知识库
     */
    @GetMapping("/mine/search")
    @Operation(summary = "搜索我的知识库")
    @SysLogAnnotion("搜索我的知识库")
    public ResponseEntity<JsonResultAo<IPage<AiKnowledgeBaseVo>>> searchMine(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "false") Boolean includeOthersPublic,
            @NotNull @Min(1) @RequestParam Integer currentPage,
            @NotNull @Min(10) @RequestParam Integer pageSize) {

        IPage<AiKnowledgeBaseVo> result = knowledgeBaseService.searchMine(keyword, includeOthersPublic, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 搜索公开知识库
     */
    @GetMapping("/public/search")
    @Operation(summary = "搜索公开知识库")
    @SysLogAnnotion("搜索公开知识库")
    public ResponseEntity<JsonResultAo<IPage<AiKnowledgeBaseVo>>> searchPublic(
            @RequestParam(defaultValue = "") String keyword,
            @NotNull @Min(1) @RequestParam Integer currentPage,
            @NotNull @Min(10) @RequestParam Integer pageSize) {

        IPage<AiKnowledgeBaseVo> result = knowledgeBaseService.searchPublic(keyword, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 知识库详情
     */
    @GetMapping("/info/{uuid}")
    @Operation(summary = "知识库详情")
    @SysLogAnnotion("获取知识库详情")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseVo>> info(@PathVariable String uuid) {
        AiKnowledgeBaseVo result = knowledgeBaseService.getByUuid(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除知识库
     */
    @PostMapping("/del/{uuid}")
    @Operation(summary = "删除知识库")
    @SysLogAnnotion("删除知识库")
    public ResponseEntity<JsonResultAo<Boolean>> softDelete(@PathVariable String uuid) {
        boolean result = knowledgeBaseService.delete(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 索引整个知识库
     */
    @PostMapping("/indexing/{uuid}")
    @Operation(summary = "索引知识库")
    @SysLogAnnotion("索引知识库")
    public ResponseEntity<JsonResultAo<Boolean>> indexing(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "") String indexTypes) {

        List<String> indexTypeList = Arrays.asList(indexTypes.split(","));
        boolean result = knowledgeBaseService.indexing(uuid, indexTypeList);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 批量索引知识点
     */
    @PostMapping("/item/indexing-list")
    @Operation(summary = "批量索引知识点")
    @SysLogAnnotion("批量索引知识点")
    public ResponseEntity<JsonResultAo<Boolean>> indexItems(@RequestBody KbItemIndexBatchReq req) {
        List<String> uuidList = Arrays.asList(req.getUuids());
        List<String> indexTypeList = Arrays.asList(req.getIndexTypes().split(","));
        boolean result = knowledgeBaseService.indexItems(uuidList, indexTypeList);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 检查知识库索引状态
     */
    @GetMapping("/indexing/check")
    @Operation(summary = "检查索引状态")
    @SysLogAnnotion("检查索引状态")
    public ResponseEntity<JsonResultAo<Boolean>> checkIndex() {
        boolean result = knowledgeBaseService.checkIndexIsFinish();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 星标切换
     */
    @PostMapping("/star/toggle")
    @Operation(summary = "星标切换")
    @SysLogAnnotion("切换知识库星标")
    public ResponseEntity<JsonResultAo<Boolean>> toggleStar(@RequestParam @NotBlank String kbUuid) {
        boolean result = knowledgeBaseService.toggleStar(kbUuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 批量创建知识项（从前端上传的文件数组）
     */
    @PostMapping("/item/batch-create")
    @Operation(summary = "批量创建知识项")
    @SysLogAnnotion("批量创建知识项")
    public ResponseEntity<JsonResultAo<List<AiKnowledgeBaseItemVo>>> batchCreate(
            @Valid @RequestBody BatchCreateRequestVo request) {

        List<AiKnowledgeBaseItemVo> results = documentProcessingService.batchCreateItems(
            request.getKbUuid(),
            request.getDoc_att_files(),
            request.getIndexAfterUpload()
        );

        return ResponseEntity.ok().body(ResultUtil.OK(results, "批量创建成功"));
    }

    /**
     * 批量索引请求VO
     */
    public static class KbItemIndexBatchReq {
        private String[] uuids;
        private String indexTypes;

        public String[] getUuids() {
            return uuids;
        }

        public void setUuids(String[] uuids) {
            this.uuids = uuids;
        }

        public String getIndexTypes() {
            return indexTypes;
        }

        public void setIndexTypes(String indexTypes) {
            this.indexTypes = indexTypes;
        }
    }

    /**
     * 从URL上传文档请求VO
     */
    public static class UploadFromUrlRequestVo {
        @NotBlank(message = "文件URL不能为空")
        private String fileUrl;

        @NotBlank(message = "文件名不能为空")
        private String fileName;

        private Long fileSize;

        private Boolean indexAfterUpload = true;

        private String indexTypes = "embedding,graphical";

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public Boolean getIndexAfterUpload() {
            return indexAfterUpload;
        }

        public void setIndexAfterUpload(Boolean indexAfterUpload) {
            this.indexAfterUpload = indexAfterUpload;
        }

        public String getIndexTypes() {
            return indexTypes;
        }

        public void setIndexTypes(String indexTypes) {
            this.indexTypes = indexTypes;
        }
    }

    /**
     * 批量创建请求VO
     */
    public static class BatchCreateRequestVo {

        @NotBlank(message = "知识库UUID不能为空")
        private String kbUuid;

        @NotEmpty(message = "文件列表不能为空")
        private List<SFileInfoVo> doc_att_files;

        private Boolean indexAfterUpload = true;

        public String getKbUuid() {
            return kbUuid;
        }

        public void setKbUuid(String kbUuid) {
            this.kbUuid = kbUuid;
        }

        public List<SFileInfoVo> getDoc_att_files() {
            return doc_att_files;
        }

        public void setDoc_att_files(List<SFileInfoVo> doc_att_files) {
            this.doc_att_files = doc_att_files;
        }

        public Boolean getIndexAfterUpload() {
            return indexAfterUpload;
        }

        public void setIndexAfterUpload(Boolean indexAfterUpload) {
            this.indexAfterUpload = indexAfterUpload;
        }
    }
}
