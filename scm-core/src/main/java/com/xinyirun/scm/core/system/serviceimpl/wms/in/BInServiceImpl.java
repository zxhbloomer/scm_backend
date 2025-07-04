package com.xinyirun.scm.core.system.serviceimpl.wms.in;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BInAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInAttachMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanTotalMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanTotalVo;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BInAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 入库单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Slf4j
@Service
public class BInServiceImpl extends ServiceImpl<BInMapper, BInEntity> implements IBInService {

    @Autowired
    private BInMapper mapper;

    @Autowired
    private BInAutoCodeServiceImpl bInAutoCodeService;
    
    @Autowired
    private BInAttachMapper bInAttachMapper;
    
    @Autowired
    private SFileMapper fileMapper;
    
    @Autowired
    private SFileInfoMapper fileInfoMapper;
    
    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;
    
    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private BInPlanTotalMapper bInPlanTotalMapper;
    
    @Autowired
    private BInPlanDetailMapper bInPlanDetailMapper;

    @Autowired
    private ICommonPoTotalService commonTotalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> startInsert(BInVo bInVo) {
        // 1. 校验业务规则
        checkInsertLogic(bInVo);
        
        // 2.保存入库单
        InsertResultAo<BInVo> insertResultAo = insert(bInVo);

        // 3.启动审批流程
        startFlowProcess(bInVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN);

        return insertResultAo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> insert(BInVo bInVo) {
        // 1. 校验业务逻辑
//        checkInsertLogic(bInVo);
        // 2. 保存主表信息
        BInEntity bInEntity = saveMainEntity(bInVo);
        // 3. 保存附件信息
        saveAttach(bInVo, bInEntity);
        // 4. 设置返回ID
        bInVo.setId(bInEntity.getId());
        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        return InsertResultUtil.OK(bInVo);
    }
    
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BInVo bInVo) {
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 根据状态设置处理相关字段
     * @param entity 入库单实体
     * @param status 状态
     */
    private void setProcessingFields(BInEntity entity, String status) {
        // 默认值设置为0
        BigDecimal zero = BigDecimal.ZERO;
        
        // 根据状态设置字段值
        if (DictConstant.DICT_B_IN_STATUS_ZERO.equals(status) || 
            DictConstant.DICT_B_IN_STATUS_THREE.equals(status) || 
            DictConstant.DICT_B_IN_STATUS_FOUR.equals(status)) {
            // 状态0、3、4：待审批、驳回、作废审批中
            entity.setProcessing_qty(zero);
            entity.setProcessing_weight(zero);
            entity.setProcessing_volume(zero);
            entity.setUnprocessed_qty(entity.getQty() != null ? entity.getQty() : zero);
            entity.setUnprocessed_weight(entity.getActual_weight() != null ? entity.getActual_weight() : zero);
            entity.setUnprocessed_volume(entity.getActual_volume() != null ? entity.getActual_volume() : zero);
            entity.setProcessed_qty(zero);
            entity.setProcessed_weight(zero);
            entity.setProcessed_volume(zero);
            entity.setCancel_qty(zero);
            entity.setCancel_volume(zero);
            entity.setCancel_weight(zero);
        } else if (DictConstant.DICT_B_IN_STATUS_ONE.equals(status)) {
            // 状态1：审批中
            entity.setProcessing_qty(entity.getQty() != null ? entity.getQty() : zero);
            entity.setProcessing_weight(entity.getActual_weight() != null ? entity.getActual_weight() : zero);
            entity.setProcessing_volume(entity.getActual_volume() != null ? entity.getActual_volume() : zero);
            entity.setUnprocessed_qty(zero);
            entity.setUnprocessed_weight(zero);
            entity.setUnprocessed_volume(zero);
            entity.setProcessed_qty(zero);
            entity.setProcessed_weight(zero);
            entity.setProcessed_volume(zero);
            entity.setCancel_qty(zero);
            entity.setCancel_volume(zero);
            entity.setCancel_weight(zero);
        } else if (DictConstant.DICT_B_IN_STATUS_TWO.equals(status) || 
                   DictConstant.DICT_B_IN_STATUS_SIX.equals(status)) {
            // 状态2、6：执行中、已完成
            entity.setProcessing_qty(zero);
            entity.setProcessing_weight(zero);
            entity.setProcessing_volume(zero);
            entity.setUnprocessed_qty(zero);
            entity.setUnprocessed_weight(zero);
            entity.setUnprocessed_volume(zero);
            entity.setProcessed_qty(entity.getQty() != null ? entity.getQty() : zero);
            entity.setProcessed_weight(entity.getActual_weight() != null ? entity.getActual_weight() : zero);
            entity.setProcessed_volume(entity.getActual_volume() != null ? entity.getActual_volume() : zero);
            entity.setCancel_qty(zero);
            entity.setCancel_volume(zero);
            entity.setCancel_weight(zero);
        } else if (DictConstant.DICT_B_IN_STATUS_FIVE.equals(status)) {
            // 状态5：已作废
            entity.setProcessing_qty(zero);
            entity.setProcessing_weight(zero);
            entity.setProcessing_volume(zero);
            entity.setUnprocessed_qty(zero);
            entity.setUnprocessed_weight(zero);
            entity.setUnprocessed_volume(zero);
            entity.setProcessed_qty(zero);
            entity.setProcessed_weight(zero);
            entity.setProcessed_volume(zero);
            entity.setCancel_qty(entity.getQty() != null ? entity.getQty() : zero);
            entity.setCancel_volume(entity.getActual_volume() != null ? entity.getActual_volume() : zero);
            entity.setCancel_weight(entity.getActual_weight() != null ? entity.getActual_weight() : zero);
        }
    }
    
    /**
     * 保存主表信息
     */
    private BInEntity saveMainEntity(BInVo bInVo) {
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);
        bInEntity.setUnit_id(SystemConstants.DEFAULT_VALUE.UNIT);
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_ONE);
        bInEntity.setCode(bInAutoCodeService.autoCode().getCode());
        bInEntity.setIs_del(Boolean.FALSE);

