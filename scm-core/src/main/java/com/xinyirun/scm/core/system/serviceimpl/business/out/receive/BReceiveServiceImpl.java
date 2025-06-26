package com.xinyirun.scm.core.system.serviceimpl.business.out.receive;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveExtraEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.cancel.BCancelVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.out.receive.BReceiveSumVo;
import com.xinyirun.scm.bean.system.vo.business.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.excel.out.BReceiveExportVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.cancel.IBCancelService;
import com.xinyirun.scm.core.system.service.business.out.receive.IBReceiveService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 收货单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-01
 */
@Service
public class BReceiveServiceImpl extends ServiceImpl<BReceiveMapper, BReceiveEntity> implements IBReceiveService {

    @Autowired
    private BReceiveMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    @Autowired
    private BReceiveExtraMapper bReceiveExtraMapper;

    @Autowired
    private BOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBCancelService ibCancelService;

    @Autowired
    private MCancelService mCancelService;

    /**
     * 查询收货单列表
     *
     * @param searchCondition
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public List<BReceiveVo> selectPageListNotCount(BReceiveVo searchCondition) {
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
     * 查询总条数
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public BReceiveVo selectListCount(BReceiveVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        BReceiveVo result = new BReceiveVo();
        Long count = mapper.selectPageMyCount(searchCondition);

        result.setTotal_count(count);
        PageCondition pageCondition = (PageCondition) BeanUtilsSupport.copyProperties(searchCondition.getPageCondition(), PageCondition.class);
        result.setPageCondition(pageCondition);
        return result;
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public Integer selectTodoCount(BReceiveVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectTodoCount(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.id")
    public BReceiveSumVo selectSumData(BReceiveVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectSumData(searchCondition);
    }

    /**
     * 获取收货单详情
     */
    @Override
    public BReceiveVo selectById(Integer id) {
        BReceiveVo vo = mapper.selectId(id);

        if (vo != null && vo.getPound_file() != null) {
            SFileEntity file = fileMapper.selectById(vo.getPound_file());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            vo.setPound_files(new ArrayList<>());
            for (SFileInfoEntity fileInfo : fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.getPound_files().add(fileInfoVo);
            }
        }

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
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(BReceiveVo vo) {
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        BReceiveEntity entity = mapper.selectById(vo.getId());
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);

        // 更新
        entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_SAVED);
        entity.setType(vo.getType());
        entity.setOwner_code(vo.getOwner_code());
        entity.setOwner_id(vo.getOwner_id());
        entity.setConsignor_code(vo.getConsignor_code());
        entity.setConsignor_id(vo.getConsignor_id());
        entity.setOutbound_time(vo.getOutbound_time());
        entity.setWarehouse_id(vo.getWarehouse_id());
        entity.setLocation_id(vo.getLocation_id());
        entity.setBin_id(vo.getBin_id());
        entity.setSku_code(vo.getSku_code());
        entity.setSku_id(vo.getSku_id());
        entity.setPlan_count(vo.getActual_count());
        entity.setPlan_weight(vo.getActual_weight());
        entity.setTgt_unit_id(vo.getUnitData() == null ? entity.getTgt_unit_id() : vo.getUnitData().getTgt_unit_id()); // 转换后的单位id
        entity.setCalc(vo.getUnitData() == null ? entity.getCalc() : vo.getUnitData().getCalc()); // 转换关系
        entity.setActual_count(vo.getActual_count());
        entity.setActual_weight(vo.getActual_weight());
        entity.setPlan_volume(vo.getPlan_volume());
        entity.setActual_volume(vo.getActual_volume());
        entity.setPrice(vo.getPrice());
        entity.setAmount(vo.getAmount());
        entity.setUnit_id(vo.getUnit_id());
        entity.setVehicle_no(vo.getVehicle_no());
        entity.setTare_weight(vo.getTare_weight());
        entity.setGross_weight(vo.getGross_weight());
        entity.setRemark(vo.getRemark());
        int updCount = mapper.updateById(entity);

