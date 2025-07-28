package com.xinyirun.scm.core.system.serviceimpl.business.so.cargo_right_transfer;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferEntity;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferAttachVo;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferVo;
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
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferMapper;
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
import com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer.IBSoCargoRightTransferService;
import com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer.IBSoCargoRightTransferTotalService;
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
 * 销售货权转移表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Slf4j
@Service
public class BSoCargoRightTransferServiceImpl extends BaseServiceImpl<BSoCargoRightTransferMapper, BSoCargoRightTransferEntity> implements IBSoCargoRightTransferService {

    @Autowired
    private BSoCargoRightTransferMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BSoCargoRightTransferDetailMapper bCargoRightTransferDetailMapper;

    @Autowired
    private BCargoRightTransferAutoCodeServiceImpl bCargoRightTransferAutoCodeService;

    @Autowired
    private BSoCargoRightTransferAttachMapper bCargoRightTransferAttachMapper;

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
    private BSoContractMapper bSoContractMapper;

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
    private IBSoCargoRightTransferTotalService iBCargoRightTransferTotalService;

    @Autowired
    private ICommonSoTotalService iCommonSoTotalService;

    /**
     * 销售货权转移  新增
     * @param BSoCargoRightTransferVo
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoCargoRightTransferVo> insert(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        // 1. 保存主表信息
        BSoCargoRightTransferEntity bCargoRightTransferEntity = saveMainEntity(BSoCargoRightTransferVo);
        // 2. 保存明细信息
        saveDetailList(BSoCargoRightTransferVo, bCargoRightTransferEntity);
        // 3. 保存附件信息
        saveAttach(BSoCargoRightTransferVo, bCargoRightTransferEntity);
        // 4. 设置返回ID
        BSoCargoRightTransferVo.setId(bCargoRightTransferEntity.getId());
        // 5. 更新货权转移财务数据
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        return InsertResultUtil.OK(BSoCargoRightTransferVo);
    }
    
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        CheckResultAo cr = checkLogic(BSoCargoRightTransferVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 保存主表信息
     */
    private BSoCargoRightTransferEntity saveMainEntity(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        BSoCargoRightTransferEntity bCargoRightTransferEntity = new BSoCargoRightTransferEntity();
        BeanUtils.copyProperties(BSoCargoRightTransferVo, bCargoRightTransferEntity);
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ONE);
        bCargoRightTransferEntity.setCode(bCargoRightTransferAutoCodeService.autoCode().getCode());
        bCargoRightTransferEntity.setIs_del(Boolean.FALSE);
        bCargoRightTransferEntity.setBpm_process_name("新增销售货权转移审批");
        
        // 根据so_order_id获取销售订单信息，设置合同相关字段
        if (bCargoRightTransferEntity.getSo_order_id() != null) {
            BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(bCargoRightTransferEntity.getSo_order_id());
            if (soOrderEntity != null) {
                bCargoRightTransferEntity.setSo_contract_id(soOrderEntity.getSo_contract_id());
                bCargoRightTransferEntity.setSo_contract_code(soOrderEntity.getSo_contract_code());
            }
        }
        
