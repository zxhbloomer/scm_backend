package com.xinyirun.scm.ai.controller.model;

import com.xinyirun.scm.ai.bean.constant.ModelConstants;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.OptionVo;
import com.xinyirun.scm.ai.bean.vo.response.ModelOptionVo;
import com.xinyirun.scm.ai.core.service.chat.SystemAIModelConfigService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
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
    @SysLogAnnotion("编辑模型设置")
    @ResponseBody
    public AiModelSourceVo editModuleConfig(@Validated @RequestBody AiModelSourceVo aiModelSourceVo) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        return systemAIModelConfigService.editModuleConfig(aiModelSourceVo, userId);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除模型")
    @SysLogAnnotion("删除模型")
    public void delModelInformation(@PathVariable String id) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        systemAIModelConfigService.delModelInformation(id, userId);
    }

    @PostMapping("/source/list")
    @Operation(summary = "系统设置-查看模型集合")
    @SysLogAnnotion("查看模型集合")
    @ResponseBody
    public List<AiModelSourceVo> getModelSourceList(@Validated @RequestBody AiModelSourceRequestVo aiModelSourceRequest) {
        return systemAIModelConfigService.getModelSourceList(aiModelSourceRequest);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取模型信息")
    @SysLogAnnotion("获取模型信息")
    @ResponseBody
    public AiModelSourceVo getModelInformation(@PathVariable String id) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        return systemAIModelConfigService.getModelSourceVo(id, userId);
    }

    @GetMapping("/source/name/list")
    @Operation(summary = "系统设置-查看模型名称集合")
    @SysLogAnnotion("查看模型名称集合")
    @ResponseBody
    public List<ModelOptionVo> getModelSourceNameList() {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : ModelConstants.SYSTEM_OWNER;
        return systemAIModelConfigService.getModelSourceNameList(userId);
    }
}