package com.xinyirun.scm.core.system.serviceimpl.business.so.socontract;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractAttachEntity;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractAttachVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;
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
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.so.socontract.IBSoContractService;
import com.xinyirun.scm.core.system.service.business.so.socontract.IBSoContractTotalService;
import com.xinyirun.scm.core.system.service.business.so.soorder.IBSoOrderTotalService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.total.CommonSoTotalServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BSoContractAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BSoOrderAutoCodeServiceImpl;
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
 * 销售合同表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Slf4j
@Service
public class BSoContractServiceImpl extends BaseServiceImpl<BSoContractMapper, BSoContractEntity> implements IBSoContractService {

    @Autowired
    private BSoContractMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BSoContractDetailMapper bSoContractDetailMapper;

    @Autowired
    private BSoContractAutoCodeServiceImpl bSoContractAutoCodeService;

    @Autowired
    private BSoContractAttachMapper bSoContractAttachMapper;

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
    private BSoOrderMapper bSoOrderMapper;

    @Autowired
    private BSoOrderDetailMapper bSoOrderDetailMapper;

    @Autowired
    private BSoOrderAutoCodeServiceImpl bSoOrderAutoCodeService;

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
    private IBSoContractTotalService iBSoContractTotalService;

    @Autowired
    private IBSoOrderTotalService iBSoOrderTotalService;

    @Autowired
    private CommonSoTotalServiceImpl commonTotalService;

    @Autowired
    private ICommonSoTotalService iCommonSoTotalService;

