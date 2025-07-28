package com.xinyirun.scm.core.system.serviceimpl.business.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.order.BOrderInvertedEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedExportVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.order.BOrderInvertedMapper;
import com.xinyirun.scm.core.system.service.business.order.IBOrderInvertedService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-08-20
 */
@Service
public class BOrderInvertedServiceImpl extends ServiceImpl<BOrderInvertedMapper, BOrderInvertedEntity> implements IBOrderInvertedService {

    @Autowired
    private BOrderInvertedMapper mapper;

    @Autowired
    private ISConfigService configService;

    /**
     * 稻谷出库计划倒排表
     *
     * @param searchCondition
     */
    @Override
    public List<BOrderInvertedVo> queryInvertedOrderOutPlan(BOrderInvertedVo searchCondition) {
        List<BOrderInvertedVo> pageList = null;

        // 分页条件
        Page<BOrderInvertedVo> pageCondition =  new Page();
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());


        // 判断查询  当天时实数据 or 快照数据
        if (searchCondition.getDate() != null && LocalDateTime.now().toLocalDate().equals(searchCondition.getDate().toLocalDate())) {
            pageList = mapper.queryRealInvertedOrder(pageCondition,searchCondition);
        } else if (searchCondition.getDate() != null) {
            pageList = mapper.querySnapshotInvertedOrder(pageCondition,searchCondition);
        }
        return pageList;
    }

    /**
     * 稻谷出库计划倒排表 日期组件数据获取
     *
     * @param searchCondition
     */
    @Override
    public BOrderInvertedVo getBadgeDate(BOrderInvertedVo searchCondition) {
        List<String> list = mapper.getBadgeDate(searchCondition);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }

//        list.add(String.valueOf(LocalDateTime.now().toLocalDate()));
        BOrderInvertedVo invertedVo  = new BOrderInvertedVo();
        invertedVo.setBadgeDate(list.stream().toArray(String[]::new));
        invertedVo.setDate(LocalDateTime.now());
        return invertedVo;
    }

    /**
     * 稻谷出库计划倒排表导出全部
     */
    @Override
    public List<BOrderInvertedExportVo> queryInvertedExportAll(BOrderInvertedVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }

        // 判断查询  当天时实数据 or
        if (param.getDate() != null && LocalDateTime.now().toLocalDate().equals(param.getDate().toLocalDate())) {
            return mapper.queryRealInvertedExportAll(param);
        } else if (param.getDate() != null) {
            // 导出快照数据
            return mapper.querySnapshotInvertedExportAll(param);
        }

        return null;
    }

    /**
     * 稻谷出库计划倒排表导出部分
     */
    @Override
    public List<BOrderInvertedExportVo> queryInvertedExport(BOrderInvertedVo param) {

        // 判断查询 当天时实数据
        if (param.getDate() != null && LocalDateTime.now().toLocalDate().equals(param.getDate().toLocalDate())) {
            return mapper.queryRealInvertedExport(param);
        } else if (param.getDate() != null) {
            // 导出快照数据
            return mapper.querySnapshotInvertedExport(param);
        }

        return null;
    }

    /**
     * 稻谷出库计划倒排表获取竞拍下拉列表
     *
     * @param searchCondition
     */
    @Override
    public BOrderInvertedVo getAuctionDateList(BOrderInvertedVo searchCondition) {
        List<String> list = mapper.getAuctionDateList();
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }

        BOrderInvertedVo invertedVo  = new BOrderInvertedVo();
        invertedVo.setAuctionDateList(list.stream().toArray(String[]::new));
        return invertedVo;
    }
}
