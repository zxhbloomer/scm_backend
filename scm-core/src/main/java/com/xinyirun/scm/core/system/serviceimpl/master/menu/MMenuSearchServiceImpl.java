package com.xinyirun.scm.core.system.serviceimpl.master.menu;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.menu.MStaffMenuCollectionEntity;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchCacheDataVo;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchDataTitleVo;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.menu.MMenuMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.user.MUserPermissionRbacMapper;
import com.xinyirun.scm.core.system.service.master.menu.IMStaffMenuCollectionService;
import com.xinyirun.scm.core.system.service.master.menu.ISMenuSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/12/29 15:37
 */

@Service
public class MMenuSearchServiceImpl implements ISMenuSearchService {

    @Autowired
    private MUserPermissionRbacMapper rbacMapper;

    @Autowired
    private MMenuMapper menuMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IMStaffMenuCollectionService collectionService;


    /**
     * 查询当前用户下的
     *
     * @return List<MMenuDataVo>
     */
    @Override
    @Cacheable(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE,
            key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #staffId")
    public List<MMenuSearchCacheDataVo> searchAll(Long staffId) {
        List<MMenuSearchCacheDataVo> result = new LinkedList<>();
        Long permissionMenuRootId = rbacMapper.getPermissionMenuRootId(staffId);
        List<MMenuSearchDataVo> list = menuMapper.selectAllByStaffId(staffId, permissionMenuRootId);
        list.forEach(this::setPinYin);
        // 组装数据, 递归方式
        generateRoutes(list, result);
        return result;
    }

    /**
     * 组装菜单
     * @param list 全部菜单
     * @param result 返回菜单
     */
    private void generateRoutes(List<MMenuSearchDataVo> list, List<MMenuSearchCacheDataVo> result) {
        // 先获取全部页面
        list.stream().filter(item -> DictConstant.DICT_SYS_MENU_TYPE_PAGE.equals(item.getType()))
                .forEach(item -> generateRoutes(item, list, new MMenuSearchCacheDataVo(), new LinkedList<>(),new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),result));
    }

    /**
     * 组长菜单
     * @param data
     * @param list 全部菜单
     * @param result
     * @param title 列表名称
     * @param meta 元数据
     * @param name_py 名称拼音
     * @param name_simple_py 名称简拼
     * @param rtn
     */
    private void generateRoutes(MMenuSearchDataVo data, List<MMenuSearchDataVo> list, MMenuSearchCacheDataVo result,
                                List<String> title, List<MMenuSearchDataTitleVo> meta, List<String> name_py,
                                List<String> name_simple_py, List<MMenuSearchCacheDataVo> rtn) {
        // 如果类型是 R, 添加总数据返回
        if (DictConstant.DICT_SYS_MENU_TYPE_ROOT.equals(data.getType())) {
            result.setTitle(title);
            result.setMeta(meta);
            result.setName_py(name_py);
            result.setName_simple_py(name_simple_py);
            rtn.add(result);
            return;
        } else if (DictConstant.DICT_SYS_MENU_TYPE_PAGE.equals(data.getType())) {
            // 只有页面类型的, 保存ID
            result.setMenu_id(data.getId());
            result.setPath(data.getPath());
            result.setIs_collection(data.getIs_collection());
        }
        title.add(0, data.getMeta_title());
        meta.add(0, data.getMeta());
        name_py.add(0, data.getName_py());
        name_simple_py.add(0, data.getName_first_py());
        // 遍历list
        for (MMenuSearchDataVo mMenuSearchDataVo : list) {
            if (null != data.getParent_id() && data.getParent_id().equals(mMenuSearchDataVo.getId())) {
                generateRoutes(mMenuSearchDataVo, list, result, title,meta,name_py, name_simple_py, rtn);
            }
        }
    }

