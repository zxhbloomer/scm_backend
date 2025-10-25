package com.xinyirun.scm.ai.controller.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowComponentService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowRuntimeService;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.ai.workflow.node.switcher.OperatorEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Resource
    private AiWorkflowRuntimeService workflowRuntimeService;

    /**
     * 新增工作流
     *
     * @param vo 工作流VO对象
     * @return 工作流VO
     */
    @Operation(summary = "新增工作流")
    @PostMapping("/add")
    @SysLogAnnotion("新增工作流")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> add(@RequestBody AiWorkflowVo vo) {
        AiWorkflowVo result = workflowService.add(vo.getTitle(), vo.getRemark(), vo.getIsPublic());
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 复制工作流
     *
     * @param wfUuid 工作流UUID
     * @return 新工作流VO
     */
    @Operation(summary = "复制工作流")
    @PostMapping("/copy/{wfUuid}")
    @SysLogAnnotion("复制工作流")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> copy(@PathVariable String wfUuid) {
        AiWorkflowVo vo = workflowService.copy(wfUuid);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 设置工作流公开状态
     *
     * @param wfUuid 工作流UUID
     * @param isPublic 是否公开
     * @return 成功响应
     */
    @Operation(summary = "设置工作流公开状态")
    @PostMapping("/set-public/{wfUuid}")
    @SysLogAnnotion("设置工作流公开状态")
    public ResponseEntity<JsonResultAo<Boolean>> setPublic(@PathVariable String wfUuid,
                                                             @RequestParam(defaultValue = "true") Boolean isPublic) {
        workflowService.setPublic(wfUuid, isPublic);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 更新工作流基本信息
     *
     * @param vo 工作流VO对象
     * @return 更新后的工作流VO
     */
    @Operation(summary = "更新工作流基本信息")
    @PostMapping("/base-info/update")
    @SysLogAnnotion("更新工作流基本信息")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> updateBaseInfo(@RequestBody AiWorkflowVo vo) {
        AiWorkflowVo result = workflowService.updateBaseInfo(vo.getWorkflowUuid(), vo.getTitle(), vo.getRemark(), vo.getIsPublic());
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 更新工作流
     * 对应AIDeepin: /api/workflow/update
     * 对应前端: workflowService.workflowUpdate({id, title, remark, isPublic, nodes, edges})
     *
     * @param vo 工作流VO对象
     * @return 更新后的工作流VO
     */
    @Operation(summary = "更新工作流")
    @PostMapping("/update")
    @SysLogAnnotion("更新工作流")
    public ResponseEntity<JsonResultAo<AiWorkflowVo>> update(@RequestBody(required = false) AiWorkflowVo vo) {
        log.info("更新工作流,workflowId:{},title:{}", vo.getId(), vo.getTitle());
        AiWorkflowVo result = workflowService.update(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除工作流
     *
     * @param uuid 工作流UUID
     * @return 成功响应
     */
    @Operation(summary = "删除工作流")
    @PostMapping("/del/{uuid}")
    @SysLogAnnotion("删除工作流")
    public ResponseEntity<JsonResultAo<Boolean>> delete(@PathVariable String uuid) {
        workflowService.softDelete(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 启用/禁用工作流
     *
     * @param uuid 工作流UUID
     * @param enable 是否启用
     * @return 成功响应
     */
    @Operation(summary = "启用/禁用工作流")
    @PostMapping("/enable/{uuid}")
    @SysLogAnnotion("启用/禁用工作流")
    public ResponseEntity<JsonResultAo<Boolean>> enable(@PathVariable String uuid,
                                                         @RequestParam Boolean enable) {
        workflowService.enable(uuid, enable);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 流式执行工作流
     *
     * @param wfUuid 工作流UUID
     * @param inputs 用户输入参数
     * @return SSE Emitter
     */
    @Operation(summary = "流式执行工作流")
    @PostMapping(value = "/run/{wfUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SysLogAnnotion("流式执行工作流")
    public SseEmitter run(@PathVariable String wfUuid,
                          @RequestBody List<JSONObject> inputs) {
        return workflowStarter.streaming(wfUuid, inputs);
    }

    /**
     * 搜索我的工作流
     *
     * @param keyword 关键词
     * @param isPublic 是否公开
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Operation(summary = "搜索我的工作流")
    @GetMapping("/mine/search")
    @SysLogAnnotion("搜索我的工作流")
    public ResponseEntity<JsonResultAo<Page<AiWorkflowVo>>> searchMine(@RequestParam(defaultValue = "") String keyword,
                                                                        @RequestParam(required = false) Boolean isPublic,
                                                                        @NotNull @Min(1) Integer currentPage,
                                                                        @NotNull @Min(10) Integer pageSize) {
        Page<AiWorkflowVo> page = workflowService.search(keyword, isPublic, currentPage, pageSize);
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

    /**
     * 获取工作流操作符列表
     * 对应AIDeepin: WorkflowController.searchPublic() - GET /workflow/public/operators
     * 对应前端: workflowApi.workflowOperators()
     *
     * @return 操作符列表
     */
    @Operation(summary = "获取工作流操作符列表")
    @GetMapping("/public/operators")
    @SysLogAnnotion("获取工作流操作符列表")
    public ResponseEntity<JsonResultAo<List<Map<String, String>>>> operatorList() {
        List<Map<String, String>> result = new ArrayList<>();
        // 根据aideepin的OperatorEnum实现，返回操作符列表
        for (OperatorEnum operator : OperatorEnum.values()) {
            result.add(Map.of("name", operator.getName(), "desc", operator.getDesc()));
        }
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    // ==================== 工作流运行时相关接口 ====================

    /**
     * 获取工作流运行时列表(分页)
     * 对应前端: workflowService.getWorkflowRuntimeList({workflowId, currentPage, pageSize})
     *
     * @param workflowId 工作流ID
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @return 运行时分页数据
     */
    @Operation(summary = "获取工作流运行时列表")
    @PostMapping("/runtime/list")
    @SysLogAnnotion("获取工作流运行时列表")
    public ResponseEntity<JsonResultAo<Page<AiWorkflowRuntimeVo>>> listRuntimeByPage(@RequestParam @NotNull Long workflowId,
                                                               @RequestParam @NotNull @Min(1) Integer currentPage,
                                                               @RequestParam @NotNull @Min(10) Integer pageSize) {
        log.info("查询工作流运行时列表,workflowId:{},currentPage:{},pageSize:{}", workflowId, currentPage, pageSize);

        // ID转UUID
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + workflowId);
        }

        Page<AiWorkflowRuntimeVo> page = workflowRuntimeService.page(
            workflow.getWorkflowUuid(), currentPage, pageSize);

        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 获取运行时节点详情列表
     * 对应前端: workflowService.getRuntimeNodeDetails(runtimeId)
     *
     * @param runtimeId 运行时ID
     * @return 运行时节点列表
     */
    @Operation(summary = "获取运行时节点详情列表")
    @GetMapping("/runtime/nodes/{runtimeId}")
    @SysLogAnnotion("获取运行时节点详情列表")
    public ResponseEntity<JsonResultAo<List<AiWorkflowRuntimeNodeVo>>> listRuntimeNodes(@PathVariable @NotNull Long runtimeId) {
        log.info("查询运行时节点列表,runtimeId:{}", runtimeId);

        // ID转UUID
        AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getById(runtimeId);
        if (runtime == null) {
            throw new RuntimeException("运行时实例不存在: " + runtimeId);
        }

        List<AiWorkflowRuntimeNodeVo> nodes = workflowRuntimeService.listByRuntimeUuid(
            runtime.getRuntimeUuid());

        return ResponseEntity.ok().body(ResultUtil.OK(nodes));
    }

    /**
     * 清空工作流运行时历史
     * 对应前端: workflowService.clearWorkflowRuntimeHistory(workflowId)
     *
     * @param workflowId 工作流ID
     * @return 清空结果
     */
    @Operation(summary = "清空工作流运行时历史")
    @PostMapping("/runtime/clear/{workflowId}")
    @SysLogAnnotion("清空工作流运行时历史")
    public ResponseEntity<JsonResultAo<Boolean>> clearRuntimeHistory(@PathVariable @NotNull Long workflowId) {
        log.info("清空工作流运行时历史,workflowId:{}", workflowId);

        // ID转UUID
        AiWorkflowEntity workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在: " + workflowId);
        }

        boolean result = workflowRuntimeService.deleteAll(workflow.getWorkflowUuid());

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除工作流运行时记录
     * 对应前端: workflowService.deleteWorkflowRuntime(runtimeId)
     *
     * @param runtimeId 运行时ID
     * @return 删除结果
     */
    @Operation(summary = "删除工作流运行时记录")
    @DeleteMapping("/runtime/{runtimeId}")
    @SysLogAnnotion("删除工作流运行时记录")
    public ResponseEntity<JsonResultAo<Boolean>> deleteRuntime(@PathVariable @NotNull Long runtimeId) {
        log.info("删除工作流运行时记录,runtimeId:{}", runtimeId);

        // ID转UUID
        AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getById(runtimeId);
        if (runtime == null) {
            throw new RuntimeException("运行时实例不存在: " + runtimeId);
        }

        boolean result = workflowRuntimeService.softDelete(runtime.getRuntimeUuid());

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 恢复工作流运行
     * 对应前端: workflowService.resumeWorkflowRun({runtimeId, userInput})
     *
     * @param runtimeId 运行时ID
     * @param userInput 用户输入
     * @return 恢复结果
     */
    @Operation(summary = "恢复工作流运行")
    @PostMapping("/runtime/resume/{runtimeId}")
    @SysLogAnnotion("恢复工作流运行")
    public ResponseEntity<JsonResultAo<Boolean>> resumeRun(@PathVariable @NotNull Long runtimeId,
                                                             @RequestParam String userInput) {
        log.info("恢复工作流运行,runtimeId:{},userInput:{}", runtimeId, userInput);

        // ID转UUID
        AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getById(runtimeId);
        if (runtime == null) {
            throw new RuntimeException("运行时实例不存在: " + runtimeId);
        }

        workflowStarter.resumeFlow(runtime.getRuntimeUuid(), userInput);

        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }
}
