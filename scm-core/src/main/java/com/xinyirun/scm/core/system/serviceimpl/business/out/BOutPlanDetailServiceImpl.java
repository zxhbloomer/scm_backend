package com.xinyirun.scm.core.system.serviceimpl.business.out;

import cn.hutool.core.net.url.UrlBuilder;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanSaveVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import com.xinyirun.scm.core.system.mapper.business.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MUnitMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutPlanAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutPlanDetailAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库计划详情 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class BOutPlanDetailServiceImpl extends BaseServiceImpl<BOutPlanDetailMapper, BOutPlanDetailEntity> implements IBOutPlanDetailService {

    @Autowired
    private BOutPlanDetailMapper mapper;

    @Autowired
    private BOutPlanMapper outPlanMapper;

    @Autowired
    private BOutPlanAutoCodeServiceImpl bOutPlanAutoCodeService;

    @Autowired
    private BOutPlanDetailAutoCodeServiceImpl bOutPlanDetailAutoCodeService;

    @Autowired
    private BOutAutoCodeServiceImpl bOutAutoCodeService;

    @Autowired
    private MUnitMapper mUnitMapper;

    @Autowired
    private TodoService todoService;

    @Autowired
    private IMGoodsUnitCalcService imGoodsUnitCalcService;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private BOutExtraMapper bOutExtraMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private MBinMapper mBinMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Autowired
    private IBpmProcessTemplatesService iBpmProcessTemplatesService;

    @Value("${server.port}")
    private int port;

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(BOutPlanSaveVo vo) {
        // 生成计划单号
        String code = bOutPlanAutoCodeService.autoCode().getCode();
        // 赋值出库计划
        BOutPlanEntity outPlanEntity = (BOutPlanEntity) BeanUtilsSupport.copyProperties(vo, BOutPlanEntity.class);
        // 编号为空则设置自动生成的单号
        if (StringUtils.isEmpty(vo.getPlan_code())) {
            outPlanEntity.setCode(code);
        }
        int rtn = outPlanMapper.insert(outPlanEntity);
        vo.setPlan_id(outPlanEntity.getId());
        // 赋值出库计划详情
        List<BOutPlanDetailEntity> planDetailList = BeanUtilsSupport.copyProperties(vo.getDetailList(), BOutPlanDetailEntity.class);

        int no = 1;
        for (BOutPlanDetailEntity detail : planDetailList) {
            detail.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);
            // 订单id
            detail.setOrder_id(vo.getOrder_id());
            detail.setOrder_type(vo.getOrder_type());
            detail.setOver_inventory_upper(vo.getOver_inventory_upper());
            detail.setOrder_detail_no(vo.getOrder_detail_no());
            // 计划单id
            detail.setPlan_id(outPlanEntity.getId());
            detail.setPending_volume(BigDecimal.ZERO);
            detail.setHas_handle_count(BigDecimal.ZERO);
            detail.setHas_handle_weight(BigDecimal.ZERO);
            detail.setHas_handle_volume(BigDecimal.ZERO);
            detail.setPending_count(detail.getCount());
            detail.setPending_weight(detail.getWeight());
            detail.setVolume(BigDecimal.ZERO);
            detail.setOver_release(Boolean.TRUE);
            // 按规则生成计划明细单号
            detail.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
            // 设置序号
            detail.setNo(no);
            mapper.insert(detail);

            no++;
            // 生成待办
            todoService.insertTodo(detail.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);
        }
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(BOutPlanSaveVo vo) {
        // 执行删除逻辑， 查询表中存在的id， 并删除
        List<Integer> oldIds = mapper.selectOutGoodsIdList(vo.getPlan_id());
        // 先删除 detail  和 todo数据
        mapper.deleteBatchIds(oldIds);
        todoService.deleteByIdsAndSerialType(oldIds,  SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL);

        // 删除状态为制单和驳回的明细数据
        // 全删全插逻辑  1：删除明细表数据，2：新增计划明细表，3：更新出库计划
//        mapper.statusDelete(vo.getPlan_id());

        // 页面传来的计划明细
        List<BOutPlanDetailEntity> outPlanDetailEntityList = BeanUtilsSupport.copyProperties(vo.getDetailList(), BOutPlanDetailEntity.class);
        int no = 1;
        for (BOutPlanDetailEntity entity : outPlanDetailEntityList) {
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);
            entity.setOrder_id(vo.getOrder_id());
            entity.setOrder_type(vo.getOrder_type());
            entity.setPlan_id(entity.getPlan_id());
            entity.setPending_volume(BigDecimal.ZERO);
            entity.setOver_inventory_upper(vo.getOver_inventory_upper());
            entity.setHas_handle_count(BigDecimal.ZERO);
            entity.setHas_handle_weight(BigDecimal.ZERO);
            entity.setHas_handle_volume(BigDecimal.ZERO);
            entity.setPending_count(entity.getCount());
            entity.setPending_weight(entity.getWeight());
            entity.setVolume(BigDecimal.ZERO);
            entity.setPlan_id(vo.getPlan_id());
            entity.setNo(no);
            no++;
            // 明细编号如果为空就按规则生成序号
            if (StringUtils.isEmpty(entity.getCode())) {
                entity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
            }
            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL,
                    SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);
            /*if (entity.getId() == null) {
                entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);
                entity.setOrder_id(vo.getOrder_id());
                entity.setOrder_type(vo.getOrder_type());
                entity.setPlan_id(entity.getPlan_id());
                entity.setPending_volume(BigDecimal.ZERO);
                entity.setHas_handle_count(BigDecimal.ZERO);
                entity.setHas_handle_weight(BigDecimal.ZERO);
                entity.setHas_handle_volume(BigDecimal.ZERO);
                entity.setPending_count(entity.getCount());
                entity.setPending_weight(entity.getWeight());
                entity.setVolume(BigDecimal.ZERO);
                entity.setPlan_id(vo.getPlan_id());
                entity.setNo(no);
                no++;
                // 明细编号如果为空就按规则生成序号
                if (StringUtils.isEmpty(entity.getCode())) {
                    entity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
                }

                // 若为审核驳回
                if (DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN.equals(entity.getStatus())) {
                    // 生成待办
                    todoService.insertTodo(entity.getId(),

                            SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);
                }


                entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);
            }*/
        }
        // 更新明细表数据
        saveOrUpdateBatch(outPlanDetailEntityList, 500);

        // 查询要修改的出库计划数据进行更新操作
        BOutPlanEntity outPlanEntity = outPlanMapper.selectById(vo.getPlan_id());
        BeanUtilsSupport.copyProperties(vo, outPlanEntity, new String[]{"id", "code"});
