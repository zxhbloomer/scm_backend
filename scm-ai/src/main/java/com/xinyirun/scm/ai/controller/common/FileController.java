package com.xinyirun.scm.ai.controller.common;

import com.xinyirun.scm.ai.core.service.AiFileService;
import com.xinyirun.scm.ai.bean.vo.common.AiFileVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * AI文件上传控制器
 * 参考: aideepin FileController.java 第115-122行
 *
 * @author SCM AI Team
 * @since 2025-10-28
 */
@Slf4j
@Tag(name = "AI文件管理")
@RestController
@RequestMapping("/api/v1/ai")
@Validated
public class FileController {

    @Resource
    private AiFileService aiFileService;

    /**
     * 文件上传
     * 参考: aideepin FileController.java upload方法
     * 返回格式: { code: 20000, data: { uuid: "...", url: "..." } }
     */
    @PostMapping(
            value = "/file/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "上传文件")
    @SysLogAnnotion("上传文件")
    public ResponseEntity<JsonResultAo<AiFileVo>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("[FileUpload] Uploading file: {}, size: {}",
                file.getOriginalFilename(), file.getSize());

        // 调用service保存文件
        AiFileVo result = aiFileService.saveFile(file);

        log.info("[FileUpload] File saved, uuid: {}", result.getUuid());

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除文件
     */
    @PostMapping("/file/del/{uuid}")
    @Operation(summary = "删除文件")
    @SysLogAnnotion("删除文件")
    public ResponseEntity<JsonResultAo<Boolean>> delete(@PathVariable String uuid) {
        boolean success = aiFileService.deleteFile(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }
}
