package com.xinyirun.scm.core.system.service.master.menu;

import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchCacheDataVo;

import java.util.List;

public interface ISMenuSearchService {

    /**
     * 查询当前用户下的所有菜单
     * @return
     */
    List<MMenuSearchCacheDataVo> searchAll(Long staffId);

    /**
     * 获取历史缓存记录
     * @param userSessionStaffId
     * @return
     */
    List<MMenuSearchCacheDataVo> getHistoryCache(Long userSessionStaffId);

    /**
     * 新增查询缓存
     * @param staffId 用户id
     * @param json json
     */
    String insertSearchCache(Long staffId, MMenuSearchCacheDataVo json);

    List<MMenuSearchCacheDataVo>  getCollection(Long userSessionStaffId, MMenuSearchCacheDataVo json);

    /**
     * 删除历史记录
     * @param staffId 用户id
     * @param json
     */
    void deleteHistory(Long staffId, MMenuSearchCacheDataVo json);
}