    /**
     * 新增查询缓存
     *
     * @param staffId 用户id
     * @param param    json
     */
    @Override
    public String insertSearchCache(Long staffId, MMenuSearchCacheDataVo param) {
        String key = DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_HISTORY + "::" + staffId;
        // 现获取redis缓存
        String jsonList = redisUtil.getString(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_HISTORY + "::" + staffId);
        if (StringUtils.isBlank(jsonList)) {
            // 添加缓存
            redisUtil.set(key, JSON.toJSONString(List.of(param)));
            return null;
        } else {
            // 解析json
            List<MMenuSearchCacheDataVo> list = JSONArray.parseArray(jsonList, MMenuSearchCacheDataVo.class);
            // 判断是否有相同数据, 根据param.meta.menu_id判断
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getMenu_id(), param.getMenu_id())) {
                    index = i;
                }
            }
            // 如果index为 -1, 直接添加元素, 如果不为-1,
            if (index == -1 && list.size() < 5) {
                list.add(0, param);
            } else if (index == -1 && list.size() >= 5) {
                // 删除最后一个元素
                list.remove(list.size() - 1);
                list.add(0, param);
            } else if (index != -1) {
                // 替换
                Collections.rotate(list.subList(0, index + 1), 1);
            }
            redisUtil.set(key, JSON.toJSONString(list));
            return null;
        }


    }

    @Override
    public List<MMenuSearchCacheDataVo> getCollection(Long staffId, MMenuSearchCacheDataVo json) {
        // 查询所有
        List<MMenuSearchCacheDataVo> list = searchAll(staffId);
        List<Long> collectionList = getCollectionMenuByStaffId(staffId);
        if (CollectionUtils.isEmpty(collectionList))
            return null;
        return list.stream().filter(item -> collectionList.contains(item.getMenu_id())).toList();
    }

    /**
     * 删除历史记录
     *
     * @param staffId 用户id
     * @param json
     */
    @Override
    public void deleteHistory(Long staffId, MMenuSearchCacheDataVo json) {
        String key = DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_HISTORY + "::" + staffId;
        // 现获取redis缓存
        String jsonList = redisUtil.getString(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_HISTORY + "::" + staffId);
        if (StringUtils.isBlank(jsonList)) {
            return;
        }
        List<MMenuSearchCacheDataVo> list = JSONArray.parseArray(jsonList, MMenuSearchCacheDataVo.class);
        // 判断是否有相同数据, 根据param.meta.menu_id判断
        list.removeIf(item -> Objects.equals(item.getMenu_id(), json.getMenu_id()));
        if (CollectionUtils.isEmpty(list)) {
            redisUtil.delete(key);
            return;
        }
        redisUtil.set(key, JSON.toJSONString(list));
    }

    /**
     * 获取缓存
     *
     * @param staffId 用户id
     * @return 返回
     */
    @Override
    public List<MMenuSearchCacheDataVo> getHistoryCache(Long staffId) {
        // 现获取redis缓存
        String jsonList = redisUtil.getString(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_HISTORY + "::" + staffId);
        if (StringUtils.isBlank(jsonList)) {
            return null;
        } else {
            // 查询当前用户的菜单
            Long permissionMenuRootId = rbacMapper.getPermissionMenuRootId(staffId);
            List<MMenuSearchDataVo> menuList = menuMapper.selectAllByStaffId(staffId, permissionMenuRootId);
            List<Long> menuIdList = menuList.stream().filter(item -> DictConstant.DICT_SYS_MENU_TYPE_PAGE.equals(item.getType()))
                    .map(MMenuSearchDataVo::getId).toList();
            List<MMenuSearchCacheDataVo> list = JSONArray.parseArray(jsonList, MMenuSearchCacheDataVo.class);
            List<Long> collectionList = getCollectionMenuByStaffId(staffId);
            // 过滤掉没有权限的, 在匹配是否已收藏的
            List<MMenuSearchCacheDataVo> result = list.stream().filter(item -> menuIdList.contains(item.getMenu_id())).toList();
            if (CollectionUtils.isEmpty(collectionList)) {
                result.forEach(item -> item.setIs_collection(false));
                return result;
            } else {
                result.forEach(item -> item.setIs_collection(collectionList.contains(item.getMenu_id())));
                return result;
            }
        }
    }

    /**
     * 获取名称拼音
     * @param item
     */
    private void setPinYin(MMenuSearchDataVo item) {
        item.setName_py(Pinyin.toPinyin(item.getMeta_title(), ""));
        StringBuilder str = new StringBuilder();
        for (char c : item.getMeta_title().toCharArray()) {
            str.append(Pinyin.toPinyin(c).charAt(0));
        }
        item.setName_first_py(str.toString());
    }


    /**
     * 获取已收藏的菜单
     * @param staffId
     * @return
     */
    private List<Long> getCollectionMenuByStaffId(Long staffId) {
        // 获取已收藏的
        return collectionService.getBaseMapper().selectList(Wrappers.<MStaffMenuCollectionEntity>lambdaQuery()
                .eq(MStaffMenuCollectionEntity::getStaff_id, staffId)).stream().map(MStaffMenuCollectionEntity::getMenu_id).toList();
    }
}
