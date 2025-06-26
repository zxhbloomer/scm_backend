package com.xinyirun.scm.controller.master.menu;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuSearchCacheDataVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.menu.IMStaffMenuCollectionService;
import com.xinyirun.scm.core.system.service.master.menu.ISMenuSearchService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Wqf
 * @Description: 菜单搜索, 仿百度,淘宝
 * @CreateTime : 2023/12/29 15:31
 */

@RestController
@RequestMapping(value = "/api/v1/menus/cache")
public class SMenuSearchController extends SystemBaseController {

    @Autowired
    private ISMenuSearchService service;


    @Autowired
    private IMStaffMenuCollectionService collectionService;

    @SysLogAnnotion("查询当前用户的所有菜单")
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Object>> search() {
        List<MMenuSearchCacheDataVo> rtn = service.searchAll(getUserSessionStaffId());
        Object json1 = JSON.toJSON(rtn);
        return ResponseEntity.ok().body(ResultUtil.OK(json1));
    }

    @SysLogAnnotion("新增查询缓存")
    @PostMapping("/save")
    public ResponseEntity<JsonResultAo<String>> insertSearchCache(@RequestBody MMenuSearchCacheDataVo json) {
        service.insertSearchCache(getUserSessionStaffId(), json);
        return ResponseEntity.ok().body(ResultUtil.OK("保存成功"));
    }

    @SysLogAnnotion("查询历史记录")
    @GetMapping("/history")
    public ResponseEntity<JsonResultAo<List<MMenuSearchCacheDataVo>>> getHistoryCache() {
        List<MMenuSearchCacheDataVo> searchCache = service.getHistoryCache(getUserSessionStaffId());
        return ResponseEntity.ok().body(ResultUtil.OK(searchCache));
    }

    @SysLogAnnotion("添加收藏")
    @PostMapping("/savecollection")
    public ResponseEntity<JsonResultAo<String>> saveCollection(@RequestBody MMenuSearchCacheDataVo json) {
        collectionService.saveCollection(getUserSessionStaffId(), json);
        return ResponseEntity.ok().body(ResultUtil.OK("保存成功"));
    }

    @SysLogAnnotion("查询收藏")
    @PostMapping("/getcollection")
    public ResponseEntity<JsonResultAo<Object>> getCollection(@RequestBody MMenuSearchCacheDataVo json) {
        List<MMenuSearchCacheDataVo> collection = service.getCollection(getUserSessionStaffId(), json);
//        JSON.toJSONString(collection, JSONWriter.Feature.ReferenceDetection);\
        Object json1 = JSON.toJSON(collection);
        return ResponseEntity.ok().body(ResultUtil.OK(json1));
    }



    @SysLogAnnotion("删除历史记录")
    @PostMapping("/deletehistory")
    public ResponseEntity<JsonResultAo<String>> deleteHistory(@RequestBody MMenuSearchCacheDataVo json) {
        service.deleteHistory(getUserSessionStaffId(), json);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }



}
