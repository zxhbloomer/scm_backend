package com.xinyirun.scm.core.system.serviceimpl.business.po.pocontract;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractAttachEntity;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractDetailEntity;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
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
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractAttachVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.po.pocontract.BPoContractAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.po.pocontract.BPoContractDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.pocontract.BPoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.po.pocontract.IBPoContractService;
import com.xinyirun.scm.core.system.service.business.po.pocontract.IBPoContractTotalService;
import com.xinyirun.scm.core.system.service.business.po.poorder.IBPoOrderTotalService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.total.CommonPoTotalServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPoContractAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPoOrderAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购合同表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Slf4j
@Service
public class BPoContractServiceImpl extends BaseServiceImpl<BPoContractMapper, BPoContractEntity> implements IBPoContractService {

    @Autowired
    private BPoContractMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BPoContractDetailMapper bPoContractDetailMapper;

    @Autowired
    private BPoContractAutoCodeServiceImpl bPoContractAutoCodeService;

    @Autowired
    private BPoContractAttachMapper bPoContractAttachMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private BPoOrderMapper bPoOrderMapper;

    @Autowired
    private BPoOrderDetailMapper bPoOrderDetailMapper;

    @Autowired
    private BPoOrderAutoCodeServiceImpl bPoOrderAutoCodeService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper  mStaffMapper;

    @Autowired
    private MGoodsMapper mGoosMapper;

    @Autowired
    private SDictDataMapper sDictDataMapper;

    @Autowired
    private MEnterpriseMapper mEnterpriseMapper;

    @Autowired
    private MGoodsSpecMapper mGoosSpecMapper;

    @Autowired
    private IBPoContractTotalService iBPoContractTotalService;

    @Autowired
    private IBPoOrderTotalService iBPoOrderTotalService;

    @Autowired
    private CommonPoTotalServiceImpl commonTotalService;

    @Autowired
    private ICommonPoTotalService iCommonPoTotalService;