        BReceiveExtraEntity deliveryExtra = bReceiveExtraMapper.selectByInId(vo.getId());

        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE);

        // 更新文件
        updateFile(fileEntity, vo, deliveryExtra);

        // 入库单id查询对应入库单其他数据
        deliveryExtra.setIs_exception(vo.getIs_exception() == null? deliveryExtra.getIs_exception() : vo.getIs_exception());
        deliveryExtra.setContract_no(vo.getContract_no() == null? deliveryExtra.getContract_no() : vo.getContract_no());
        deliveryExtra.setOrder_id(vo.getOrder_id() == null? deliveryExtra.getOrder_id() : vo.getOrder_id());
        deliveryExtra.setExceptionexplain(vo.getExceptionexplain() == null? deliveryExtra.getExceptionexplain() : vo.getExceptionexplain());
        deliveryExtra.setPrice(vo.getPrice());
        deliveryExtra.setContract_dt(vo.getContract_dt());
        deliveryExtra.setContract_num(vo.getContract_num());
        deliveryExtra.setBill_type(vo.getBill_type());
        deliveryExtra.setClient_id(vo.getClient_id());
        deliveryExtra.setClient_code(vo.getClient_code());
        deliveryExtra.setId(deliveryExtra.getId());

        bReceiveExtraMapper.updateById(deliveryExtra);

        if (updCount == 0) {
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    /**
     * check逻辑
     */
    public void checkLogic(BReceiveEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 是否制单或者驳回状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_RETURN)) {
                    throw new BusinessException(entity.getCode() + ":修改失败，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 是否制单或驳回状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_RETURN)) {
                    throw new BusinessException(entity.getCode() + ":无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经作废
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL) || Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法重复作废");
                }
                if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SUBMITTED)) {
                    throw new BusinessException("入库单：" + entity.getCode() + " 已提交，无法作废");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已提交状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode() + ":无法驳回，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.FINISH_CHECK_TYPE:
                // 是否已审核状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL)) {
                    throw new BusinessException(entity.getCode() + ":完成操作失败，该单据不是已审核状态");
                }
                break;
            default:
        }
    }

    /**
     * 更新文件 1. 如果没图片, 调过, 如果有, 先删除,后新增
     *
     * @param vo    请求参数对象
     * @param extra 额外参数对象
     */
    private void updateFile(SFileEntity fileEntity, BReceiveVo vo, BReceiveExtraEntity extra) {
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
     * 提交
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> submit(List<BReceiveVo> searchCondition) {
        int updCount = 0;
        Boolean updateFlag = false;
        List<BReceiveEntity> list = mapper.selectIds(searchCondition);
        for (BReceiveEntity entity : list) {

            // check
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_SUBMITTED);
            entity.setE_dt(null);
            entity.setE_id(null);
            entity.setE_opinion(null);
            entity.setInventory_account_id(null);
            updateFlag = super.updateById(entity);
            log.debug("更新后updateFlag: " + updateFlag);

            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            if (entity.getPlan_detail_id() != null) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCountByReceive(detail.getId());

                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

                outPlanDetailMapper.updateById(detail);
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_SUBMIT);

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_AUDIT);
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
    public UpdateResultAo<Boolean> audit(List<BReceiveVo> searchCondition) {
        Boolean updateFlag = false;

        for (BReceiveVo vo : searchCondition) {
            BReceiveEntity entity = mapper.selectById(vo.getId());
            entity.setDbversion(vo.getDbversion());
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_PASSED);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_dt(LocalDateTime.now());
            entity.setInventory_account_id(null);
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
            updateFlag = super.updateById(entity);

            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_AUDIT);
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelAudit(List<BReceiveVo> searchCondition) {
        int updCount = 0;
        List<BReceiveEntity> list = mapper.selectIds(searchCondition);
        for (int i = 0; i < list.size(); i++) {
            BReceiveEntity entity = list.get(i);
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);

            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getStaff_id().intValue());
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_CANCEL);
            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setInventory_account_id(null);
            updCount = mapper.updateById(entity);

            // 生成作废单
            BCancelVo bCancelVo = new BCancelVo();
            bCancelVo.setBusiness_id(entity.getId());
            bCancelVo.setBusiness_type(SystemConstants.SERIAL_TYPE.B_RECEIVE);
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
            if (entity.getPlan_detail_id() != null && !DictConstant.DICT_B_RECEIVE_STATUS_SAVED.equals(entity.getStatus())) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());
                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCountByReceive(detail.getId());
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
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_CANCEL);

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
    public UpdateResultAo<Boolean> reject(List<BReceiveVo> searchCondition) {
//        int updCount = 0;
        Boolean updateFlag = false;
        List<BReceiveEntity> list = mapper.selectIds(searchCondition);
        for (BReceiveEntity entity : list) {

            // check
            checkLogic(entity, CheckResultAo.REJECT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_RETURN);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_dt(LocalDateTime.now());
            entity.setInventory_account_id(null);
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_TRUE);
            updateFlag = super.updateById(entity);
            if (!updateFlag) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 查询出库计划明细，更新已处理和待处理数量
            if (entity.getPlan_detail_id() != null) {
                BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

                BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCountByReceive(detail.getId());
                detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量
                detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量
                detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量
                detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
                outPlanDetailMapper.updateById(detail);
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_REJECT);

        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelReject(List<BReceiveVo> searchCondition) {
        Boolean updateFlag = false;
        List<BReceiveEntity> list = mapper.selectIds(searchCondition);

        for(BReceiveEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);

            // 作废驳回
            entity.setStatus(entity.getPre_status());

            LambdaUpdateWrapper<BReceiveEntity> wrapper = new LambdaUpdateWrapper<BReceiveEntity>()
                    .eq(BReceiveEntity::getId, entity.getId())
                    .set(BReceiveEntity::getStatus, entity.getPre_status());

            updateFlag = super.update(null, wrapper);

            // 删除对应作废理由
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_RECEIVE);
            mCancelService.delete(mCancelVo);

            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_AUDIT);
        }

        return UpdateResultUtil.OK(true);
    }

    @Override
    public List<BReceiveVo> selectList(BReceiveVo vo) {
        return mapper.selectListById(vo);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancel(List<BReceiveVo> searchCondition) {
        int updCount = 0;
        List<BReceiveEntity> list = mapper.selectIds(searchCondition);

        for(int i = 0; i < list.size(); i++) {
            BReceiveEntity entity = list.get(i);

            // check
            checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            // 制单和驳回状态直接作废
            if(DictConstant.DICT_B_RECEIVE_STATUS_SAVED.equals(entity.getStatus()) || DictConstant.DICT_B_RECEIVE_STATUS_RETURN.equals(entity.getStatus())){
                entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_CANCEL);
            } else {
                entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED);
            }
            LambdaUpdateWrapper<BReceiveEntity> wrapper = new LambdaUpdateWrapper<BReceiveEntity>()
                    .eq(BReceiveEntity::getId, entity.getId())
                    .set(BReceiveEntity::getStatus, entity.getStatus())
                    .set(BReceiveEntity::getPre_status, entity.getPre_status());

            updCount = mapper.update(null, wrapper);

            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_RECEIVE);
            mCancelVo.setRemark(searchCondition.get(i).getRemark());
            mCancelService.insert(mCancelVo);

            // 生成待办, 制单和驳回状态不生成待办
            if (!DictConstant.DICT_B_RECEIVE_STATUS_SAVED.equals(entity.getPre_status()) && !DictConstant.DICT_B_RECEIVE_STATUS_RETURN.equals(entity.getPre_status())) {
                todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_AUDIT);
            }

        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    public List<BReceiveExportVo> selectExportList(List<BReceiveVo> searchCondition) {
        // 查询出库数据
        if (searchCondition == null) {
            searchCondition = new ArrayList<>();
        }
        BReceiveVo[] list = searchCondition.toArray(new BReceiveVo[searchCondition.size()]);
        return mapper.selectExportList(list);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BReceiveExportVo> selectExportAllList(BReceiveVo searchCondition) {
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

}
