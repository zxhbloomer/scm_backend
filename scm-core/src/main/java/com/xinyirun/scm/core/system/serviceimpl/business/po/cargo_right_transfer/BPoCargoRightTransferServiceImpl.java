package com.xinyirun.scm.core.system.serviceimpl.business.po.cargo_right_transfer;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferEntity;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferAttachVo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
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
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferMapper;
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
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBPoCargoRightTransferService;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBPoCargoRightTransferTotalService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BCargoRightTransferAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 货权转移表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Slf4j
@Service
public class BPoCargoRightTransferServiceImpl extends BaseServiceImpl<BPoCargoRightTransferMapper, BPoCargoRightTransferEntity> implements IBPoCargoRightTransferService {

    @Autowired
    private BPoCargoRightTransferMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BPoCargoRightTransferDetailMapper bCargoRightTransferDetailMapper;

    @Autowired
    private BCargoRightTransferAutoCodeServiceImpl bCargoRightTransferAutoCodeService;

    @Autowired
    private BPoCargoRightTransferAttachMapper bCargoRightTransferAttachMapper;

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
    private BPoContractMapper bPoContractMapper;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private MGoodsMapper mGoodsMapper;

    @Autowired
    private SDictDataMapper sDictDataMapper;

    @Autowired
    private MEnterpriseMapper mEnterpriseMapper;

    @Autowired
    private MGoodsSpecMapper mGoodsSpecMapper;

    @Autowired
    private IBPoCargoRightTransferTotalService iBCargoRightTransferTotalService;

    @Autowired
    private ICommonPoTotalService iCommonPoTotalService;

    /**
     * 货权转移  新增
     * @param BPoCargoRightTransferVo
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoCargoRightTransferVo> insert(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        // 1. 保存主表信息
        BPoCargoRightTransferEntity bCargoRightTransferEntity = saveMainEntity(BPoCargoRightTransferVo);
        // 2. 保存明细信息
        saveDetailList(BPoCargoRightTransferVo, bCargoRightTransferEntity);
        // 3. 保存附件信息
        saveAttach(BPoCargoRightTransferVo, bCargoRightTransferEntity);
        // 4. 设置返回ID
        BPoCargoRightTransferVo.setId(bCargoRightTransferEntity.getId());
        // 5. 更新货权转移财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        return InsertResultUtil.OK(BPoCargoRightTransferVo);
    }
    
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        CheckResultAo cr = checkLogic(BPoCargoRightTransferVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 保存主表信息
     */
    private BPoCargoRightTransferEntity saveMainEntity(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        BPoCargoRightTransferEntity bCargoRightTransferEntity = new BPoCargoRightTransferEntity();
        BeanUtils.copyProperties(BPoCargoRightTransferVo, bCargoRightTransferEntity);
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ONE);
        bCargoRightTransferEntity.setCode(bCargoRightTransferAutoCodeService.autoCode().getCode());
        bCargoRightTransferEntity.setIs_del(Boolean.FALSE);
        bCargoRightTransferEntity.setBpm_process_name("新增货权转移审批");
        
        // 根据po_order_id获取采购订单信息，设置合同相关字段
        if (bCargoRightTransferEntity.getPo_order_id() != null) {
            BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(bCargoRightTransferEntity.getPo_order_id());
            if (poOrderEntity != null) {
                bCargoRightTransferEntity.setPo_contract_id(poOrderEntity.getPo_contract_id());
                bCargoRightTransferEntity.setPo_contract_code(poOrderEntity.getPo_contract_code());
            }
        }
        
