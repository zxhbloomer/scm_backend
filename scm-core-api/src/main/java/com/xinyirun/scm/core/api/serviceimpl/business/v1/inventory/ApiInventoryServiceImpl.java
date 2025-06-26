package com.xinyirun.scm.core.api.serviceimpl.business.v1.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.core.api.mapper.business.inventory.ApiInventoryMapper;
import com.xinyirun.scm.core.api.service.business.v1.inventory.ApiInventoryService;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiInventoryServiceImpl extends ApiBaseServiceImpl<ApiInventoryMapper, MInventoryEntity> implements ApiInventoryService {

    @Autowired
    private ApiInventoryMapper mInventoryMapper;

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
