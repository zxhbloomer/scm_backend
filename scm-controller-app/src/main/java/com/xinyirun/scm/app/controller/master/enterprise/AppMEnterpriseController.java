package com.xinyirun.scm.app.controller.master.enterprise;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.master.enterprise.AppMEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.LimitAnnotion;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.app.service.master.enterprise.AppIMEnterpriseService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  企业 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/app/v1/enterprise")
public class AppMEnterpriseController extends AppBaseController {

    @Autowired
    private AppIMEnterpriseService service;

    @Autowired
    private ISPagesService isPagesService;

    @SysLogAppAnnotion("根据查询条件，获取企业列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<IPage<AppMEnterpriseVo>>> pageList(@RequestBody(required = false) AppMEnterpriseVo searchCondition) {
        IPage<AppMEnterpriseVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(list));
    }

    @SysLogAppAnnotion("获取企业类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<List<AppMEnterpriseVo>>> getType(@RequestBody(required = false) AppMEnterpriseVo searchCondition) {
        List<AppMEnterpriseVo> list = service.getType(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(list));
    }

//    @SysLogAppAnnotion("企业数据更新保存")
//    @PostMapping("/insert")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> insert(@RequestBody(required = false) AppMEnterpriseVo bean) {
//
//        if(service.insert(bean).isSuccess()){
//            return ResponseEntity.ok().body(AppResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存失败。");
//        }
//    }


    @SysLogAppAnnotion("企业数据校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<String>> checkLogic(@RequestBody(required = false) AppMEnterpriseVo bean) {
        AppCheckResultAo AppCheckResultAo = service.checkLogic(bean, bean.getCheck_type());
        if (!AppCheckResultAo.isSuccess()) {
            throw new BusinessException(AppCheckResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
        }
    }


    @SysLogAppAnnotion("企业数据更新保存,增加审批流")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> insert(@RequestBody(required = false) AppMEnterpriseVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(AppResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存失败。");
        }
    }

    @SysLogAppAnnotion("获取企业详情")
    @PostMapping("/get")
    @LimitAnnotion(key = "AppMEnterpriseController.getDetail", period = 10, count = 1, name = "获取企业详情", prefix = "limit")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> getDetail(@RequestBody(required = false) AppMEnterpriseVo searchCondition) {
        AppMEnterpriseVo AppMEnterpriseVo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(AppMEnterpriseVo));
    }


//    @SysLogAppAnnotion("企业数据更新保存")
//    @PostMapping("/save")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> save(@RequestBody(required = false) AppMEnterpriseVo bean) {
//
//        if(service.update(bean).isSuccess()){
//            return ResponseEntity.ok().body(AppResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }

    @SysLogAppAnnotion("企业数据更新保存,增加审批流")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> save(@RequestBody(required = false) AppMEnterpriseVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(AppResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAppAnnotion("删除企业")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> delete(@RequestBody(required = false)List<AppMEnterpriseVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(AppResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAppAnnotion("导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) AppMEnterpriseVo searchConditionList, HttpServletResponse response) throws IOException {
        List<MEnterpiseExcelVo> list = service.export(searchConditionList);
        new EasyExcelUtil<>(MEnterpiseExcelVo.class).exportExcel("企业管理"  + DateTimeUtil.getDate(),"企业管理",list, response);
    }

    @SysLogAppAnnotion("全部导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) AppMEnterpriseVo searchConditionList, HttpServletResponse response) throws IOException {
        List<MEnterpiseExcelVo> list = service.exportAll(searchConditionList);
        new EasyExcelUtil<>(MEnterpiseExcelVo.class).exportExcel("企业管理"  + DateTimeUtil.getDate(),"企业管理",list, response);
    }

    @SysLogAppAnnotion("根据查询条件，获取企业列表")
    @PostMapping("/pagelistbyadjust")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<IPage<AppMEnterpriseVo>>> pagelistByAdjust(@RequestBody(required = false) AppMEnterpriseVo searchCondition) {
        IPage<AppMEnterpriseVo> list = service.pagelistByAdjust(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(list));
    }

    @SysLogAppAnnotion("获取企业调整信息")
    @PostMapping("/getadjustdetail")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppMEnterpriseVo>> getAdjustDetail(@RequestBody(required = false) AppMEnterpriseVo searchCondition) {
        AppMEnterpriseVo AppMEnterpriseVo = service.getAdjustDetail(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(AppMEnterpriseVo));
    }

}
