package com.xinyirun.scm.core.system.serviceimpl.business.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.order.BOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.order.BOrderMapper;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2022-03-02
 */
@Service
public class BOrderServiceImpl extends BaseServiceImpl<BOrderMapper, BOrderEntity> implements IBOrderService {

    @Autowired
    private BOrderMapper mapper;

    @Autowired
    private ISConfigService configService;

    @Override
    public IPage<BOrderVo> selectPage(BOrderVo searchCondition) {

        // 分页条件
        Page<BOrderEntity> pageCondition =  new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<BOrderVo> pageList = mapper.selectPage(pageCondition, searchCondition);

//        List<BOrderVo> bOrderVoList = new ArrayList<>();
//        for (BOrderVo vo : pageList.getRecords()) {
//            BOrderGoodsVo bOrderGoodsVo = new BOrderGoodsVo();
//            bOrderGoodsVo.setOrder_id(vo.getSerial_id());
//            if (Objects.equals(vo.getSerial_type(), SystemConstants.ORDER.B_IN_ORDER)) {
//                List<BOrderGoodsVo> orderGoodsVoList = mapper.selectInGoodsList(bOrderGoodsVo);
//                vo.setDetailListData(orderGoodsVoList);
//            } else if (Objects.equals(vo.getSerial_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
//                List<BOrderGoodsVo> orderGoodsVoList = mapper.selectOutGoodsList(bOrderGoodsVo);
//                vo.setDetailListData(orderGoodsVoList);
//            }
//
//            bOrderVoList.add(vo);
//        }
//        pageList.setRecords(bOrderVoList);

        return pageList;
    }

    @Override
    public BOrderVo selectByOrderNo(BOrderVo searchCondition) {
        return mapper.selectDetailByOrderNo(searchCondition);
    }

    @Override
    public BOrderVo selectOrder(String order_type, Integer order_id) {
        BOrderVo searchCondition = new BOrderVo();
        searchCondition.setSerial_type(order_type);
        searchCondition.setSerial_id(order_id);
        return mapper.selectOrder(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BOrderVo vo) {
        BOrderEntity entity = new BOrderEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 根据查询条件，获取订单信息 用于合同号是多个的情况
     *
     * @param searchCondition 实体对象
     * @return
     */
    @Override
    public IPage<BOrderVo> selectPage2(BOrderVo searchCondition) {
        // 分页条件
        Page<BOrderEntity> pageCondition =  new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<BOrderVo> pageList = mapper.selectPage2(pageCondition, searchCondition);
        return pageList;
    }
}