        // 保存入库计划的数据时是，查询下，获取计划数量、计划重量、计划体积
        if (bInVo.getPlan_detail_id() != null) {
            // 根据入库计划明细ID查询具体的计划数据
            BInPlanDetailEntity planDetail = bInPlanDetailMapper.selectById(bInVo.getPlan_detail_id());
            if (planDetail != null) {
                bInEntity.setPlan_qty(planDetail.getQty());
                bInEntity.setPlan_weight(planDetail.getWeight());
                bInEntity.setPlan_volume(planDetail.getVolume());
            }
        }

        bInEntity.setBpm_process_name("新增入库单审批");
        
        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());
        
        int result = mapper.insert(bInEntity);
        if (result == 0) {
            throw new BusinessException("新增失败");
        }
        return bInEntity;
    }
    
    /**
     * 保存附件信息
     */
    private void saveAttach(BInVo bInVo, BInEntity bInEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bInEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN);
        BInAttachEntity bInAttachEntity = insertFile(fileEntity, bInVo, new BInAttachEntity());
        bInAttachEntity.setIn_id(bInEntity.getId());
        int insert = bInAttachMapper.insert(bInAttachEntity);
        if (insert == 0) {
            throw new BusinessException("附件新增失败");
        }
    }
    
    /**
     * 附件文件处理
     */
    public BInAttachEntity insertFile(SFileEntity fileEntity, BInVo vo, BInAttachEntity extra) {
        // 入库单附件新增
        if (vo.getOne_file() != null && vo.getOne_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getOne_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOne_file(null);
        }

        if (vo.getTwo_file() != null && vo.getTwo_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getTwo_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setTwo_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setTwo_file(null);
        }

        if (vo.getThree_file() != null && vo.getThree_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getThree_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setThree_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setThree_file(null);
        }

        if (vo.getFour_file() != null && vo.getFour_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getFour_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setFour_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setFour_file(null);
        }


        return extra;
    }

    /**
     * 作废附件文件处理
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BInVo vo) {
        // 作废附件新增
        if (vo.getCancel_files() != null && vo.getCancel_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo cancel_file : vo.getCancel_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                cancel_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(cancel_file, fileInfoEntity);
                fileInfoEntity.setFile_name(cancel_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }
        return fileEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInVo> startUpdate(BInVo bInVo) {
        // 1. 校验业务规则
        checkUpdateLogic(bInVo);
        
        // 2.修改入库单
        UpdateResultAo<BInVo> updateResultAo = update(bInVo);

        // 3.启动审批流程
        startFlowProcess(bInVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN);

        return updateResultAo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInVo> update(BInVo bInVo) {
        // 1. 校验业务逻辑
        checkUpdateLogic(bInVo);
        
        // 2. 更新主表信息
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ONE);
        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("修改失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        return UpdateResultUtil.OK(bInVo);
    }
    
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BInVo bInVo) {
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    @Override
    public IPage<BInVo> selectPage(BInVo searchCondition) {
        // 分页条件
        Page<BInVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取入库单信息
     */
    @Override
    public BInVo selectById(Integer id) {
        BInVo bInVo = mapper.selectById(id);
        if (bInVo == null) {
            throw new BusinessException("入库单不存在");
        }

        // 附件信息处理 - 4个附件文件
        List<SFileInfoVo> one_files = isFileService.selectFileInfo(bInVo.getDoc_one_file());
        bInVo.setOne_file(one_files);
        
        List<SFileInfoVo> two_files = isFileService.selectFileInfo(bInVo.getDoc_two_file());
        bInVo.setTwo_file(two_files);
        
        List<SFileInfoVo> three_files = isFileService.selectFileInfo(bInVo.getDoc_three_file());
        bInVo.setThree_file(three_files);
        
        List<SFileInfoVo> four_files = isFileService.selectFileInfo(bInVo.getDoc_four_file());
        bInVo.setFour_file(four_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_IN_STATUS_FIVE.equals(bInVo.getStatus())) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bInVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                bInVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    bInVo.setCancel_doc_att_files(cancel_doc_att_files);
                }

                // 通过表m_staff获取作废提交人名称
                MStaffVo searchCondition = new MStaffVo();
                searchCondition.setId(mCancelVo.getC_id());
                MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                if (staffVo != null) {
                    bInVo.setCancel_name(staffVo.getName());
                }

                // 作废时间
                bInVo.setCancel_time(mCancelVo.getC_time());
            }
        }

        return bInVo;
    }

    @Override
    public CheckResultAo checkLogic(BInVo bean, String checkType) {
        // 基础校验
        if (bean == null) {
            return CheckResultUtil.NG("入库单信息不能为空");
        }

        // 新增校验
        if (CheckResultAo.INSERT_CHECK_TYPE.equals(checkType)) {
            // 业务字段校验
            CheckResultAo businessCheckResult = checkBusinessLogic(bean);
            if (!businessCheckResult.isSuccess()) {
                return businessCheckResult;
            }
        }

        // 更新校验
        if (CheckResultAo.UPDATE_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            if (bean.getDbversion() == null) {
                return CheckResultUtil.NG("数据版本不能为空");
            }
            // 业务字段校验
            CheckResultAo businessCheckResult = checkBusinessLogic(bean);
            if (!businessCheckResult.isSuccess()) {
                return businessCheckResult;
            }
        }
        
        // 删除校验
        if (CheckResultAo.DELETE_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            
            BInVo bInVo = selectById(bean.getId());
            BInEntity bInEntity = new BInEntity();
            BeanUtils.copyProperties(bInVo, bInEntity);
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }

            // 只有待审批或审批拒绝状态的单据才能删除
            if (!DictConstant.DICT_B_IN_STATUS_ZERO.equals(bInEntity.getStatus()) && !DictConstant.DICT_B_IN_STATUS_THREE.equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有待审批或审批拒绝状态的单据才能删除");
            }
        }

        // 作废校验
        if (CheckResultAo.CANCEL_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }

            BInVo bInVo = selectById(bean.getId());
            BInEntity bInEntity = new BInEntity();
            BeanUtils.copyProperties(bInVo, bInEntity);
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }

            // 只有审批通过状态的单据才能作废
            if (!DictConstant.DICT_B_IN_STATUS_TWO.equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有审批通过状态的单据才能作废");
            }
        }

        // 完成校验
        if (CheckResultAo.FINISH_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }

            BInVo bInVo = selectById(bean.getId());
            BInEntity bInEntity = new BInEntity();
            BeanUtils.copyProperties(bInVo, bInEntity);
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }
            
            // 只有审批通过状态的单据才能完成
            if (!DictConstant.DICT_B_IN_STATUS_TWO.equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有审批通过状态的单据才能完成");
            }
        }

        return CheckResultUtil.OK();
    }

    /**
     * 业务字段校验
     */
    private CheckResultAo checkBusinessLogic(BInVo bean) {
        if (StringUtils.isEmpty(bean.getType())) {
            return CheckResultUtil.NG("入库类型不能为空");  
        }
        if (bean.getOwner_id() == null) {
            return CheckResultUtil.NG("货主不能为空");
        }
        if (bean.getSku_id() == null) {
            return CheckResultUtil.NG("商品规格编号不能为空");
        }
        if (bean.getWarehouse_id() == null) {
            return CheckResultUtil.NG("仓库不能为空");
        }
        if (bean.getQty() == null) {
            return CheckResultUtil.NG("入库数量不能为空");
        }
        if (bean.getInbound_time() == null) {
            return CheckResultUtil.NG("入库时间不能为空");
        }
        if (bean.getPrice() == null) {
            return CheckResultUtil.NG("入库单价不能为空");
        }
        return CheckResultUtil.OK();
    }

    @Override
    public List<BInVo> selectExportList(BInVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BInVo querySum(BInVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    @Override
    public BInVo getPrintInfo(BInVo searchCondition) {
        return selectById(searchCondition.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BInVo> searchCondition) {
        try {
            if (CollectionUtil.isEmpty(searchCondition)) {
                throw new BusinessException("删除数据不能为空");
            }

            int count = 0;
            for (BInVo vo : searchCondition) {
                if (vo.getId() != null) {
                    // 删除前check
                    CheckResultAo cr = checkLogic(vo, CheckResultAo.DELETE_CHECK_TYPE);
                    if (!cr.isSuccess()) {
                        throw new BusinessException(cr.getMessage());
                    }
                    
                    // 逻辑删除
                    BInEntity entity = new BInEntity();
                    entity.setId(vo.getId());
                    entity.setIs_del(true);
                    
                    int result = mapper.updateById(entity);
                    if (result > 0) {
                        count++;
                    }
                }
            }

            if (count > 0) {
                return DeleteResultUtil.OK(count);
            }
            throw new BusinessException("删除失败");
        } catch (Exception e) {
            log.error("入库单删除失败", e);
            throw new BusinessException("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BInVo bInVo) {
        // 删除前check
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 删除主表
        int result = mapper.deleteById(bInVo.getId());
        if (result == 0) {
            throw new BusinessException("删除失败");
        }
        
        return DeleteResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BInVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bInEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bInEntity.setBpm_cancel_process_name("作废入库单审批");
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_FOUR); // 设置为作废待审批状态
        
        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());
        
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("修改失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getRemark());
        mCancelService.insert(mCancelVo);

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        // 3.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN_CANCEL);

        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BInVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_SIX); // 设置为完成状态
        
        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());
        
        int update = mapper.updateById(bInEntity);
        if (update == 0) {
            throw new BusinessException("修改失败");
        }

        return UpdateResultUtil.OK(update);
    }

    /**
     * check批次是否重复
     *
     * @param lot
     * @return
     */
    @Override
    public boolean isDuplicate(String lot) {
        if (org.apache.commons.lang.StringUtils.isEmpty(lot)) {
            return false;
        }
        Integer count = mapper.countLot(lot);
        if (count == null) {
            return false;
        }
        // 发生重复
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Override
    public BInEntity setBillInForUpdate(Integer id) {
        return mapper.setBillInForUpdate(id);
    }

    // ========== BPM回调方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BInVo searchCondition) {
        log.debug("===》审批流程创建成功，更新开始《===");
        BInVo bInVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("入库单编号：", bInVo.getCode());
        jsonObject.put("入库时间：", bInVo.getInbound_time());
        jsonObject.put("类型：", bInVo.getType_name());
        jsonObject.put("入库仓库：", bInVo.getWarehouse_name());
        jsonObject.put("入库商品：", bInVo.getGoods_name());
        jsonObject.put("入库数量：", bInVo.getQty());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bInVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BInVo searchCondition) {
        log.debug("===》入库单[{}]审批流程更新最新审批人，更新开始《===", searchCondition.getId());

        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);
        bInEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bInEntity);

        log.debug("===》入库单[{}]审批流程更新最新审批人,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废流程创建成功，更新开始《===", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("入库单编号：", bInVo.getCode());
        jsonObject.put("入库时间：", bInVo.getInbound_time());
        jsonObject.put("类型：", bInVo.getType_name());
        jsonObject.put("入库仓库：", bInVo.getWarehouse_name());
        jsonObject.put("入库商品：", bInVo.getGoods_name());
        jsonObject.put("入库数量：", bInVo.getQty());
        jsonObject.put("作废理由：", bInVo.getRemark());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bInVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        log.debug("===》入库单[{}]作废流程创建成功，更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程通过，更新开始《===", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_FIVE);  // 使用状态5表示已作废
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        bInEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新作废状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        log.debug("===》入库单[{}]作废审批流程通过,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程拒绝，更新开始《===", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        // 作废拒绝，恢复到正常状态
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_TWO); // 恢复到审批通过状态
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN);
        mCancelService.delete(mCancelVo);

        log.debug("===》入库单[{}]作废审批流程拒绝,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程取消，更新开始《===", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        // 作废取消，恢复到正常状态
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_TWO); // 恢复到审批通过状态
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN);
        mCancelService.delete(mCancelVo);

        log.debug("===》入库单[{}]作废审批流程取消,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 保存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废流程保存最新审批人《===", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setNext_approve_name(searchCondition.getNext_approve_name());

        int result = mapper.updateById(bInEntity);
        log.debug("===》入库单[{}]作废流程保存最新审批人结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BInVo bean, String type){
        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(bean.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            bBpmProcessVo.setCode(bpmProcessTemplatesService.getBpmFLowCodeByType(type));
            bBpmProcessVo.setSerial_type(type);
            bBpmProcessVo.setForm_data(bean.getForm_data());
            bBpmProcessVo.setForm_json(bean);
            bBpmProcessVo.setForm_class(bean.getClass().getName());
            bBpmProcessVo.setSerial_id(bean.getId());
            bBpmProcessVo.setInitial_process(bean.getInitial_process());
            bBpmProcessVo.setProcess_users(bean.getProcess_users());

            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    // ============ BPM 审批回调方法实现 ============

    /**
     * 审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_TWO); // 设置为审批通过状态
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        log.debug("====》入库单[{}]审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_THREE); // 设置为审批拒绝状态
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        log.debug("====》入库单[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程取消，更新开始《====", searchCondition.getId());
        BInVo bInVo = selectById(searchCondition.getId());
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);

        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_ZERO); // 设置为待审批状态
        bInEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);

        // 根据状态设置处理相关字段
        setProcessingFields(bInEntity, bInEntity.getStatus());

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        // 更新入库单汇总数据
        commonTotalService.reCalculateAllTotalDataByInboundId(bInEntity.getId());

        log.debug("====》入库单[{}]审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }
}
