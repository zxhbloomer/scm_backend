package com.xinyirun.scm.core.system.serviceimpl.business.out;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.cancel.BCancelVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BWarehouseGoodsOutExportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BWarehouseGoodsVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutExportVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MLocationMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.cancel.IBCancelService;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 出库单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
@Slf4j
public class BOutServiceImpl extends BaseServiceImpl<BOutMapper, BOutEntity> implements IBOutService {

    @Autowired
    private BOutMapper mapper;

    @Autowired
    private BOutExtraMapper outExtraMapper;

    @Autowired
    private BOutAutoCodeServiceImpl outCode;

    @Autowired
    private BOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BOutPlanMapper outPlanMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    @Autowired
    private IMInventoryService imInventoryService;

    @Autowired
    private IMGoodsUnitCalcService imGoodsUnitCalcService;

    @Autowired
    private MLocationMapper locationMapper;

    @Autowired
    private MBinMapper binMapper;

    @Autowired
    private IBOrderService ibOrderService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBCancelService ibCancelService;

    /**
     * 查询分页列表
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public IPage<BOutVo> selectPage(BOutVo searchCondition) {
        // 分页条件
        Page<BOutEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());

        // 替换分页插件自动count sql 因为该sql执行速度非常慢
        pageCondition.setCountId("selectPageMyCount");

        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public Integer selectTodoCount(BOutVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectTodoCount(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BOutSumVo selectSumData(BOutVo searchCondition) {

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectSumData(searchCondition);
    }

    /**
     * 获取列表，页面查询
     */
    @Override
    public List<BOutVo> selectList(BOutVo searchCondition) {
        // 查询出库数据
        List<BOutVo> result = mapper.selectOutList(searchCondition);
        return result;
    }

    @Override
    public List<BOutVo> selectListByPlanId(Integer plan_id) {
        return mapper.selectListByPlanId(plan_id);
    }

    @Override
    public List<BOutExportVo> selectExportList(List<BOutVo> searchCondition) {
        // 查询出库数据
        if (searchCondition == null) {
            searchCondition = new ArrayList<>();
        }
        BOutVo[] list = searchCondition.toArray(new BOutVo[searchCondition.size()]);
        return mapper.selectExportList(list);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BOutExportVo> selectExportAllList(BOutVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectPageMyCount(searchCondition);
            if (StringUtils.isNotNull(count) && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportAllList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(BOutVo vo) {

        // 插入逻辑保存
        BOutEntity entity = (BOutEntity) BeanUtilsSupport.copyProperties(vo, BOutEntity.class);

        MGoodsSpecEntity specEntity = goodsSpecMapper.selectById(vo.getSku_id());
        entity.setSku_code(specEntity.getCode());

        // 编号为空则自动生成编号
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(outCode.autoCode().getCode());
        }

        entity.setPlan_count(vo.getActual_count());
        entity.setPlan_weight(vo.getActual_weight());
        entity.setTgt_unit_id(vo.getUnitData().getTgt_unit_id()); // 转换后的单位id
        entity.setCalc(vo.getUnitData().getCalc()); // 转换关系
        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_SAVED);

        int rtn = mapper.insert(entity);
        // 设置其他出库的出库单id
        vo.setOut_id(entity.getId());
        BOutExtraEntity outExtraEntity = (BOutExtraEntity) BeanUtilsSupport.copyProperties(vo, BOutExtraEntity.class);
        // 附件新增逻辑
        insertFile(entity, vo, outExtraEntity);

        outExtraMapper.insert(outExtraEntity);

        entity.setInventory_account_id(null);
        // 插入逻辑保存
        vo.setId(entity.getId());

        // 生成待办
        todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_SUBMIT);

        return InsertResultUtil.OK(rtn);
    }