    /**
     * 销售合同  新增
     * @param bSoContractVo
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoContractVo> insert(BSoContractVo bSoContractVo) {
        // 1. 保存主表信息
        BSoContractEntity bSoContractEntity = saveMainEntity(bSoContractVo);
        // 2. 保存明细信息
        saveDetailList(bSoContractVo, bSoContractEntity);
        // 3. 保存附件信息
        saveAttach(bSoContractVo, bSoContractEntity);
        // 4. 设置返回ID
        bSoContractVo.setId(bSoContractEntity.getId());
        // 5. 更新合同财务数据
        iCommonSoTotalService.reCalculateAllTotalDataBySoContractId(bSoContractEntity.getId());

        return InsertResultUtil.OK(bSoContractVo);
    }
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BSoContractVo bSoContractVo) {
        CheckResultAo cr = checkLogic(bSoContractVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    /**
     * 保存主表信息
     */
    private BSoContractEntity saveMainEntity(BSoContractVo bSoContractVo) {
        BSoContractEntity bSoContractEntity = new BSoContractEntity();
        BeanUtils.copyProperties(bSoContractVo, bSoContractEntity);
        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ONE);
        bSoContractEntity.setCode(bSoContractAutoCodeService.autoCode().getCode());
        bSoContractEntity.setIs_del(Boolean.FALSE);
        bSoContractEntity.setBpm_process_name("新增销售合同审批");
        if (StringUtils.isEmpty(bSoContractEntity.getContract_code())){
            bSoContractEntity.setContract_code(bSoContractEntity.getCode());
        }
        bSoContractEntity.setAuto_create_order(true);
        List<BSoContractDetailVo> detailListData = bSoContractVo.getDetailListData();
        calculateContractAmounts(detailListData, bSoContractEntity);
        int bSalContract = mapper.insert(bSoContractEntity);
        if (bSalContract == 0){
            throw new BusinessException("新增失败");
        }
        return bSoContractEntity;
    }
    /**
     * 保存明细信息
     */
    private void saveDetailList(BSoContractVo bSoContractVo, BSoContractEntity bSoContractEntity) {
        List<BSoContractDetailVo> detailListData = bSoContractVo.getDetailListData();
        for (BSoContractDetailVo detailListDatum : detailListData) {
            BSoContractDetailEntity bSoContractDetailEntity = new BSoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoContractDetailEntity);
            bSoContractDetailEntity.setSo_contract_id(bSoContractEntity.getId());
            bSoContractDetailEntity.setAmount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice()).setScale(2, RoundingMode.HALF_UP));
            bSoContractDetailEntity.setTax_amount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice())
                            .multiply(detailListDatum.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));
            int bSalContractDetail = bSoContractDetailMapper.insert(bSoContractDetailEntity);
            if (bSalContractDetail == 0){
                throw new BusinessException("新增失败");
            }
        }
    }
    /**
     * 保存附件信息
     */
    private void saveAttach(BSoContractVo bSoContractVo, BSoContractEntity bSoContractEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
        BSoContractAttachEntity bSoContractAttachEntity = insertFile(fileEntity, bSoContractVo, new BSoContractAttachEntity());
        bSoContractAttachEntity.setSo_contract_id(bSoContractEntity.getId());
        int insert = bSoContractAttachMapper.insert(bSoContractAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 销售合同  新增
     *
     * @param bSoContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoContractVo> startInsert(BSoContractVo bSoContractVo) {
        // 1. 校验业务规则
        checkInsertLogic(bSoContractVo);
        
        // 2.保存销售合同
        InsertResultAo<BSoContractVo> insertResultAo = insert(bSoContractVo);

        // 3.启动审批流程
        startFlowProcess(bSoContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT);

        return insertResultAo;
    }

    @Override
    public IPage<BSoContractVo> selectPage(BSoContractVo searchCondition) {
        // 分页条件
        Page<BSoContractVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询销售合同page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取销售合同信息
     * @param id
     */
    @Override
    public BSoContractVo selectById(Integer id) {
        BSoContractVo bSoContractVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(bSoContractVo.getDoc_att_file());
        bSoContractVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_SO_CONTRACT_STATUS_FOUR.equals(bSoContractVo.getStatus()) || Objects.equals(bSoContractVo.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bSoContractVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            bSoContractVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                bSoContractVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            bSoContractVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            bSoContractVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (bSoContractVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(bSoContractVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            bSoContractVo.setProject(bProjectVo);
        }
        return bSoContractVo;
    }

    /**
     * 销售合同  更新
     *
     * @param bSoContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BSoContractVo bSoContractVo) {
        // 1. 校验业务规则
        checkUpdateLogic(bSoContractVo);
        
        // 2.保存销售合同
        UpdateResultAo<Integer> insertResultAo = update(bSoContractVo);

        // 3.启动审批流程
        startFlowProcess(bSoContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT);

        return insertResultAo;
    }

    /**
     * 更新销售合同信息
     *
     * @param bSoContractVo
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BSoContractVo bSoContractVo) {
        // 1. 更新主表信息
        BSoContractEntity bSoContractEntity = updateMainEntity(bSoContractVo);
        // 2. 更新明细信息
        updateDetailList(bSoContractVo, bSoContractEntity);
        // 3. 更新附件信息
        updateAttach(bSoContractVo, bSoContractEntity);
        // 4. 更新合同财务数据
        iCommonSoTotalService.reCalculateAllTotalDataBySoContractId(bSoContractEntity.getId());

        return UpdateResultUtil.OK(1);
    }
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BSoContractVo bSoContractVo) {
        CheckResultAo cr = checkLogic(bSoContractVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    /**
     * 更新主表信息
     */
    private BSoContractEntity updateMainEntity(BSoContractVo bSoContractVo) {
        BSoContractEntity bSoContractEntity = (BSoContractEntity) BeanUtilsSupport.copyProperties(bSoContractVo, BSoContractEntity.class);
        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ONE);
        bSoContractEntity.setBpm_process_name("更新销售合同审批");
        List<BSoContractDetailVo> detailListData = bSoContractVo.getDetailListData();
        calculateContractAmounts(detailListData, bSoContractEntity);
        int updCount = mapper.updateById(bSoContractEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bSoContractEntity;
    }
    /**
     * 更新明细信息
     */
    private void updateDetailList(BSoContractVo bSoContractVo, BSoContractEntity bSoContractEntity) {
        List<BSoContractDetailVo> detailListData = bSoContractVo.getDetailListData();
        bSoContractDetailMapper.deleteBySoContractId(bSoContractEntity.getId());
        for (BSoContractDetailVo detailListDatum : detailListData) {
            BSoContractDetailEntity bSoContractDetailEntity = new BSoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoContractDetailEntity);
            bSoContractDetailEntity.setSo_contract_id(bSoContractEntity.getId());
            bSoContractDetailEntity.setAmount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice()).setScale(2, RoundingMode.HALF_UP));
            bSoContractDetailEntity.setTax_amount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice())
                            .multiply(detailListDatum.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));
            int bSalContractDetail = bSoContractDetailMapper.insert(bSoContractDetailEntity);
            if (bSalContractDetail == 0){
                throw new BusinessException("新增销售合同明细表-商品失败");
            }
        }
    }
    /**
     * 更新附件信息
     */
    private void updateAttach(BSoContractVo bSoContractVo, BSoContractEntity bSoContractEntity) {
        BSoContractAttachVo bSoContractAttachVo = bSoContractAttachMapper.selectBySoContractId(bSoContractEntity.getId());
        if (bSoContractAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bSoContractEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
            BSoContractAttachEntity bSoContractAttachEntity =(BSoContractAttachEntity) BeanUtilsSupport.copyProperties(bSoContractAttachVo, BSoContractAttachEntity.class);
            insertFile(fileEntity, bSoContractVo, bSoContractAttachEntity);
            bSoContractAttachEntity.setSo_contract_id(bSoContractEntity.getId());
            int update = bSoContractAttachMapper.updateById(bSoContractAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bSoContractEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
            BSoContractAttachEntity bSoContractAttachEntity = new BSoContractAttachEntity();
            insertFile(fileEntity, bSoContractVo, bSoContractAttachEntity);
            bSoContractAttachEntity.setSo_contract_id(bSoContractEntity.getId());
            int insert = bSoContractAttachMapper.insert(bSoContractAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 删除销售合同信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BSoContractVo> searchCondition) {
        for (BSoContractVo bSoContractVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(bSoContractVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());
            bSoContractEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bSoContractEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 按销售合同合计
     *
     * @param searchCondition
     */
    @Override
    public BSoContractVo querySum(BSoContractVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 销售合同校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BSoContractVo bean, String checkType) {
        List<BSoContractVo> bSoContractVos = mapper.validateDuplicateContractCode(bean);
        BSoContractEntity bSoContractEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("请添加商品数据！");
                }

                Map<String, Long> collect = bean.getDetailListData()
                        .stream()
                        .map(BSoContractDetailVo::getSku_code)
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
                if (CollectionUtil.isNotEmpty(bSoContractVos)){
                    String err = "合同编号重复：系统检测到" + bean.getContract_code() + "已被使用，请输入其他编号继续操作";
                    return CheckResultUtil.NG(err);
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoContractEntity = mapper.selectById(bean.getId());
                if (bSoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_ZERO) && !Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，销售合同[%s]不是待审批,驳回状态,无法修改",bSoContractEntity.getCode()));

                }

                if (bean.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect2 = bean.getDetailListData()
                        .stream()
                        .map(BSoContractDetailVo::getSku_code)
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
                if (CollectionUtil.isNotEmpty(bSoContractVos)){
                    String err = "合同编号重复：系统检测到" + bean.getContract_code() + "已被使用，请输入其他编号继续操作";
                    return CheckResultUtil.NG(err);
                }

                break;
            // 删除校验
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoContractEntity = mapper.selectById(bean.getId());
                if (bSoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_ZERO) && !Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，销售合同[%s]不是待审批,驳回状态,无法删除",bSoContractEntity.getCode()));
                }

                List<BSoOrderVo> delBArPayVo = bSoOrderMapper.selectBySoContractId(bean.getId());
                if (CollectionUtil.isNotEmpty(delBArPayVo)) {
                    return CheckResultUtil.NG("删除失败，存在销售订单");
                }
                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoContractEntity = mapper.selectById(bean.getId());
                if (bSoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE) || Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售合同[%s]无法重复作废",bSoContractEntity.getCode()));
                }
                if (!Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售合同[%s]审核中，无法作废",bSoContractEntity.getCode()));
                }

                List<BSoOrderVo> cancelOrderVos = bSoOrderMapper.selectBySoContractIdNotByStatus(bean.getId(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE);
                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
                    return CheckResultUtil.NG(String.format("作废失败，销售单号[%s]数据未作废，请先完成该销售订单的作废。",cancelOrderVos.stream().map(BSoOrderVo::getCode).collect(Collectors.toList())));
                }
                break;
            // 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:

                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoContractEntity = mapper.selectById(bean.getId());
                if (bSoContractEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否审批通过
                if (Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，销售合同[%s]未进入执行状态",bSoContractEntity.getCode()));
                }

                List<BSoOrderVo> finishOrderVos = bSoOrderMapper.selectBySoContractIdNotByStatus(bean.getId(), DictConstant.DICT_B_SO_ORDER_STATUS_SIX);
                if (CollectionUtil.isNotEmpty(finishOrderVos)){
                    return CheckResultUtil.NG(String.format("系统检测到合同编号[%s]存在未完成的销售订单，请完成订单[%s]后再提交。",bean.getContract_code(),finishOrderVos.stream().findFirst().get().getCode()));
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BSoContractVo bean,String type){
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
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BSoContractVo bSoContractVo1){
        log.debug("====》审批流程创建成功，更新开始《====");
        BSoContractVo bSoContractVo = selectById(bSoContractVo1.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 客户：xxx，主体企业：xxx，合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("客户：", bSoContractVo.getCustomer_name());
        jsonObject.put("主体企业：", bSoContractVo.getSeller_name());
        jsonObject.put("合同金额:", bSoContractVo.getContract_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(bSoContractVo1.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bSoContractVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }
    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]审批流程通过，更新开始《====", bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setBpm_instance_id(bSoContractVo.getBpm_instance_id());
        bSoContractEntity.setBpm_instance_code(bSoContractVo.getBpm_instance_code());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程通过,更新结束《====", bSoContractVo.getId());

        /**
         * 根据审批后自动生成订单，自动生成的订单-已经审批
         */
        BSoContractVo vo = selectById(bSoContractVo.getId());
        if (vo.getAuto_create_order() != null && vo.getAuto_create_order()) {
            log.debug("====》开始自动创建销售订单《====");
            createAutoOrder(vo, bSoContractEntity);
            log.debug("====》自动创建销售订单完成《====");
        }

        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]审批流程拒绝，更新开始《====", bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_THREE);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程拒绝,更新结束《====", bSoContractVo.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]审批流程撤销，更新开始《====", bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ZERO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程撤销,更新结束《====", bSoContractVo.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]审批流程更新最新审批人，更新开始《====", bSoContractVo.getId());

        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());
        bSoContractEntity.setBpm_instance_id(bSoContractVo.getBpm_instance_id());
        bSoContractEntity.setBpm_instance_code(bSoContractVo.getBpm_instance_code());
        bSoContractEntity.setNext_approve_name(bSoContractVo.getNext_approve_name());
        int i = mapper.updateById(bSoContractEntity);

        log.debug("====》销售合同[{}]审批流程更新最新审批人,更新结束《====", bSoContractVo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public BSoContractVo getPrintInfo(BSoContractVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_SO_CONTRACT);
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
    public List<BSoContractVo> selectExportList(BSoContractVo param) {
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
    public BSoContractAttachEntity insertFile(SFileEntity fileEntity, BSoContractVo vo, BSoContractAttachEntity extra) {
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
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BSoContractVo bSoContractVo1){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BSoContractVo bSoContractVo = selectById(bSoContractVo1.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", bSoContractVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(bSoContractVo1.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bSoContractVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]审批流程通过，更新开始《====",bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setBpm_cancel_instance_id(bSoContractVo.getBpm_instance_id());
        bSoContractEntity.setBpm_cancel_instance_code(bSoContractVo.getBpm_instance_code());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程通过,更新结束《====",bSoContractVo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]作废审批流程拒绝，更新开始《====",bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CONTRACT);
        mCancelService.delete(mCancelVo);

        log.debug("====》销售合同[{}]作废审批流程拒绝,更新结束《====",bSoContractVo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]作废审批流程撤销，更新开始《====",bSoContractVo.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CONTRACT);
        mCancelService.delete(mCancelVo);

        log.debug("====》销售合同[{}]作废审批流程撤销,更新结束《====",bSoContractVo.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BSoContractVo bSoContractVo) {
        log.debug("====》销售合同[{}]作废审批流程更新最新审批人，更新开始《====",bSoContractVo.getId());

        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        bSoContractEntity.setBpm_cancel_instance_id(bSoContractVo.getBpm_instance_id());
        bSoContractEntity.setBpm_cancel_instance_code(bSoContractVo.getBpm_instance_code());
        bSoContractEntity.setNext_approve_name(bSoContractVo.getNext_approve_name());
        int i = mapper.updateById(bSoContractEntity);

        log.debug("====》销售合同[{}]作废审批流程更新最新审批人，更新结束《====",bSoContractVo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param bSoContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BSoContractVo bSoContractVo) {

        // 作废前check
        CheckResultAo cr = checkLogic(bSoContractVo, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoContractEntity bSoContractEntity = mapper.selectById(bSoContractVo.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
        fileEntity = insertCancelFile(fileEntity, bSoContractVo);

        bSoContractEntity.setBpm_cancel_process_name("作废销售合同审批");
        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_FOUR);
        int insert = mapper.updateById(bSoContractEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoContractEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CONTRACT);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(bSoContractVo.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(bSoContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> complete(BSoContractVo searchCondition) {
        // 完成前校验
        CheckResultAo cr = validateComplete(searchCondition);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());
        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_SIX);
        int update = mapper.updateById(bSoContractEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        return UpdateResultUtil.OK(update);
    }

    /**
     * 完成校验
     * @param contractVo
     * @return
     */
    @Override
    public CheckResultAo validateComplete(BSoContractVo contractVo) {
        if (contractVo.getId() == null) {
            return CheckResultUtil.NG("id不能为空");
        }

        BSoContractEntity bSoContractEntity = mapper.selectById(contractVo.getId());
        if (bSoContractEntity == null) {
            return CheckResultUtil.NG("销售合同不存在");
        }

        // 查询销售合同下是否存在未完成的销售订单
        List<BSoOrderVo> unfinishedOrders = bSoOrderMapper.selectUnfinishedOrdersBySoContractId(contractVo.getId());
        if (CollectionUtil.isNotEmpty(unfinishedOrders)) {
            List<String> orderCodes = unfinishedOrders.stream().map(BSoOrderVo::getCode).collect(Collectors.toList());
            return CheckResultUtil.NG(String.format("校验出错：销售合同管理，编号%s的数据存在尚未完成的销售订单[%s]。", 
                    bSoContractEntity.getCode(), String.join("、", orderCodes)));
        }

        return CheckResultUtil.OK();
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BSoContractVo vo) {
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
    public List<BSoContractImportVo> importData(List<BSoContractImportVo> beans) {
        List<BSoContractImportVo>  vos = new ArrayList<>();
        for (BSoContractImportVo vo : beans) {
            // 字典类value设置：类型、结算方式、结算单据类型、运输方式；以及主表类id设置：客户(必填)、主体企业(必填)、物料名称(必填)、规格(必填)
            setImportBean(vo);

            BSoContractEntity entity = new BSoContractEntity();
            entity.setCode(bSoContractAutoCodeService.autoCode().getCode());
            entity.setType(vo.getType());
            entity.setContract_code(vo.getContract_code());
            entity.setCustomer_id(vo.getCustomer_id());
            entity.setSeller_id(vo.getSeller_id());
            entity.setSign_date(vo.getSign_date());
            entity.setExpiry_date(vo.getExpiry_date());
            entity.setDelivery_date(vo.getDelivery_date());
            entity.setDelivery_type(vo.getDelivery_type());
            entity.setSettle_type(vo.getSettle_type());
            entity.setBill_type(vo.getBill_type());
            entity.setPayment_type(DictConstant.DICT_B_SO_CONTRACT_PAYMENT_TYPE_ONE);
            entity.setDelivery_location(vo.getDelivery_location());
            entity.setAuto_create_order(Boolean.TRUE);
            entity.setRemark(vo.getRemark());
            entity.setIs_del(false);
            entity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ZERO); // 待审批
            // 计算合同总金额：商品数量 * 商品单价
            entity.setContract_amount_sum(vo.getQty().multiply(vo.getPrice()));
            // 总销售数量：商品数量
            entity.setContract_total(vo.getQty());
            // 总税额：税率 * 合同总金额 / 100
            entity.setTax_amount_sum(vo.getTax_rate().multiply(entity.getContract_amount_sum()).divide(new BigDecimal(100)));
            // 参入主标
            mapper.insert(entity);

            // 更新合同财务数据
            iCommonSoTotalService.reCalculateAllTotalDataBySoContractId(entity.getId());


            // 处理从表
            BSoContractDetailEntity detailEntity = new BSoContractDetailEntity();
            detailEntity.setSo_contract_id(entity.getId());
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
            bSoContractDetailMapper.insert(detailEntity);

            vos.add(vo);
        }
        return vos;
    }

    /**
     * excel 导入校验
     * 校验客户是否存在
     * 
     * @param vo 当前合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 返回true表示重复，false表示不重复
     */
    public boolean checkCustomerIsExists(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 创建MEnterpriseVo对象用于查询
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getCustomer_name());
        mEnterpriseVo.setIsCustomer(true); // 设置为客户查询
        
        // 调用validateDuplicateName方法查询是否存在
        List<MEnterpriseVo> result = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);
        
        // 判断返回结果
        // 如果size>=1，表示存在，返回true；否则返回false
        // null也视为不重复，返回false
        return result != null && result.size() >= 1;
    }

    /**
     * excel 导入校验
     * 校验客户是否存在
     *
     * @param vo 当前合同导入对象
     * @param vos 所有合同导入对象列表
     * @return 返回true表示重复，false表示不重复
     */
    public boolean checkSellerAndSysCompanyIsExists(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 创建MEnterpriseVo对象用于查询
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getSeller_name());
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
    public Boolean checDictExistByNameType (BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_TYPE, vo.getType_name());
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
    public Boolean checDictExistByNameSettleType (BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_SETTLE_TYPE, vo.getSettle_type_name());
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
    public Boolean checDictExistByNameBillType (BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_BILL_TYPE, vo.getBill_type_name());
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
    public Boolean checDictExistByNamedeliveryType (BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        if(!StringUtils.isNotBlank(vo.getDelivery_type_name())) {
            return true; // 非必填，如果没有填写，直接返回true
        }
        // 校验
        SDictDataVo selectByName = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_DELIVERY_TYPE, vo.getDelivery_type_name());
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
    public Boolean checkContractNoDuplicate(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
        // 创建参数对象
        BSoContractVo contractVo = new BSoContractVo();
        contractVo.setContract_code(vo.getContract_code());

        // 调用mapper的校验方法
        List<BSoContractVo> bSoContractVos = mapper.validateDuplicateContractCode(contractVo);

        /**
         * 判断返回结果，size>=1表示数据存在，返回false，null或size=0表示不重复返回true
         */
        if( bSoContractVos == null || bSoContractVos.size() == 0) {
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
    public boolean checkGoodsNameIsExists(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
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
    public boolean checkGoodsSpecNameIsExists(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
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
    public boolean checkTaxData(BSoContractImportVo vo, ArrayList<BSoContractImportVo> vos) {
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
     * 主表类id设置：客户(必填)、主体企业(必填)、物料名称(必填)、规格(必填)
     * @param vo
     * @return
     */
    public void setImportBean (BSoContractImportVo vo) {
        // 类型（必填）：字典的label->value
        SDictDataVo typeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_TYPE, vo.getType_name());
        vo.setType(typeData.getDict_value());

        // 结算方式（必填）：字典的label->value
        SDictDataVo settleTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_SETTLE_TYPE, vo.getSettle_type_name());
        vo.setSettle_type(settleTypeData.getDict_value());

        // 结算单据类型（必填）：字典的label->value
        SDictDataVo billTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_BILL_TYPE, vo.getBill_type_name());
        vo.setBill_type(billTypeData.getDict_value());

        // 运输方式（非必填）：字典的label->value
        if (StringUtils.isNotBlank(vo.getDelivery_type_name())) {
            SDictDataVo deliveryTypeData = sDictDataMapper.getDetailByCodeAndDictLabel(DictConstant.DICT_B_SO_CONTRACT_DELIVERY_TYPE, vo.getDelivery_type_name());
            vo.setDelivery_type(deliveryTypeData.getDict_value());
        } else {
            vo.setDelivery_type(null); // 如果没有填写，设置为null
        }

        // 客户（必填）：查询企业表，获取id
        MEnterpriseVo mEnterpriseVo = new MEnterpriseVo();
        mEnterpriseVo.setName(vo.getCustomer_name());
        mEnterpriseVo.setIsCustomer(true); // 设置为客户查询
        List<MEnterpriseVo> customer = mEnterpriseMapper.validateDuplicateName(mEnterpriseVo);
        vo.setCustomer_id(customer.get(0).getId());

        // 主体企业(必填):查询企业表，获取id
        MEnterpriseVo seller = new MEnterpriseVo();
        seller.setName(vo.getSeller_name());
        seller.setIsSysCompany(true); // 设置为主体企业查询
        List<MEnterpriseVo> result = mEnterpriseMapper.validateDuplicateName(seller);
        vo.setSeller_id(result.get(0).getId());

        // 物料名称（必填）：查询商品表，获取id
        List<MGoodsSpecEntity> sku = mGoosSpecMapper.selectByName(vo.getGoods_name(), vo.getSku_name());
        vo.setSku_code(sku.get(0).getCode());
        vo.setSku_id(sku.get(0).getId());
    }

    /**
     * 计算合同总金额、总销售数量（吨）、总税额
     * @param detailListData 合同明细数据
     * @param bSoContractEntity 销售合同实体对象
     */
    private void calculateContractAmounts(List<BSoContractDetailVo> detailListData, BSoContractEntity bSoContractEntity) {
        BigDecimal contractAmountSum = BigDecimal.ZERO;
        BigDecimal contractTotal = BigDecimal.ZERO;
        BigDecimal taxAmountSum = BigDecimal.ZERO;
        
        if (detailListData != null && !detailListData.isEmpty()) {
            for (BSoContractDetailVo detail : detailListData) {
                BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
                
                // 计算合同总金额：sum(明细.qty * 明细.price)
                BigDecimal amount = qty.multiply(price);
                contractAmountSum = contractAmountSum.add(amount);
                
                // 计算总销售数量（吨）：sum(明细.qty)
                contractTotal = contractTotal.add(qty);
                  // 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
                BigDecimal taxAmount = qty.multiply(price).multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                taxAmountSum = taxAmountSum.add(taxAmount);
            }
        }
        
        // 设置计算结果到实体对象
        bSoContractEntity.setContract_amount_sum(contractAmountSum);
        bSoContractEntity.setContract_total(contractTotal);
        bSoContractEntity.setTax_amount_sum(taxAmountSum);
    }

    /**
     * 自动创建销售订单
     * @param bSoContractVo 销售合同查询条件
     * @param bSoContractEntity 销售合同实体
     */
    private void createAutoOrder(BSoContractVo bSoContractVo, BSoContractEntity bSoContractEntity) {
//        try {
            // 1. 创建销售订单主表数据
            BSoOrderEntity bSoOrderEntity = new BSoOrderEntity();
            
            // 基本字段映射
            bSoOrderEntity.setCode(bSoOrderAutoCodeService.autoCode().getCode());
            bSoOrderEntity.setCustomer_name(bSoContractEntity.getCustomer_name());
            bSoOrderEntity.setCustomer_code(bSoContractEntity.getCustomer_code());
            bSoOrderEntity.setCustomer_id(bSoContractEntity.getCustomer_id());
            bSoOrderEntity.setSo_contract_code(bSoContractEntity.getContract_code());
            bSoOrderEntity.setProject_code(bSoContractEntity.getProject_code());
            bSoOrderEntity.setSeller_name(bSoContractEntity.getSeller_name());
            bSoOrderEntity.setSeller_code(bSoContractEntity.getSeller_code());
            bSoOrderEntity.setSeller_id(bSoContractEntity.getSeller_id());
            bSoOrderEntity.setOrder_date(LocalDateTime.now());
            bSoOrderEntity.setDelivery_date(bSoContractEntity.getDelivery_date());
            bSoOrderEntity.setDelivery_type(bSoContractEntity.getDelivery_type());
            bSoOrderEntity.setSettle_type(bSoContractEntity.getSettle_type());
            bSoOrderEntity.setBill_type(bSoContractEntity.getBill_type());
            bSoOrderEntity.setPayment_type(bSoContractEntity.getPayment_type());
            bSoOrderEntity.setDelivery_location(bSoContractEntity.getDelivery_location());
            bSoOrderEntity.setRemark(bSoContractEntity.getRemark());
            bSoOrderEntity.setSo_contract_id(bSoContractEntity.getId()); // 关联销售合同ID
            
            // 计算订单总金额、总数量、总税额
            BigDecimal orderAmountSum = BigDecimal.ZERO;
            BigDecimal orderTotal = BigDecimal.ZERO;
            BigDecimal taxAmountSum = BigDecimal.ZERO;
            List<BSoContractDetailVo> detailListData = bSoContractVo.getDetailListData();
            for (BSoContractDetailVo detail : detailListData) {
                    BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                    BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                    BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
                    
                    // 计算订单总金额：商品数量 * 商品单价
                    BigDecimal amount = qty.multiply(price);
                    orderAmountSum = orderAmountSum.add(amount);
                    
                    // 总销售数量：商品数量
                    orderTotal = orderTotal.add(qty);
                    
                    // 总税额：税率 * 合同总金额 / 100
                    BigDecimal taxAmount = taxRate.multiply(amount).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    taxAmountSum = taxAmountSum.add(taxAmount);
                }

            // 设置订单状态为已审批（跳过审批流程）
            bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
            bSoOrderEntity.setIs_del(Boolean.FALSE);
            bSoOrderEntity.setBpm_instance_id(bSoContractVo.getBpm_instance_id());
            bSoOrderEntity.setBpm_instance_code(bSoContractVo.getBpm_instance_code());
            bSoOrderEntity.setBpm_process_name("自动生成销售订单（已审批）");
            bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
            
            // 插入订单主表
            int orderResult = bSoOrderMapper.insert(bSoOrderEntity);

            // 更新订单财务数据
//            iBSoOrderTotalService.calcOrderAmountAndTax(bSoOrderEntity.getId());

            if (orderResult == 0) {
                throw new BusinessException("自动创建销售订单失败");
            }
            
            log.debug("自动创建销售订单成功，订单ID：{}, 订单编号：{}", bSoOrderEntity.getId(), bSoOrderEntity.getCode());
              // 2. 创建销售订单明细数据
            if (bSoContractVo.getDetailListData() != null && !bSoContractVo.getDetailListData().isEmpty()) {
                for (BSoContractDetailVo contractDetail : bSoContractVo.getDetailListData()) {
                    BSoOrderDetailEntity orderDetail = new BSoOrderDetailEntity();
                    
                    // 明细字段映射
                    orderDetail.setSo_order_id(bSoOrderEntity.getId());
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
                    int detailResult = bSoOrderDetailMapper.insert(orderDetail);
                    if (detailResult == 0) {
                        throw new BusinessException("自动创建销售订单明细失败");
                    }
                }
                
                log.debug("自动创建销售订单明细成功，明细数量：{}", bSoContractVo.getDetailListData().size());
            }

            commonTotalService.reCalculateAllTotalDataBySoOrderId(bSoOrderEntity.getId());

//        } catch (Exception e) {
//            log.error("自动创建销售订单失败：{}", e.getMessage(), e);
//            throw new BusinessException("自动创建销售订单失败：" + e.getMessage());
//        }
    }
}