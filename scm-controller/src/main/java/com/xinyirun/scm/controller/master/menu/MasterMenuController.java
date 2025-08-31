package com.xinyirun.scm.controller.master.menu;

import com.xinyirun.scm.bean.entity.master.menu.MMenuEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertOrUpdateResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuDataVo;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuRedirectVo;
import com.xinyirun.scm.bean.system.vo.master.menu.MMenuVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.menu.IMMenuService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/menus")
@Slf4j
// @Api(tags = "菜单相关")
public class MasterMenuController extends SystemBaseController {

    @Autowired
    private IMMenuService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取菜单主表信息")
    // @ApiOperation(value = "根据参数id，获取菜单主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MMenuVo>> list(@RequestBody(required = false) MMenuDataVo searchCondition) {
        MMenuVo entity = service.getTreeData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取级联信息")
    // @ApiOperation(value = "获取级联数据")
    @PostMapping("/cascader/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MMenuDataVo>>> cascaderList(@RequestBody(required = false) MMenuVo searchCondition) {
        List<MMenuDataVo> vo = service.getCascaderList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo, JsonResultTypeConstants.STRING_EMPTY_BOOLEAN_FALSE));
    }

    @SysLogAnnotion("系统菜单数据更新保存")
    // @ApiOperation(value = "系统菜单数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuDataVo>> save(@RequestBody(required = false) MMenuEntity bean) {
        UpdateResultAo<MMenuDataVo> rtn = service.update(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"系统菜单数据更新保存成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("系统菜单数据新增菜单组")
    // @ApiOperation(value = "新增菜单组")
    @PostMapping("/addmenugroup")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuDataVo>> addMenuGroup(@RequestBody(required = false) MMenuDataVo bean) {
        InsertResultAo<MMenuDataVo> rtn = service.addMenuGroup(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增菜单组成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("系统菜单数据新增菜单组")
    // @ApiOperation(value = "新增顶部导航栏")
    @PostMapping("/addtopnav")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuDataVo>> addTopNav(@RequestBody(required = false) MMenuDataVo bean) {
        InsertResultAo<MMenuDataVo> rtn = service.addTopNav(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增菜单组成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("系统菜单数据新增菜单组")
    // @ApiOperation(value = "新增子节点")
    @PostMapping("/addsubnode")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuDataVo>> addSubNode(@RequestBody(required = false) MMenuDataVo bean) {
        InsertResultAo<MMenuDataVo> rtn = service.addSubNode(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增菜单组成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("系统菜单数据新增菜单组")
    // @ApiOperation(value = "新增子菜单")
    @PostMapping("/addsubmenu")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuDataVo>> addSubMenu(@RequestBody(required = false) MMenuDataVo bean) {
        InsertResultAo<MMenuDataVo> rtn = service.addSubMenu(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增菜单组成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("模块按钮表数据逻辑物理删除，部分数据")
    // @ApiOperation(value = "根据参数id，逻辑删除数据")
    @PostMapping("/realdelete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> realDelete(@RequestBody(required = false) MMenuDataVo searchCondition) {
        if(searchCondition == null) {
            return ResponseEntity.ok().body(ResultUtil.OK("没有数据"));
        } else {
            service.realDeleteByCode(searchCondition);
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("系统菜单数据更新保存，拖拽后，全量更新")
    // @ApiOperation(value = "系统菜单数据更新保存，拖拽后，全量更新")
    @PostMapping("/dragsave")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> dragsave(@RequestBody(required = false) List<MMenuDataVo> beans) {
        service.dragsave(beans);
        return ResponseEntity.ok().body(ResultUtil.OK("拖拽更新成功"));
    }

    @SysLogAnnotion("菜单重定向更新保存")
    // @ApiOperation(value = "菜单重定向更新保存")
    @PostMapping("/redirect/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MMenuRedirectVo>> saveRedirect(@RequestBody(required = false) MMenuRedirectVo bean) {
        InsertOrUpdateResultAo<MMenuRedirectVo> rtn = service.saveRedirect(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"菜单重定向更新保存成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取根菜单节点列表")
    @PostMapping("/root/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MMenuDataVo>>> getRootMenuList() {
        List<MMenuDataVo> rootMenus = service.getRootMenuList();
        return ResponseEntity.ok().body(ResultUtil.OK(rootMenus));
    }
}