    /**
     * 采购合同  新增
     * @param BPoContractVo
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoContractVo> insert(BPoContractVo BPoContractVo) {
        // 1. 保存主表信息
        BPoContractEntity bPoContractEntity = saveMainEntity(BPoContractVo);
        // 2. 保存明细信息
        saveDetailList(BPoContractVo, bPoContractEntity);
        // 3. 保存附件信息
        saveAttach(BPoContractVo, bPoContractEntity);
        // 4. 设置返回ID
        BPoContractVo.setId(bPoContractEntity.getId());
        // 5. 更新合同财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByPoContractId(bPoContractEntity.getId());

        return InsertResultUtil.OK(BPoContractVo);
    }
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BPoContractVo BPoContractVo) {
        CheckResultAo cr = checkLogic(BPoContractVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    /**
     * 保存主表信息
     */
    private BPoContractEntity saveMainEntity(BPoContractVo BPoContractVo) {
        BPoContractEntity bPoContractEntity = new BPoContractEntity();
        BeanUtils.copyProperties(BPoContractVo, bPoContractEntity);
        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_ONE);
        bPoContractEntity.setCode(bPoContractAutoCodeService.autoCode().getCode());
        bPoContractEntity.setIs_del(Boolean.FALSE);
        bPoContractEntity.setBpm_process_name("新增采购合同审批");
        if (StringUtils.isEmpty(bPoContractEntity.getContract_code())){
            bPoContractEntity.setContract_code(bPoContractEntity.getCode());
        }
        bPoContractEntity.setAuto_create_order(true);
        List<BPoContractDetailVo> detailListData = BPoContractVo.getDetailListData();
        calculateContractAmounts(detailListData, bPoContractEntity);
        int bPurContract = mapper.insert(bPoContractEntity);
        if (bPurContract == 0){
            throw new BusinessException("新增失败");
        }
        return bPoContractEntity;
    }
    /**
     * 保存明细信息
     */
    private void saveDetailList(BPoContractVo BPoContractVo, BPoContractEntity bPoContractEntity) {
        List<BPoContractDetailVo> detailListData = BPoContractVo.getDetailListData();
        for (BPoContractDetailVo detailListDatum : detailListData) {
            BPoContractDetailEntity bPoContractDetailEntity = new BPoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bPoContractDetailEntity);
            bPoContractDetailEntity.setPo_contract_id(bPoContractEntity.getId());
            bPoContractDetailEntity.setAmount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice()).setScale(2, RoundingMode.HALF_UP));
            bPoContractDetailEntity.setTax_amount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice())
                            .multiply(detailListDatum.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));
            int bPurContractDetail = bPoContractDetailMapper.insert(bPoContractDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增失败");
            }
        }
    }
    /**
     * 保存附件信息
     */
    private void saveAttach(BPoContractVo BPoContractVo, BPoContractEntity bPoContractEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bPoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CONTRACT);
        BPoContractAttachEntity bPoContractAttachEntity = insertFile(fileEntity, BPoContractVo, new BPoContractAttachEntity());
        bPoContractAttachEntity.setPo_contract_id(bPoContractEntity.getId());
        int insert = bPoContractAttachMapper.insert(bPoContractAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 采购合同  新增
     *
     * @param BPoContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoContractVo> startInsert(BPoContractVo BPoContractVo) {
        // 1. 校验业务规则
        checkInsertLogic(BPoContractVo);
        
        // 2.保存采购合同
        InsertResultAo<BPoContractVo> insertResultAo = insert(BPoContractVo);

        // 3.启动审批流程
        startFlowProcess(BPoContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT);

        return insertResultAo;
    }

    @Override
    public IPage<BPoContractVo> selectPage(BPoContractVo searchCondition) {
        // 分页条件
        Page<BPoContractVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取采购合同信息
     * @param id
     */
    @Override
    public BPoContractVo selectById(Integer id) {
        BPoContractVo BPoContractVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(BPoContractVo.getDoc_att_file());
        BPoContractVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_PO_CONTRACT_STATUS_FOUR.equals(BPoContractVo.getStatus()) || Objects.equals(BPoContractVo.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(BPoContractVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CONTRACT);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            BPoContractVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                BPoContractVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            BPoContractVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            BPoContractVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (BPoContractVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(BPoContractVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            BPoContractVo.setProject(bProjectVo);
        }
        return BPoContractVo;
    }

    /**
     * 采购合同  新增
     *
     * @param BPoContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BPoContractVo BPoContractVo) {
        // 1. 校验业务规则
        checkUpdateLogic(BPoContractVo);
        
        // 2.保存采购合同
        UpdateResultAo<Integer> insertResultAo = update(BPoContractVo);

        // 3.启动审批流程
        startFlowProcess(BPoContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT);

        return insertResultAo;
    }

    /**
     * 更新采购合同信息
     *
     * @param BPoContractVo
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BPoContractVo BPoContractVo) {
        // 1. 更新主表信息
        BPoContractEntity bPoContractEntity = updateMainEntity(BPoContractVo);
        // 2. 更新明细信息
        updateDetailList(BPoContractVo, bPoContractEntity);
        // 3. 更新附件信息
        updateAttach(BPoContractVo, bPoContractEntity);
        // 4. 更新合同财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByPoContractId(bPoContractEntity.getId());

        return UpdateResultUtil.OK(1);
    }
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BPoContractVo BPoContractVo) {
        CheckResultAo cr = checkLogic(BPoContractVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    /**
     * 更新主表信息
     */
    private BPoContractEntity updateMainEntity(BPoContractVo BPoContractVo) {
        BPoContractEntity bPoContractEntity = (BPoContractEntity) BeanUtilsSupport.copyProperties(BPoContractVo, BPoContractEntity.class);
        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_ONE);
        bPoContractEntity.setBpm_process_name("更新采购合同审批");
        List<BPoContractDetailVo> detailListData = BPoContractVo.getDetailListData();
        calculateContractAmounts(detailListData, bPoContractEntity);
        int updCount = mapper.updateById(bPoContractEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bPoContractEntity;
    }
    /**
     * 更新明细信息
     */
    private void updateDetailList(BPoContractVo BPoContractVo, BPoContractEntity bPoContractEntity) {
        List<BPoContractDetailVo> detailListData = BPoContractVo.getDetailListData();
        bPoContractDetailMapper.deleteByPoContractId(bPoContractEntity.getId());
        for (BPoContractDetailVo detailListDatum : detailListData) {
            BPoContractDetailEntity bPoContractDetailEntity = new BPoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bPoContractDetailEntity);
            bPoContractDetailEntity.setPo_contract_id(bPoContractEntity.getId());
            bPoContractDetailEntity.setAmount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice()).setScale(2, RoundingMode.HALF_UP));
            bPoContractDetailEntity.setTax_amount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice())
                            .multiply(detailListDatum.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));
            int bPurContractDetail = bPoContractDetailMapper.insert(bPoContractDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增购合同明细表-商品失败");
            }
        }
    }
    /**
     * 更新附件信息
     */
    private void updateAttach(BPoContractVo BPoContractVo, BPoContractEntity bPoContractEntity) {
        BPoContractAttachVo BPoContractAttachVo = bPoContractAttachMapper.selectByPoContractId(bPoContractEntity.getId());
        if (BPoContractAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bPoContractEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CONTRACT);
            BPoContractAttachEntity bPoContractAttachEntity =(BPoContractAttachEntity) BeanUtilsSupport.copyProperties(BPoContractAttachVo, BPoContractAttachEntity.class);
            insertFile(fileEntity, BPoContractVo, bPoContractAttachEntity);
            bPoContractAttachEntity.setPo_contract_id(bPoContractEntity.getId());
            int update = bPoContractAttachMapper.updateById(bPoContractAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bPoContractEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CONTRACT);
            BPoContractAttachEntity bPoContractAttachEntity = new BPoContractAttachEntity();
            insertFile(fileEntity, BPoContractVo, bPoContractAttachEntity);
            bPoContractAttachEntity.setPo_contract_id(bPoContractEntity.getId());
            int insert = bPoContractAttachMapper.insert(bPoContractAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 删除采购合同信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BPoContractVo> searchCondition) {
        for (BPoContractVo BPoContractVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(BPoContractVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BPoContractEntity bPoContractEntity = mapper.selectById(BPoContractVo.getId());
            bPoContractEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bPoContractEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 按采购合同合计
     *
     * @param searchCondition
     */
    @Override
    public BPoContractVo querySum(BPoContractVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 采购合同校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BPoContractVo bean, String checkType) {
        List<BPoContractVo> BPoContractVos = mapper.validateDuplicateContractCode(bean);
        BPoContractEntity bPoContractEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("请添加商品数据！");
                }

                Map<String, Long> collect = bean.getDetailListData()
                        .stream()
                        .map(BPoContractDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1)
                        result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result);
                }

                // 判断合同号是否重复
                if (CollectionUtil.isNotEmpty(BPoContractVos)){
                    String err = "合同编号重复：系统检测到" + bean.getContract_code() + "已被使用，请输入其他编号继续操作";
                    return CheckResultUtil.NG(err);
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoContractEntity = mapper.selectById(bean.getId());
                if (bPoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_ZERO) && !Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，采购合同[%s]不是待审批,驳回状态,无法修改",bPoContractEntity.getCode()));

                }

                if (bean.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect2 = bean.getDetailListData()
                        .stream()
                        .map(BPoContractDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result2 = new ArrayList<>();
                collect2.forEach((k,v)->{
                    if(v>1)
                        result2.add(k);
                });

                if (result2!=null&&result2.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result2);
                }

                // 判断合同号是否重复
                if (CollectionUtil.isNotEmpty(BPoContractVos)){
                    String err = "合同编号重复：系统检测到" + bean.getContract_code() + "已被使用，请输入其他编号继续操作";
                    return CheckResultUtil.NG(err);
                }

                break;
            // 删除校验
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoContractEntity = mapper.selectById(bean.getId());
                if (bPoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_ZERO) && !Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，采购合同[%s]不是待审批,驳回状态,无法删除",bPoContractEntity.getCode()));
                }

                List<BPoOrderVo> delBApPayVo = bPoOrderMapper.selectByPoContractId(bean.getId());
                if (CollectionUtil.isNotEmpty(delBApPayVo)) {
                    return CheckResultUtil.NG("删除失败，存在采购订单");
                }
                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoContractEntity = mapper.selectById(bean.getId());
                if (bPoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FIVE) || Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，采购合同[%s]无法重复作废",bPoContractEntity.getCode()));
                }
                if (!Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，采购合同[%s]审核中，无法作废",bPoContractEntity.getCode()));
                }

                List<BPoOrderVo> cancelOrderVos = bPoOrderMapper.selectByPoContractIdNotByStatus(bean.getId(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FIVE);
                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
                    return CheckResultUtil.NG(String.format("作废失败，采购单号[%s]数据未作废，请先完成该采购订单的作废。",cancelOrderVos.stream().map(BPoOrderVo::getCode).collect(Collectors.toList())));
                }
                break;
            // 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:

                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoContractEntity = mapper.selectById(bean.getId());
                if (bPoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否审批通过
                if (Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，采购合同[%s]未进入执行状态",bPoContractEntity.getCode()));
                }

                List<BPoOrderVo> finishOrderVos = bPoOrderMapper.selectByPoContractIdNotByStatus(bean.getId(), DictConstant.DICT_B_PO_ORDER_STATUS_SIX);
                if (CollectionUtil.isNotEmpty(finishOrderVos)){
                    return CheckResultUtil.NG(String.format("系统检测到合同编号[%s]存在未完成的采购订单，请完成订单[%s]后再提交。",bean.getContract_code(),finishOrderVos.stream().findFirst().get().getCode()));
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BPoContractVo bean, String type){
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
     *  审批流程回调 更新bpm_instance的摘要数据
     *  审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BPoContractVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        BPoContractVo BPoContractVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 供应商：xxx，主体企业：xxx，合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("供应商：", BPoContractVo.getSupplier_name());
        jsonObject.put("主体企业：", BPoContractVo.getPurchaser_name());
        jsonObject.put("合同金额:", BPoContractVo.getContract_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BPoContractVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }
    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bPoContractEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购合同[{}]审批流程通过,更新结束《====", searchCondition.getId());

        /**
         * 根据审批后自动生成订单，自动生成的订单-已经审批
         */
        BPoContractVo vo = selectById(searchCondition.getId());
        if (vo.getAuto_create_order() != null && vo.getAuto_create_order()) {
            log.debug("====》开始自动创建采购订单《====");
            createAutoOrder(vo, bPoContractEntity);
            log.debug("====》自动创建采购订单完成《====");
        }

        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_THREE);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购合同[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]审批流程撤销，更新开始《====", searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_ZERO);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购合同[{}]审批流程撤销,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());
        bPoContractEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bPoContractEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bPoContractEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bPoContractEntity);

        log.debug("====》采购合同[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public BPoContractVo getPrintInfo(BPoContractVo searchCondition) {
        /**
         * 获取打印配置信息
         * 1、从s_config中获取到：print_system_config、
         */
        SConfigEntity _data = isConfigService.selectByKey(SystemConstants.PRINT_SYSTEM_CONFIG);
        String url = _data.getValue();
        String token = _data.getExtra1();

        /**
         * 获取打印配置信息
         * 2、从s_page中获取到print_code
         */
        SPagesVo param = new SPagesVo();
        param.setCode(PageCodeConstant.PAGE_PO_CONTRACT);
        SPagesVo pagesVo = isPagesService.get(param);

        /**
         * 获取打印配置信息
         * 3、从s_app_config中获取，报表系统的app_key，securit_key
         */
//        SAppConfigEntity key = isAppConfigService.getDataByAppCode(AppConfigConstant.PRINT_SYSTEM_CODE);

        String printUrl =  url + pagesVo.getPrint_code() + "?token=" + token + "&id=" + searchCondition.getId();
//        printUrl = printUrl + "&app_key=" + key.getApp_key() + "&secret_key=" + key.getSecret_key();
        searchCondition.setPrint_url(printUrl);
        searchCondition.setQr_code(printUrl);
        log.debug("打印地址：" + printUrl);
        return searchCondition;
    }

    /**
     * 导出查询
     *
     * @param param
     */
    @Override
    public List<BPoContractVo> selectExportList(BPoContractVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(param.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectExportCount(param);

            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportList(param);
    }

    /**
     * 附件逻辑 全删全增
     */
    public BPoContractAttachEntity insertFile(SFileEntity fileEntity, BPoContractVo vo, BPoContractAttachEntity extra) {
        // 其他附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 其他附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setOne_file(null);
        }
        return extra;
    }


    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BPoContractVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BPoContractVo BPoContractVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", BPoContractVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BPoContractVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bPoContractEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_FIVE);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购合同[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bPoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CONTRACT);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购合同[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO);
        bPoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bPoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CONTRACT);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购合同[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BPoContractVo searchCondition) {
        log.debug("====》采购合同[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        bPoContractEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bPoContractEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bPoContractEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bPoContractEntity);

        log.debug("====》采购合同[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BPoContractVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bPoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CONTRACT);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bPoContractEntity.setBpm_cancel_process_name("作废采购合同审批");
        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_FOUR);
        int insert = mapper.updateById(bPoContractEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bPoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CONTRACT);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BPoContractVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoContractEntity bPoContractEntity = mapper.selectById(searchCondition.getId());
        bPoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_SIX);
        int update = mapper.updateById(bPoContractEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        return UpdateResultUtil.OK(update);
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BPoContractVo vo) {
        // 其他附件新增
        if (vo.getCancel_files() != null && vo.getCancel_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getCancel_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }
        return fileEntity;
    }

    /**
     * 导入数据
     *
     * @param beans
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BPoContractImportVo> importData(List<BPoContractImportVo> beans) {
        List<BPoContractImportVo>  vos = new ArrayList<>();
        for (BPoContractImportVo vo : beans) {
            // 字典类value设置：类型、结算方式、结算单据类型、运输方式；以及主表类id设置：供应商(必填)、主体企业(必填)、物料名称(必填)、规格(必填)
            setImportBean(vo);

            BPoContractEntity entity = new BPoContractEntity();
            entity.setCode(bPoContractAutoCodeService.autoCode().getCode());
            entity.setType(vo.getType());
            entity.setContract_code(vo.getContract_code());
            entity.setSupplier_id(vo.getSupplier_id());
            entity.setPurchaser_id(vo.getPurchaser_id());
            entity.setSign_date(vo.getSign_date());
            entity.setExpiry_date(vo.getExpiry_date());
            entity.setDelivery_date(vo.getDelivery_date());
            entity.setDelivery_type(vo.getDelivery_type());
            entity.setSettle_type(vo.getSettle_type());
            entity.setBill_type(vo.getBill_type());
            entity.setPayment_type(DictConstant.DICT_B_PO_CONTRACT_PAYMENT_TYPE_ONE);
            entity.setDelivery_location(vo.getDelivery_location());
            entity.setAuto_create_order(Boolean.TRUE);
            entity.setRemark(vo.getRemark());
            entity.setIs_del(false);
            entity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_ZERO); // 待审批
            // 计算合同总金额：商品数量 * 商品单价
            entity.setContract_amount_sum(vo.getQty().multiply(vo.getPrice()));
            // 总采购数量：商品数量
            entity.setContract_total(vo.getQty());
            // 总税额：税率 * 合同总金额 / 100
            entity.setTax_amount_sum(vo.getTax_rate().multiply(entity.getContract_amount_sum()).divide(new BigDecimal(100)));
            // 参入主标
            mapper.insert(entity);

            // 更新合同财务数据
            iCommonPoTotalService.reCalculateAllTotalDataByPoContractId(entity.getId());


            // 处理从表
            BPoContractDetailEntity detailEntity = new BPoContractDetailEntity();
            detailEntity.setPo_contract_id(entity.getId());
            detailEntity.setGoods_id(vo.getGoods_id());
            detailEntity.setGoods_code(vo.getGoods_code());
            detailEntity.setGoods_name(vo.getGoods_name());
            detailEntity.setSku_code(vo.getSku_code());
            detailEntity.setSku_name(vo.getSku_name());
            detailEntity.setSku_id(vo.getSku_id());
            detailEntity.setOrigin(vo.getOrigin());
            detailEntity.setQty(vo.getQty());
            detailEntity.setPrice(vo.getPrice());
            detailEntity.setAmount(detailEntity.getQty().multiply(detailEntity.getPrice()).setScale(2, RoundingMode.HALF_UP));
            detailEntity.setTax_rate(vo.getTax_rate());
            // vo.getTax_rate() * vo.getAmount() / 100
            detailEntity.setTax_amount(
                    detailEntity.getQty().multiply(detailEntity.getPrice())
                            .multiply(detailEntity.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));
            bPoContractDetailMapper.insert(detailEntity);

            vos.add(vo);
        }
        return vos;
    }

    /**
     * excel 导入校验
     * 校验供应商是否存在
     * 
     * @param vo 当前合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 返回true表示重复，false表示不重复
     */
    public boolean checkSupplierIsExists(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 创建MEnterpriseVo对象用于查询
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getSupplier_name());
        mEnterpriseVo.setIsSupplier(true); // 设置为供应商查询
        
        // 调用validateDuplicateName方法查询是否存在
        List<MEnterpriseVo> result = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);
        
        // 判断返回结果
        // 如果size>=1，表示存在，返回true；否则返回false
        // null也视为不重复，返回false
        return result != null && result.size() >= 1;
    }

    /**
     * excel 导入校验
     * 校验供应商是否存在
     *
     * @param vo 当前合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 返回true表示重复，false表示不重复
     */
    public boolean checkCustomerAndSysCompanyIsExists(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 创建MEnterpriseVo对象用于查询
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getPurchaser_name());
//        mEnterpriseVo.setIsCustomer(true); // 此处不需要设置，因为主体企业，不需要考虑这些，主体企业技能采购、也能销售
        mEnterpriseVo.setIsSysCompany(true); // 设置为主体企业查询

        // 调用validateDuplicateName方法查询是否存在
        List<MEnterpriseVo> result = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);

        // 判断返回结果
        // 如果size>=1，表示存在重复，返回true；否则返回false
        // null也视为不重复，返回false
        return result != null && result.size() >= 1;
    }

    /**
     * excel 导入校验
     * 校验字典，按名称查询是否存在
     * 类型：0：标准合同；1：框架合同
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checDictExistByNameType (BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_TYPE, vo.getType_name());
        if (selectByName == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * excel 导入校验
     * 校验字典，按名称查询是否存在
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checDictExistByNameSettleType (BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_SETTLE_TYPE, vo.getSettle_type_name());
        if (selectByName == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * excel 导入校验
     * 校验字典，按名称查询是否存在
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checDictExistByNameBillType (BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_BILL_TYPE, vo.getBill_type_name());
        if (selectByName == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * excel 导入校验
     * 校验字典，按名称查询是否存在
     * 运输方式：1-公路；2-铁路；3-多式联运；
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checDictExistByNamedeliveryType (BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        if(!StringUtils.isNotBlank(vo.getDelivery_type_name())) {
            return true; // 非必填，如果没有填写，直接返回true
        }
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_DELIVERY_TYPE, vo.getDelivery_type_name());
        if (selectByName == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * excel 导入校验
     * 校验合同编号是否重复
     * @param vo 当前校验的合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 如果重复返回true，不重复返回false
     */
    public Boolean checkContractNoDuplicate(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 创建参数对象
        BPoContractVo contractVo = new BPoContractVo();
        contractVo.setContract_code(vo.getContract_code());

        // 调用mapper的校验方法
        List<BPoContractVo> BPoContractVos = mapper.validateDuplicateContractCode(contractVo);

        /**
         * 判断返回结果，size>=1表示数据存在，返回false，null或size=0表示不重复返回true
         */
        if( BPoContractVos == null || BPoContractVos.size() == 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * excel 导入校验
     * 校验商品名称是否存在
     * @param vo 当前校验的合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 返回true表示重复，false表示不重复
     */
    public boolean checkGoodsNameIsExists(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 获取商品名称
        String goodsName = vo.getGoods_name();
        
        // 调用mapper的selectByName方法查询是否存在该商品名称
        List<MGoodsEntity> result = mGoosMapper.selectByName(goodsName);

        // 判断返回结果
        // 如果size>=1，表示存在重复，返回true；否则返回false
        // null也视为不重复，返回false
        return result != null && result.size() >= 1;
    }

    /**
     * 校验商品规格名称是否存在
     * @param vo 导入的VO对象
     * @param vos 导入的VO对象列表
     * @return 如果存在返回true，否则返回false
     */
    public boolean checkGoodsSpecNameIsExists(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 获取商品名称
        String goodsName = vo.getGoods_name();
        // 从vo中获取sku_name
        String skuName = vo.getSku_name();

        // 调用mGoosSpecMapper.selectByName方法，传入sku_name
        List<MGoodsSpecEntity> specList = mGoosSpecMapper.selectByName(goodsName, skuName);

        // 判断返回结果的大小，如果size>=1，说明该规格名称已存在，返回true；否则返回false
        if (specList != null && specList.size() >= 1) {
            return true;
        }

        return false;
    }

    /**
     * 校验税率方法
     * @param vo 当前校验的合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 如果税率有效返回true，否则返回false
     */
    public boolean checkTaxData(BPoContractImportVo vo, ArrayList<BPoContractImportVo> vos) {
        // 从vo中获取tax_rate
        BigDecimal taxRate = vo.getTax_rate();
        
        // 如果税率为null，返回false
        if (taxRate == null) {
            return false;
        }
        
        // 定义有效的税率值：9、13、17、6
        BigDecimal[] validTaxRates = {
            new BigDecimal("6"),
            new BigDecimal("9"),
            new BigDecimal("13"),
            new BigDecimal("17")
        };
        
        // 判断税率是否为有效值之一
        for (BigDecimal validRate : validTaxRates) {
            if (taxRate.compareTo(validRate) == 0) {
                return true;
            }
        }
        
        // 如果不是有效税率，返回false
        return false;
    }

    /**
     * excel 导入bean设置
     * 字典类value设置：类型、结算方式、结算单据类型、运输方式
     * 主表类id设置：供应商(必填)、主体企业(必填)、物料名称(必填)、规格(必填)
     * @param vo
     * @return
     */
    public void setImportBean (BPoContractImportVo vo) {
        // 类型（必填）：字典的label->value
        SDictDataVo typeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_TYPE, vo.getType_name());
        vo.setType(typeData.getDict_value());

        // 结算方式（必填）：字典的label->value
        SDictDataVo settleTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_SETTLE_TYPE, vo.getSettle_type_name());
        vo.setSettle_type(settleTypeData.getDict_value());

        // 结算单据类型（必填）：字典的label->value
        SDictDataVo billTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_BILL_TYPE, vo.getBill_type_name());
        vo.setBill_type(billTypeData.getDict_value());

        // 运输方式（非必填）：字典的label->value
        if (StringUtils.isNotBlank(vo.getDelivery_type_name())) {
            SDictDataVo deliveryTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_PO_CONTRACT_DELIVERY_TYPE, vo.getDelivery_type_name());
            vo.setDelivery_type(deliveryTypeData.getDict_value());
        } else {
            vo.setDelivery_type(null); // 如果没有填写，设置为null
        }

        // 供应商（必填）：查询企业表，获取id
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getSupplier_name());
        mEnterpriseVo.setIsSupplier(true); // 设置为供应商查询
        List<MEnterpriseVo> supplier = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);
        vo.setSupplier_id(supplier.get(0).getId());

        // 主体企业(必填):查询企业表，获取id
        MEnterpriseVo purchaser = new MEnterpriseVo();
        purchaser.setName(vo.getPurchaser_name());
        purchaser.setIsSysCompany(true); // 设置为主体企业查询
        List<MEnterpriseVo> result = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);
        vo.setPurchaser_id(result.get(0).getId());

        // 物料名称（必填）：查询商品表，获取id
        List<MGoodsSpecEntity> sku = mGoosSpecMapper.selectByName(vo.getGoods_name(), vo.getSku_name());
        vo.setSku_code(sku.get(0).getCode());
        vo.setSku_id(sku.get(0).getId());
    }

    /**
     * 计算合同总金额、总采购数量（吨）、总税额
     * @param detailListData 合同明细数据
     * @param bPoContractEntity 采购合同实体对象
     */
    private void calculateContractAmounts(List<BPoContractDetailVo> detailListData, BPoContractEntity bPoContractEntity) {
        BigDecimal contractAmountSum = BigDecimal.ZERO;
        BigDecimal contractTotal = BigDecimal.ZERO;
        BigDecimal taxAmountSum = BigDecimal.ZERO;
        
        if (detailListData != null && !detailListData.isEmpty()) {
            for (BPoContractDetailVo detail : detailListData) {
                BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
                
                // 计算合同总金额：sum(明细.qty * 明细.price)
                BigDecimal amount = qty.multiply(price);
                contractAmountSum = contractAmountSum.add(amount);
                
                // 计算总采购数量（吨）：sum(明细.qty)
                contractTotal = contractTotal.add(qty);
                  // 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
                BigDecimal taxAmount = qty.multiply(price).multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                taxAmountSum = taxAmountSum.add(taxAmount);
            }
        }
        
        // 设置计算结果到实体对象
        bPoContractEntity.setContract_amount_sum(contractAmountSum);
        bPoContractEntity.setContract_total(contractTotal);
        bPoContractEntity.setTax_amount_sum(taxAmountSum);
    }

    /**
     * 自动创建采购订单
     * @param BPoContractVo 采购合同查询条件
     * @param bPoContractEntity 采购合同实体
     */
    private void createAutoOrder(BPoContractVo BPoContractVo, BPoContractEntity bPoContractEntity) {
//        try {
            // 1. 创建采购订单主表数据
            BPoOrderEntity bPoOrderEntity = new BPoOrderEntity();
            
            // 基本字段映射
            bPoOrderEntity.setCode(bPoOrderAutoCodeService.autoCode().getCode());
            bPoOrderEntity.setSupplier_name(bPoContractEntity.getSupplier_name());
            bPoOrderEntity.setSupplier_code(bPoContractEntity.getSupplier_code());
            bPoOrderEntity.setSupplier_id(bPoContractEntity.getSupplier_id());
            bPoOrderEntity.setPo_contract_code(bPoContractEntity.getContract_code());
            bPoOrderEntity.setProject_code(bPoContractEntity.getProject_code());
            bPoOrderEntity.setPurchaser_name(bPoContractEntity.getPurchaser_name());
            bPoOrderEntity.setPurchaser_code(bPoContractEntity.getPurchaser_code());
            bPoOrderEntity.setPurchaser_id(bPoContractEntity.getPurchaser_id());
            bPoOrderEntity.setOrder_date(LocalDateTime.now());
            bPoOrderEntity.setDelivery_date(bPoContractEntity.getDelivery_date());
            bPoOrderEntity.setDelivery_type(bPoContractEntity.getDelivery_type());
            bPoOrderEntity.setSettle_type(bPoContractEntity.getSettle_type());
            bPoOrderEntity.setBill_type(bPoContractEntity.getBill_type());
            bPoOrderEntity.setPayment_type(bPoContractEntity.getPayment_type());
            bPoOrderEntity.setDelivery_location(bPoContractEntity.getDelivery_location());
            bPoOrderEntity.setRemark(bPoContractEntity.getRemark());
            bPoOrderEntity.setPo_contract_id(bPoContractEntity.getId()); // 关联采购合同ID
            
            // 计算订单总金额、总数量、总税额
            BigDecimal orderAmountSum = BigDecimal.ZERO;
            BigDecimal orderTotal = BigDecimal.ZERO;
            BigDecimal taxAmountSum = BigDecimal.ZERO;
            List<BPoContractDetailVo> detailListData = BPoContractVo.getDetailListData();
            for (BPoContractDetailVo detail : detailListData) {
                    BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                    BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                    BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
                    
                    // 计算订单总金额：商品数量 * 商品单价
                    BigDecimal amount = qty.multiply(price);
                    orderAmountSum = orderAmountSum.add(amount);
                    
                    // 总采购数量：商品数量
                    orderTotal = orderTotal.add(qty);
                    
                    // 总税额：税率 * 合同总金额 / 100
                    BigDecimal taxAmount = taxRate.multiply(amount).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    taxAmountSum = taxAmountSum.add(taxAmount);
                }

            // 设置订单状态为已审批（跳过审批流程）
            bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_TWO);
            bPoOrderEntity.setIs_del(Boolean.FALSE);
            bPoOrderEntity.setBpm_instance_id(BPoContractVo.getBpm_instance_id());
            bPoOrderEntity.setBpm_instance_code(BPoContractVo.getBpm_instance_code());
            bPoOrderEntity.setBpm_process_name("自动生成采购订单（已审批）");
            bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
            
            // 插入订单主表
            int orderResult = bPoOrderMapper.insert(bPoOrderEntity);

            // 更新订单财务数据