        List<BPoCargoRightTransferDetailVo> detailListData = BPoCargoRightTransferVo.getDetailListData();
        int result = mapper.insert(bCargoRightTransferEntity);
        if (result == 0){
            throw new BusinessException("新增失败");
        }
        return bCargoRightTransferEntity;
    }
    
    /**
     * 保存明细信息
     */
    private void saveDetailList(BPoCargoRightTransferVo BPoCargoRightTransferVo, BPoCargoRightTransferEntity bCargoRightTransferEntity) {
        List<BPoCargoRightTransferDetailVo> detailListData = BPoCargoRightTransferVo.getDetailListData();
        for (BPoCargoRightTransferDetailVo detailListDatum : detailListData) {
            // 根据po_order_detail_id直接查询采购订单明细
            BPoOrderDetailEntity poOrderDetailEntity = bPoOrderDetailMapper.selectById(detailListDatum.getPo_order_detail_id());
            
            BPoCargoRightTransferDetailEntity bCargoRightTransferDetailEntity = new BPoCargoRightTransferDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bCargoRightTransferDetailEntity);
            bCargoRightTransferDetailEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            
            // 如果查询到采购订单明细数据，将其设置到实体中
            if (poOrderDetailEntity != null) {
                bCargoRightTransferDetailEntity.setGoods_id(poOrderDetailEntity.getGoods_id());
                bCargoRightTransferDetailEntity.setGoods_code(poOrderDetailEntity.getGoods_code());
                bCargoRightTransferDetailEntity.setGoods_name(poOrderDetailEntity.getGoods_name());
                bCargoRightTransferDetailEntity.setSku_id(poOrderDetailEntity.getSku_id());
                bCargoRightTransferDetailEntity.setSku_code(poOrderDetailEntity.getSku_code());
                bCargoRightTransferDetailEntity.setSku_name(poOrderDetailEntity.getSku_name());
                bCargoRightTransferDetailEntity.setUnit_id(poOrderDetailEntity.getUnit_id());
                bCargoRightTransferDetailEntity.setOrigin(poOrderDetailEntity.getOrigin());
                bCargoRightTransferDetailEntity.setOrder_qty(poOrderDetailEntity.getQty());
                bCargoRightTransferDetailEntity.setOrder_price(poOrderDetailEntity.getPrice());
                bCargoRightTransferDetailEntity.setOrder_amount(poOrderDetailEntity.getAmount());
                
                // 获取采购订单编号并设置到po_order_code字段
                BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(poOrderDetailEntity.getPo_order_id());
                if (poOrderEntity != null) {
                    bCargoRightTransferDetailEntity.setPo_order_code(poOrderEntity.getCode());
                }
            }
            
            // 计算转移金额
            if (detailListDatum.getTransfer_qty() != null && detailListDatum.getTransfer_price() != null) {
                bCargoRightTransferDetailEntity.setTransfer_amount(
                        detailListDatum.getTransfer_qty().multiply(detailListDatum.getTransfer_price()).setScale(2, RoundingMode.HALF_UP));
            }
            
            int result = bCargoRightTransferDetailMapper.insert(bCargoRightTransferDetailEntity);
            if (result == 0){
                throw new BusinessException("新增失败");
            }
        }
    }
    
    /**
     * 保存附件信息
     */
    private void saveAttach(BPoCargoRightTransferVo BPoCargoRightTransferVo, BPoCargoRightTransferEntity bCargoRightTransferEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER);
        BPoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity = insertFile(fileEntity, BPoCargoRightTransferVo, new BPoCargoRightTransferAttachEntity());
        bCargoRightTransferAttachEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
        int insert = bCargoRightTransferAttachMapper.insert(bCargoRightTransferAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 货权转移  新增
     *
     * @param BPoCargoRightTransferVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoCargoRightTransferVo> startInsert(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        // 1. 校验业务规则
        checkInsertLogic(BPoCargoRightTransferVo);
        
        // 2.保存货权转移
        InsertResultAo<BPoCargoRightTransferVo> insertResultAo = insert(BPoCargoRightTransferVo);

        // 3.启动审批流程
        startFlowProcess(BPoCargoRightTransferVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CARGO_RIGHT_TRANSFER);

        return insertResultAo;
    }

    @Override
    public IPage<BPoCargoRightTransferVo> selectPage(BPoCargoRightTransferVo searchCondition) {
        // 分页条件
        Page<BPoCargoRightTransferVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询货权转移page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取货权转移信息
     * @param id
     */
    @Override
    public BPoCargoRightTransferVo selectById(Integer id) {
        BPoCargoRightTransferVo BPoCargoRightTransferVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(BPoCargoRightTransferVo.getDoc_att_file());
        BPoCargoRightTransferVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FOUR.equals(BPoCargoRightTransferVo.getStatus()) || Objects.equals(BPoCargoRightTransferVo.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(BPoCargoRightTransferVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            BPoCargoRightTransferVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                BPoCargoRightTransferVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            BPoCargoRightTransferVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            BPoCargoRightTransferVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (BPoCargoRightTransferVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(BPoCargoRightTransferVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            BPoCargoRightTransferVo.setProject(bProjectVo);
        }
        
        return BPoCargoRightTransferVo;
    }

    /**
     * 货权转移  更新
     *
     * @param BPoCargoRightTransferVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        // 1. 校验业务规则
        checkUpdateLogic(BPoCargoRightTransferVo);
        
        // 2.保存货权转移
        UpdateResultAo<Integer> updateResultAo = update(BPoCargoRightTransferVo);

        // 3.启动审批流程
        startFlowProcess(BPoCargoRightTransferVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CARGO_RIGHT_TRANSFER);

        return updateResultAo;
    }

    /**
     * 更新货权转移信息
     *
     * @param BPoCargoRightTransferVo
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        // 1. 更新主表信息
        BPoCargoRightTransferEntity bCargoRightTransferEntity = updateMainEntity(BPoCargoRightTransferVo);
        // 2. 更新明细信息
        updateDetailList(BPoCargoRightTransferVo, bCargoRightTransferEntity);
        // 3. 更新附件信息
        updateAttach(BPoCargoRightTransferVo, bCargoRightTransferEntity);
        // 4. 更新货权转移财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        return UpdateResultUtil.OK(1);
    }
    
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        CheckResultAo cr = checkLogic(BPoCargoRightTransferVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 更新主表信息
     */
    private BPoCargoRightTransferEntity updateMainEntity(BPoCargoRightTransferVo BPoCargoRightTransferVo) {
        BPoCargoRightTransferEntity bCargoRightTransferEntity = (BPoCargoRightTransferEntity) BeanUtilsSupport.copyProperties(BPoCargoRightTransferVo, BPoCargoRightTransferEntity.class);
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ONE);
        bCargoRightTransferEntity.setBpm_process_name("更新货权转移审批");
        
        // 根据po_order_id获取采购订单信息，设置合同相关字段
        if (bCargoRightTransferEntity.getPo_order_id() != null) {
            BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(bCargoRightTransferEntity.getPo_order_id());
            if (poOrderEntity != null) {
                bCargoRightTransferEntity.setPo_contract_id(poOrderEntity.getPo_contract_id());
                bCargoRightTransferEntity.setPo_contract_code(poOrderEntity.getPo_contract_code());
            }
        }
        
        List<BPoCargoRightTransferDetailVo> detailListData = BPoCargoRightTransferVo.getDetailListData();
        int updCount = mapper.updateById(bCargoRightTransferEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bCargoRightTransferEntity;
    }
    
    /**
     * 更新明细信息
     */
    private void updateDetailList(BPoCargoRightTransferVo BPoCargoRightTransferVo, BPoCargoRightTransferEntity bCargoRightTransferEntity) {
        List<BPoCargoRightTransferDetailVo> detailListData = BPoCargoRightTransferVo.getDetailListData();
        bCargoRightTransferDetailMapper.deleteByCargoRightTransferId(bCargoRightTransferEntity.getId());
        for (BPoCargoRightTransferDetailVo detailListDatum : detailListData) {
            // 根据po_order_detail_id直接查询采购订单明细
            BPoOrderDetailEntity poOrderDetailEntity = bPoOrderDetailMapper.selectById(detailListDatum.getPo_order_detail_id());
            
            BPoCargoRightTransferDetailEntity bCargoRightTransferDetailEntity = new BPoCargoRightTransferDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bCargoRightTransferDetailEntity);
            bCargoRightTransferDetailEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            
            // 如果查询到采购订单明细数据，将其设置到实体中
            if (poOrderDetailEntity != null) {
                bCargoRightTransferDetailEntity.setGoods_id(poOrderDetailEntity.getGoods_id());
                bCargoRightTransferDetailEntity.setGoods_code(poOrderDetailEntity.getGoods_code());
                bCargoRightTransferDetailEntity.setGoods_name(poOrderDetailEntity.getGoods_name());
                bCargoRightTransferDetailEntity.setSku_id(poOrderDetailEntity.getSku_id());
                bCargoRightTransferDetailEntity.setSku_code(poOrderDetailEntity.getSku_code());
                bCargoRightTransferDetailEntity.setSku_name(poOrderDetailEntity.getSku_name());
                bCargoRightTransferDetailEntity.setUnit_id(poOrderDetailEntity.getUnit_id());
                bCargoRightTransferDetailEntity.setOrigin(poOrderDetailEntity.getOrigin());
                bCargoRightTransferDetailEntity.setOrder_qty(poOrderDetailEntity.getQty());
                bCargoRightTransferDetailEntity.setOrder_price(poOrderDetailEntity.getPrice());
                bCargoRightTransferDetailEntity.setOrder_amount(poOrderDetailEntity.getAmount());
                
                // 获取采购订单编号并设置到po_order_code字段
                BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(poOrderDetailEntity.getPo_order_id());
                if (poOrderEntity != null) {
                    bCargoRightTransferDetailEntity.setPo_order_code(poOrderEntity.getCode());
                }
            }
            
            // 计算转移金额
            if (detailListDatum.getTransfer_qty() != null && detailListDatum.getTransfer_price() != null) {
                bCargoRightTransferDetailEntity.setTransfer_amount(
                        detailListDatum.getTransfer_qty().multiply(detailListDatum.getTransfer_price()).setScale(2, RoundingMode.HALF_UP));
            }
            
            int result = bCargoRightTransferDetailMapper.insert(bCargoRightTransferDetailEntity);
            if (result == 0){
                throw new BusinessException("新增货权转移明细表-商品失败");
            }
        }
    }
    
    /**
     * 更新附件信息
     */
    private void updateAttach(BPoCargoRightTransferVo BPoCargoRightTransferVo, BPoCargoRightTransferEntity bCargoRightTransferEntity) {
        BPoCargoRightTransferAttachVo BPoCargoRightTransferAttachVo = bCargoRightTransferAttachMapper.selectByCargoRightTransferId(bCargoRightTransferEntity.getId());
        if (BPoCargoRightTransferAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER);
            BPoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity =(BPoCargoRightTransferAttachEntity) BeanUtilsSupport.copyProperties(BPoCargoRightTransferAttachVo, BPoCargoRightTransferAttachEntity.class);
            insertFile(fileEntity, BPoCargoRightTransferVo, bCargoRightTransferAttachEntity);
            bCargoRightTransferAttachEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            int update = bCargoRightTransferAttachMapper.updateById(bCargoRightTransferAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER);
            BPoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity = new BPoCargoRightTransferAttachEntity();
            insertFile(fileEntity, BPoCargoRightTransferVo, bCargoRightTransferAttachEntity);
            bCargoRightTransferAttachEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            int insert = bCargoRightTransferAttachMapper.insert(bCargoRightTransferAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 删除货权转移信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BPoCargoRightTransferVo> searchCondition) {
        for (BPoCargoRightTransferVo BPoCargoRightTransferVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(BPoCargoRightTransferVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(BPoCargoRightTransferVo.getId());
            bCargoRightTransferEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bCargoRightTransferEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 按货权转移合计
     *
     * @param searchCondition
     */
    @Override
    public BPoCargoRightTransferVo querySum(BPoCargoRightTransferVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 货权转移校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BPoCargoRightTransferVo bean, String checkType) {
        BPoCargoRightTransferEntity bCargoRightTransferEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("校验出错：货权转移-商品信息数据不能为空。");
                }

                // 校验货权转移-商品信息数据中商品中的转移数量是否>0
                for (BPoCargoRightTransferDetailVo detail : bean.getDetailListData()) {
                    if (detail.getTransfer_qty() == null || detail.getTransfer_qty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG(String.format("校验出错：货权转移-商品信息数据\"商品编号：%s\"的转移数量需要填写正确的额值。", detail.getSku_code()));
                    }
                }

                // 校验po_order_id和po_contract_id的关联关系
                if (bean.getPo_order_id() != null && bean.getPo_contract_id() != null) {
                    BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(bean.getPo_order_id());
                    if (poOrderEntity == null) {
                        return CheckResultUtil.NG("采购订单不存在");
                    }
                    if (!Objects.equals(poOrderEntity.getPo_contract_id(), bean.getPo_contract_id())) {
                        return CheckResultUtil.NG("采购订单与采购合同不匹配");
                    }
                }

                // 校验采购合同是否存在且状态正确
                if (bean.getPo_contract_id() != null) {
                    BPoContractEntity bPoContractEntity = bPoContractMapper.selectById(bean.getPo_contract_id());
                    if (bPoContractEntity == null) {
                        return CheckResultUtil.NG("采购合同不存在");
                    }
                    if (!Objects.equals(bPoContractEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_TWO)) {
                        return CheckResultUtil.NG("采购合同未审批通过，无法进行货权转移");
                    }
                }

                Map<String, Long> collect = bean.getDetailListData()
                        .stream()
                        .map(BPoCargoRightTransferDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1)
                        result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result);
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bCargoRightTransferEntity = mapper.selectById(bean.getId());
                if (bCargoRightTransferEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ZERO) && !Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，货权转移[%s]不是待审批,驳回状态,无法修改",bCargoRightTransferEntity.getCode()));

                }

                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()){
                    return CheckResultUtil.NG("校验出错：货权转移-商品信息数据不能为空。");
                }

                // 校验货权转移-商品信息数据中商品中的转移数量是否>0
                for (BPoCargoRightTransferDetailVo detail : bean.getDetailListData()) {
                    if (detail.getTransfer_qty() == null || detail.getTransfer_qty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG(String.format("校验出错：货权转移-商品信息数据\"商品编号：%s\"的转移数量需要填写正确的额值。", detail.getSku_code()));
                    }
                }

                // 校验po_order_id和po_contract_id的关联关系
                if (bean.getPo_order_id() != null && bean.getPo_contract_id() != null) {
                    BPoOrderEntity poOrderEntity = bPoOrderMapper.selectById(bean.getPo_order_id());
                    if (poOrderEntity == null) {
                        return CheckResultUtil.NG("采购订单不存在");
                    }
                    if (!Objects.equals(poOrderEntity.getPo_contract_id(), bean.getPo_contract_id())) {
                        return CheckResultUtil.NG("采购订单与采购合同不匹配");
                    }
                }

                Map<String, Long> collect2 = bean.getDetailListData()
                        .stream()
                        .map(BPoCargoRightTransferDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result2 = new ArrayList<>();
                collect2.forEach((k,v)->{
                    if(v>1)
                        result2.add(k);
                });

                if (result2!=null&&result2.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result2);
                }

                break;
            // 删除校验
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bCargoRightTransferEntity = mapper.selectById(bean.getId());
                if (bCargoRightTransferEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                // 是否待审批或者驳回状态
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ZERO) && !Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，货权转移[%s]不是待审批,驳回状态,无法删除",bCargoRightTransferEntity.getCode()));
                }

                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bCargoRightTransferEntity = mapper.selectById(bean.getId());
                if (bCargoRightTransferEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FIVE) || Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，货权转移[%s]无法重复作废",bCargoRightTransferEntity.getCode()));
                }
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，货权转移[%s]审核中，无法作废",bCargoRightTransferEntity.getCode()));
                }

                break;
            // 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:

                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bCargoRightTransferEntity = mapper.selectById(bean.getId());
                if (bCargoRightTransferEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否审批通过
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，货权转移[%s]未进入执行状态",bCargoRightTransferEntity.getCode()));
                }

                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BPoCargoRightTransferVo bean, String type){
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
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BPoCargoRightTransferVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        BPoCargoRightTransferVo BPoCargoRightTransferVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 转移方：xxx，接收方：xxx，转移金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("货权转移数量（吨）：", BPoCargoRightTransferVo.getTotal_qty());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BPoCargoRightTransferVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }
    
    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BPoCargoRightTransferVo searchCondition) {
        log.debug("====》货权转移[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 审批通过后重新计算总计数据
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(searchCondition.getId());

        log.debug("====》货权转移[{}]审批流程通过,更新结束《====", searchCondition.getId());

        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BPoCargoRightTransferVo searchCondition) {
        log.debug("====》货权转移[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_THREE);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());
        log.debug("====》货权转移[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BPoCargoRightTransferVo searchCondition) {
        log.debug("====》货权转移[{}]审批流程撤销，更新开始《====", searchCondition.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ZERO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        log.debug("====》货权转移[{}]审批流程撤销,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BPoCargoRightTransferVo searchCondition) {
        log.debug("====》货权转移[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());
        bCargoRightTransferEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bCargoRightTransferEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bCargoRightTransferEntity);

        log.debug("====》货权转移[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param vo
     */
    @Override
    public BPoCargoRightTransferVo getPrintInfo(BPoCargoRightTransferVo vo) {
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
        param.setCode(PageCodeConstant.PAGE_B_PO_CARGO_RIGHT_TRANSFER);
        SPagesVo pagesVo = isPagesService.get(param);

        /**
         * 获取打印配置信息
         * 3、从s_app_config中获取，报表系统的app_key，securit_key
         */
//        SAppConfigEntity key = isAppConfigService.getDataByAppCode(AppConfigConstant.PRINT_SYSTEM_CODE);

        String printUrl =  url + pagesVo.getPrint_code() + "?token=" + token + "&id=" + vo.getId();
//        printUrl = printUrl + "&app_key=" + key.getApp_key() + "&secret_key=" + key.getSecret_key();
        vo.setPrint_url(printUrl);
        vo.setQr_code(printUrl);
        log.debug("打印地址：" + printUrl);
        return vo;
    }

    /**
     * 导出查询
     *
     * @param param
     */
    @Override
    public List<BPoCargoRightTransferVo> selectExportList(BPoCargoRightTransferVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(param.getId()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
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
    public BPoCargoRightTransferAttachEntity insertFile(SFileEntity fileEntity, BPoCargoRightTransferVo vo, BPoCargoRightTransferAttachEntity extra) {
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
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BPoCargoRightTransferVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BPoCargoRightTransferVo BPoCargoRightTransferVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", BPoCargoRightTransferVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BPoCargoRightTransferVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BPoCargoRightTransferVo vo) {
        log.debug("====》货权转移[{}]审批流程通过，更新开始《====",vo.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FIVE);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());
        log.debug("====》货权转移[{}]审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BPoCargoRightTransferVo vo) {
        log.debug("====》货权转移[{}]作废审批流程拒绝，更新开始《====",vo.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CARGO_RIGHT_TRANSFER);
        mCancelService.delete(mCancelVo);

        log.debug("====》货权转移[{}]作废审批流程拒绝,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BPoCargoRightTransferVo searchCondition) {
        log.debug("====》货权转移[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CARGO_RIGHT_TRANSFER);
        mCancelService.delete(mCancelVo);

        log.debug("====》货权转移[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BPoCargoRightTransferVo vo) {
        log.debug("====》货权转移[{}]作废审批流程更新最新审批人，更新开始《====",vo.getId());

        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());
        bCargoRightTransferEntity.setNext_approve_name(vo.getNext_approve_name());
        int i = mapper.updateById(bCargoRightTransferEntity);

        log.debug("====》货权转移[{}]作废审批流程更新最新审批人，更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BPoCargoRightTransferVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bCargoRightTransferEntity.setBpm_cancel_process_name("作废货权转移审批");
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FOUR);
        int insert = mapper.updateById(bCargoRightTransferEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_CARGO_RIGHT_TRANSFER);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CARGO_RIGHT_TRANSFER_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BPoCargoRightTransferVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_SIX);
        int update = mapper.updateById(bCargoRightTransferEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 完成后重新计算总计数据
        iCommonPoTotalService.reCalculateAllTotalDataByCargoRightTransferId(searchCondition.getId());

        return UpdateResultUtil.OK(update);
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BPoCargoRightTransferVo vo) {
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