package com.xinyirun.scm.core.system.serviceimpl.business.monitor;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BCarriageOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.BCarriageOrderGoodsEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.monitor.BCarriageOrderGoodsMapper;
import com.xinyirun.scm.core.system.mapper.business.monitor.BCarriageOrderMapper;
import com.xinyirun.scm.core.system.service.business.monitor.IBCarriageOrderService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BCarriageAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/8/21 14:23
 */

@Service
public class BCarriageOrderServiceImpl extends ServiceImpl<BCarriageOrderMapper, BCarriageOrderEntity> implements IBCarriageOrderService {

    @Autowired
    private BCarriageOrderMapper mapper;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private BCarriageOrderGoodsMapper goodsMapper;

    @Autowired
    private BCarriageAutoCodeServiceImpl autoCodeService;


    /**
     * 頁面查詢
     *
     * @param param
     * @return
     */
    @Override
    public IPage<BCarriageOrderVo> selectPage(BCarriageOrderVo param) {

        // 分页条件
        Page<BCarriageOrderVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.selectPageList(param, pageCondition);
    }

    /**
     * 导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BCarriageOrderExportVo> exportList(BCarriageOrderVo param) {
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (param.getIds() != null && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportList(param);
    }

    /**
     * 新增
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BCarriageOrderVo> insert(BCarriageOrderVo param) {
        check(param);

        // 订单编号
        String code = autoCodeService.autoCode().getCode();

        // 创建 实体类
        BCarriageOrderEntity entity = createEntityBuilder(param);
        entity.setOrderNo(code);
        mapper.insert(entity);
        // 新增详情
        BCarriageOrderGoodsEntity goodsEntity = new BCarriageOrderGoodsEntity();
        goodsEntity.setOrderId(entity.getId());
        goodsEntity.setOrderNo(param.getOrder_no());
//        goodsEntity.setNo("1");
//        goodsEntity.setPrice(param.getPrice());
//        goodsEntity.setNum(param.getNum());
        goodsMapper.insert(goodsEntity);

        // 查询新增的数据
        BCarriageOrderVo result = getVoById(entity.getId());
        return InsertResultUtil.OK(result);
    }

    /**
     * 查询详情
     *
     * @param id 订单 id
     * @return
     */
    @Override
    public BCarriageOrderVo getVoById(Integer id) {
        return mapper.selectVoById(id);
    }

    /**
     * 更新
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BCarriageOrderVo> updateByParam(BCarriageOrderVo param) {
        Assert.notNull(param.getId(), "ID不能为空");
        check(param);
        BCarriageOrderEntity entity = mapper.selectById(param.getId());
        BCarriageOrderEntity newEntity = createEntityBuilder(param);
        newEntity.setId(entity.getId());
        newEntity.setC_time(entity.getC_time());
        newEntity.setC_id(entity.getC_id());
        mapper.updateById(newEntity);

        // 更新详情
        List<BCarriageOrderGoodsEntity> goodsEntities = goodsMapper.selectList(Wrappers.<BCarriageOrderGoodsEntity>lambdaQuery()
                .eq(BCarriageOrderGoodsEntity::getOrderId, param.getId()));
        goodsEntities.forEach(item -> {
            item.setOrderNo(param.getOrder_no());
            goodsMapper.updateById(item);
        });

        // 查询详情
        BCarriageOrderVo result = getVoById(entity.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 创建实体类
     * @param param
     * @return
     */
    private BCarriageOrderEntity createEntityBuilder(BCarriageOrderVo param) {
        return BCarriageOrderEntity.builder()
                .orderNo(param.getOrder_no())
                .carriageContractCode(param.getCarriage_contract_code())
                .companyName(param.getCompany_name())
                .orgName(param.getOrg_name())
                .sign_dt(param.getSign_dt())
                .deadline_dt(param.getDeadline_dt())
                .transportTypeName(param.getTransport_type_name())
                .originPlace(param.getOrigin_place())
                .destinationPlace(param.getDestination_place())
                .price(param.getPrice())
                .num(param.getNum())
                .transportAmount(param.getTransport_amount())
                .status("执行中")
                .transportAmountTax(param.getTransport_amount_tax())
                .haul_distance(param.getHaul_distance())
                .sales_contract_code(param.getSales_contract_code())
                .pay_type(param.getPay_type())
                .total_amount(param.getTotal_amount())
                .build();
    }

    /**
     * 新增, 修改校验
     *
     * @param param
     */
    private void check(BCarriageOrderVo param) {
        // 编号不能重复
        List<Integer> ids = mapper.select2Validation(param.getOrder_no(), param.getId());
        if (!CollectionUtils.isEmpty(ids)) {
            throw new InsertErrorException("新增失败, 订单编号重复!");
        }

    }
}