//            iBPoOrderTotalService.calcOrderAmountAndTax(bPoOrderEntity.getId());

            if (orderResult == 0) {
                throw new BusinessException("自动创建采购订单失败");
            }
            
            log.debug("自动创建采购订单成功，订单ID：{}, 订单编号：{}", bPoOrderEntity.getId(), bPoOrderEntity.getCode());
              // 2. 创建采购订单明细数据
            if (BPoContractVo.getDetailListData() != null && !BPoContractVo.getDetailListData().isEmpty()) {
                for (BPoContractDetailVo contractDetail : BPoContractVo.getDetailListData()) {
                    BPoOrderDetailEntity orderDetail = new BPoOrderDetailEntity();
                    
                    // 明细字段映射
                    orderDetail.setPo_order_id(bPoOrderEntity.getId());
                    orderDetail.setGoods_code(contractDetail.getGoods_code());
                    orderDetail.setGoods_id(contractDetail.getGoods_id());
                    orderDetail.setGoods_name(contractDetail.getGoods_name());
                    orderDetail.setSku_id(contractDetail.getSku_id());
                    orderDetail.setSku_name(contractDetail.getSku_name());
                    orderDetail.setSku_code(contractDetail.getSku_code());
                    orderDetail.setUnit_id(contractDetail.getUnit_id());

                    orderDetail.setOrigin(contractDetail.getOrigin());
                    orderDetail.setQty(contractDetail.getQty());
                    orderDetail.setPrice(contractDetail.getPrice());
                    orderDetail.setTax_rate(contractDetail.getTax_rate());
                    
                    // 重新计算明细金额和税额
                    BigDecimal qty = contractDetail.getQty() != null ? contractDetail.getQty() : BigDecimal.ZERO;
                    BigDecimal price = contractDetail.getPrice() != null ? contractDetail.getPrice() : BigDecimal.ZERO;
                    BigDecimal taxRate = contractDetail.getTax_rate() != null ? contractDetail.getTax_rate() : BigDecimal.ZERO;
                    
                    // 计算明细金额：数量 * 单价
                    BigDecimal amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                    orderDetail.setAmount(amount);
                    
                    // 计算明细税额：getTax_rate() * getAmount() / 100
                    BigDecimal taxAmount = taxRate.multiply(amount).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    orderDetail.setTax_amount(taxAmount);
                    
                    // 插入订单明细
                    int detailResult = bPoOrderDetailMapper.insert(orderDetail);
                    if (detailResult == 0) {
                        throw new BusinessException("自动创建采购订单明细失败");
                    }
                }
                
                log.debug("自动创建采购订单明细成功，明细数量：{}", BPoContractVo.getDetailListData().size());
            }

            commonTotalService.reCalculateAllTotalDataByPoOrderId(bPoOrderEntity.getId());

//        } catch (Exception e) {
//            log.error("自动创建采购订单失败：{}", e.getMessage(), e);
//            throw new BusinessException("自动创建采购订单失败：" + e.getMessage());
//        }
    }
}
