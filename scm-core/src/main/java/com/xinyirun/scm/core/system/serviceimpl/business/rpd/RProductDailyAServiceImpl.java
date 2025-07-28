package com.xinyirun.scm.core.system.serviceimpl.business.rpd;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyAEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyExportVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.core.system.mapper.business.rpd.RProductDailyAMapper;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyAService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 稻谷 加工日报表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Service
public class RProductDailyAServiceImpl extends ServiceImpl<RProductDailyAMapper, RProductDailyAEntity> implements IRProductDailyAService {

    @Autowired
    private RProductDailyAMapper mapper;

    /**
     *
     */
    @Override
    public void lockR_product_daily_a_10(BProductDailyVo vo) {
        mapper.lockR_product_daily_a_10(vo);
    }

    /**
     * 保存全部
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_a_20(BProductDailyVo vo) {
        // 删除符合条件的
        mapper.deleteR_product_daily_a_21(vo);

        // 新增符合条件的
        mapper.insertR_product_daily_a_22(vo);
    }

    /**
     * 分页查询
     *
     * @param param
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tab2.warehouse_id")
    public IPage<BProductDailyVo> selectPageList(BProductDailyVo param) {
        // 分页条件
        Page<BProductDailyVo> page = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(page, param.getPageCondition().getSort());
        // 查询入库计划page
        IPage<BProductDailyVo> result = mapper.selectPageList(page, param);
        return result;
    }

    /**
     * 加工报表 导出查询
     *
     * @param vo
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tab1.warehouse_id")
    public List<BProductDailyExportVo> exportList(BProductDailyVo vo) {
        return mapper.exportList(vo);
    }

    /**
     * 查询当前时间
     *
     * @return
     */
    @Override
    public LocalDateTime selectNowTime() {
        return mapper.selectNowTime();
    }

    /**
     * 加工报表, 根据每日库存生成
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_a_200(BProductDailyVo vo) {
        // 删除符合条件的
        mapper.deleteR_product_daily_a_201(vo);

        // 新增符合条件的
        mapper.insertR_product_daily_a_202(vo);
    }

    /**
     * @param vo
     */
    @Override
    public void lockR_product_daily_a_100(BProductDailyVo vo) {
        mapper.lockR_product_daily_a_100(vo);
    }

    /**
     * 加工报表, 合计
     *
     * @param vo
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tab2.warehouse_id")
    public List<BProductDailyVo> selectListSumApi(BProductDailyVo vo) {
        return mapper.selectListSumApi(vo);
    }
}
