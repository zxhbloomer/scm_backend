/*
 * SCM AI Module - LLM Provider Rest Controller
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商REST控制器，与ByteDesk源码类名方法名保持一致
 */
package com.xinyirun.scm.ai.provider;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LLM提供商REST控制器
 * 与ByteDesk源码类名和方法名保持完全一致
 * 
 * @author SCM-AI Module  
 * @version 1.0.0
 * @since 2025-01-12
 */
@RestController
@RequestMapping("/api/v1/provider")
@Description("LLM Provider Controller - Large Language Model provider management and configuration APIs")
public class LlmProviderRestController {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    /**
     * 查询组织下的LLM提供商
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @SysLogAnnotion("查询组织下的LLM提供商")
    @PostMapping("/queryByOrg")
    public ResponseEntity<JsonResultAo<IPage<LlmProviderResponse>>> queryByOrg(@RequestBody(required = false) LlmProviderRequest request) {
        IPage<LlmProviderResponse> page = llmProviderRestService.queryPage(request);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 查询用户下的LLM提供商
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @SysLogAnnotion("查询用户下的LLM提供商")
    @PostMapping("/queryByUser")
    public ResponseEntity<JsonResultAo<IPage<LlmProviderResponse>>> queryByUser(@RequestBody(required = false) LlmProviderRequest request) {
        IPage<LlmProviderResponse> page = llmProviderRestService.queryPage(request);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 查询指定LLM提供商
     * 
     * @param request 查询请求
     * @return 提供商详情
     */
    @SysLogAnnotion("查询指定LLM提供商")
    @PostMapping("/queryByUid")
    public ResponseEntity<JsonResultAo<LlmProviderResponse>> queryByUid(@RequestBody(required = false) LlmProviderRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("提供商UID不能为空");
        }
        
        LlmProviderResponse response = llmProviderRestService.findByUid(request.getUid())
            .orElseThrow(() -> new BusinessException("提供商不存在"));
        
        return ResponseEntity.ok().body(ResultUtil.OK(response));
    }

    /**
     * 创建LLM提供商
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @SysLogAnnotion("创建LLM提供商")
    @PostMapping("/create")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<LlmProviderResponse>> create(@RequestBody LlmProviderRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("提供商名称不能为空");
        }
        
        try {
            LlmProviderResponse response = llmProviderRestService.create(request);
            return ResponseEntity.ok().body(ResultUtil.OK(response, "创建成功"));
        } catch (Exception e) {
            throw new InsertErrorException("创建LLM提供商失败：" + e.getMessage());
        }
    }

    /**
     * 更新LLM提供商
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    @SysLogAnnotion("更新LLM提供商")
    @PostMapping("/update")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<LlmProviderResponse>> update(@RequestBody LlmProviderRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("提供商UID不能为空");
        }
        
        try {
            LlmProviderResponse response = llmProviderRestService.update(request);
            return ResponseEntity.ok().body(ResultUtil.OK(response, "更新成功"));
        } catch (Exception e) {
            throw new UpdateErrorException("更新LLM提供商失败：" + e.getMessage());
        }
    }

    /**
     * 删除LLM提供商
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @SysLogAnnotion("删除LLM提供商")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody LlmProviderRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("提供商UID不能为空");
        }
        
        try {
            llmProviderRestService.deleteByUid(request.getUid());
            return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
        } catch (Exception e) {
            throw new UpdateErrorException("删除LLM提供商失败：" + e.getMessage());
        }
    }

    /**
     * 导出LLM提供商
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @SysLogAnnotion("导出LLM提供商")
    @PostMapping("/export")
    public Object export(@RequestBody(required = false) LlmProviderRequest request, HttpServletResponse response) {
        // TODO: 实现导出功能
        throw new UnsupportedOperationException("导出功能暂未实现");
    }

    /**
     * 获取LLM提供商默认配置
     * 
     * @return 默认配置
     */
    @SysLogAnnotion("获取LLM提供商默认配置")
    @GetMapping("/config/default")
    public ResponseEntity<JsonResultAo<List<LlmProviderResponse>>> getLlmProviderConfigDefault() {
        List<LlmProviderResponse> response = llmProviderRestService.findByEnabled();
        return ResponseEntity.ok().body(ResultUtil.OK(response));
    }
}