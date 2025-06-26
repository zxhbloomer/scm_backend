package com.xinyirun.scm.core.system.serviceimpl.business.soorder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderAttachVo;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderDetailVo;
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
import com.xinyirun.scm.core.system.mapper.business.soorder.BSoOrderAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.soorder.BSoOrderDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.soorder.BSoOrderMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.soorder.IBSoOrderService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BSoOrderAutoCodeServiceImpl;
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
 * 销售订单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Slf4j
@Service
public class BSoOrderServiceImpl extends ServiceImpl<BSoOrderMapper, BSoOrderEntity> implements IBSoOrderService {

    @Autowired
    private BSoOrderMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BSoOrderAttachMapper bSoOrderAttachMapper;

    @Autowired
    private BSoOrderDetailMapper bSoOrderDetailMapper;

    @Autowired
    private BSoOrderAutoCodeServiceImpl bSoOrderAutoCodeService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private MCancelService mCancelService;

    /**
     * 获取销售订单信息
     * @param id
     */
    @Override
    public SoOrderVo selectById(Integer id) {
        SoOrderVo soOrderVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(soOrderVo.getDoc_att_file());
        soOrderVo.setDoc_att_files(doc_att_files);
        return soOrderVo;
    }

    /**
     * 销售订单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<SoOrderVo> startInsert(SoOrderVo searchCondition) {
        // 1.保存销售订单
        InsertResultAo<SoOrderVo> insertResultAo = insert(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER);
        return insertResultAo;
    }

    /**
     * 销售合同  新增
     * @param soOrderVo
     */
    public InsertResultAo<SoOrderVo> insert(SoOrderVo soOrderVo) {

        // 插入前check
        CheckResultAo cr = checkLogic(soOrderVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.保存基础信息
        BSoOrderEntity bSoOrderEntity = new BSoOrderEntity();
        BeanUtils.copyProperties(soOrderVo, bSoOrderEntity);
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ONE);
        bSoOrderEntity.setCode(bSoOrderAutoCodeService.autoCode().getCode());

        /** 未删除 */
        bSoOrderEntity.setIs_del(Boolean.FALSE);

        /** 审批流程名称 */
        bSoOrderEntity.setBpm_process_name("新增销售订单审批");


        int bPurContract = mapper.insert(bSoOrderEntity);
        if (bPurContract == 0){
            throw new BusinessException("新增失败");
        }

        // 2.保存销售订单明细表-商品
        List<SoOrderDetailVo> detailListData = soOrderVo.getDetailListData();
        for (SoOrderDetailVo detailListDatum : detailListData) {
            BSoOrderDetailEntity bSoOrderDetailEntity = new BSoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoOrderDetailEntity);
            bSoOrderDetailEntity.setSo_order_id(bSoOrderEntity.getId());
            int bPurContractDetail = bSoOrderDetailMapper.insert(bSoOrderDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增失败");
            }
        }

        // 3.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);

        BSoOrderAttachEntity bSoOrderAttachEntity = insertFile(fileEntity, soOrderVo, new BSoOrderAttachEntity());
        bSoOrderAttachEntity.setSo_order_id(bSoOrderEntity.getId());
        int insert = bSoOrderAttachMapper.insert(bSoOrderAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        soOrderVo.setId(bSoOrderEntity.getId());
        return InsertResultUtil.OK(soOrderVo);
    }

    @Override
    public CheckResultAo checkLogic(SoOrderVo searchCondition, String checkType) {
        BSoOrderEntity bSoOrderEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect = searchCondition.getDetailListData()
                        .stream()
                        .map(SoOrderDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result);
                }

                // 标准合同下推校验 只能下推一个订单
                if (ObjectUtil.isNotEmpty(searchCondition.getSo_contract_id())) {
                    List<SoOrderVo> soOrderVos = mapper.validateDuplicateContractId(searchCondition);
                    if (CollectionUtil.isNotEmpty(soOrderVos)) {
                        return CheckResultUtil.NG("标准合同已存在下推订单", soOrderVos);
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_ZERO) && !Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，销售订单[%s]不是待审批,驳回状态,无法修改",bSoOrderEntity.getCode()));
                }

                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect2 = searchCondition.getDetailListData()
                        .stream()
                        .map(SoOrderDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result2 = new ArrayList<>();
                collect2.forEach((k,v)->{
                    if(v>1) result2.add(k);
                });

                if (result2!=null&&result2.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result2);
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_ZERO) && !Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，销售订单[%s]不是待审批,驳回状态,无法删除",bSoOrderEntity.getCode()));
                }
                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_FIVE) || Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售订单[%s]无法重复作废",bSoOrderEntity.getCode()));
                }
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售订单[%s]审核中，无法作废",bSoOrderEntity.getCode()));
                }

