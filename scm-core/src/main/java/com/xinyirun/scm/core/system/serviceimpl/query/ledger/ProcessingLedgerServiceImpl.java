package com.xinyirun.scm.core.system.serviceimpl.query.ledger;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.report.ledger.*;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.query.ledger.ProcessingLedgerMapper;
import com.xinyirun.scm.core.system.service.query.ledger.ProcessingLedgerService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Wqf
 * @Description: 加工入库台账
 * @CreateTime : 2023/8/1 15:06
 */

@Service
public class ProcessingLedgerServiceImpl implements ProcessingLedgerService {

    @Autowired
    private ProcessingLedgerMapper mapper;

    @Autowired
    private ISConfigService isConfigService;

    /**
     * 饲用稻谷定向出入库进度报备表/明细表
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<ProcessingRiceWarehouseInProgressVo> queryRicePageList(ProcessingRiceWarehouseInProgressVo searchCondition) {
        // 分页条件
        Page<ProcessingRiceWarehouseInProgressVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryRicePageList(searchCondition, pageCondition);
    }

    /**
     * 饲用稻谷定向出入库进度报备表/明细表  合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public ProcessingRiceWarehouseInProgressVo queryRicePageListSum(ProcessingRiceWarehouseInProgressVo searchCondition) {
        return mapper.queryRicePageListSum(searchCondition);
    }

    /**
     * 饲用稻谷定向出入库进度报备表/明细表  导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<ProcessingRiceWarehouseInProgressExportVo> queryRicePageListExport(ProcessingRiceWarehouseInProgressVo searchCondition) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == searchCondition.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectRicePageExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryRicePageListExport(searchCondition);
    }

    /**
     * 玉米进度表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> queryMaizePageList(ProcessingMaizeAndWheatWarehouseInProgressVo param) {
        // 分页条件
        Page<ProcessingMaizeAndWheatWarehouseInProgressVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryMaizePageList(param, pageCondition);
    }

    /**
     * 玉米进度表 合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public ProcessingMaizeAndWheatWarehouseInProgressVo queryMaizePageListSum(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        return mapper.queryMaizePageListSum(searchCondition);
    }

    /**
     * 玉米进度表 导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> queryMaizePageListExport(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == searchCondition.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectMaizeExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryMaizeListExport(searchCondition);
    }

    /**
     * 小麦进度表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> queryWheatPageList(ProcessingMaizeAndWheatWarehouseInProgressVo param) {
        // 分页条件
        Page<ProcessingMaizeAndWheatWarehouseInProgressVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryWheatPageList(param, pageCondition);
    }

    /**
     * 小麦进度表 求和
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public ProcessingMaizeAndWheatWarehouseInProgressVo queryWheatPageListSum(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        return mapper.queryWheatPageListSum(searchCondition);
    }

    /**
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> queryWheatListExport(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == searchCondition.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectWheatExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryWheatListExport(searchCondition);
    }

    /**
     * 稻壳出库明细表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<ProcessingRiceHullWarehouseOutDetailVo> queryRiceHullPageList(ProcessingRiceHullWarehouseOutDetailVo param) {
        // 分页条件
        Page<ProcessingMaizeAndWheatWarehouseInProgressVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryRiceHullPageList(param, pageCondition);
    }

    /**
     * 稻壳出库明细表 导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<ProcessingRiceHullWarehouseOutDetailExportVo> exportRicehullOutList(ProcessingRiceHullWarehouseOutDetailVo param) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == param.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectRicehullExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryRicehullListExport(param);
    }

    /**
     * 糙米出库进度表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<ProcessingGrainWarehouseInOutDetailVo> queryGrainOutPageList(ProcessingGrainWarehouseInOutDetailVo param) {
        // 分页条件
        Page<ProcessingGrainWarehouseInOutDetailVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryGrainOutPageList(param, pageCondition);
    }

    /**
     * 糙米出库进度表 合计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public ProcessingGrainWarehouseInOutDetailVo queryGrainOutPageListSum(ProcessingGrainWarehouseInOutDetailVo param) {
        return mapper.queryGrainOutPageListSum(param);
    }

    /**
     * 糙米出库进度表 导出
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<ProcessingGrainWarehouseOutDetailExportVo> queryGrainOutPageListExport(ProcessingGrainWarehouseInOutDetailVo param) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == param.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.queryGrainOutExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryGrainOutExport(param);
    }

    /**
     * 糙米入库进度表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<ProcessingGrainWarehouseInOutDetailVo> queryGrainInPageList(ProcessingGrainWarehouseInOutDetailVo param) {
        // 分页条件
        Page<ProcessingGrainWarehouseInOutDetailVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryGrainInPageList(param, pageCondition);
    }

    /**
     * 糙米入库进度表, 合计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public ProcessingGrainWarehouseInOutDetailVo queryGrainInListSum(ProcessingGrainWarehouseInOutDetailVo param) {
        return mapper.queryGrainInListSum(param);
    }

    /**
     * 糙米入库进度表, 导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<ProcessingGrainWarehouseInDetailExportVo> exportGrainInList(ProcessingGrainWarehouseInOutDetailVo param) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == param.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.exportGrainInListNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportGrainInList(param);
    }

    /**
     * 混合物出库进度表
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<ProcessingComboWarehouseOutProgressVo> queryComboOutPageList(ProcessingComboWarehouseOutProgressVo param) {
        // 分页条件
        Page<ProcessingComboWarehouseOutProgressVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());

        // 替换分页插件自动count sql 因为该sql执行速度非常慢
        pageCondition.setCountId("exportComboListNum");
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.queryComboOutPageList(param, pageCondition);
    }

    /**
     * 混合物 求和
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public ProcessingComboWarehouseOutProgressVo queryComboOutListSum(ProcessingComboWarehouseOutProgressVo param) {
        return mapper.queryComboOutListSum(param);
    }

    /**
     * 混合物 导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<ProcessingComboWarehouseOutProgressExportVo> exportComboList(ProcessingComboWarehouseOutProgressVo param) {
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == param.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.exportComboListNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportComboList(param);
    }
}
