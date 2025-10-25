package com.xinyirun.scm.ai.controller.mcp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.vo.mcp.AiMcpVo;
import com.xinyirun.scm.ai.bean.vo.mcp.AiUserMcpVo;
import com.xinyirun.scm.ai.core.service.mcp.AiMcpService;
import com.xinyirun.scm.ai.core.service.mcp.AiUserMcpService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP管理Controller
 *
 * 提供MCP模板和用户MCP配置的管理接口
 *
 * @author SCM-AI Team
 */
@Slf4j
@Tag(name = "MCP管理")
@RestController
@RequestMapping("/api/v1/ai/mcp")
@Validated
public class McpController {

    @Resource
    private AiMcpService mcpService;

    @Resource
    private AiUserMcpService userMcpService;

    // ==================== MCP模板管理 ====================

    /**
     * 添加MCP模板
     *
     * @param name MCP名称
     * @param icon 图标
     * @param remark 描述
     * @param transportType 传输类型(sse/stdio)
     * @param sseUrl SSE连接URL
     * @param stdioCommand STDIO命令
     * @param presetParams 预设参数
     * @param customizedParamDefinitions 可自定义参数定义
     * @param installType 安装类型(docker/local/remote/wasm)
     * @return MCP VO
     */
    @Operation(summary = "添加MCP模板")
    @PostMapping("/template/add")
    @SysLogAnnotion("添加MCP模板")
    public ResponseEntity<JsonResultAo<AiMcpVo>> addTemplate(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String icon,
            @RequestParam(required = false) String remark,
            @RequestParam @NotBlank String transportType,
            @RequestParam(required = false) String sseUrl,
            @RequestParam(required = false) String stdioCommand,
            @RequestBody(required = false) Map<String, Object> presetParams,
            @RequestBody(required = false) Map<String, Object> customizedParamDefinitions,
            @RequestParam(defaultValue = "local") String installType) {
        AiMcpVo vo = mcpService.add(name, icon, remark, transportType, sseUrl, stdioCommand,
                presetParams, customizedParamDefinitions, installType);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 更新MCP模板
     *
     * @param mcpUuid MCP UUID
     * @param name MCP名称
     * @param icon 图标
     * @param remark 描述
     * @param transportType 传输类型
     * @param sseUrl SSE连接URL
     * @param stdioCommand STDIO命令
     * @param presetParams 预设参数
     * @param customizedParamDefinitions 可自定义参数定义
     * @param installType 安装类型
     * @return MCP VO
     */
    @Operation(summary = "更新MCP模板")
    @PostMapping("/template/update")
    @SysLogAnnotion("更新MCP模板")
    public ResponseEntity<JsonResultAo<AiMcpVo>> updateTemplate(
            @RequestParam @NotBlank String mcpUuid,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String icon,
            @RequestParam(required = false) String remark,
            @RequestParam @NotBlank String transportType,
            @RequestParam(required = false) String sseUrl,
            @RequestParam(required = false) String stdioCommand,
            @RequestBody(required = false) Map<String, Object> presetParams,
            @RequestBody(required = false) Map<String, Object> customizedParamDefinitions,
            @RequestParam(defaultValue = "local") String installType) {
        AiMcpVo vo = mcpService.update(mcpUuid, name, icon, remark, transportType, sseUrl,
                stdioCommand, presetParams, customizedParamDefinitions, installType);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 删除MCP模板
     *
     * @param mcpUuid MCP UUID
     * @return 是否成功
     */
    @Operation(summary = "删除MCP模板")
    @PostMapping("/template/delete/{mcpUuid}")
    @SysLogAnnotion("删除MCP模板")
    public ResponseEntity<JsonResultAo<Boolean>> deleteTemplate(@PathVariable @NotBlank String mcpUuid) {
        boolean success = mcpService.delete(mcpUuid);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }

    /**
     * 启用/禁用MCP模板
     *
     * @param mcpUuid MCP UUID
     * @param isEnable 是否启用(0-禁用,1-启用)
     * @return 是否成功
     */
    @Operation(summary = "启用/禁用MCP模板")
    @PostMapping("/template/set-enable/{mcpUuid}")
    @SysLogAnnotion("启用/禁用MCP模板")
    public ResponseEntity<JsonResultAo<Boolean>> setTemplateEnable(
            @PathVariable @NotBlank String mcpUuid,
            @RequestParam @NotNull Integer isEnable) {
        boolean success = mcpService.setEnable(mcpUuid, isEnable);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }

    /**
     * 获取MCP模板详情
     *
     * @param mcpUuid MCP UUID
     * @return MCP VO
     */
    @Operation(summary = "获取MCP模板详情")
    @GetMapping("/template/detail/{mcpUuid}")
    @SysLogAnnotion("获取MCP模板详情")
    public ResponseEntity<JsonResultAo<AiMcpVo>> getTemplateDetail(@PathVariable @NotBlank String mcpUuid) {
        AiMcpVo vo = mcpService.getDetail(mcpUuid);
        if (vo == null) {
            throw new RuntimeException("MCP模板不存在");
        }
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 获取所有启用的MCP模板列表
     *
     * @return MCP列表
     */
    @Operation(summary = "获取所有启用的MCP模板列表")
    @GetMapping("/template/list-enable")
    @SysLogAnnotion("获取启用的MCP模板列表")
    public ResponseEntity<JsonResultAo<List<AiMcpVo>>> listEnableTemplates() {
        List<AiMcpVo> list = mcpService.listAllEnable();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 分页查询MCP模板
     *
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Operation(summary = "分页查询MCP模板")
    @GetMapping("/template/list")
    @SysLogAnnotion("分页查询MCP模板")
    public ResponseEntity<JsonResultAo<Page<AiMcpVo>>> listTemplates(
            @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Page<AiMcpVo> page = mcpService.listByPage(currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    // ==================== 用户MCP配置管理 ====================

    /**
     * 添加用户MCP配置
     *
     * @param userId 用户ID
     * @param mcpId MCP模板ID
     * @param customizedParams 用户自定义参数
     * @return 用户MCP VO
     */
    @Operation(summary = "添加用户MCP配置")
    @PostMapping("/user/add")
    @SysLogAnnotion("添加用户MCP配置")
    public ResponseEntity<JsonResultAo<AiUserMcpVo>> addUserMcp(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull Long mcpId,
            @RequestBody(required = false) Map<String, Object> customizedParams) {
        AiUserMcpVo vo = userMcpService.add(userId, mcpId, customizedParams);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 更新用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @param customizedParams 用户自定义参数
     * @return 用户MCP VO
     */
    @Operation(summary = "更新用户MCP配置")
    @PostMapping("/user/update")
    @SysLogAnnotion("更新用户MCP配置")
    public ResponseEntity<JsonResultAo<AiUserMcpVo>> updateUserMcp(
            @RequestParam @NotBlank String userMcpUuid,
            @RequestParam @NotNull Long userId,
            @RequestBody(required = false) Map<String, Object> customizedParams) {
        AiUserMcpVo vo = userMcpService.update(userMcpUuid, userId, customizedParams);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 删除用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "删除用户MCP配置")
    @PostMapping("/user/delete/{userMcpUuid}")
    @SysLogAnnotion("删除用户MCP配置")
    public ResponseEntity<JsonResultAo<Boolean>> deleteUserMcp(
            @PathVariable @NotBlank String userMcpUuid,
            @RequestParam @NotNull Long userId) {
        boolean success = userMcpService.delete(userMcpUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }

    /**
     * 启用/禁用用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @param isEnable 是否启用(0-禁用,1-启用)
     * @return 是否成功
     */
    @Operation(summary = "启用/禁用用户MCP配置")
    @PostMapping("/user/set-enable/{userMcpUuid}")
    @SysLogAnnotion("启用/禁用用户MCP配置")
    public ResponseEntity<JsonResultAo<Boolean>> setUserMcpEnable(
            @PathVariable @NotBlank String userMcpUuid,
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull Integer isEnable) {
        boolean success = userMcpService.setEnable(userMcpUuid, userId, isEnable);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }

    /**
     * 获取用户的所有MCP配置列表
     *
     * @param userId 用户ID
     * @return 用户MCP列表
     */
    @Operation(summary = "获取用户的所有MCP配置列表")
    @GetMapping("/user/list")
    @SysLogAnnotion("获取用户MCP配置列表")
    public ResponseEntity<JsonResultAo<List<AiUserMcpVo>>> listUserMcps(@RequestParam @NotNull Long userId) {
        List<AiUserMcpVo> list = userMcpService.listByUser(userId);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 获取用户启用的MCP配置列表
     *
     * @param userId 用户ID
     * @return 用户MCP列表
     */
    @Operation(summary = "获取用户启用的MCP配置列表")
    @GetMapping("/user/list-enabled")
    @SysLogAnnotion("获取用户启用的MCP配置列表")
    public ResponseEntity<JsonResultAo<List<AiUserMcpVo>>> listEnabledUserMcps(@RequestParam @NotNull Long userId) {
        List<AiUserMcpVo> list = userMcpService.listEnabledByUser(userId);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 获取用户MCP配置详情
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @return 用户MCP VO
     */
    @Operation(summary = "获取用户MCP配置详情")
    @GetMapping("/user/detail/{userMcpUuid}")
    @SysLogAnnotion("获取用户MCP配置详情")
    public ResponseEntity<JsonResultAo<AiUserMcpVo>> getUserMcpDetail(
            @PathVariable @NotBlank String userMcpUuid,
            @RequestParam @NotNull Long userId) {
        AiUserMcpVo vo = userMcpService.getDetail(userMcpUuid, userId);
        if (vo == null) {
            throw new RuntimeException("用户MCP配置不存在");
        }
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }
}
