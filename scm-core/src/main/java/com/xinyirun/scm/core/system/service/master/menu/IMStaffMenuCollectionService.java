package com.xinyirun.scm.core.system.service.master.menu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.menu.MStaffMenuCollectionEntity;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchCacheDataVo;

public interface IMStaffMenuCollectionService extends IService<MStaffMenuCollectionEntity> {

    void saveCollection(Long userSessionStaffId, MMenuSearchCacheDataVo json);
}