//                List<BApVo> cancelOrderVos = bApMapper.selByPoCodeNotByStatus(searchCondition.getId(), DictConstant.DICT_B_AP_STATUS_FIVE);
//                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
//                    return CheckResultUtil.NG(String.format("作废失败，付款管理[%s]数据未作废，请先完成该付款管理的作废。",cancelOrderVos.stream().map(BApVo::getCode).collect(Collectors.toList())));
//                }
                break;
            // todo 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，销售订单[%s]未进入执行状态",bSoOrderEntity.getCode()));
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 列表查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<SoOrderVo> selectPage(SoOrderVo searchCondition) {
        // 分页条件
        Page<SoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 销售订单 统计
     *
     * @param searchCondition
     */
    @Override
    public SoOrderVo querySum(SoOrderVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 销售订单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(SoOrderVo searchCondition) {
        // 1.保存销售订单
        UpdateResultAo<Integer> insertResultAo = update(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER);

        return insertResultAo;
    }

    /**
     * 审批流程回调
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(SoOrderVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        SoOrderVo soOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("合同金额:", soOrderVo.getOrder_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(soOrderVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 更新销售订单信息
     *
     * @param soOrderVo
     */
    public UpdateResultAo<Integer> update(SoOrderVo soOrderVo) {

        // 插入前check
        CheckResultAo cr = checkLogic(soOrderVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoOrderEntity bSoOrderEntity = (BSoOrderEntity) BeanUtilsSupport.copyProperties(soOrderVo, BSoOrderEntity.class);
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ONE);
        int updCount = mapper.updateById(bSoOrderEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        /** 审批流程名称 */
        bSoOrderEntity.setBpm_process_name("更新销售订单审批");

        // 2.保存销售订单明细表-商品 全删全增
        bSoOrderDetailMapper.delete(new LambdaQueryWrapper<BSoOrderDetailEntity>()
                .eq(BSoOrderDetailEntity :: getSo_order_id, bSoOrderEntity.getId()));
        List<SoOrderDetailVo> detailListData = soOrderVo.getDetailListData();
        for (SoOrderDetailVo detailListDatum : detailListData) {
            BSoOrderDetailEntity bSoOrderDetailEntity = new BSoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoOrderDetailEntity);
            bSoOrderDetailEntity.setSo_order_id(bSoOrderEntity.getId());
            int bPurContractDetail = bSoOrderDetailMapper.insert(bSoOrderDetailEntity);
            if (bPurContractDetail == 0){
                throw new BusinessException("新增销售订单明细表-商品失败");
            }
        }

        // 3.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);

        SoOrderAttachVo soOrderAttachVo = bSoOrderAttachMapper.selBySoOrderId(bSoOrderEntity.getId());
        BSoOrderAttachEntity bSoOrderAttachEntity = (BSoOrderAttachEntity) BeanUtilsSupport.copyProperties(soOrderAttachVo, BSoOrderAttachEntity.class);

        insertFile(fileEntity, soOrderVo, bSoOrderAttachEntity);
        bSoOrderAttachEntity.setSo_order_id(bSoOrderEntity.getId());
        int insert = bSoOrderAttachMapper.updateById(bSoOrderAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增附件信息失败");
        }

        return UpdateResultUtil.OK(updCount);
    }

    /**
     * 删除销售订单信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<SoOrderVo> searchCondition) {
        for (SoOrderVo soOrderVo : searchCondition) {

            // 插入前check
            CheckResultAo cr = checkLogic(soOrderVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BSoOrderEntity bSoOrderEntity = mapper.selectById(soOrderVo.getId());
            bSoOrderEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bSoOrderEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public SoOrderVo getPrintInfo(SoOrderVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_SO_ORDER);
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
     * 报表导出
     *
     * @param param
     */
    @Override
    public List<SoOrderVo> selectExportList(SoOrderVo param) {
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
     * 启动审批流
     */
    public void startFlowProcess(SoOrderVo bean,String type){
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
     * 附件逻辑 全删全增
     */
    public BSoOrderAttachEntity insertFile(SFileEntity fileEntity, SoOrderVo vo, BSoOrderAttachEntity extra) {
        //  其他附件附件全删
       /* if (vo.getDoc_att_file()!=null){
            deleteFile(vo.getDoc_att_file());
        }*/
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
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(SoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程拒绝 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(SoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_THREE);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售订单[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(SoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ZERO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》销售订单[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(SoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());
        bSoOrderEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bSoOrderEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bSoOrderEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bSoOrderEntity);

        log.debug("====》销售订单[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(SoOrderVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        SoOrderVo soOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", soOrderVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(soOrderVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(SoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_FIVE);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(SoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购订单[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(SoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购订单[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(SoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bSoOrderEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bSoOrderEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bSoOrderEntity);

        log.debug("====》采购订单[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(SoOrderVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bSoOrderEntity.setBpm_cancel_process_name("作废采购订单审批");
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_FOUR);
        int insert = mapper.updateById(bSoOrderEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, SoOrderVo vo) {
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
