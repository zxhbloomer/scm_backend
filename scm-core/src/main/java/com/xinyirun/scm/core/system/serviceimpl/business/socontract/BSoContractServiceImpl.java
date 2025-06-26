package com.xinyirun.scm.core.system.serviceimpl.business.socontract;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractEntity;
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
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractAttachVo;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractVo;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
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
import com.xinyirun.scm.core.system.mapper.business.socontract.BSoContractAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.socontract.BSoContractDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.socontract.BSoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.soorder.BSoOrderMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.socontract.IBSoContractService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BSoContractAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class BSoContractServiceImpl extends ServiceImpl<BSoContractMapper, BSoContractEntity> implements IBSoContractService {

    @Autowired
    private BSoContractMapper mapper;

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
    private MCancelService mCancelService;

    /**
     * 销售合同  新增
     * @param soContractVo
     */
    public InsertResultAo<SoContractVo> insert(SoContractVo soContractVo) {

        // 插入前check
        CheckResultAo cr = checkLogic(soContractVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.保存基础信息
        BSoContractEntity bSoContractEntity = new BSoContractEntity();
        BeanUtils.copyProperties(soContractVo, bSoContractEntity);
        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ONE);
        bSoContractEntity.setCode(bSoContractAutoCodeService.autoCode().getCode());

        /** 未删除 */
        bSoContractEntity.setIs_del(Boolean.FALSE);
        /** 审批流程名称 */
        bSoContractEntity.setBpm_process_name("新增销售合同审批");


        if (StringUtils.isEmpty(bSoContractEntity.getContract_code())){
            bSoContractEntity.setContract_code(bSoContractEntity.getCode());
        }

        if (bSoContractEntity.getAuto_create_order()==null){
            bSoContractEntity.setAuto_create_order("1");
        }
        int bPurContract = mapper.insert(bSoContractEntity);
        if (bPurContract == 0){
            throw new BusinessException("新增失败");
        }


        // 2.保存销售合同明细表-商品
        List<SoContractDetailVo> detailListData = soContractVo.getDetailListData();
        for (SoContractDetailVo detailListDatum : detailListData) {
            BSoContractDetailEntity BSoContractDetailEntity = new BSoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, BSoContractDetailEntity);
            BSoContractDetailEntity.setSo_contract_id(bSoContractEntity.getId());
            int bPurContractDetail = bSoContractDetailMapper.insert(BSoContractDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增失败");
            }
        }

        // 3.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);

        BSoContractAttachEntity BSoContractAttachEntity = insertFile(fileEntity, soContractVo, new BSoContractAttachEntity());
        BSoContractAttachEntity.setSo_contract_id(bSoContractEntity.getId());
        int insert = bSoContractAttachMapper.insert(BSoContractAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        soContractVo.setId(bSoContractEntity.getId());
        return InsertResultUtil.OK(soContractVo);
    }

    /**
     * 销售合同  新增
     *
     * @param soContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<SoContractVo> startInsert(SoContractVo soContractVo) {
        // 1.保存销售合同
        InsertResultAo<SoContractVo> insertResultAo = insert(soContractVo);

        // 2.启动审批流程
        startFlowProcess(soContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT);

        return insertResultAo;
    }

    @Override
    public IPage<SoContractVo> selectPage(SoContractVo searchCondition) {
        // 分页条件
        Page<SoContractVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取销售合同信息
     * @param id
     */
    @Override
    public SoContractVo selectById(Integer id) {
        SoContractVo SoContractVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(SoContractVo.getDoc_att_file());
        SoContractVo.setDoc_att_files(doc_att_files);
        return SoContractVo;
    }

    /**
     * 销售合同  新增
     *
     * @param soContractVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(SoContractVo soContractVo) {
        // 1.保存销售合同
        UpdateResultAo<Integer> insertResultAo = update(soContractVo);

        // 2.启动审批流程
        startFlowProcess(soContractVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT);

        return insertResultAo;
    }

    /**
     * 更新销售合同信息
     *
     * @param soContractVo
     */
    public UpdateResultAo<Integer> update(SoContractVo soContractVo) {

        // 插入前check
        CheckResultAo cr = checkLogic(soContractVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoContractEntity BSoContractEntity = (BSoContractEntity) BeanUtilsSupport.copyProperties(soContractVo, BSoContractEntity.class);
        BSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_ONE);
        int updCount = mapper.updateById(BSoContractEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        /** 审批流程名称 */
        BSoContractEntity.setBpm_process_name("更新销售合同审批");

        // 2.保存销售合同明细表-商品 全删全增
        bSoContractDetailMapper.delete(new LambdaQueryWrapper<BSoContractDetailEntity>()
                .eq(BSoContractDetailEntity :: getSo_contract_id, BSoContractEntity.getId()));
        List<SoContractDetailVo> detailListData = soContractVo.getDetailListData();
        for (SoContractDetailVo detailListDatum : detailListData) {
            BSoContractDetailEntity BSoContractDetailEntity = new BSoContractDetailEntity();
            BeanUtils.copyProperties(detailListDatum, BSoContractDetailEntity);
            BSoContractDetailEntity.setSo_contract_id(BSoContractEntity.getId());
            int bPurContractDetail = bSoContractDetailMapper.insert(BSoContractDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增购合同明细表-商品失败");
            }
        }

        // 3.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(BSoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);

        SoContractAttachVo soContractAttachVo = bSoContractAttachMapper.selBySoContractId(BSoContractEntity.getId());
        BSoContractAttachEntity bSoContractAttachEntity = (BSoContractAttachEntity) BeanUtilsSupport.copyProperties(soContractAttachVo, BSoContractAttachEntity.class);

        insertFile(fileEntity, soContractVo, bSoContractAttachEntity);
        bSoContractAttachEntity.setSo_contract_id(BSoContractEntity.getId());
        int insert = bSoContractAttachMapper.updateById(bSoContractAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增附件信息失败");
        }

        return UpdateResultUtil.OK(updCount);
    }

    /**
     * 删除销售合同信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<SoContractVo> searchCondition) {
        for (SoContractVo soContractVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(soContractVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BSoContractEntity bSoContractEntity = mapper.selectById(soContractVo.getId());
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
    public SoContractVo querySum(SoContractVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 销售合同校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(SoContractVo bean, String checkType) {
        List<SoContractVo> soContractVos = mapper.validateDuplicateContractCode(bean);
        BSoContractEntity bSoContractEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect = bean.getDetailListData()
                        .stream()
                        .map(SoContractDetailVo::getSku_code)
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
                if (CollectionUtil.isNotEmpty(soContractVos)){
                    String err = "合同编号重复：系统检测到“" + bean.getContract_code() + "”已被使用，请输入其他编号继续操作";
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
                        .map(SoContractDetailVo::getSku_code)
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
                if (CollectionUtil.isNotEmpty(soContractVos)){
                    String err = "合同编号重复：系统检测到“" + bean.getContract_code() + "”已被使用，请输入其他编号继续操作";
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
                    return CheckResultUtil.NG(String.format("修改失败，销售合同[%s]不是待审批,驳回状态,无法修改",bSoContractEntity.getCode()));
                }

                List<SoOrderVo> delBApPayVo = bSoOrderMapper.selectBySoContractId(bean.getId());
                if (CollectionUtil.isNotEmpty(delBApPayVo)) {
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

                List<SoOrderVo> cancelOrderVos = bSoOrderMapper.selectBySoContractIdNotByStatus(bean.getId(), DictConstant.DICT_B_SO_ORDER_STATUS_FIVE);
                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
                    return CheckResultUtil.NG("作废失败，销售单号"+cancelOrderVos.stream().map(SoOrderVo::getCode).collect(Collectors.toList())+"数据未作废，请先完成该采购合同的作废。");
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

                // 是否完成状态
                if (Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，采购合同[%s]未进入执行状态",bSoContractEntity.getCode()));
                }

                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(SoContractVo bean,String type){
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

            // 启动出库计划审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    /**
     *  审批流程回调
     *  审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(SoContractVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        SoContractVo soContractVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("合同金额:", soContractVo.getContract_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(soContractVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }


    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(SoContractVo searchCondition) {
        log.debug("====》销售合同审批流程通过，更新审核状态开始《====");
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同审批流程通过,更新审核状态结束《====");
        return UpdateResultUtil.OK(i);

    }

    /**
     * 销售合同审批流程拒绝 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(SoContractVo searchCondition) {
        log.debug("====》销售合同审批流程拒绝，更新审核状态开始《====");
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_THREE);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同审批流程拒绝,更新审核状态结束《====");
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_PO_CONTRACT_STATUS_ZERO);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());
        bSoContractEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bSoContractEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bSoContractEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bSoContractEntity);

        log.debug("====》销售合同[{}]审批流程更新最新审批人,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public SoContractVo getPrintInfo(SoContractVo searchCondition) {
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
    public List<SoContractVo> selectExportList(SoContractVo param) {
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
    public BSoContractAttachEntity insertFile(SFileEntity fileEntity, SoContractVo vo, BSoContractAttachEntity extra) {
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
            extra.setFour_file(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setFour_file(null);
        }

        return extra;
    }


    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(SoContractVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        SoContractVo soContractVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", soContractVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(soContractVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        bSoContractEntity.setStatus(DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE);
        bSoContractEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoContractEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售合同[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

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

        log.debug("====》销售合同[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

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

        log.debug("====》销售合同[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(SoContractVo searchCondition) {
        log.debug("====》销售合同[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());
        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        bSoContractEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bSoContractEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bSoContractEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bSoContractEntity);

        log.debug("====》销售合同[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }


    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(SoContractVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoContractEntity bSoContractEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoContractEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CONTRACT);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

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
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, SoContractVo vo) {
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
}
