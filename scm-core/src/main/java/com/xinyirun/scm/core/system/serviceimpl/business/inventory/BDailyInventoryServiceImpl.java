package com.xinyirun.scm.core.system.serviceimpl.business.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventorySumVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MDailyInventoryExportVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.inventory.BDailyInventoryMapper;
import com.xinyirun.scm.core.system.mapper.master.inventory.MInventoryMapper;
import com.xinyirun.scm.core.system.service.business.inventory.IBDailyInventoryService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 入库单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class BDailyInventoryServiceImpl extends BaseServiceImpl<BDailyInventoryMapper, BDailyInventoryEntity> implements IBDailyInventoryService {

    @Autowired
    private BDailyInventoryMapper mapper;

    @Autowired
    private MInventoryMapper mInventoryMapper;

    @Autowired
    private ISConfigService configService;

    /**
     * 查询分页列表
     * @param searchCondition 查询条件
     * @return 数据列表
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public IPage<BDailyInventoryVo> selectPage(BDailyInventoryVo searchCondition) {
        // 分页条件
        Page<BDailyInventoryEntity> pageCondition =  new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "warehouse_id")
    public IPage<BDailyInventoryVo> selectPageNew(BDailyInventoryVo searchCondition) {
        // 分页条件
        Page<BDailyInventoryEntity> pageCondition =  new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPageNew(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public BDailyInventorySumVo selectSumData(BDailyInventoryVo searchCondition) {
        return mapper.selectSumData(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public BDailyInventorySumVo selectSumDataNew(BDailyInventoryVo searchCondition) {
        return mapper.selectSumDataNew(searchCondition);
    }

    @Override
    public List<MDailyInventoryExportVo> selectExportList(List<BDailyInventoryVo> searchCondition) {
        return mapper.selectExportListNew(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "tt1.warehouse_id")
    public List<MDailyInventoryExportVo> selectExportAllList(BDailyInventoryVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportAllListNew(searchCondition);
    }



    /**
     * 分页查询库存
     */
    @Override
    public List<ApiInventoryVo> getInventory(ApiInventoryVo vo) {
        // 分页条件
        Page<MInventoryEntity> pageCondition ;
        long current = 0;
        long size = 50;
        if (vo.getPaging() == null) {
            vo.setPaging(Boolean.FALSE);
        }
        // 判断是否启用分页
        if(vo.getPaging()) {
            if(vo.getCurrent() != null) {
                current = vo.getCurrent();
            }
            if(vo.getSize() != null) {
                size = vo.getSize();
            }
        }
        pageCondition =  new Page(current, size);
        // 通过page进行排序
        PageUtil.setSort(pageCondition, "id");
        IPage<ApiInventoryVo> page = mInventoryMapper.getInventory(pageCondition,vo);
        return page.getRecords();
    }

    @Override
    public List<ApiDailyInventoryVo> getDailyInventory() {
        // 查询每日库存数据
        return mInventoryMapper.getDailyInventory();
    }

    @Override
    public List<ApiMaterialConvertPriceVo> getMaterialConvertPrice() {
        // 查询物料转换价格数据
        return mInventoryMapper.getMaterialConvertPrice();
    }
}
