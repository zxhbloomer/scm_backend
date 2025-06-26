package com.xinyirun.scm.core.system.serviceimpl.master.menu;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinyirun.scm.bean.entity.master.menu.MStaffMenuCollectionEntity;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchCacheDataVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.master.menu.IMStaffMenuCollectionMapper;
import com.xinyirun.scm.core.system.service.master.menu.IMStaffMenuCollectionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  菜单 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class IMStaffMenuCollectionServiceImpl extends BaseServiceImpl<IMStaffMenuCollectionMapper, MStaffMenuCollectionEntity> implements IMStaffMenuCollectionService {

    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, key = "#staffId")
    public void saveCollection(Long staffId, MMenuSearchCacheDataVo vo) {
        // 查詢當前用户下的收藏
        List<MStaffMenuCollectionEntity> list = baseMapper.selectList(Wrappers.<MStaffMenuCollectionEntity>lambdaQuery()
                .allEq(Map.of(MStaffMenuCollectionEntity::getStaff_id, staffId, MStaffMenuCollectionEntity::getMenu_id, vo.getMenu_id())));
        if (CollectionUtils.isEmpty(list) && vo.getIs_collection()) {
            // 新增
            MStaffMenuCollectionEntity entity = new MStaffMenuCollectionEntity();
            entity.setStaff_id(staffId);
            entity.setMenu_id(vo.getMenu_id());
            entity.set_collection(true);
            baseMapper.insert(entity);
        }
        // 如果取消收藏, 删除
        if (!CollectionUtils.isEmpty(list) && !vo.getIs_collection()) {
            baseMapper.deleteBatchIds(list.stream().map(MStaffMenuCollectionEntity::getId).toList());
        }
    }
}
