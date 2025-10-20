package com.xinyirun.scm.ai.controller.config;

import com.xinyirun.scm.ai.common.constant.ModelConstants;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.config.DefaultModelsVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.core.service.config.AiConfigService;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI模型配置Controller
 */
@RestController
@RequestMapping("/api/v1/ai/model/config")
@Tag(name = "系统设置-AI-模型配置")
public class AiModelConfigController {

    @Resource
    private AiModelConfigService aiModelConfigService;

    @Resource
    private AiConfigService aiConfigService;

    @PostMapping("/edit-source")
    @Operation(summary = "系统设置-编辑模型设置")
    @SysLogAnnotion("编辑模型设置")
    @ResponseBody
    public AiModelConfigVo editModelConfig(@Validated @RequestBody AiModelConfigVo aiModelConfigVo) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        return aiModelConfigService.editModelConfig(aiModelConfigVo, userId);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除模型")
    @SysLogAnnotion("删除模型")
    public void deleteModelConfig(@PathVariable Long id) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        aiModelConfigService.deleteModelConfig(id, userId);
    }

    @PostMapping("/source/list")
    @Operation(summary = "系统设置-查看模型集合")
    @SysLogAnnotion("查看模型集合")
    @ResponseBody
    public List<AiModelConfigVo> getModelConfigList(@Validated @RequestBody AiModelSourceRequestVo request) {
        return aiModelConfigService.getModelConfigList(request);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取模型信息")
    @SysLogAnnotion("获取模型信息")
    @ResponseBody
    public AiModelConfigVo getModelConfig(@PathVariable Long id) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        return aiModelConfigService.getModelConfigVo(id, userId);
    }

    @PostMapping("/llm-models")
    @Operation(summary = "获取可用的语言模型列表")
    @SysLogAnnotion("获取可用的语言模型列表")
    public ResponseEntity<JsonResultAo<List<AiModelConfigVo>>> getLlmModels() {
        List<AiModelConfigVo> result = aiModelConfigService.getAvailableLlmModels();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/vision-models")
    @Operation(summary = "获取可用的视觉模型列表")
    @SysLogAnnotion("获取可用的视觉模型列表")
    public ResponseEntity<JsonResultAo<List<AiModelConfigVo>>> getVisionModels() {
        List<AiModelConfigVo> result = aiModelConfigService.getAvailableVisionModels();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/embedding-models")
    @Operation(summary = "获取可用的嵌入模型列表")
    @SysLogAnnotion("获取可用的嵌入模型列表")
    public ResponseEntity<JsonResultAo<List<AiModelConfigVo>>> getEmbeddingModels() {
        List<AiModelConfigVo> result = aiModelConfigService.getAvailableEmbeddingModels();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/default-models")
    @Operation(summary = "获取默认模型配置")
    @SysLogAnnotion("获取默认模型配置")
    public ResponseEntity<JsonResultAo<DefaultModelsVo>> getDefaultModels() {
        DefaultModelsVo result = aiConfigService.getDefaultModels();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/default-model")
    @Operation(summary = "设置默认模型")
    @SysLogAnnotion("设置默认模型")
    @ResponseBody
    public void setDefaultModel(@RequestBody Map<String, Object> request) {
        String modelType = (String) request.get("modelType");
        Object modelIdObj = request.get("modelId");

        Long modelId = null;
        if (modelIdObj != null) {
            if (modelIdObj instanceof Number) {
                modelId = ((Number) modelIdObj).longValue();
            } else if (modelIdObj instanceof String) {
                try {
                    modelId = Long.parseLong((String) modelIdObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("模型ID格式错误");
                }
            }
        }

        aiConfigService.setDefaultModel(modelType, modelId);
    }
}
