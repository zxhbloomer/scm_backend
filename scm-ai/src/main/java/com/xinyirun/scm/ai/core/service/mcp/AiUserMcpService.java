package com.xinyirun.scm.ai.core.service.mcp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.mcp.AiMcpEntity;
import com.xinyirun.scm.ai.bean.entity.mcp.AiUserMcpEntity;
import com.xinyirun.scm.ai.bean.vo.mcp.AiUserMcpVo;
import com.xinyirun.scm.ai.core.mapper.mcp.AiMcpMapper;
import com.xinyirun.scm.ai.core.mapper.mcp.AiUserMcpMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户MCP配置服务
 *
 * <p>基于AIDeepin UserMcpService实现</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiUserMcpService extends ServiceImpl<AiUserMcpMapper, AiUserMcpEntity> {

    @Resource
    private AiMcpMapper mcpMapper;

    /**
     * 添加用户MCP配置
     *
     * @param userId 用户ID
     * @param mcpId MCP模板ID
     * @param customizedParams 用户自定义参数
     * @return 用户MCP VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiUserMcpVo add(Long userId, Long mcpId, Map<String, Object> customizedParams) {
        // 检查MCP模板是否存在
        AiMcpEntity mcp = mcpMapper.selectOne(
                mcpMapper.selectList(null).stream()
                        .filter(m -> m.getId().equals(mcpId) && m.getIsDeleted() == 0)
                        .findFirst()
                        .map(m -> m)
                        .orElse(null)
        );

        if (mcp == null) {
            throw new RuntimeException("MCP模板不存在");
        }

        // 检查用户是否已配置此MCP
        AiUserMcpEntity existing = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getMcpId, mcpId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (existing != null) {
            throw new RuntimeException("已配置此MCP,请勿重复添加");
        }

        AiUserMcpEntity userMcp = new AiUserMcpEntity();
        userMcp.setUserMcpUuid(UuidUtil.createShort());
        userMcp.setUserId(userId);
        userMcp.setMcpId(mcpId);
        userMcp.setMcpCustomizedParams(customizedParams);
        userMcp.setIsEnable(1);
        userMcp.setIsDeleted(0);

        baseMapper.insert(userMcp);

        AiUserMcpEntity savedEntity = baseMapper.selectById(userMcp.getId());
        AiUserMcpVo vo = new AiUserMcpVo();
        BeanUtils.copyProperties(savedEntity, vo);

        // 填充MCP模板信息
        if (mcp != null) {
            vo.setMcpName(mcp.getName());
            vo.setMcpIcon(mcp.getIcon());
            vo.setMcpRemark(mcp.getRemark());
            vo.setTransportType(mcp.getTransportType());
            vo.setSseUrl(mcp.getSseUrl());
            vo.setStdioCommand(mcp.getStdioCommand());
            vo.setPresetParams(mcp.getPresetParams());
            vo.setCustomizedParamDefinitions(mcp.getCustomizedParamDefinitions());
            vo.setInstallType(mcp.getInstallType());
        }

        return vo;
    }

    /**
     * 更新用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @param customizedParams 用户自定义参数
     * @return 用户MCP VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiUserMcpVo update(String userMcpUuid, Long userId, Map<String, Object> customizedParams) {
        AiUserMcpEntity userMcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserMcpUuid, userMcpUuid)
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (userMcp == null) {
            throw new RuntimeException("用户MCP配置不存在");
        }

        userMcp.setMcpCustomizedParams(customizedParams);
        baseMapper.updateById(userMcp);

        AiMcpEntity mcp = mcpMapper.selectById(userMcp.getMcpId());
        AiUserMcpEntity savedEntity = baseMapper.selectById(userMcp.getId());
        AiUserMcpVo vo = new AiUserMcpVo();
        BeanUtils.copyProperties(savedEntity, vo);

        // 填充MCP模板信息
        if (mcp != null) {
            vo.setMcpName(mcp.getName());
            vo.setMcpIcon(mcp.getIcon());
            vo.setMcpRemark(mcp.getRemark());
            vo.setTransportType(mcp.getTransportType());
            vo.setSseUrl(mcp.getSseUrl());
            vo.setStdioCommand(mcp.getStdioCommand());
            vo.setPresetParams(mcp.getPresetParams());
            vo.setCustomizedParamDefinitions(mcp.getCustomizedParamDefinitions());
            vo.setInstallType(mcp.getInstallType());
        }

        return vo;
    }

    /**
     * 删除用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String userMcpUuid, Long userId) {
        AiUserMcpEntity userMcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserMcpUuid, userMcpUuid)
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (userMcp == null) {
            throw new RuntimeException("用户MCP配置不存在");
        }

        lambdaUpdate()
                .eq(AiUserMcpEntity::getId, userMcp.getId())
                .set(AiUserMcpEntity::getIsDeleted, 1)
                .update();

        return true;
    }

    /**
     * 启用/禁用用户MCP配置
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @param isEnable 是否启用(0-禁用,1-启用)
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setEnable(String userMcpUuid, Long userId, Integer isEnable) {
        AiUserMcpEntity userMcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserMcpUuid, userMcpUuid)
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (userMcp == null) {
            throw new RuntimeException("用户MCP配置不存在");
        }

        lambdaUpdate()
                .eq(AiUserMcpEntity::getId, userMcp.getId())
                .set(AiUserMcpEntity::getIsEnable, isEnable)
                .update();

        return true;
    }

    /**
     * 获取用户的所有MCP配置列表
     *
     * @param userId 用户ID
     * @return 用户MCP列表
     */
    public List<AiUserMcpVo> listByUser(Long userId) {
        List<AiUserMcpEntity> userMcpList = baseMapper.selectList(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .orderByDesc(AiUserMcpEntity::getId)
                        .getWrapper()
        );

        List<AiUserMcpVo> voList = new ArrayList<>();
        for (AiUserMcpEntity userMcp : userMcpList) {
            AiMcpEntity mcp = mcpMapper.selectById(userMcp.getMcpId());
            if (mcp != null && mcp.getIsDeleted() == 0) {
                AiUserMcpVo vo = new AiUserMcpVo();
                BeanUtils.copyProperties(userMcp, vo);

                // 填充MCP模板信息
                vo.setMcpName(mcp.getName());
                vo.setMcpIcon(mcp.getIcon());
                vo.setMcpRemark(mcp.getRemark());
                vo.setTransportType(mcp.getTransportType());
                vo.setSseUrl(mcp.getSseUrl());
                vo.setStdioCommand(mcp.getStdioCommand());
                vo.setPresetParams(mcp.getPresetParams());
                vo.setCustomizedParamDefinitions(mcp.getCustomizedParamDefinitions());
                vo.setInstallType(mcp.getInstallType());

                voList.add(vo);
            }
        }
        return voList;
    }

    /**
     * 获取用户启用的MCP配置列表
     *
     * @param userId 用户ID
     * @return 用户MCP列表
     */
    public List<AiUserMcpVo> listEnabledByUser(Long userId) {
        List<AiUserMcpEntity> userMcpList = baseMapper.selectList(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsEnable, 1)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .orderByDesc(AiUserMcpEntity::getId)
                        .getWrapper()
        );

        List<AiUserMcpVo> voList = new ArrayList<>();
        for (AiUserMcpEntity userMcp : userMcpList) {
            AiMcpEntity mcp = mcpMapper.selectById(userMcp.getMcpId());
            if (mcp != null && mcp.getIsDeleted() == 0 && mcp.getIsEnable() == 1) {
                AiUserMcpVo vo = new AiUserMcpVo();
                BeanUtils.copyProperties(userMcp, vo);

                // 填充MCP模板信息
                vo.setMcpName(mcp.getName());
                vo.setMcpIcon(mcp.getIcon());
                vo.setMcpRemark(mcp.getRemark());
                vo.setTransportType(mcp.getTransportType());
                vo.setSseUrl(mcp.getSseUrl());
                vo.setStdioCommand(mcp.getStdioCommand());
                vo.setPresetParams(mcp.getPresetParams());
                vo.setCustomizedParamDefinitions(mcp.getCustomizedParamDefinitions());
                vo.setInstallType(mcp.getInstallType());

                voList.add(vo);
            }
        }
        return voList;
    }

    /**
     * 获取用户MCP配置详情
     *
     * @param userMcpUuid 用户MCP UUID
     * @param userId 用户ID
     * @return 用户MCP VO
     */
    public AiUserMcpVo getDetail(String userMcpUuid, Long userId) {
        AiUserMcpEntity userMcp = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiUserMcpEntity::getUserMcpUuid, userMcpUuid)
                        .eq(AiUserMcpEntity::getUserId, userId)
                        .eq(AiUserMcpEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (userMcp == null) {
            return null;
        }

        AiMcpEntity mcp = mcpMapper.selectById(userMcp.getMcpId());
        if (mcp == null) {
            return null;
        }

        AiUserMcpVo vo = new AiUserMcpVo();
        BeanUtils.copyProperties(userMcp, vo);

        // 填充MCP模板信息
        vo.setMcpName(mcp.getName());
        vo.setMcpIcon(mcp.getIcon());
        vo.setMcpRemark(mcp.getRemark());
        vo.setTransportType(mcp.getTransportType());
        vo.setSseUrl(mcp.getSseUrl());
        vo.setStdioCommand(mcp.getStdioCommand());
        vo.setPresetParams(mcp.getPresetParams());
        vo.setCustomizedParamDefinitions(mcp.getCustomizedParamDefinitions());
        vo.setInstallType(mcp.getInstallType());

        return vo;
    }
}
