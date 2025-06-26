package com.xinyirun.scm.core.system.serviceimpl.business.monitor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorFileExportSettingsEntity;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000068Vo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000158Vo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorFileExportSettingsMapper;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorFileExportSettingsService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 监管任务文件导出 配置 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-08-18
 */
@Service
@Slf4j
public class BMonitorFileExportSettingsServiceImpl extends ServiceImpl<BMonitorFileExportSettingsMapper, BMonitorFileExportSettingsEntity> implements IBMonitorFileExportSettingsService {

    @Autowired
    private ISPagesService pagesService;

    /**
     * 根据 staff ID 查询
     *
     * @return
     */
    @Override
    public P00000068Vo getByStaffId() {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.PAGE_MONITOR);
        SPagesVo pagesVo = pagesService.get(sPagesVo);
        P00000068Vo p68 = pagesVo.getP00000068Vo();
        if (null == p68) {
            throw new BusinessException("监管任务附件导出字段未配置");
        }
        BMonitorFileExportSettingsEntity entity = baseMapper.selectOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, SecurityUtil.getStaff_id())
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ZERO));
        if (null == entity) {
            return p68;
        }
        JSONObject config = JSON.parseObject(entity.getConfig_json());

        // entity 不为 null, 需要将 配置的值 给 对应的 列
        p68.getFile_1().put("value", config.getJSONObject("file_1").getBoolean("value"));
        p68.getFile_2().put("value", config.getJSONObject("file_2").getBoolean("value"));
        p68.getFile_40().put("value", config.getJSONObject("file_40").getBoolean("value"));
        p68.getFile_3().put("value", config.getJSONObject("file_3").getBoolean("value"));
        p68.getFile_4().put("value", config.getJSONObject("file_4").getBoolean("value"));
        p68.getFile_38().put("value", config.getJSONObject("file_38").getBoolean("value"));
        p68.getFile_39().put("value", config.getJSONObject("file_39").getBoolean("value"));
        p68.getFile_8().put("value", config.getJSONObject("file_8").getBoolean("value"));
        p68.getFile_17().put("value", config.getJSONObject("file_17").getBoolean("value"));
        p68.getFile_18().put("value", config.getJSONObject("file_18").getBoolean("value"));
        p68.getFile_19().put("value", config.getJSONObject("file_19").getBoolean("value"));
        p68.getFile_20().put("value", config.getJSONObject("file_20").getBoolean("value"));
        p68.getFile_21().put("value", config.getJSONObject("file_21").getBoolean("value"));
        p68.getFile_22().put("value", config.getJSONObject("file_22").getBoolean("value"));
        p68.getFile_26().put("value", config.getJSONObject("file_26").getBoolean("value"));
        p68.getFile_35().put("value", config.getJSONObject("file_35").getBoolean("value"));
        p68.getFile_36().put("value", config.getJSONObject("file_36").getBoolean("value"));
        p68.getFile_37().put("value", config.getJSONObject("file_37").getBoolean("value"));
        return p68;
    }

    /**
     * 保存 / 更新
     *
     * @param jsonObject
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAndFlush(JSONObject jsonObject) {
        Long staffId = SecurityUtil.getStaff_id();
        BMonitorFileExportSettingsEntity entity = null;
        entity = baseMapper.selectOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, staffId)
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ZERO));
        if (null != entity && entity.getId() != null) {
            entity.setConfig_json(jsonObject.toJSONString());
        } else {
            entity = new BMonitorFileExportSettingsEntity();
            entity.setStaff_id(staffId);
            entity.setType(DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ZERO);
            entity.setConfig_json(jsonObject.toJSONString());
        }
        this.saveOrUpdate(entity);

    }

    /**
     * 根据 staff ID 查询
     *
     * @return
     */
    @Override
    public P00000158Vo getByDirectStaffId() {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.P_MONITOR_DIRECT);
        SPagesVo pagesVo = pagesService.get(sPagesVo);
        P00000158Vo p158 = pagesVo.getP00000158Vo();
        if (null == p158) {
            throw new BusinessException("监管任务附件导出字段未配置");
        }
        BMonitorFileExportSettingsEntity entity = baseMapper.selectOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, SecurityUtil.getStaff_id())
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ONE));
        if (null == entity) {
            return p158;
        }
        JSONObject config = JSON.parseObject(entity.getConfig_json());

        // entity 不为 null, 需要将 配置的值 给 对应的 列
        p158.getFile_35().put("value", config.getJSONObject("file_35").getBoolean("value"));
        p158.getFile_41().put("value", config.getJSONObject("file_41").getBoolean("value"));
        p158.getFile_42().put("value", config.getJSONObject("file_42").getBoolean("value"));
        p158.getFile_37().put("value", config.getJSONObject("file_37").getBoolean("value"));

        p158.getFile_17().put("value", config.getJSONObject("file_17").getBoolean("value"));
        p158.getFile_38().put("value", config.getJSONObject("file_38").getBoolean("value"));
        p158.getFile_43().put("value", config.getJSONObject("file_43").getBoolean("value"));
        p158.getFile_19().put("value", config.getJSONObject("file_19").getBoolean("value"));
        return p158;
    }

    /**
     * 保存 / 更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDirect(JSONObject jsonObject) {
        Long staffId = SecurityUtil.getStaff_id();
        BMonitorFileExportSettingsEntity entity = null;
        entity = baseMapper.selectOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, staffId)
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ONE));
        if (null != entity && entity.getId() != null) {
            entity.setConfig_json(jsonObject.toJSONString());
        } else {
            entity = new BMonitorFileExportSettingsEntity();

            entity.setType(DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ONE);
            entity.setStaff_id(staffId);
            entity.setConfig_json(jsonObject.toJSONString());
        }
        this.saveOrUpdate(entity);
    }
}