    /**
     * 附件新增逻辑
     */
    public void insertFile(BOutEntity entity, BOutVo vo, BOutExtraEntity extra) {
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
        // 磅单附件新增
        if (vo.getPound_files() != null && vo.getPound_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo poundFile : vo.getPound_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                poundFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(poundFile, fileInfoEntity);
                fileInfoEntity.setFile_name(poundFile.getFileName());
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 磅单附件id
            extra.setPound_file(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 出库照片新增
        if (vo.getOut_photo_files() != null && vo.getOut_photo_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo outPhotoFile : vo.getOut_photo_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                outPhotoFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(outPhotoFile, fileInfoEntity);
                fileInfoEntity.setFile_name(outPhotoFile.getFileName());
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 磅单附件id
            extra.setOut_photo_file(fileEntity.getId());
        }
    }

    /**
     * 提交
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> submit(List<BOutVo> searchCondition) {
        int updCount = 0;
        Boolean updateFlag = false;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
        for (BOutEntity entity : list) {

            // check
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_SUBMITTED);
            entity.setE_dt(null);
            entity.setE_id(null);
            entity.setE_opinion(null);
            entity.setInventory_account_id(null);
//            updCount = mapper.updateById(entity);
            updateFlag = super.updateById(entity);
            log.debug("更新后updateFlag: " + updateFlag);
            // 更新库存
//            super.updWmsStockByOutBill(entity.getId());
            iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());

            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            if (entity.getPlan_detail_id() != null) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(detail.getId());

                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

                outPlanDetailMapper.updateById(detail);
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_SUBMIT);

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_AUDIT);
        }
        return UpdateResultUtil.OK(true);
    }

    /**
     * 审核
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> audit(List<BOutVo> searchCondition) {
        log.debug("------------------出库单批量审批start-----------------");
//        int updCount = 0;
        Boolean updateFlag = false;
//        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();
//        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
//        log.debug("Bean Size: "+list.size());
        for (BOutVo vo : searchCondition) {
            BOutEntity entity = mapper.selectById(vo.getId());
            entity.setDbversion(vo.getDbversion());
            log.debug("更新前Bean: " + JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_dt(LocalDateTime.now());
            entity.setInventory_account_id(null);
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//            updCount = super.updateById(entity);
            updateFlag = super.updateById(entity);
//            super.updWmsStockByOutBill(entity.getId());
            log.debug("更新后updateFlag: " + updateFlag);
            iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            log.debug("更新后Bean: " + JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_AUDIT);

        }
        log.debug("------------------出库单批量审批end-----------------");
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelAudit(List<BOutVo> searchCondition) {
        int updCount = 0;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
        for (int i = 0; i < list.size(); i++) {
            BOutEntity entity = list.get(i);
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);


            String status = entity.getStatus();

            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getStaff_id().intValue());
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_CANCEL);
            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setInventory_account_id(null);
            updCount = mapper.updateById(entity);
            // 更新库存
            if (!Objects.equals(status, DictConstant.DICT_B_OUT_STATUS_SAVED)) {
                iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
            }

            // 生成作废单
            BCancelVo bCancelVo = new BCancelVo();
            bCancelVo.setBusiness_id(entity.getId());
            bCancelVo.setBusiness_type(SystemConstants.SERIAL_TYPE.B_OUT);
            bCancelVo.setBusiness_code(entity.getCode());
            if (entity.getE_dt() != null) {
                bCancelVo.setStatus("1");
            } else {
                bCancelVo.setStatus("0");
            }
            bCancelVo.setWarehouse_id(entity.getWarehouse_id());
            bCancelVo.setLocation_id(entity.getLocation_id());
            bCancelVo.setBin_id(entity.getBin_id());
            bCancelVo.setSku_code(entity.getSku_code());
            bCancelVo.setSku_id(entity.getSku_id());
            bCancelVo.setOwner_code(entity.getOwner_code());
            bCancelVo.setOwner_id(entity.getOwner_id());
            bCancelVo.setQty(entity.getActual_weight());
            bCancelVo.setTime(entity.getE_dt());
            bCancelVo.setCancel_time(LocalDateTime.now());
            ibCancelService.insert(bCancelVo);

            // 查询出库计划明细，更新已处理和待处理数量
            if (entity.getPlan_detail_id() != null && !DictConstant.DICT_B_OUT_STATUS_SAVED.equals(entity.getStatus())) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(detail.getId());

                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

                outPlanDetailMapper.updateById(detail);
            }

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_CANCEL);

        }

        return UpdateResultUtil.OK(true);
    }

    /**
     * 驳回
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> reject(List<BOutVo> searchCondition) {
//        int updCount = 0;
        Boolean updateFlag = false;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
        for (BOutEntity entity : list) {

            // check
            checkLogic(entity, CheckResultAo.REJECT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_RETURN);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_dt(LocalDateTime.now());
            entity.setInventory_account_id(null);
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_TRUE);
            updateFlag = super.updateById(entity);
//            super.updWmsStockByOutBill(entity.getId());
            log.debug("更新后updateFlag: " + updateFlag);
            iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 查询出库计划明细，更新已处理和待处理数量
            if (entity.getPlan_detail_id() != null) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(detail.getId());

                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

                outPlanDetailMapper.updateById(detail);
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_REJECT);

        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelReject(List<BOutVo> searchCondition) {
        Boolean updateFlag = false;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);

        for(BOutEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);

            // 作废驳回
            entity.setStatus(entity.getPre_status());

            LambdaUpdateWrapper<BOutEntity> wrapper = new LambdaUpdateWrapper<BOutEntity>()
                    .eq(BOutEntity::getId, entity.getId())
                    .set(BOutEntity::getStatus, entity.getPre_status());

            updateFlag = super.update(null, wrapper);

//            updateFlag = super.updateById(entity);

            // 删除对应作废理由
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT);
            mCancelService.delete(mCancelVo);

            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_AUDIT);
        }

        return UpdateResultUtil.OK(true);
    }

    /**
     * 作废
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancel(List<BOutVo> searchCondition) {
        int updCount = 0;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
        for (int i = 0; i < list.size(); i++) {
            BOutEntity entity = list.get(i);
            // check
            checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);

            entity.setPre_status(entity.getStatus());
            // 制单和驳回状态直接作废
            if (entity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_SAVED) || entity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_RETURN)) {
                entity.setStatus(DictConstant.DICT_B_OUT_STATUS_CANCEL);
            } else {
                entity.setStatus(DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED);
            }
            entity.setInventory_account_id(null);

            LambdaUpdateWrapper<BOutEntity> wrapper = new LambdaUpdateWrapper<BOutEntity>()
                    .eq(BOutEntity::getId, entity.getId())
                    .set(BOutEntity::getStatus, entity.getStatus())
                    .set(BOutEntity::getInventory_account_id, null)
                    .set(BOutEntity::getPre_status, entity.getPre_status());

            updCount = mapper.update(null, wrapper);

//            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT);
            mCancelVo.setRemark(searchCondition.get(i).getRemark());
            mCancelService.insert(mCancelVo);

            // 生成待办, 制单和驳回状态不生成待办
            if (!entity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_SAVED) && !entity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_RETURN)) {
                todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_AUDIT);
            }
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> finish(List<BOutVo> searchCondition) {
//        int updCount = 0;
        boolean updateFlag = false;
        List<BOutEntity> list = mapper.selectIdsOut(searchCondition);
        for (BOutEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.FINISH_CHECK_TYPE);

            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_FINISH);
//            updCount = mapper.updateById(entity);
            updateFlag = super.updateById(entity);
            log.debug("更新后updateFlag: " + updateFlag);
            // 更新库存check
            checkLogic(entity, CheckResultAo.CANCEL_INVENTORY_CHECK_TYPE);

            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_FINISH);


        }
        return UpdateResultUtil.OK(true);
    }


    /**
     * id查询出库单对象
     *
     * @param id
     * @return
     */
    @Override
    public BOutVo selectById(int id) {
        BOutVo vo = mapper.selectId(id);
        // 磅单附件
        if (vo != null && vo.getPound_file() != null) {
            // 查询磅单附件主表对象
            SFileEntity file = fileMapper.selectById(vo.getPound_file());
            // 查询磅单从表数据集合
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            vo.setPound_files(new ArrayList<>());
            for (SFileInfoEntity fileInfo : fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                // 设置返回页面磅单附件集合
                vo.getPound_files().add(fileInfoVo);
            }
        }
        // 出库单附件
        if (vo != null && vo.getOut_photo_file() != null) {
            SFileEntity file = fileMapper.selectById(vo.getOut_photo_file());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            vo.setOut_photo_files(new ArrayList<>());
            for (SFileInfoEntity fileInfo : fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.getOut_photo_files().add(fileInfoVo);
            }
        }
        // 查询可用库存
        if (vo != null) {
            BigDecimal qtyAvaibleBySWO = imInventoryService.getQtyAvaibleBySWO(vo.getSku_id(), vo.getWarehouse_id(), vo.getOwner_id());
            vo.setQty_avaible(qtyAvaibleBySWO);
        }
        return vo;
    }


    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Override
    public BOutEntity setBillOutForUpdate(Integer id) {
        return mapper.setBillOutForUpdate(id);
    }

    @Override
    public List<BOutEntity> selectIdsOut(List<BOutVo> searchCondition) {
        return mapper.selectIdsOut(searchCondition);
    }

    @Override
    public List<BOutVo> importBOut(List<BOutImportVo> list) {
        List<BOutVo> bOutVoList = new ArrayList<>();

        for (BOutImportVo vo : list) {

            // 查询计划明细数据
            BOutPlanDetailVo bOutPlanDetailVo = new BOutPlanDetailVo();
            bOutPlanDetailVo.setPlan_code(vo.getPlan_code());
            bOutPlanDetailVo.setSku_code(vo.getSku_code());
            BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectPlanByCode(bOutPlanDetailVo);

            // 出库计划数据
            BOutPlanEntity outPlanEntity = outPlanMapper.selectById(outPlanDetailEntity.getPlan_id());

            // 转换后的单位id
            MGoodsUnitCalcVo searchCondition = new MGoodsUnitCalcVo();
            searchCondition.setSku_id(outPlanDetailEntity.getSku_id());
            searchCondition.setSrc_unit_id(outPlanDetailEntity.getUnit_id());
            MGoodsUnitCalcVo mGoodsUnitCalcVo = imGoodsUnitCalcService.selectOne(searchCondition);

            BOrderVo orderVo = ibOrderService.selectOrder(outPlanDetailEntity.getOrder_type(), outPlanDetailEntity.getOrder_id());

            // 上浮
            SConfigEntity orderOverRelease = isConfigService.selectByKey(SystemConstants.ORDER_OVER_RELEASE);
            if ("1".equals(orderOverRelease.getValue()) && orderVo != null && Objects.equals(Boolean.TRUE, orderVo.getOver_inventory_policy())) {
                // 不可超发 验证出库数量是否大于待出库数量
                // 已出库数量
                BigDecimal weight = outPlanDetailMapper.selectWaitOperatedCount1(outPlanDetailEntity.getId());
                if (orderVo.getOver_inventory_upper() == null) {
                    orderVo.setOver_inventory_upper(BigDecimal.ZERO);
                }
                // 最大出库量
                BigDecimal floatUpWeight = orderVo.getContract_num().multiply(BigDecimal.ONE.add(orderVo.getOver_inventory_upper())).stripTrailingZeros();

                if ((weight.add(new BigDecimal(vo.getActual_weight()))).compareTo(floatUpWeight) > 0) {
                    throw new AppBusinessException("超过最大出库量，最大出库量为：" + floatUpWeight.doubleValue());
                }
            }
            // 是否锁库存
            if (Objects.equals(Boolean.FALSE, outPlanDetailEntity.getLock_inventory())) {
                // 可用库存>所有的sum(出库计划（库存开关为开启），待出库数量) 数量
                // 查询库存数据
                MInventoryVo mInventoryVo = new MInventoryVo();
                mInventoryVo.setOwner_id(outPlanEntity.getOwner_id());
                mInventoryVo.setWarehouse_id(outPlanDetailEntity.getWarehouse_id());
                mInventoryVo.setSku_id(outPlanDetailEntity.getSku_id());
                MInventoryVo inventoryInfo = imInventoryService.getInventoryInfo(mInventoryVo);
                BigDecimal weight = outPlanDetailMapper.selectWaitOperateCount(outPlanDetailEntity.getId());
                if (inventoryInfo != null && inventoryInfo.getQty_avaible().compareTo(weight) < 0) {
                    throw new BusinessException(outPlanDetailEntity.getCode() + ":库存不足!");
                }
            }

            BOutEntity out = (BOutEntity) BeanUtilsSupport.copyProperties(vo, BOutEntity.class);

            MGoodsSpecEntity specEntity = goodsSpecMapper.selectById(outPlanDetailEntity.getSku_id());
            out.setSku_code(specEntity.getCode());
            out.setSku_id(specEntity.getId());

            // 自动生成出库单编号
            out.setCode(outCode.autoCode().getCode());
            out.setStatus(DictConstant.DICT_B_OUT_STATUS_SAVED);
            // 出库计划id
            out.setPlan_id(outPlanDetailEntity.getPlan_id());
            // 出库计划明细id
            out.setPlan_detail_id(outPlanDetailEntity.getId());
            // 计划数量
            out.setPlan_count(outPlanDetailEntity.getCount());
            // 计划重量
            out.setPlan_weight(outPlanDetailEntity.getWeight());
            // 计划体积
            out.setPlan_volume(BigDecimal.ZERO);
            // 入库数量
            out.setActual_count(new BigDecimal(vo.getActual_weight()));
            // 入库重量
            out.setActual_weight(new BigDecimal(vo.getActual_weight()).multiply(mGoodsUnitCalcVo.getCalc()));
            // 入库体积
            out.setActual_volume(BigDecimal.ZERO);
            // 仓库id
            // 委托方id
            out.setConsignor_id(outPlanEntity.getConsignor_id());
            // 委托方code
            out.setConsignor_code(outPlanEntity.getConsignor_code());
            // 货主id
            out.setOwner_id(outPlanEntity.getOwner_id());
            // 货主code
            out.setOwner_code(outPlanEntity.getOwner_code());
            out.setWarehouse_id(outPlanDetailEntity.getWarehouse_id());
//            in.setLocation_id(inPlanDetailEntity)
            // 查询库区
            MLocationEntity locationEntity = locationMapper.selectLocationByWarehouseId(outPlanDetailEntity.getWarehouse_id());
            out.setLocation_id(locationEntity.getId());
            // 查询库位
            MBinEntity binEntity = binMapper.selecBinByWarehouseId(outPlanDetailEntity.getWarehouse_id());
            out.setBin_id(binEntity.getId());

            //             转换后的单位id
            out.setTgt_unit_id(mGoodsUnitCalcVo.getTgt_unit_id());
            // 转换关系
            out.setCalc(mGoodsUnitCalcVo.getCalc());
            // 单位
            out.setUnit_id(outPlanDetailEntity.getUnit_id());
//            out.setPrice(new BigDecimal(outPlanDetailEntity.getPrice()));
//            out.setAmount(out.getPrice().multiply(out.getActual_weight()));

            //入库实际
//            out.setOutbound_time(LocalDateTimeUtils.convertDateToLDT(vo.getOutbound_time()));
//            out.setOutbound_time(LocalDateTimeUtils.convertDateToLDT(vo.getOutbound_time()));
            out.setOutbound_time(vo.getOutbound_time());
            out.setId(null);

            // 入库类型
            out.setType(String.valueOf(outPlanEntity.getType()));

            // 下推新增出库单
            int result = mapper.insert(out);

            // 新增出库单从表数据
            BOutExtraEntity extra = (BOutExtraEntity) BeanUtilsSupport.copyProperties(vo, BOutExtraEntity.class);
            extra.setId(null);
            extra.setOut_id(out.getId());
            // 实收车数为Null的话同步中台接口会报错
            if (extra.getCar_count() == null) {
                extra.setCar_count(SystemConstants.CAR_COUNT);
            }
            outExtraMapper.insert(extra);

            BOutVo bOutVo = new BOutVo();
            bOutVo.setId(out.getId());
            bOutVoList.add(bOutVo);
        }

        return bOutVoList;
    }

    /**
     * "按仓库类型仓库商品-出库
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<BWarehouseGoodsVo> queryOutInventory(BWarehouseGoodsVo searchCondition) {
        // 分页条件
        Page<BOutEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());

        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mapper.queryOutInventory(searchCondition, pageCondition);
    }

    /**
     * 合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BWarehouseGoodsVo queryOutInventorySum(BWarehouseGoodsVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryOutInventorySum(searchCondition);
    }

    /**
     * 全部导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BWarehouseGoodsOutExportVo> queryOutInventoryExportAll(BWarehouseGoodsVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryOutInventoryExportAll(searchCondition);
    }

    /**
     * 批量导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BWarehouseGoodsOutExportVo> queryOutInventoryExport(List<BWarehouseGoodsVo> searchCondition) {
        return mapper.queryOutInventoryExport(searchCondition);
    }

    /**
     * 当日累计出库量
     *
     * @param param 入参
     * @return MQtyLossScheduleReportVo
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BQtyLossScheduleReportVo> getOutStatistics(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.getOutStatistics(param);
    }

    /**
     * 根据 计划 id 查询 主键id
     *
     * @param plan_id 计划id集合
     * @return
     */
    @Override
    public List<BOutVo> selectIdsByOutPlanIds(List<Integer> plan_id) {
        return mapper.selectIdsByOutPlanIds(plan_id);
    }

    /**
     * 查询 出库单 商品 code 和 审核时间
     *
     * @param id 出库单 id
     * @return
     */
    @Override
    public BOutVo selectEdtAndGoodsCode(Integer id) {
        return mapper.selectEdtAndGoodsCode(id);
    }

    /**
     * 当日累计物流统计区域，增加原粮出库数量，取值采购合同关联的，且审批通过时间是当天的，且仓库类型是直属库的出库单.出库数量(换算前)
     *
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tab3.id")
    public BigDecimal getOutRawGrainCount(BQtyLossScheduleReportVo param) {
        return mapper.selectOutRawGrainCount(param);
    }

    /**
     * 查询出库单列表 不查询数量
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BOutVo> selectPageListNotCount(BOutVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        String defaultSort = "";

        String sort = searchCondition.getPageCondition().getSort();
        String sortType = "DESC";
        if (StringUtils.isNotEmpty(sort)) {
            if (sort.startsWith("-")) {
                sort = sort.substring(1);
            } else {
                sortType = "ASC";
            }

            // 默认增加一个按u_time倒序
            if (!sort.contains("_time")) {
                defaultSort = ", u_time desc";
            }
        }
        return mapper.selectPageListNotCount(searchCondition, sort, sortType, defaultSort);
    }

    /**
     * 查询出库单列表 查询数量
     *
     * @param searchCondition
     * @return
     */
    @Override
    public BOutVo selectPageListCount(BOutVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        BOutVo result = new BOutVo();
        Long count = mapper.selectPageMyCount(searchCondition);

        result.setTotal_count(count);
        PageCondition pageCondition = (PageCondition) BeanUtilsSupport.copyProperties(searchCondition.getPageCondition(), PageCondition.class);
        result.setPageCondition(pageCondition);
        return result;
    }

    /**
     * 更新
     *
     * @param bean 参数
     * @return UpdateResultAo<BOutVo>
     */
    @Override
    public UpdateResultAo<BOutVo> updateOut(BOutVo bean) {
        BOutEntity entity = mapper.selectById(bean.getId());
        // 状态校验
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        // 更新
        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_SAVED);
        entity.setType(bean.getType());
        entity.setOwner_code(bean.getOwner_code());
        entity.setOwner_id(bean.getOwner_id());
        entity.setConsignor_code(bean.getConsignor_code());
        entity.setConsignor_id(bean.getConsignor_id());
        entity.setOutbound_time(bean.getOutbound_time());
        entity.setWarehouse_id(bean.getWarehouse_id());
        entity.setLocation_id(bean.getLocation_id());
        entity.setBin_id(bean.getBin_id());
        entity.setSku_code(bean.getSku_code());
        entity.setSku_id(bean.getSku_id());
        entity.setPlan_count(bean.getActual_count());
        entity.setPlan_weight(bean.getActual_weight());
        entity.setTgt_unit_id(bean.getUnitData() == null ? entity.getTgt_unit_id() : bean.getUnitData().getTgt_unit_id()); // 转换后的单位id
        entity.setCalc(bean.getUnitData() == null ? entity.getCalc() : bean.getUnitData().getCalc()); // 转换关系
        entity.setActual_count(bean.getActual_count());
        entity.setActual_weight(bean.getActual_weight());
        entity.setPlan_volume(bean.getPlan_volume());
        entity.setActual_volume(bean.getActual_volume());
        entity.setPrice(bean.getPrice());
        entity.setAmount(bean.getAmount());
        entity.setUnit_id(bean.getUnit_id());
        entity.setVehicle_no(bean.getVehicle_no());
        entity.setTare_weight(bean.getTare_weight());
        entity.setGross_weight(bean.getGross_weight());
        entity.setRemark(bean.getRemark());
        mapper.updateById(entity);

        // 更新 b_out_extra
        BOutExtraEntity extraEntity = outExtraMapper.selectByInId(entity.getId());
        // 更新文件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
        updateFile(fileEntity, bean, extraEntity);

        extraEntity.setIs_exception(bean.getIs_exception() == null? extraEntity.getIs_exception() : bean.getIs_exception());
        extraEntity.setContract_no(bean.getContract_no() == null? extraEntity.getContract_no() : bean.getContract_no());
        extraEntity.setOrder_id(bean.getOrder_id() == null? extraEntity.getOrder_id() : bean.getOrder_id());
        extraEntity.setExceptionexplain(bean.getExceptionexplain() == null? extraEntity.getExceptionexplain() : bean.getExceptionexplain());
        extraEntity.setPrice(bean.getPrice());
        extraEntity.setContract_dt(bean.getContract_dt());
        extraEntity.setContract_num(bean.getContract_num());
        extraEntity.setBill_type(bean.getBill_type());
        extraEntity.setClient_id(bean.getClient_id());
        extraEntity.setClient_code(bean.getClient_code());
        outExtraMapper.updateById(extraEntity);

        // 返回更新的数据
        BOutVo bOutVo = this.selectById(entity.getId());
        return UpdateResultUtil.OK(bOutVo);
    }

    /**
     * 出库单直接作废
     * for 监管任务维护里的作废, 生产订单的作废
     * @param searchConditionList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelDirect(List<BOutVo> searchConditionList) {
        if (CollectionUtils.isEmpty(searchConditionList)) {
            return;
        }
        int updCount = 0;
        List<BOutEntity> list = mapper.selectIdsOut(searchConditionList);
        for (int i = 0; i < list.size(); i++) {
            BOutEntity entity = list.get(i);
            String status = entity.getStatus();
            // check
            checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);

            entity.setPre_status(entity.getStatus());
            // 直接作废
            entity.setStatus(DictConstant.DICT_B_OUT_STATUS_CANCEL);
            entity.setInventory_account_id(null);
            updCount = mapper.updateById(entity);

            // 更新库存
            if (!Objects.equals(status, DictConstant.DICT_B_OUT_STATUS_SAVED)) {
                iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
            }

            // 查询出库计划明细，更新已处理和待处理数量
            if (entity.getPlan_detail_id() != null && !DictConstant.DICT_B_OUT_STATUS_SAVED.equals(status)) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(detail.getId());

                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

                outPlanDetailMapper.updateById(detail);
            }


            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT);
            mCancelVo.setRemark(searchConditionList.get(i).getRemark());
            mCancelService.insert(mCancelVo);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_CANCEL);

        }

    }

    /**
     * 更新文件 1. 如果没图片, 调过, 如果有, 先删除,后新增
     *
     * @param vo    请求参数对象
     * @param extra 额外参数对象
     */
    private void updateFile(SFileEntity fileEntity, BOutVo vo, BOutExtraEntity extra) {
        // 磅单附件更新
        if (vo.getPound_files() != null && !vo.getPound_files().isEmpty()) {
            if (vo.getPound_file() != null) {
                deleteFile(extra.getPound_file());
            } else {
                // 主表新增
                fileMapper.insert(fileEntity);
                extra.setPound_file(fileEntity.getId());
                fileEntity.setId(null);
            }
            // 详情表新增
            insertFileDetails(vo.getPound_files(), extra.getPound_file());
        }

        // 出库照片
        if (vo.getOut_photo_files() != null && !vo.getOut_photo_files().isEmpty()) {
            if (vo.getOut_photo_file() != null) {
                deleteFile(extra.getOut_photo_file());
            } else {
                // 主表新增
                fileMapper.insert(fileEntity);
                extra.setOut_photo_file(fileEntity.getId());
                fileEntity.setId(null);
            }
            // 详情表新增
            insertFileDetails(vo.getOut_photo_files(), extra.getOut_photo_file());
        }
    }

    /**
     * 根据文件ID删除文件信息
     *
     * @param fileId 文件ID
     */
    private void deleteFile(Integer fileId) {
        if (fileId != null) {
            fileInfoMapper.delete(Wrappers.<SFileInfoEntity>lambdaQuery().eq(SFileInfoEntity::getF_id, fileId));
        }
    }

    /**
     * 将文件信息插入到详情表中
     *
     * @param files  文件信息列表
     * @param fileId 文件ID
     */
    private void insertFileDetails(List<SFileInfoVo> files, Integer fileId) {
        // 详情表新增
        for (SFileInfoVo goodsFile : files) {
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            goodsFile.setF_id(fileId);
            BeanUtilsSupport.copyProperties(goodsFile, fileInfoEntity);
            fileInfoEntity.setFile_name(goodsFile.getFileName());
            fileInfoMapper.insert(fileInfoEntity);
        }
    }

    /**
     * check逻辑
     *
     * @return
     */
    public void checkLogic(BOutEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 是否制单或驳回状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_RETURN)) {
                    throw new BusinessException(entity.getCode() + ":无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经作废
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL) || Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法重复作废");
                }
                if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_EXPIRES)) {
                    throw new BusinessException("出库单：" + entity.getCode() + " 已过期，无法作废");
                }
                if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SUBMITTED)) {
                    throw new BusinessException("出库单：" + entity.getCode() + " 已提交，无法作废");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已提交状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode() + ":无法驳回，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_RETURN)) {
                    throw new BusinessException(entity.getCode() + ":当前状态无法修改");
                }
            default:
        }
    }

    // 出库计划单号校验
    public Boolean checkBOutPlan(BOutImportVo vo, ArrayList<BOutImportVo> vos) {
        // 查询计划明细数据
        BOutPlanVo bOutPlanVo = outPlanMapper.getPlanByCode(vo.getPlan_code());
        if (bOutPlanVo == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 出库计划单号校验
    public Boolean checkBOutPlanStatus(BOutImportVo vo, ArrayList<BOutImportVo> vos) {
        // 查询计划明细数据
        BOutPlanDetailVo bOutPlanDetailVo = new BOutPlanDetailVo();
        bOutPlanDetailVo.setPlan_code(vo.getPlan_code());
        bOutPlanDetailVo.setSku_code(vo.getSku_code());
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectPlanByCode(bOutPlanDetailVo);
        if (!Objects.equals(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED, outPlanDetailEntity.getStatus())) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 物料规格校验
    public Boolean checkSku(BOutImportVo vo, ArrayList<BOutImportVo> vos) throws Exception {
        MGoodsSpecVo specEntity = goodsSpecMapper.selectByCode(vo.getSku_code());
        if (specEntity == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 订单编号校验
    public Boolean checkOrder(BOutImportVo vo, ArrayList<BOutImportVo> vos) throws Exception {
        BOrderVo bOrderVo = new BOrderVo();
        bOrderVo.setOrder_no(vo.getOrder_no());
        BOrderVo orderVo = ibOrderService.selectByOrderNo(bOrderVo);
        if (orderVo == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 计划下物料规格校验
    public Boolean checkPlanSku(BOutImportVo vo, ArrayList<BOutImportVo> vos) throws Exception {
        // 查询计划明细数据
        BOutPlanDetailVo bOutPlanDetailVo = new BOutPlanDetailVo();
        bOutPlanDetailVo.setPlan_code(vo.getPlan_code());
        bOutPlanDetailVo.setSku_code(vo.getSku_code());
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectPlanByCode(bOutPlanDetailVo);
        if (outPlanDetailEntity == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 计划下订单校验
    public Boolean checkPlanOrder(BOutImportVo vo, ArrayList<BOutImportVo> vos) throws Exception {
        // 查询计划明细数据
        BOutPlanDetailVo bOutPlanDetailVo = new BOutPlanDetailVo();
        bOutPlanDetailVo.setPlan_code(vo.getPlan_code());
        bOutPlanDetailVo.setSku_code(vo.getSku_code());
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectPlanByCode(bOutPlanDetailVo);

        BOrderVo bOrderVo = new BOrderVo();
        bOrderVo.setOrder_no(vo.getOrder_no());
        BOrderVo orderVo = ibOrderService.selectByOrderNo(bOrderVo);
        if (!Objects.equals(outPlanDetailEntity.getOrder_id(), orderVo.getSerial_id()) || !Objects.equals(outPlanDetailEntity.getOrder_type(), orderVo.getSerial_type())) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    // 出库时间校验
//    public Boolean checkOutboundTime(BOutImportVo vo, ArrayList<BOutImportVo> vos) {
//        // 查询计划明细数据
//        LocalDateTime dateTime = LocalDateTimeUtils.convertDateToLDT(vo.getOutbound_time());
//        if (dateTime == null) {
//            return Boolean.FALSE;
//        } else {
//            return Boolean.TRUE;
//        }
//    }

    // 库存校验
    public Boolean checkInventory(BOutImportVo vo, ArrayList<BOutImportVo> vos) {
        // 查询计划
        BOutPlanVo bOutPlanVo = outPlanMapper.getPlanByCode(vo.getPlan_code());
        List<BOutImportVo> list = new ArrayList();
        list.addAll(vos);
        list.add(vo);

        // 查询计划明细数据
        BOutPlanDetailVo bOutPlanDetailVo = new BOutPlanDetailVo();
        bOutPlanDetailVo.setPlan_code(vo.getPlan_code());
        bOutPlanDetailVo.setSku_code(vo.getSku_code());
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectPlanByCode(bOutPlanDetailVo);

        // 查询库存数据
        MInventoryVo mInventoryVo = new MInventoryVo();
        mInventoryVo.setOwner_id(bOutPlanVo.getOwner_id());
        mInventoryVo.setWarehouse_id(outPlanDetailEntity.getWarehouse_id());
        mInventoryVo.setSku_id(outPlanDetailEntity.getSku_id());
        MInventoryVo inventoryInfo = imInventoryService.getInventoryInfo(mInventoryVo);

        for (BOutImportVo v : list) {
            v.setCombine_key(v.getPlan_code() + v.getSku_code());
            v.setActual_weight_double_value(Double.parseDouble(v.getActual_weight()));
        }

        Map<String, Double> sum = list.stream().collect(Collectors.groupingBy(BOutImportVo::getCombine_key, Collectors.summingDouble(BOutImportVo::getActual_weight_double_value)));
        BigDecimal qty_sum = BigDecimal.valueOf(sum.get(vo.getPlan_code() + vo.getSku_code()));

        if (inventoryInfo == null || inventoryInfo.getQty_avaible().compareTo(qty_sum) < 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

}
