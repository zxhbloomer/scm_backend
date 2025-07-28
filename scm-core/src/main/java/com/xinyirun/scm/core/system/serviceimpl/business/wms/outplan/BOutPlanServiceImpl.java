package com.xinyirun.scm.core.system.serviceimpl.business.wms.outplan;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanAttachEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanEntity;
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
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanAttachVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.wms.outplan.IBOutPlanService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutPlanAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutPlanDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 出库计划 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Slf4j
@Service
public class BOutPlanServiceImpl extends BaseServiceImpl<BOutPlanMapper, BOutPlanEntity> implements IBOutPlanService {

    @Autowired
    private BOutPlanMapper mapper;

    @Autowired
    private BOutPlanDetailMapper bOutPlanDetailMapper;

    @Autowired
    private BOutPlanAutoCodeServiceImpl bOutPlanAutoCodeService;

    @Autowired
    private BOutPlanDetailAutoCodeServiceImpl bOutPlanDetailAutoCodeService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private ICommonSoTotalService iCommonSoTotalService;

    @Autowired
    private BOutPlanAttachMapper bOutPlanAttachMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private ICommonSoTotalService commonTotalService;

    /**
     * 出库计划新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BOutPlanVo> insert(BOutPlanVo bOutPlanVo) {
        // 2. 保存主表信息
        BOutPlanEntity bOutPlanEntity = saveMainEntity(bOutPlanVo);
        // 3. 保存明细信息
        saveDetailList(bOutPlanVo, bOutPlanEntity);
        // 4. 保存附件信息
        saveAttach(bOutPlanVo, bOutPlanEntity);
        // 5. 设置返回ID
        bOutPlanVo.setId(bOutPlanEntity.getId());
        // 6. 更新出库计划汇总数据
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        return InsertResultUtil.OK(bOutPlanVo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BOutPlanVo bOutPlanVo) {
        CheckResultAo cr = checkLogic(bOutPlanVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BOutPlanEntity saveMainEntity(BOutPlanVo bOutPlanVo) {
        BOutPlanEntity bOutPlanEntity = new BOutPlanEntity();
        BeanUtils.copyProperties(bOutPlanVo, bOutPlanEntity);
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_ONE);
        bOutPlanEntity.setCode(bOutPlanAutoCodeService.autoCode().getCode());
        bOutPlanEntity.setIs_del(0);
        bOutPlanEntity.setBpm_process_name("新增出库计划审批");
        
        List<BOutPlanDetailVo> detailListData = bOutPlanVo.getDetailListData();
        calculatePlanAmounts(detailListData, bOutPlanEntity);
        bOutPlanEntity.setId(null); // 确保ID为null，避免插入时使用旧ID
        int result = mapper.insert(bOutPlanEntity);
        if (result == 0){
            throw new BusinessException("新增失败");
        }
        return bOutPlanEntity;
    }

    /**
     * 保存明细信息
     */
    private void saveDetailList(BOutPlanVo bOutPlanVo, BOutPlanEntity bOutPlanEntity) {
        List<BOutPlanDetailVo> detailListData = bOutPlanVo.getDetailListData();
        if (detailListData != null && !detailListData.isEmpty()) {
            int no = 1; // 序号从1开始累加
            for (BOutPlanDetailVo detailVo : detailListData) {
                BOutPlanDetailEntity bOutPlanDetailEntity = new BOutPlanDetailEntity();
                BeanUtils.copyProperties(detailVo, bOutPlanDetailEntity);
                bOutPlanDetailEntity.setOut_plan_id(bOutPlanEntity.getId());
                bOutPlanDetailEntity.setNo(no); // 设置累加序号
                
                // 使用自动编码生成 code
                bOutPlanDetailEntity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
                
                // 如果出库计划类型是销售出库，设置 serial 相关字段
                if (DictConstant.DICT_B_OUT_PLAN_TYPE_ZERO.equals(bOutPlanVo.getType())) {
                    bOutPlanDetailEntity.setSerial_id(detailVo.getOrder_id());
                    bOutPlanDetailEntity.setSerial_code(detailVo.getOrder_code());
                    bOutPlanDetailEntity.setSerial_type(DictConstant.DICT_B_OUT_PLAN_TYPE);
                }
                
                // 设置未处理数量、重量、体积
                bOutPlanDetailEntity.setUnprocessed_qty(bOutPlanDetailEntity.getQty());
                bOutPlanDetailEntity.setUnprocessed_weight(BigDecimal.ZERO);
                bOutPlanDetailEntity.setUnprocessed_volume(BigDecimal.ZERO);
                
                int result = bOutPlanDetailMapper.insert(bOutPlanDetailEntity);
                if (result == 0){
                    throw new BusinessException("明细新增失败");
                }
                no++; // 序号递增
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BOutPlanVo bOutPlanVo, BOutPlanEntity bOutPlanEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bOutPlanEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
        BOutPlanAttachEntity bOutPlanAttachEntity = insertFile(fileEntity, bOutPlanVo, new BOutPlanAttachEntity());
        bOutPlanAttachEntity.setOut_plan_id(bOutPlanEntity.getId());
        int insert = bOutPlanAttachMapper.insert(bOutPlanAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增附件失败");
        }
    }

    /**
     * 启动审批流新增出库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BOutPlanVo> startInsert(BOutPlanVo bOutPlanVo) {
        // 1. 校验业务规则
        checkInsertLogic(bOutPlanVo);
        
        // 2.保存出库计划
        InsertResultAo<BOutPlanVo> insertResultAo = insert(bOutPlanVo);

        // 3.启动审批流程
        startFlowProcess(bOutPlanVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN);

        return insertResultAo;
    }

    @Override
    public IPage<BOutPlanVo> selectPage(BOutPlanVo searchCondition) {
        // 分页条件
        Page<BOutPlanVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询出库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取出库计划信息
     */
    @Override
    public BOutPlanVo selectById(Integer id) {
        BOutPlanVo bOutPlanVo = mapper.selectId(id);
        if (bOutPlanVo == null) {
            throw new BusinessException("出库计划不存在");
        }

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(bOutPlanVo.getDoc_att_file());
        bOutPlanVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_OUT_PLAN_STATUS_FIVE.equals(bOutPlanVo.getStatus())) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bOutPlanVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                bOutPlanVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    bOutPlanVo.setCancel_doc_att_files(cancel_doc_att_files);
                }

                // 通过表m_staff获取作废提交人名称
                MStaffVo searchCondition = new MStaffVo();
                searchCondition.setId(mCancelVo.getC_id());
                MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                if (staffVo != null) {
                    bOutPlanVo.setCancel_name(staffVo.getName());
                }

                // 作废时间
                bOutPlanVo.setCancel_time(mCancelVo.getC_time());
            }
        }

        return bOutPlanVo;
    }

    /**
     * 修改出库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BOutPlanVo> update(BOutPlanVo bOutPlanVo) {

        // 2. 保存主表信息
        BOutPlanEntity bOutPlanEntity = new BOutPlanEntity();
        BeanUtils.copyProperties(bOutPlanVo, bOutPlanEntity);
        
        List<BOutPlanDetailVo> detailListData = bOutPlanVo.getDetailListData();
        calculatePlanAmounts(detailListData, bOutPlanEntity);

        bOutPlanEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ONE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 3. 删除旧明细
        bOutPlanDetailMapper.deleteByOutPlanId(bOutPlanVo.getId());
        
        // 4. 保存新明细信息
        saveDetailList(bOutPlanVo, bOutPlanEntity);

        // 5. 更新附件信息
        updateAttach(bOutPlanVo, bOutPlanEntity);

        // 6. 更新出库计划汇总数据
//        iCommonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        return UpdateResultUtil.OK(bOutPlanVo);
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BOutPlanVo bOutPlanVo, BOutPlanEntity bOutPlanEntity) {
        BOutPlanAttachVo bOutPlanAttachVo = bOutPlanAttachMapper.selectByOutPlanId(bOutPlanEntity.getId());
        if (bOutPlanAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bOutPlanEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
            BOutPlanAttachEntity bOutPlanAttachEntity = (BOutPlanAttachEntity) BeanUtilsSupport.copyProperties(bOutPlanAttachVo, BOutPlanAttachEntity.class);
            insertFile(fileEntity, bOutPlanVo, bOutPlanAttachEntity);
            bOutPlanAttachEntity.setOut_plan_id(bOutPlanEntity.getId());
            int update = bOutPlanAttachMapper.updateById(bOutPlanAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bOutPlanEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
            BOutPlanAttachEntity bOutPlanAttachEntity = new BOutPlanAttachEntity();
            insertFile(fileEntity, bOutPlanVo, bOutPlanAttachEntity);
            bOutPlanAttachEntity.setOut_plan_id(bOutPlanEntity.getId());
            int insert = bOutPlanAttachMapper.insert(bOutPlanAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 启动审批流修改出库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BOutPlanVo> startUpdate(BOutPlanVo bOutPlanVo) {
        // 1. 校验业务规则
        CheckResultAo cr = checkLogic(bOutPlanVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 2.修改出库计划
        UpdateResultAo<BOutPlanVo> updateResultAo = update(bOutPlanVo);

        // 3.启动审批流程
        startFlowProcess(bOutPlanVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN);

        return updateResultAo;
    }

    /**
     * 删除出库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BOutPlanVo bOutPlanVo) {
        // 删除前check
        CheckResultAo cr = checkLogic(bOutPlanVo, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 删除明细
        bOutPlanDetailMapper.deleteByOutPlanId(bOutPlanVo.getId());
        
        // 删除主表
        int result = mapper.deleteById(bOutPlanVo.getId());
        if (result == 0) {
            throw new BusinessException("删除失败");
        }

        return DeleteResultUtil.OK(result);
    }

    @Override
    public CheckResultAo checkLogic(BOutPlanVo bean, String checkType) {
        BOutPlanEntity bOutPlanEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData() == null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("请添加明细数据！");
                }
                
                // 校验明细数量
                for (BOutPlanDetailVo detailVo : bean.getDetailListData()) {
                    if (detailVo.getQty() == null || detailVo.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：提交的货物明细中存在尚未完成设置的数据。");
                    }
                }
                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bOutPlanEntity = mapper.selectById(bean.getId());
                if (bOutPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                
                // 是否待审批或者驳回状态
                if (!Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_ZERO)
                    && !Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，出库计划[%s]不是待审批,驳回状态,无法修改", bOutPlanEntity.getCode()));
                }

                if (bean.getDetailListData() == null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个明细");
                }
                break;
                
            // 删除校验
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bOutPlanEntity = mapper.selectById(bean.getId());
                if (bOutPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                
                // 是否待审批或者驳回状态
                if (!Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_ZERO)
                    && !Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，出库计划[%s]不是待审批,驳回状态,无法删除", bOutPlanEntity.getCode()));
                }
                break;
                
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bOutPlanEntity = mapper.selectById(bean.getId());
                if (bOutPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_FIVE)
                    || Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，出库计划[%s]无法重复作废", bOutPlanEntity.getCode()));
                }
                
                if (!Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，出库计划[%s]审核中，无法作废", bOutPlanEntity.getCode()));
                }
                break;
                
            // 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bOutPlanEntity = mapper.selectById(bean.getId());
                if (bOutPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否审批通过
                if (!Objects.equals(bOutPlanEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，出库计划[%s]未审批通过", bOutPlanEntity.getCode()));
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    @Override
    public List<BOutPlanVo> selectExportList(BOutPlanVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BOutPlanVo querySum(BOutPlanVo searchCondition) {
        return null;
    }

    @Override
    public BOutPlanVo getPrintInfo(BOutPlanVo searchCondition) {
        return null;
    }

    @Override
    public DeleteResultAo<Integer> delete(List<BOutPlanVo> searchCondition) {
        return null;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BOutPlanVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bOutPlanEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bOutPlanEntity.setBpm_cancel_process_name("作废出库计划审批");
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_FOUR);
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bOutPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getRemark());
        mCancelService.insert(mCancelVo);

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        // 3.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN_CANCEL);

        return UpdateResultUtil.OK(result);
    }

    /**
     * 完成
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BOutPlanVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SIX);
        int update = mapper.updateById(bOutPlanEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        return UpdateResultUtil.OK(update);
    }

    /**
     * 初始化计划数据
     */
    @Override
    public List<BOutPlanDetailVo> initPlanData(BOutPlanDetailVo searchCondition) {
        return mapper.initPlanData(searchCondition);
    }

    /**
     * 计算出库计划金额
     */
    private void calculatePlanAmounts(List<BOutPlanDetailVo> detailListData, BOutPlanEntity bOutPlanEntity) {
        BigDecimal planAmountSum = BigDecimal.ZERO;
        BigDecimal planTotal = BigDecimal.ZERO;
        
        if (detailListData != null && !detailListData.isEmpty()) {
            for (BOutPlanDetailVo detail : detailListData) {
                BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                
                // 计算总金额：sum(明细.qty * 明细.price)
                BigDecimal amount = qty.multiply(price);
                planAmountSum = planAmountSum.add(amount);
                
                // 计算总数量：sum(明细.qty)
                planTotal = planTotal.add(qty);
            }
        }
        
        // 设置计算结果到实体对象（如果主表有相关字段）
        // bOutPlanEntity.setPlan_amount_sum(planAmountSum);
        // bOutPlanEntity.setPlan_total(planTotal);
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BOutPlanVo bean, String type){
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

    /**
     * 附件处理
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BOutPlanVo vo) {
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

    /**
     * 附件文件处理
     */
    public BOutPlanAttachEntity insertFile(SFileEntity fileEntity, BOutPlanVo vo, BOutPlanAttachEntity extra) {
        // 出库计划附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 出库计划附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOne_file(null);
        }
        return extra;
    }

    // ============ BPM 回调方法实现 ============

    /**
     * 审批流程回调 - 创建流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BOutPlanVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        BOutPlanVo bOutPlanVo = selectById(searchCondition.getId());

        /**
         * 1、更新bmp_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("出库计划编号：", bOutPlanVo.getCode());
        jsonObject.put("计划时间：", bOutPlanVo.getPlan_time());
        jsonObject.put("类型：", bOutPlanVo.getType_name());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bOutPlanVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        bOutPlanEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bOutPlanEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_TWO);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        log.debug("====》出库计划[{}]审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_THREE);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        log.debug("====》出库计划[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]审批流程取消，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_ZERO);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        log.debug("====》出库计划[{}]审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    // ============ 作废BPM 回调方法实现 ============

    /**
     * 作废审批流程回调 - 创建流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BOutPlanVo searchCondition){
        // 作废流程创建逻辑
        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]作废审批流程通过，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        bOutPlanEntity.setBpm_cancel_instance_id(searchCondition.getBpm_cancel_instance_id());
        bOutPlanEntity.setBpm_cancel_instance_code(searchCondition.getBpm_cancel_instance_code());
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_FIVE);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新作废状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        log.debug("====》出库计划[{}]作废审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]作废审批流程拒绝，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        // 作废拒绝，恢复到正常状态
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_TWO);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bOutPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN);
        mCancelService.delete(mCancelVo);


        log.debug("====》出库计划[{}]作废审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]作废审批流程取消，更新开始《====", searchCondition.getId());
        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());

        // 作废取消，恢复到正常状态
        bOutPlanEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_TWO);
        bOutPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bOutPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByOutPlanId(bOutPlanEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bOutPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN);
        mCancelService.delete(mCancelVo);

        log.debug("====》出库计划[{}]作废审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 保存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BOutPlanVo searchCondition) {
        // 作废保存逻辑
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BOutPlanVo searchCondition) {
        log.debug("====》出库计划[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BOutPlanEntity bOutPlanEntity = mapper.selectById(searchCondition.getId());
        bOutPlanEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bOutPlanEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bOutPlanEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bOutPlanEntity);

        log.debug("====》出库计划[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }
}