        List<BSoCargoRightTransferDetailVo> detailListData = BSoCargoRightTransferVo.getDetailListData();
        int result = mapper.insert(bCargoRightTransferEntity);
        if (result == 0){
            throw new BusinessException("新增失败");
        }
        return bCargoRightTransferEntity;
    }
    
    /**
     * 保存明细信息
     */
    private void saveDetailList(BSoCargoRightTransferVo BSoCargoRightTransferVo, BSoCargoRightTransferEntity bCargoRightTransferEntity) {
        List<BSoCargoRightTransferDetailVo> detailListData = BSoCargoRightTransferVo.getDetailListData();
        for (BSoCargoRightTransferDetailVo detailListDatum : detailListData) {
            // 根据so_order_detail_id直接查询销售订单明细
            BSoOrderDetailEntity soOrderDetailEntity = bSoOrderDetailMapper.selectById(detailListDatum.getSo_order_detail_id());
            
            BSoCargoRightTransferDetailEntity bCargoRightTransferDetailEntity = new BSoCargoRightTransferDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bCargoRightTransferDetailEntity);
            bCargoRightTransferDetailEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            
            // 如果查询到销售订单明细数据，将其设置到实体中
            if (soOrderDetailEntity != null) {
                bCargoRightTransferDetailEntity.setGoods_id(soOrderDetailEntity.getGoods_id());
                bCargoRightTransferDetailEntity.setGoods_code(soOrderDetailEntity.getGoods_code());
                bCargoRightTransferDetailEntity.setGoods_name(soOrderDetailEntity.getGoods_name());
                bCargoRightTransferDetailEntity.setSku_id(soOrderDetailEntity.getSku_id());
                bCargoRightTransferDetailEntity.setSku_code(soOrderDetailEntity.getSku_code());
                bCargoRightTransferDetailEntity.setSku_name(soOrderDetailEntity.getSku_name());
                bCargoRightTransferDetailEntity.setUnit_id(soOrderDetailEntity.getUnit_id());
                bCargoRightTransferDetailEntity.setOrigin(soOrderDetailEntity.getOrigin());
                bCargoRightTransferDetailEntity.setOrder_qty(soOrderDetailEntity.getQty());
                bCargoRightTransferDetailEntity.setOrder_price(soOrderDetailEntity.getPrice());
                bCargoRightTransferDetailEntity.setOrder_amount(soOrderDetailEntity.getAmount());
                
                // 获取销售订单编号并设置到so_order_code字段
                BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(soOrderDetailEntity.getSo_order_id());
                if (soOrderEntity != null) {
                    bCargoRightTransferDetailEntity.setSo_order_code(soOrderEntity.getCode());
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
    private void saveAttach(BSoCargoRightTransferVo BSoCargoRightTransferVo, BSoCargoRightTransferEntity bCargoRightTransferEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER);
        BSoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity = insertFile(fileEntity, BSoCargoRightTransferVo, new BSoCargoRightTransferAttachEntity());
        bCargoRightTransferAttachEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
        int insert = bCargoRightTransferAttachMapper.insert(bCargoRightTransferAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 销售货权转移  新增
     *
     * @param BSoCargoRightTransferVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoCargoRightTransferVo> startInsert(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        // 1. 校验业务规则
        checkInsertLogic(BSoCargoRightTransferVo);
        
        // 2.保存货权转移
        InsertResultAo<BSoCargoRightTransferVo> insertResultAo = insert(BSoCargoRightTransferVo);

        // 3.启动审批流程
        startFlowProcess(BSoCargoRightTransferVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CARGO_RIGHT_TRANSFER);

        return insertResultAo;
    }

    @Override
    public IPage<BSoCargoRightTransferVo> selectPage(BSoCargoRightTransferVo searchCondition) {
        // 分页条件
        Page<BSoCargoRightTransferVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
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
    public BSoCargoRightTransferVo selectById(Integer id) {
        BSoCargoRightTransferVo BSoCargoRightTransferVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(BSoCargoRightTransferVo.getDoc_att_file());
        BSoCargoRightTransferVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FOUR.equals(BSoCargoRightTransferVo.getStatus()) || Objects.equals(BSoCargoRightTransferVo.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(BSoCargoRightTransferVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            BSoCargoRightTransferVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                BSoCargoRightTransferVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            BSoCargoRightTransferVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            BSoCargoRightTransferVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (BSoCargoRightTransferVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(BSoCargoRightTransferVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            BSoCargoRightTransferVo.setProject(bProjectVo);
        }
        
        return BSoCargoRightTransferVo;
    }

    /**
     * 销售货权转移  更新
     *
     * @param BSoCargoRightTransferVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        // 1. 校验业务规则
        checkUpdateLogic(BSoCargoRightTransferVo);
        
        // 2.保存货权转移
        UpdateResultAo<Integer> updateResultAo = update(BSoCargoRightTransferVo);

        // 3.启动审批流程
        startFlowProcess(BSoCargoRightTransferVo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CARGO_RIGHT_TRANSFER);

        return updateResultAo;
    }

    /**
     * 更新货权转移信息
     *
     * @param BSoCargoRightTransferVo
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        // 1. 更新主表信息
        BSoCargoRightTransferEntity bCargoRightTransferEntity = updateMainEntity(BSoCargoRightTransferVo);
        // 2. 更新明细信息
        updateDetailList(BSoCargoRightTransferVo, bCargoRightTransferEntity);
        // 3. 更新附件信息
        updateAttach(BSoCargoRightTransferVo, bCargoRightTransferEntity);
        // 4. 更新货权转移财务数据
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        return UpdateResultUtil.OK(1);
    }
    
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        CheckResultAo cr = checkLogic(BSoCargoRightTransferVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 更新主表信息
     */
    private BSoCargoRightTransferEntity updateMainEntity(BSoCargoRightTransferVo BSoCargoRightTransferVo) {
        BSoCargoRightTransferEntity bCargoRightTransferEntity = (BSoCargoRightTransferEntity) BeanUtilsSupport.copyProperties(BSoCargoRightTransferVo, BSoCargoRightTransferEntity.class);
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ONE);
        bCargoRightTransferEntity.setBpm_process_name("更新销售货权转移审批");
        
        // 根据so_order_id获取销售订单信息，设置合同相关字段
        if (bCargoRightTransferEntity.getSo_order_id() != null) {
            BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(bCargoRightTransferEntity.getSo_order_id());
            if (soOrderEntity != null) {
                bCargoRightTransferEntity.setSo_contract_id(soOrderEntity.getSo_contract_id());
                bCargoRightTransferEntity.setSo_contract_code(soOrderEntity.getSo_contract_code());
            }
        }
        
        List<BSoCargoRightTransferDetailVo> detailListData = BSoCargoRightTransferVo.getDetailListData();
        int updCount = mapper.updateById(bCargoRightTransferEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bCargoRightTransferEntity;
    }
    
    /**
     * 更新明细信息
     */
    private void updateDetailList(BSoCargoRightTransferVo BSoCargoRightTransferVo, BSoCargoRightTransferEntity bCargoRightTransferEntity) {
        List<BSoCargoRightTransferDetailVo> detailListData = BSoCargoRightTransferVo.getDetailListData();
        bCargoRightTransferDetailMapper.deleteByCargoRightTransferId(bCargoRightTransferEntity.getId());
        for (BSoCargoRightTransferDetailVo detailListDatum : detailListData) {
            // 根据so_order_detail_id直接查询销售订单明细
            BSoOrderDetailEntity soOrderDetailEntity = bSoOrderDetailMapper.selectById(detailListDatum.getSo_order_detail_id());
            
            BSoCargoRightTransferDetailEntity bCargoRightTransferDetailEntity = new BSoCargoRightTransferDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bCargoRightTransferDetailEntity);
            bCargoRightTransferDetailEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            
            // 如果查询到销售订单明细数据，将其设置到实体中
            if (soOrderDetailEntity != null) {
                bCargoRightTransferDetailEntity.setGoods_id(soOrderDetailEntity.getGoods_id());
                bCargoRightTransferDetailEntity.setGoods_code(soOrderDetailEntity.getGoods_code());
                bCargoRightTransferDetailEntity.setGoods_name(soOrderDetailEntity.getGoods_name());
                bCargoRightTransferDetailEntity.setSku_id(soOrderDetailEntity.getSku_id());
                bCargoRightTransferDetailEntity.setSku_code(soOrderDetailEntity.getSku_code());
                bCargoRightTransferDetailEntity.setSku_name(soOrderDetailEntity.getSku_name());
                bCargoRightTransferDetailEntity.setUnit_id(soOrderDetailEntity.getUnit_id());
                bCargoRightTransferDetailEntity.setOrigin(soOrderDetailEntity.getOrigin());
                bCargoRightTransferDetailEntity.setOrder_qty(soOrderDetailEntity.getQty());
                bCargoRightTransferDetailEntity.setOrder_price(soOrderDetailEntity.getPrice());
                bCargoRightTransferDetailEntity.setOrder_amount(soOrderDetailEntity.getAmount());
                
                // 获取销售订单编号并设置到so_order_code字段
                BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(soOrderDetailEntity.getSo_order_id());
                if (soOrderEntity != null) {
                    bCargoRightTransferDetailEntity.setSo_order_code(soOrderEntity.getCode());
                }
            }
            
            // 计算转移金额
            if (detailListDatum.getTransfer_qty() != null && detailListDatum.getTransfer_price() != null) {
                bCargoRightTransferDetailEntity.setTransfer_amount(
                        detailListDatum.getTransfer_qty().multiply(detailListDatum.getTransfer_price()).setScale(2, RoundingMode.HALF_UP));
            }
            
            int result = bCargoRightTransferDetailMapper.insert(bCargoRightTransferDetailEntity);
            if (result == 0){
                throw new BusinessException("新增销售货权转移明细表-商品失败");
            }
        }
    }
    
    /**
     * 更新附件信息
     */
    private void updateAttach(BSoCargoRightTransferVo BSoCargoRightTransferVo, BSoCargoRightTransferEntity bCargoRightTransferEntity) {
        BSoCargoRightTransferAttachVo BSoCargoRightTransferAttachVo = bCargoRightTransferAttachMapper.selectByCargoRightTransferId(bCargoRightTransferEntity.getId());
        if (BSoCargoRightTransferAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER);
            BSoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity =(BSoCargoRightTransferAttachEntity) BeanUtilsSupport.copyProperties(BSoCargoRightTransferAttachVo, BSoCargoRightTransferAttachEntity.class);
            insertFile(fileEntity, BSoCargoRightTransferVo, bCargoRightTransferAttachEntity);
            bCargoRightTransferAttachEntity.setCargo_right_transfer_id(bCargoRightTransferEntity.getId());
            int update = bCargoRightTransferAttachMapper.updateById(bCargoRightTransferAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER);
            BSoCargoRightTransferAttachEntity bCargoRightTransferAttachEntity = new BSoCargoRightTransferAttachEntity();
            insertFile(fileEntity, BSoCargoRightTransferVo, bCargoRightTransferAttachEntity);
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
    public DeleteResultAo<Integer> delete(List<BSoCargoRightTransferVo> searchCondition) {
        for (BSoCargoRightTransferVo BSoCargoRightTransferVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(BSoCargoRightTransferVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(BSoCargoRightTransferVo.getId());
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
    public BSoCargoRightTransferVo querySum(BSoCargoRightTransferVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 销售货权转移校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BSoCargoRightTransferVo bean, String checkType) {
        BSoCargoRightTransferEntity bCargoRightTransferEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("校验出错：销售货权转移-商品信息数据不能为空。");
                }

                // 校验货权转移-商品信息数据中商品中的转移数量是否>0
                for (BSoCargoRightTransferDetailVo detail : bean.getDetailListData()) {
                    if (detail.getTransfer_qty() == null || detail.getTransfer_qty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG(String.format("校验出错：销售货权转移-商品信息数据\"商品编号：%s\"的转移数量需要填写正确的额值。", detail.getSku_code()));
                    }
                }

                // 校验so_order_id和so_contract_id的关联关系
                if (bean.getSo_order_id() != null && bean.getSo_contract_id() != null) {
                    BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(bean.getSo_order_id());
                    if (soOrderEntity == null) {
                        return CheckResultUtil.NG("销售订单不存在");
                    }
                    if (!Objects.equals(soOrderEntity.getSo_contract_id(), bean.getSo_contract_id())) {
                        return CheckResultUtil.NG("销售订单与销售合同不匹配");
                    }
                }

                // 校验销售合同是否存在且状态正确
                if (bean.getSo_contract_id() != null) {
                    BSoContractEntity bSoContractEntity = bSoContractMapper.selectById(bean.getSo_contract_id());
                    if (bSoContractEntity == null) {
                        return CheckResultUtil.NG("销售合同不存在");
                    }
                    if (!Objects.equals(bSoContractEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_TWO)) {
                        return CheckResultUtil.NG("销售合同未审批通过，无法进行货权转移");
                    }
                }

                Map<String, Long> collect = bean.getDetailListData()
                        .stream()
                        .map(BSoCargoRightTransferDetailVo::getSku_code)
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
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ZERO) && !Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，销售货权转移[%s]不是待审批,驳回状态,无法修改",bCargoRightTransferEntity.getCode()));

                }

                if (bean.getDetailListData()==null || bean.getDetailListData().isEmpty()){
                    return CheckResultUtil.NG("校验出错：销售货权转移-商品信息数据不能为空。");
                }

                // 校验货权转移-商品信息数据中商品中的转移数量是否>0
                for (BSoCargoRightTransferDetailVo detail : bean.getDetailListData()) {
                    if (detail.getTransfer_qty() == null || detail.getTransfer_qty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG(String.format("校验出错：销售货权转移-商品信息数据\"商品编号：%s\"的转移数量需要填写正确的额值。", detail.getSku_code()));
                    }
                }

                // 校验so_order_id和so_contract_id的关联关系
                if (bean.getSo_order_id() != null && bean.getSo_contract_id() != null) {
                    BSoOrderEntity soOrderEntity = bSoOrderMapper.selectById(bean.getSo_order_id());
                    if (soOrderEntity == null) {
                        return CheckResultUtil.NG("销售订单不存在");
                    }
                    if (!Objects.equals(soOrderEntity.getSo_contract_id(), bean.getSo_contract_id())) {
                        return CheckResultUtil.NG("销售订单与销售合同不匹配");
                    }
                }

                Map<String, Long> collect2 = bean.getDetailListData()
                        .stream()
                        .map(BSoCargoRightTransferDetailVo::getSku_code)
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
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ZERO) && !Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，销售货权转移[%s]不是待审批,驳回状态,无法删除",bCargoRightTransferEntity.getCode()));
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
                if (Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FIVE) || Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售货权转移[%s]无法重复作废",bCargoRightTransferEntity.getCode()));
                }
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售货权转移[%s]审核中，无法作废",bCargoRightTransferEntity.getCode()));
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
                if (!Objects.equals(bCargoRightTransferEntity.getStatus(), DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，销售货权转移[%s]未进入执行状态",bCargoRightTransferEntity.getCode()));
                }

                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BSoCargoRightTransferVo bean, String type){
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
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BSoCargoRightTransferVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        BSoCargoRightTransferVo BSoCargoRightTransferVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 转移方：xxx，接收方：xxx，转移金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("销售货权转移数量（吨）：", BSoCargoRightTransferVo.getTotal_qty());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BSoCargoRightTransferVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }
    
    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BSoCargoRightTransferVo searchCondition) {
        log.debug("====》销售货权转移[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 审批通过后重新计算总计数据
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(searchCondition.getId());

        log.debug("====》销售货权转移[{}]审批流程通过,更新结束《====", searchCondition.getId());

        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BSoCargoRightTransferVo searchCondition) {
        log.debug("====》销售货权转移[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_THREE);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());
        log.debug("====》销售货权转移[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BSoCargoRightTransferVo searchCondition) {
        log.debug("====》销售货权转移[{}]审批流程撤销，更新开始《====", searchCondition.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ZERO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        log.debug("====》销售货权转移[{}]审批流程撤销,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BSoCargoRightTransferVo searchCondition) {
        log.debug("====》销售货权转移[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());
        bCargoRightTransferEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bCargoRightTransferEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bCargoRightTransferEntity);

        log.debug("====》销售货权转移[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param vo
     */
    @Override
    public BSoCargoRightTransferVo getPrintInfo(BSoCargoRightTransferVo vo) {
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
        param.setCode(PageCodeConstant.PAGE_B_SO_CARGO_RIGHT_TRANSFER);
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
    public List<BSoCargoRightTransferVo> selectExportList(BSoCargoRightTransferVo param) {
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
    public BSoCargoRightTransferAttachEntity insertFile(SFileEntity fileEntity, BSoCargoRightTransferVo vo, BSoCargoRightTransferAttachEntity extra) {
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
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BSoCargoRightTransferVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BSoCargoRightTransferVo BSoCargoRightTransferVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", BSoCargoRightTransferVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BSoCargoRightTransferVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BSoCargoRightTransferVo vo) {
        log.debug("====》销售货权转移[{}]审批流程通过，更新开始《====",vo.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FIVE);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());
        log.debug("====》销售货权转移[{}]审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BSoCargoRightTransferVo vo) {
        log.debug("====》销售货权转移[{}]作废审批流程拒绝，更新开始《====",vo.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CARGO_RIGHT_TRANSFER);
        mCancelService.delete(mCancelVo);

        log.debug("====》销售货权转移[{}]作废审批流程拒绝,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BSoCargoRightTransferVo searchCondition) {
        log.debug("====》销售货权转移[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO);
        bCargoRightTransferEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bCargoRightTransferEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CARGO_RIGHT_TRANSFER);
        mCancelService.delete(mCancelVo);

        log.debug("====》销售货权转移[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BSoCargoRightTransferVo vo) {
        log.debug("====》销售货权转移[{}]作废审批流程更新最新审批人，更新开始《====",vo.getId());

        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(vo.getId());

        bCargoRightTransferEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bCargoRightTransferEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());
        bCargoRightTransferEntity.setNext_approve_name(vo.getNext_approve_name());
        int i = mapper.updateById(bCargoRightTransferEntity);

        log.debug("====》销售货权转移[{}]作废审批流程更新最新审批人，更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BSoCargoRightTransferVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bCargoRightTransferEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bCargoRightTransferEntity.setBpm_cancel_process_name("作废销售货权转移审批");
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FOUR);
        int insert = mapper.updateById(bCargoRightTransferEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bCargoRightTransferEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_CARGO_RIGHT_TRANSFER);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(bCargoRightTransferEntity.getId());

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CARGO_RIGHT_TRANSFER_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BSoCargoRightTransferVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoCargoRightTransferEntity bCargoRightTransferEntity = mapper.selectById(searchCondition.getId());
        bCargoRightTransferEntity.setStatus(DictConstant.DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_SIX);
        int update = mapper.updateById(bCargoRightTransferEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 完成后重新计算总计数据
        iCommonSoTotalService.reCalculateAllTotalDataByCargoRightTransferId(searchCondition.getId());

        return UpdateResultUtil.OK(update);
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BSoCargoRightTransferVo vo) {
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