package com.xinyirun.scm.ai.core.service.mcp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.mcp.AiMcpEntity;
import com.xinyirun.scm.ai.bean.vo.mcp.AiMcpVo;
import com.xinyirun.scm.ai.core.mapper.mcp.AiMcpMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP服务器模板服务
 *
 * <p>基于AIDeepin McpService实现</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiMcpService extends ServiceImpl<AiMcpMapper, AiMcpEntity> {

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
    @Transactional(rollbackFor = Exception.class)
    public AiMcpVo add(String name, String icon, String remark, String transportType,
                       String sseUrl, String stdioCommand, Map<String, Object> presetParams,
                       Map<String, Object> customizedParamDefinitions, String installType) {
        AiMcpEntity mcp = new AiMcpEntity();
        mcp.setMcpUuid(UuidUtil.createShort());
        mcp.setName(name);
        mcp.setIcon(icon);
        mcp.setRemark(remark);
        mcp.setTransportType(transportType);
        mcp.setSseUrl(sseUrl);
        mcp.setStdioCommand(stdioCommand);
        mcp.setPresetParams(presetParams);
        mcp.setCustomizedParamDefinitions(customizedParamDefinitions);
        mcp.setInstallType(installType);
        mcp.setIsEnable(true);
        mcp.setIsDeleted(false);

        baseMapper.insert(mcp);
        AiMcpEntity savedEntity = baseMapper.selectById(mcp.getId());
        AiMcpVo vo = new AiMcpVo();
        BeanUtils.copyProperties(savedEntity, vo);
        return vo;
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
    @Transactional(rollbackFor = Exception.class)
    public AiMcpVo update(String mcpUuid, String name, String icon, String remark,
                          String transportType, String sseUrl, String stdioCommand,
                          Map<String, Object> presetParams,
                          Map<String, Object> customizedParamDefinitions,
                          String installType) {
        AiMcpEntity mcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiMcpEntity::getMcpUuid, mcpUuid)
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (mcp == null) {
            throw new RuntimeException("MCP模板不存在");
        }

        mcp.setName(name);
        mcp.setIcon(icon);
        mcp.setRemark(remark);
        mcp.setTransportType(transportType);
        mcp.setSseUrl(sseUrl);
        mcp.setStdioCommand(stdioCommand);
        mcp.setPresetParams(presetParams);
        mcp.setCustomizedParamDefinitions(customizedParamDefinitions);
        mcp.setInstallType(installType);

        baseMapper.updateById(mcp);
        AiMcpEntity savedEntity = baseMapper.selectById(mcp.getId());
        AiMcpVo vo = new AiMcpVo();
        BeanUtils.copyProperties(savedEntity, vo);
        return vo;
    }

    /**
     * 删除MCP模板
     *
     * @param mcpUuid MCP UUID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String mcpUuid) {
        AiMcpEntity mcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiMcpEntity::getMcpUuid, mcpUuid)
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (mcp == null) {
            throw new RuntimeException("MCP模板不存在");
        }

        lambdaUpdate()
                .eq(AiMcpEntity::getId, mcp.getId())
                .set(AiMcpEntity::getIsDeleted, 1)
                .update();

        return true;
    }

    /**
     * 启用/禁用MCP模板
     *
     * @param mcpUuid MCP UUID
     * @param isEnable 是否启用(0-禁用,1-启用)
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setEnable(String mcpUuid, Integer isEnable) {
        AiMcpEntity mcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiMcpEntity::getMcpUuid, mcpUuid)
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (mcp == null) {
            throw new RuntimeException("MCP模板不存在");
        }

        lambdaUpdate()
                .eq(AiMcpEntity::getId, mcp.getId())
                .set(AiMcpEntity::getIsEnable, isEnable)
                .update();

        return true;
    }

    /**
     * 获取MCP模板详情
     *
     * @param mcpUuid MCP UUID
     * @return MCP VO
     */
    public AiMcpVo getDetail(String mcpUuid) {
        AiMcpEntity mcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiMcpEntity::getMcpUuid, mcpUuid)
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (mcp == null) {
            return null;
        }

        AiMcpVo vo = new AiMcpVo();
        BeanUtils.copyProperties(mcp, vo);
        return vo;
    }

    /**
     * 获取所有启用的MCP模板列表
     *
     * @return MCP列表
     */
    public List<AiMcpVo> listAllEnable() {
        List<AiMcpEntity> mcpList = baseMapper.selectList(
                lambdaQuery()
                        .eq(AiMcpEntity::getIsEnable, 1)
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .orderByDesc(AiMcpEntity::getId)
                        .getWrapper()
        );

        List<AiMcpVo> voList = new ArrayList<>();
        for (AiMcpEntity mcp : mcpList) {
            AiMcpVo vo = new AiMcpVo();
            BeanUtils.copyProperties(mcp, vo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 分页查询MCP模板
     *
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiMcpVo> listByPage(Integer currentPage, Integer pageSize) {
        Page<AiMcpEntity> page = baseMapper.selectPage(
                new Page<>(currentPage, pageSize),
                lambdaQuery()
                        .eq(AiMcpEntity::getIsDeleted, 0)
                        .orderByDesc(AiMcpEntity::getId)
                        .getWrapper()
        );

        Page<AiMcpVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AiMcpVo> voList = new ArrayList<>();
        for (AiMcpEntity entity : page.getRecords()) {
            AiMcpVo vo = new AiMcpVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }
}