//        outPlanEntity.setU_id(null);
//        outPlanEntity.setU_time(null);

        // 更新出库计划
        int rtn = outPlanMapper.updateById(outPlanEntity);
        return UpdateResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insertForAllocate(BOutPlanSaveVo vo, Boolean auto) {
        // 赋值出库计划
        BOutPlanEntity entity = (BOutPlanEntity) BeanUtilsSupport.copyProperties(vo, BOutPlanEntity.class);
        // 获取默认选中的数据
        MUnitVo unit = mUnitMapper.selectByName(SystemConstants.DEFAULT_UNIT.NAME, true);

        // 生成入库计划单号
        String code = bOutPlanAutoCodeService.autoCode().getCode();
        int rtn = 0;
        // 单号为空则设置自动生成的单号
        entity.setCode(code);
        rtn = outPlanMapper.insert(entity);
        // 赋值出库计划详情
        List<BOutPlanDetailEntity> outPlanDetailList = BeanUtilsSupport.copyProperties(vo.getDetailList(), BOutPlanDetailEntity.class);

        int no = 1;
        for (BOutPlanDetailEntity outPlanDetail : outPlanDetailList) {
            // 入库订单id
            outPlanDetail.setOrder_id(vo.getOrder_id());
            outPlanDetail.setOrder_type(vo.getOrder_type());
            outPlanDetail.setPlan_id(entity.getId());
            outPlanDetail.setPending_volume(BigDecimal.ZERO);
            outPlanDetail.setHas_handle_count(BigDecimal.ZERO);
            outPlanDetail.setHas_handle_weight(BigDecimal.ZERO);
            outPlanDetail.setHas_handle_volume(BigDecimal.ZERO);
            outPlanDetail.setPending_count(outPlanDetail.getCount());
            outPlanDetail.setPending_weight(outPlanDetail.getWeight());
            outPlanDetail.setVolume(BigDecimal.ZERO);
            outPlanDetail.setUnit_id(unit.getId());
            // 序号赋值
            outPlanDetail.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
            outPlanDetail.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_TWO);
            // 设置序号
            outPlanDetail.setNo(no);
            mapper.insert(outPlanDetail);

            no++;

            if (auto.equals(Boolean.TRUE)) {
                BOutEntity out = new BOutEntity();

                // 出库类型
                out.setType(DictConstant.DICT_B_OUT_TYPE_DB);

                out.setSku_code(outPlanDetail.getCode());
                out.setSku_id(outPlanDetail.getSku_id());

                // 自动生成出库单编号
                out.setCode(bOutAutoCodeService.autoCode().getCode());
                out.setStatus(DictConstant.DICT_B_OUT_STATUS_SUBMITTED);
                // 出库计划id
                out.setPlan_id(outPlanDetail.getPlan_id());
                // 出库计划明细id
                out.setPlan_detail_id(outPlanDetail.getId());
                // 计划数量
                out.setPlan_count(outPlanDetail.getCount());
                // 计划重量
                out.setPlan_weight(outPlanDetail.getWeight());
                // 计划体积
                out.setActual_volume(outPlanDetail.getVolume());
                // 数量
                out.setActual_count(outPlanDetail.getCount());
                // 重量
                out.setActual_weight(outPlanDetail.getWeight());
                // 体积
                out.setPlan_volume(outPlanDetail.getVolume());
                // 委托方id
                out.setConsignor_id(entity.getConsignor_id());
                // 委托方code
                out.setConsignor_code(entity.getConsignor_code());
                // 货主id
                out.setOwner_id(entity.getOwner_id());
                // 货主code
                out.setOwner_code(entity.getOwner_code());
                // 出库时间
                out.setOutbound_time(LocalDateTime.now());
                out.setE_id(SecurityUtil.getStaff_id().intValue());
                out.setE_dt(LocalDateTime.now());
                out.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
                // 转换关系
                MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
                mGoodsUnitCalcVo.setSku_id(outPlanDetail.getSku_id());
                mGoodsUnitCalcVo.setSrc_unit_id(outPlanDetail.getUnit_id());
                MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
                out.setCalc(goodsUnitCalcVo.getCalc());
                // 转换后的单位id
                out.setTgt_unit_id(goodsUnitCalcVo.getTgt_unit_id());
                out.setUnit_id(outPlanDetail.getUnit_id());

                // 设置仓库，库区，库位
                MBinEntity binEntity = mBinMapper.selecBinByWarehouseId(outPlanDetail.getWarehouse_id());
                out.setWarehouse_id(binEntity.getWarehouse_id());
                out.setLocation_id(binEntity.getLocation_id());
                out.setBin_id(binEntity.getId());

                out.setPrice(BigDecimal.ZERO);
                out.setAmount(BigDecimal.ZERO);
                out.setId(null);
                // 下推新增出库单
                int result = bOutMapper.insert(out);

                // 新增出库单从表数据
                BOutExtraEntity extra = (BOutExtraEntity) BeanUtilsSupport.copyProperties(vo, BOutExtraEntity.class);
                extra.setId(null);
                // 新增附件
                extra.setOut_id(out.getId());
                bOutExtraMapper.insert(extra);

                // 库存计算
                iCommonInventoryLogicService.updWmsStockByOutBill(out.getId());

                out.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
                out.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
                out.setE_dt(LocalDateTime.now());
                out.setE_id(SecurityUtil.getStaff_id().intValue());
                bOutMapper.updateById(out);
                // 库存计算
                iCommonInventoryLogicService.updWmsStockByOutBill(out.getId());
            }
        }
        vo.setPlan_id(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public BOutPlanDetailVo selectById(Integer id) {
        return mapper.selectDataById(id);
    }

    @Override
    public List<BOutPlanListVo> selectByPlanId(Integer plan_id) {
        return mapper.selectByPlanId(plan_id);
    }


    /**
     * 判断 当前 ID下的出库计划是否作废
     *
     * @param detailId
     */
    @Override
    public void checkPalnStatus(Integer detailId) {
        BOutPlanVo vo = mapper.selectByPlanDetailId(detailId);
        // 判断是否存在已作废数据
        if (!DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL.equals(vo.getStatus())) {
            throw new BusinessException(String.format("该物流订单存在下发的出库计划（%s），无法作废，请先作废出库计划", vo.getCode()));
        }
    }

    /**
     * 根据 计划 ID 查询 detail_id
     *
     * @param plan_id
     * @return
     */
    @Override
    public List<BOutPlanListVo> selectByPlanIds(List<Integer> plan_id) {
        return mapper.selectByPlanIds(plan_id);
    }

    /**
     * 查询 出库计划详情
     *
     * @param orderId   销售订单id
     * @param orderType 订单类型
     * @return
     */
    @Override
    public List<BOutPlanListVo> selectOutPlanByOrderIdAndOrderType(Integer orderId, String orderType) {
        return mapper.selectOutPlanByOrderIdAndOrderType(orderId, orderType);
    }

    /**
     * 查询 出库单详情
     *
     * @param orderId   销售订单id
     * @param orderType 订单类型
     * @return
     */
    @Override
    public List<BOutPlanListVo> selectOutByOrderIdAndOrderType(Integer orderId, String orderType) {
        return mapper.selectOutByOrderIdAndOrderType(orderId, orderType);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> newInsert(BOutPlanSaveVo vo) {

        // 生成计划单号
        String code = bOutPlanAutoCodeService.autoCode().getCode();
        // 赋值出库计划
        BOutPlanEntity outPlanEntity = (BOutPlanEntity) BeanUtilsSupport.copyProperties(vo, BOutPlanEntity.class);
        // 编号为空则设置自动生成的单号
        if (StringUtils.isEmpty(vo.getPlan_code())) {
            outPlanEntity.setCode(code);
        }
        int rtn = outPlanMapper.insert(outPlanEntity);
        vo.setPlan_id(outPlanEntity.getId());
        // 赋值出库计划详情
        List<BOutPlanDetailEntity> planDetailList = BeanUtilsSupport.copyProperties(vo.getDetailList(), BOutPlanDetailEntity.class);

        int no = 1;
        for (BOutPlanDetailEntity detail : planDetailList) {
            detail.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED);

            // 订单id
            detail.setOrder_id(vo.getOrder_id());
            detail.setOrder_type(vo.getOrder_type());
            detail.setOver_inventory_upper(vo.getOver_inventory_upper());
            detail.setOrder_detail_no(vo.getOrder_detail_no());
            // 计划单id
            detail.setPlan_id(outPlanEntity.getId());
            detail.setPending_volume(BigDecimal.ZERO);
            detail.setHas_handle_count(BigDecimal.ZERO);
            detail.setHas_handle_weight(BigDecimal.ZERO);
            detail.setHas_handle_volume(BigDecimal.ZERO);
            detail.setPending_count(detail.getCount());
            detail.setPending_weight(detail.getWeight());
            detail.setVolume(BigDecimal.ZERO);
            detail.setOver_release(Boolean.TRUE);
            // 按规则生成计划明细单号
            detail.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());
            // 设置序号
            detail.setNo(no);
            mapper.insert(detail);

            no++;
            // 生成待办
            todoService.insertTodo(detail.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);

        }

        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(vo.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            bBpmProcessVo.setCode(iBpmProcessTemplatesService.getBpmFLowCodeByType(SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN));
            bBpmProcessVo.setSerial_type(SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_OUT_PLAN);
            bBpmProcessVo.setForm_data(vo.getForm_data());
            bBpmProcessVo.setForm_json(vo);
            bBpmProcessVo.setForm_class(vo.getClass().getName());
            bBpmProcessVo.setSerial_id(outPlanEntity.getId());
            bBpmProcessVo.setInitial_process(vo.getInitial_process());
            bBpmProcessVo.setProcess_users(vo.getProcess_users());


            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动出库计划审批流
            String url = getBusinessCenterUrl("/scm/api/v1/bpm/process/createstartprocess");
            ResponseEntity<BBpmProcessVo> response = restTemplate.postForEntity(url, bBpmProcessVo, BBpmProcessVo.class);
            log.debug("===============启动审批流结果================" + response.getBody());
        }
        return InsertResultUtil.OK(rtn);
    }

    protected String getBusinessCenterUrl(String uri) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();

            String url = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .addPath(uri)
                    .build();
            return url.replaceAll("%2F", "/");
        } catch (Exception e) {
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }
}
