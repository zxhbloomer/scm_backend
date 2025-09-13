/*
 * SCM AI Module - LLM Model Rest Controller
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI模型REST控制器，与ByteDesk源码类名方法名保持一致
 */
package com.xinyirun.scm.ai.model;

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
 * LLM模型REST控制器
 * 与ByteDesk源码类名和方法名保持完全一致
 * 
 * @author SCM-AI Module  
 * @version 1.0.0
 * @since 2025-01-12
 */
@RestController
@RequestMapping("/api/v1/model")
@Description("LLM Model Controller - Large Language Model management and configuration APIs")
public class LlmModelRestController {

    @Autowired
    private LlmModelRestService llmModelRestService;

    /**
     * 查询组织下的LLM模型
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @SysLogAnnotion("查询组织下的LLM模型")
    @PostMapping("/queryByOrg")
    public ResponseEntity<JsonResultAo<IPage<LlmModelResponse>>> queryByOrg(@RequestBody(required = false) LlmModelRequest request) {
        IPage<LlmModelResponse> page = llmModelRestService.queryPage(request);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 查询用户下的LLM模型
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @SysLogAnnotion("查询用户下的LLM模型")
    @PostMapping("/queryByUser")
    public ResponseEntity<JsonResultAo<IPage<LlmModelResponse>>> queryByUser(@RequestBody(required = false) LlmModelRequest request) {
        IPage<LlmModelResponse> page = llmModelRestService.queryPage(request);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 查询指定LLM模型
     * 
     * @param request 查询请求
     * @return 模型详情
     */
    @SysLogAnnotion("查询指定LLM模型")
    @PostMapping("/queryByUid")
    public ResponseEntity<JsonResultAo<LlmModelResponse>> queryByUid(@RequestBody(required = false) LlmModelRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("模型UID不能为空");
        }
        
        LlmModelResponse response = llmModelRestService.findByUid(request.getUid())
            .orElseThrow(() -> new BusinessException("模型不存在"));
        
        return ResponseEntity.ok().body(ResultUtil.OK(response));
    }

    /**
     * 创建LLM模型
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @SysLogAnnotion("创建LLM模型")
    @PostMapping("/create")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<LlmModelResponse>> create(@RequestBody LlmModelRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("模型名称不能为空");
        }
        if (request.getProviderUid() == null || request.getProviderUid().trim().isEmpty()) {
            throw new BusinessException("提供商UID不能为空");
        }
        
        try {
            LlmModelResponse response = llmModelRestService.create(request);
            return ResponseEntity.ok().body(ResultUtil.OK(response, "创建成功"));
        } catch (Exception e) {
            throw new InsertErrorException("创建LLM模型失败：" + e.getMessage());
        }
    }

    /**
     * 更新LLM模型
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    @SysLogAnnotion("更新LLM模型")
    @PostMapping("/update")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<LlmModelResponse>> update(@RequestBody LlmModelRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("模型UID不能为空");
        }
        
        try {
            LlmModelResponse response = llmModelRestService.update(request);
            return ResponseEntity.ok().body(ResultUtil.OK(response, "更新成功"));
        } catch (Exception e) {
            throw new UpdateErrorException("更新LLM模型失败：" + e.getMessage());
        }
    }

    /**
     * 删除LLM模型
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @SysLogAnnotion("删除LLM模型")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody LlmModelRequest request) {
        if (request.getUid() == null || request.getUid().trim().isEmpty()) {
            throw new BusinessException("模型UID不能为空");
        }
        
        try {
            llmModelRestService.deleteByUid(request.getUid());
            return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
        } catch (Exception e) {
            throw new UpdateErrorException("删除LLM模型失败：" + e.getMessage());
        }
    }

    /**
     * 导出LLM模型
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @SysLogAnnotion("导出LLM模型")
    @PostMapping("/export")
    public Object export(@RequestBody(required = false) LlmModelRequest request, HttpServletResponse response) {
        // TODO: 实现导出功能
        throw new UnsupportedOperationException("导出功能暂未实现");
    }

    /**
     * 根据提供商UID查询模型列表
     * 
     * @param request 查询请求
     * @return 模型列表
     */
    @SysLogAnnotion("根据提供商UID查询模型列表")
    @PostMapping("/queryByProviderUid")
    public ResponseEntity<JsonResultAo<List<LlmModelResponse>>> queryByProviderUid(@RequestBody(required = false) LlmModelRequest request) {
        if (request.getProviderUid() == null || request.getProviderUid().trim().isEmpty()) {
            throw new BusinessException("提供商UID不能为空");
        }
        
        List<LlmModelResponse> response = llmModelRestService.findByProviderUid(request.getProviderUid());
        return ResponseEntity.ok().body(ResultUtil.OK(response));
    }
}