package com.xinyirun.scm.ai.controller.model;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.domain.AiModelSource;
import com.xinyirun.scm.ai.bean.dto.request.AiModelSourceDTO;
import com.xinyirun.scm.ai.bean.dto.request.AiModelSourceRequest;
import com.xinyirun.scm.ai.bean.dto.sdk.OptionDTO;
import com.xinyirun.scm.ai.common.util.SessionUtils;
import com.xinyirun.scm.ai.core.service.model.SystemAIModelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/model/config")
@Tag(name = "系统设置-AI-模型配置")
public class SystemAIModelConfigController {

    @Resource
    private SystemAIModelConfigService systemAIModelConfigService;

    @PostMapping("/edit-source")
    @Operation(summary = "系统设置-编辑模型设置")
    @ResponseBody
    public AiModelSource editModuleConfig(@Validated @RequestBody AiModelSourceDTO aiModelSourceDTO) {
        return systemAIModelConfigService.editModuleConfig(aiModelSourceDTO, SessionUtils.getUserId());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除模型")
    public void delModelInformation(@PathVariable String id) {
        systemAIModelConfigService.delModelInformation(id, SessionUtils.getUserId());
    }

    @PostMapping("/source/list")
    @Operation(summary = "系统设置-查看模型集合")
    @ResponseBody
    public List<AiModelSourceDTO> getModelSourceList(@Validated @RequestBody AiModelSourceRequest aiModelSourceRequest) {
        return systemAIModelConfigService.getModelSourceList(aiModelSourceRequest);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取模型信息")
    @ResponseBody
    public AiModelSourceDTO getModelInformation(@PathVariable String id) {
        return systemAIModelConfigService.getModelSourceDTO(id, SessionUtils.getUserId());
    }

    @GetMapping("/source/name/list")
    @Operation(summary = "系统设置-查看模型名称集合")
    @ResponseBody
    public List<OptionDTO> getModelSourceNameList() {
        return systemAIModelConfigService.getModelSourceNameList(SessionUtils.getUserId());
    }
}