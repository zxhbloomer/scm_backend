package com.xinyirun.scm.controller.master.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffExportVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/staff")
@Slf4j
// @Api(tags = "员工主表相关")
public class MasterStaffController extends SystemBaseController {

    @Autowired
    private IMStaffService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取员工主表信息")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MStaffVo>>> list(@RequestBody(required = false) MStaffVo searchCondition) {
        IPage<MStaffVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("获取个人信息接口")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffVo>> getDetail(@RequestBody(required = false) MStaffVo searchCondition) {
        MStaffVo vo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("员工主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> save(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.update(bean, request.getSession()).isSuccess()) {
            super.doResetUserSessionByStaffId(bean.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("当前员工数据更新保存")
    @PostMapping("/self/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> saveSelf(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.updateSelf(bean).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("员工主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> insert(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.insert(bean, request.getSession()).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("员工头像初始化")
    @GetMapping("/avatar/init")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> insertAvatar() {
        service.initAvatar();
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("员工主表数据导出 全部")
    // @ApiOperation(value = "根据选择的数据，员工主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MStaffVo searchCondition, HttpServletResponse response)
            throws IOException {
        List<MStaffExportVo> searchResult = service.selectExportAllList(searchCondition);
        ExcelUtil<MStaffExportVo> util = new ExcelUtil<>(MStaffExportVo.class);
        util.exportExcel("员工主表数据导出", "员工主表数据", searchResult, response);
    }

    @SysLogAnnotion("员工主表数据导出 部分")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MStaffVo> searchConditionList,
                                HttpServletResponse response) throws IOException {
        List<MStaffExportVo> rtnList = service.selectExportList(searchConditionList);
//        List<MStaffExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MStaffExportVo.class);
        ExcelUtil<MStaffExportVo> util = new ExcelUtil<>(MStaffExportVo.class);
        util.exportExcel("员工主表数据导出", "员工主表数据", rtnList, response);
    }

    @SysLogAnnotion("员工主表数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MStaffVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("查询岗位员工")
    @PostMapping("/position/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffPositionVo>> getPositionStaffData(@RequestBody(required = false) MStaffPositionVo searchCondition) {
        MStaffPositionVo vo = service.getPositionStaffData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("设置员工岗位")
    @PostMapping("/position/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setPositionStaff(@RequestBody(required = false) MStaffPositionVo searchCondition) {
        service.setPositionStaff(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("头像上传")
    // @ApiOperation(value = "app:头像上传")
    @PostMapping("/avatar")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<String>> saveAvatar(String url) {
        service.saveAvatar(url);
        return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
    }

}
