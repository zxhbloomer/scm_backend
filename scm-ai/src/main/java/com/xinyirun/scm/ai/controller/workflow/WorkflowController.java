package com.xinyirun.scm.ai.controller.workflow;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowComponentService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowService;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI工作流Controller
 *
 * 提供工作流的创建、编辑、执行等功能的REST API
 *
 * @author SCM-AI Team
 */
@Slf4j
@Tag(name = "AI工作流管理")
@RestController
@RequestMapping("/api/v1/ai/workflow")
@Validated
public class WorkflowController {

    @Resource
    private WorkflowStarter workflowStarter;

    @Resource
    private AiWorkflowService workflowService;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    /**
     * 新增工作流
     *
     * @param title 标题
     * @param remark 备注
     * @param isPublic 是否公开
     * @param userId 用户ID
     * @return 工作流VO
     */
    @Operation(summary = "新增工作流")
    @PostMapping("/add")
    @SysLogAnnotion("新增工作流")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> add(@RequestParam String title,
                                                            @RequestParam(required = false) String remark,
                                                            @RequestParam(defaultValue = "0") Integer isPublic,
                                                            @RequestParam Long userId) {
        AiWorkflowVo vo = workflowService.add(title, remark, isPublic, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 复制工作流
     *
     * @param wfUuid 工作流UUID
     * @param userId 用户ID
     * @return 新工作流VO
     */
    @Operation(summary = "复制工作流")
    @PostMapping("/copy/{wfUuid}")
    @SysLogAnnotion("复制工作流")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> copy(@PathVariable String wfUuid,
                                                             @RequestParam Long userId) {
        AiWorkflowVo vo = workflowService.copy(wfUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 设置工作流公开状态
     *
     * @param wfUuid 工作流UUID
     * @param isPublic 是否公开
     * @param userId 用户ID
     * @return 成功响应
     */
    @Operation(summary = "设置工作流公开状态")
    @PostMapping("/set-public/{wfUuid}")
    @SysLogAnnotion("设置工作流公开状态")
    public ResponseEntity<JsonResultAo<Boolean>> setPublic(@PathVariable String wfUuid,
                                                             @RequestParam(defaultValue = "1") Integer isPublic,
                                                             @RequestParam Long userId) {
        workflowService.setPublic(wfUuid, isPublic, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 更新工作流基本信息
     *
     * @param wfUuid 工作流UUID
     * @param title 标题
     * @param remark 备注
     * @param isPublic 是否公开
     * @param userId 用户ID
     * @return 更新后的工作流VO
     */
    @Operation(summary = "更新工作流基本信息")
    @PostMapping("/base-info/update")
    @SysLogAnnotion("更新工作流基本信息")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> updateBaseInfo(@RequestParam String wfUuid,
                                                                       @RequestParam String title,
                                                                       @RequestParam(required = false) String remark,
                                                                       @RequestParam(required = false) Integer isPublic,
                                                                       @RequestParam Long userId) {
        AiWorkflowVo vo = workflowService.updateBaseInfo(wfUuid, title, remark, isPublic, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 删除工作流
     *
     * @param uuid 工作流UUID
     * @param userId 用户ID
     * @return 成功响应
     */
    @Operation(summary = "删除工作流")
    @PostMapping("/del/{uuid}")
    @SysLogAnnotion("删除工作流")
    public ResponseEntity<JsonResultAo<Boolean>> delete(@PathVariable String uuid,
                                                         @RequestParam Long userId) {
        workflowService.softDelete(uuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 启用/禁用工作流
     *
     * @param uuid 工作流UUID
     * @param enable 是否启用(0-禁用,1-启用)
     * @param userId 用户ID
     * @return 成功响应
     */
    @Operation(summary = "启用/禁用工作流")
    @PostMapping("/enable/{uuid}")
    @SysLogAnnotion("启用/禁用工作流")
    public ResponseEntity<JsonResultAo<Boolean>> enable(@PathVariable String uuid,
                                                         @RequestParam Integer enable,
                                                         @RequestParam Long userId) {
        workflowService.enable(uuid, enable, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 流式执行工作流
     *
     * @param wfUuid 工作流UUID
     * @param userId 用户ID
     * @param inputs 用户输入参数
     * @return SSE Emitter
     */
    @Operation(summary = "流式执行工作流")
    @PostMapping(value = "/run/{wfUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SysLogAnnotion("流式执行工作流")
    public SseEmitter run(@PathVariable String wfUuid,
                          @RequestParam Long userId,
                          @RequestBody List<ObjectNode> inputs) {
        return workflowStarter.streaming(userId, wfUuid, inputs);
    }

    /**
     * 搜索我的工作流
     *
     * @param keyword 关键词
     * @param isPublic 是否公开
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Operation(summary = "搜索我的工作流")
    @GetMapping("/mine/search")
    @SysLogAnnotion("搜索我的工作流")
    public ResponseEntity<JsonResultAo<Page<AiWorkflowVo>>> searchMine(@RequestParam(defaultValue = "") String keyword,
                                                                        @RequestParam(required = false) Integer isPublic,
                                                                        @RequestParam Long userId,
                                                                        @NotNull @Min(1) Integer currentPage,
                                                                        @NotNull @Min(10) Integer pageSize) {
        Page<AiWorkflowVo> page = workflowService.search(keyword, isPublic, userId, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 搜索公开工作流
     *
     * @param keyword 搜索关键词
     * @param currentPage 当前页数
     * @param pageSize 每页数量
     * @return 工作流列表
     */
    @Operation(summary = "搜索公开工作流")
    @GetMapping("/public/search")
    @SysLogAnnotion("搜索公开工作流")
    public ResponseEntity<JsonResultAo<Page<AiWorkflowVo>>> searchPublic(@RequestParam(defaultValue = "") String keyword,
                                                                          @NotNull @Min(1) Integer currentPage,
                                                                          @NotNull @Min(10) Integer pageSize) {
        Page<AiWorkflowVo> page = workflowService.searchPublic(keyword, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 获取所有启用的组件列表
     *
     * @return 组件列表
     */
    @Operation(summary = "获取所有启用的组件列表")
    @GetMapping("/public/component/list")
    @SysLogAnnotion("获取所有启用的组件列表")
    public ResponseEntity<JsonResultAo<List<AiWorkflowComponentEntity>>> componentList() {
        List<AiWorkflowComponentEntity> list = workflowComponentService.getAllEnable();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
