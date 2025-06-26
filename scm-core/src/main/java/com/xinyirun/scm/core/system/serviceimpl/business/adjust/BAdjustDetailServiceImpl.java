package com.xinyirun.scm.core.system.serviceimpl.business.adjust;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustDetailVo;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.*;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.adjust.BAdjustDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.adjust.BAdjustMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BAdjustAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BAdjustDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class BAdjustDetailServiceImpl extends BaseServiceImpl<BAdjustDetailMapper, BAdjustDetailEntity> implements IBAdjustDetailService {

    @Autowired
    private BAdjustDetailMapper mapper;

    @Autowired
    private BAdjustMapper adjustMapper;

    @Autowired
    private BAdjustAutoCodeServiceImpl autoCode;

    @Autowired
    BAdjustDetailAutoCodeServiceImpl detailAutoCode;

    @Autowired
    private IMGoodsSpecService imGoodsSpecService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BAdjustVo vo) {
        // 赋值
        BAdjustEntity entity = (BAdjustEntity) BeanUtilsSupport.copyProperties(vo, BAdjustEntity.class);

        // 生成单号
        String code = autoCode.autoCode().getCode();
        int rtn = 0;
        // 单号为空则设置自动生成的单号
        if(StringUtils.isEmpty(vo.getCode())){
            entity.setCode(code);
        }

        // 赋值详情
        List<BAdjustDetailEntity> adjustDetailList = BeanUtilsSupport.copyProperties(vo.getDetailList(),BAdjustDetailEntity.class);

        // check
        check(vo.getDetailList());

        rtn = adjustMapper.insert(entity);

        for(BAdjustDetailEntity adjustDetail:adjustDetailList){
            adjustDetail.setStatus(DictConstant.DICT_B_ADJUST_STATUS_SAVED);
            adjustDetail.setCode(detailAutoCode.autoCode().getCode());
            adjustDetail.setAdjust_id(entity.getId());
            // 计算货值
            adjustDetail.setAdjusted_amount(adjustDetail.getQty_adjust().multiply(adjustDetail.getAdjusted_price()));
            MGoodsSpecVo spec = imGoodsSpecService.selectById(adjustDetail.getSku_id());
            // 设置sku_coode
            adjustDetail.setSku_code(spec.getSku_code());
            if (adjustDetail.getQty() == null) {
                adjustDetail.setQty(BigDecimal.ZERO);
            }

            // 设置库存的差值
            adjustDetail.setQty_diff(adjustDetail.getQty_adjust().subtract(adjustDetail.getQty()));
            mapper.insert(adjustDetail);
            vo.setId(adjustDetail.getId());

            // 生成待办
            todoService.insertTodo(adjustDetail.getId(), SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL, SystemConstants.PERMS.B_ADJUST_SUBMIT);
        }
        vo.setAdjust_id(entity.getId());

        // 附件新增
        if(vo.getFiles() != null && vo.getFiles().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            insertFiles(entity,fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getFiles()) {
                insertFileInfo(fileEntity,fileInfoVo);
            }
            // 磅单附件id
            entity.setFiles_id(fileEntity.getId());
            adjustMapper.updateById(entity);
        }

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 新增附件主表数据
     */
    public void insertFiles(BAdjustEntity entity, SFileEntity fileEntity) {
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_ADJUST);
        // 主表新增
        fileMapper.insert(fileEntity);
    }

    /**
     * 新增附件详情数据
     */
    public void insertFileInfo(SFileEntity fileEntity,SFileInfoVo fileInfoVo) {
        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
        fileInfoVo.setF_id(fileEntity.getId());
        BeanUtilsSupport.copyProperties(fileInfoVo,fileInfoEntity);
        fileInfoEntity.setFile_name(fileInfoVo.getFileName());
        fileInfoMapper.insert(fileInfoEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insertAudit(BAdjustVo vo) {
        // 赋值
        BAdjustEntity entity = (BAdjustEntity) BeanUtilsSupport.copyProperties(vo, BAdjustEntity.class);

        // 生成单号
        String code = autoCode.autoCode().getCode();
        int rtn = 0;
        // 单号为空则设置自动生成的单号
        if(StringUtils.isEmpty(vo.getCode())){
            entity.setCode(code);
        }

        // 赋值详情
        List<BAdjustDetailEntity> adjustDetailList = BeanUtilsSupport.copyProperties(vo.getDetailList(),BAdjustDetailEntity.class);

        // check
        check(vo.getDetailList());

        rtn = adjustMapper.insert(entity);

        for(BAdjustDetailEntity adjustDetail:adjustDetailList){
            adjustDetail.setStatus(DictConstant.DICT_B_ADJUST_STATUS_SAVED);
            adjustDetail.setCode(detailAutoCode.autoCode().getCode());
            adjustDetail.setAdjust_id(entity.getId());
            // 计算货值
            adjustDetail.setAdjusted_amount(adjustDetail.getQty_adjust().multiply(adjustDetail.getAdjusted_price()));
            MGoodsSpecVo spec = imGoodsSpecService.selectById(adjustDetail.getSku_id());
            // 设置sku_coode
            adjustDetail.setSku_code(spec.getSku_code());
            if (adjustDetail.getQty() == null) {
                adjustDetail.setQty(BigDecimal.ZERO);
            }

            // 设置库存的差值
            adjustDetail.setQty_diff(adjustDetail.getQty_adjust().subtract(adjustDetail.getQty()));
            adjustDetail.setStatus(DictConstant.DICT_B_ADJUST_STATUS_PASSED);
            if (SecurityUtil.getUpdateUser_id() != null) {
                adjustDetail.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            }
            adjustDetail.setE_dt(LocalDateTime.now());

            mapper.insert(adjustDetail);
            vo.setId(adjustDetail.getId());

            // 计算库存
            iCommonInventoryLogicService.updWmsStockByAdjustBill(adjustDetail.getId());

            // 生成已办
            todoService.insertAlreadyDo(adjustDetail.getId(), SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL, SystemConstants.PERMS.B_ADJUST_AUDIT);
        }
        vo.setAdjust_id(entity.getId());

        return null;
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(BAdjustVo vo) {
        // 全删全插逻辑  1：删除明细表数据，2：新增计划明细表，3：更新入库计划

        // 删除状态为制单和驳回的明细数据
        mapper.statusDelete(vo.getAdjust_id());

        // 页面传来的计划明细
        List<BAdjustDetailEntity> adjustDetailEntityList = BeanUtilsSupport.copyProperties(vo.getDetailList(), BAdjustDetailEntity.class);
        for(BAdjustDetailEntity entity:adjustDetailEntityList) {
            if(entity.getId() == null) {
                entity.setStatus(DictConstant.DICT_B_ADJUST_STATUS_SAVED);
                entity.setAdjust_id(vo.getAdjust_id());
            }
        }
        // 更新明细表数据
        saveOrUpdateBatch(adjustDetailEntityList, 500);

        BAdjustEntity adjustEntity = adjustMapper.selectById(vo.getAdjust_id());
//        adjustEntity.setU_id(null);
//        adjustEntity.setU_time(null);

        // 更新入库计划
        int rtn = adjustMapper.updateById(adjustEntity);
        return UpdateResultUtil.OK(rtn);
    }

    @Override
    public BAdjustDetailEntity setAdjustForUpdate(Integer id) {
        return mapper.setBillAdjustForUpdate(id);
    }

    /**
     * 按仓库类型仓库商品-调整
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<BWarehouseGoodsVo> queryAdjustInventory(BWarehouseGoodsVo searchCondition) {
        // 分页条件
        Page<BAdjustDetailEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryAdjustInventory(pageCondition, searchCondition);

    }

    /**
     * 按仓库类型仓库商品-库存
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public IPage<BWarehouseGoodsVo> queryInventoryList(BWarehouseGoodsVo searchCondition) {
        // 分页条件
        Page<BAdjustDetailEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryInventoryList(pageCondition, searchCondition);
    }

    /**
     * 计算全部数量
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public BWarehouseGoodsVo queryReportInventorySum(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryReportInventorySum(searchCondition);
    }

    /**
     * 导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BWarehouseGoodsExportVo> queryReportInventoryExport(List<BWarehouseGoodsVo> searchCondition) {
        return mapper.queryReportInventoryExport(searchCondition);
    }

    /**
     * 导出全部
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public List<BWarehouseGoodsExportVo> queryReportInventoryExportAll(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryReportInventoryExportAll(searchCondition);
    }

    /**
     * 合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BWarehouseGoodsVo queryAdjustInventorySum(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryAdjustInventorySum(searchCondition);
    }

    /**
     * 导出全部
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BWarehouseGoodsAdjustExportVo> queryAdjustInventoryExportAll(BWarehouseGoodsVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectAdjustExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryAdjustInventoryExportAll(searchCondition);
    }

    /**
     * 导出部分
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BWarehouseGoodsAdjustExportVo> queryAdjustInventoryExport(List<BWarehouseGoodsVo> searchCondition) {
        return mapper.queryAdjustInventoryExport(searchCondition);
    }

    /**
     * 库存导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BWarehouseGoodsTotalExportVo> queryReportTotalExportAll(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectTotalCount(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryReportTotalExportAll(searchCondition);
    }

    /**
     * 库存部分导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BWarehouseGoodsTotalExportVo> queryReportTotalExport(List<BWarehouseGoodsVo> searchCondition) {
        return mapper.queryReportTotalExport(searchCondition);
    }

    /**
     * 按仓库Id， 商品ID导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public List<BWarehouseInventoryExportVo> queryReportExportAll(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectTotalExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryReportExportAll(searchCondition);
    }

    /**
     * 按仓库Id， 商品ID导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BWarehouseInventoryExportVo> queryReportExport(List<BWarehouseGoodsVo> searchCondition) {
        return mapper.queryReportExport(searchCondition);
    }

    /**
     * 按仓库类型汇总商品
     *
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public List<BWarehouseGoodsVo> queryWarehouseTypeList(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryWarehouseTypeList(searchCondition);
    }

    /**
     * 按仓库类型, 仓库商品, 存货
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<BWarehouseGoodsVo> selectTotalPageList(BWarehouseGoodsVo searchCondition) {
        // 分页条件
        Page<BAdjustDetailEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectTotalPageList(pageCondition, searchCondition);
    }

    /**
     * 按仓库类型, 仓库商品, 存货 合計
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BWarehouseGoodsVo selectTotalPageListSum(BWarehouseGoodsVo searchCondition) {
        return mapper.selectTotalPageListSum(searchCondition);
    }


    private void check(List<BAdjustDetailVo> list) {
        for (BAdjustDetailVo vo : list) {
            List<BAdjustDetailVo> voList = mapper.getWaitAuditDatas(vo);
            if (voList.size() > 0) {
                throw new BusinessException("该数据存在待审核的调整单");
            }
        }
    }
}
