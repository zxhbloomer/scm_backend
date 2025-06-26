package com.xinyirun.scm.controller.master.enterprise;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.enterprise.IMEnterpriseService;
import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
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
@RequestMapping(value = "/api/v1/enterprise")
public class MEnterpriseController extends SystemBaseController {

    @Autowired
    private IMEnterpriseService service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISLogImportService isLogImportService;

    @SysLogAnnotion("根据查询条件，获取企业列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> pageList(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取企业类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MEnterpriseVo>>> getType(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        List<MEnterpriseVo> list = service.getType(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

//    @SysLogAnnotion("企业数据更新保存")
//    @PostMapping("/insert")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> insert(@RequestBody(required = false) MEnterpriseVo bean) {
//
//        if(service.insert(bean).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存失败。");
//        }
//    }


    @SysLogAnnotion("企业数据校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) MEnterpriseVo bean) {
        CheckResultAo checkResultAo = service.checkLogic(bean, bean.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }


    @SysLogAnnotion("企业数据更新保存,增加审批流")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> insert(@RequestBody(required = false) MEnterpriseVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存失败。");
        }
    }

    @SysLogAnnotion("获取企业详情")
    @PostMapping("/getdetail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> getDetail(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        MEnterpriseVo mEnterpriseVo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(mEnterpriseVo));
    }


//    @SysLogAnnotion("企业数据更新保存")
//    @PostMapping("/save")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> save(@RequestBody(required = false) MEnterpriseVo bean) {
//
//        if(service.update(bean).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }

    @SysLogAnnotion("企业数据更新保存,增加审批流")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> save(@RequestBody(required = false) MEnterpriseVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("删除企业")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> delete(@RequestBody(required = false)List<MEnterpriseVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MEnterpriseVo searchConditionList, HttpServletResponse response) throws IOException {
        List<MEnterpiseExcelVo> list = service.export(searchConditionList);
        new EasyExcelUtil<>(MEnterpiseExcelVo.class).exportExcel("企业管理"  + DateTimeUtil.getDate(),"企业管理",list, response);
    }

    @SysLogAnnotion("全部导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MEnterpriseVo searchConditionList, HttpServletResponse response) throws IOException {
        List<MEnterpiseExcelVo> list = service.exportAll(searchConditionList);
        new EasyExcelUtil<>(MEnterpiseExcelVo.class).exportExcel("企业管理"  + DateTimeUtil.getDate(),"企业管理",list, response);
    }

    @SysLogAnnotion("根据查询条件，获取企业列表")
    @PostMapping("/adjustlist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MEnterpriseHisVo>>> getAdjustList(@RequestBody(required = false) MEnterpriseHisVo searchCondition) {
        List<MEnterpriseHisVo> list = service.getAdjustList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取企业调整信息")
    @PostMapping("/getadjustdetail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> getAdjustDetail(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        MEnterpriseVo mEnterpriseVo = service.getAdjustDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(mEnterpriseVo));
    }


    @SysLogAnnotion("企业数据导入")
    @PostMapping("/import")
    public ResponseEntity<JsonResultAo<Object>> importData(@RequestBody(required = false) MEnterpriseImportVo vo, HttpServletResponse response) throws Exception {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(vo.getPage_code());
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        if (Objects.equals(pagesVo.getImport_processing(), Boolean.TRUE)) {
            throw new BusinessException("还有未完成的导入任务，请稍后重试");
        }
        try{
            SLogImportVo sLogImportVo = new SLogImportVo();

            sLogImportVo.setImport_json(pagesVo.getImport_json());
            sLogImportVo.setPage_code(pagesVo.getCode());
            sLogImportVo.setPage_name(pagesVo.getName());
            sLogImportVo.setUpload_url(vo.getUrl());


            isPagesService.updateImportProcessingTrue(pagesVo);

            // 文件下载并check类型
            // 1、获取模板配置类
            String json = pagesVo.getImport_json();

            SystemExcelReader pr = super.downloadExcelAndImportData(vo.getUrl(), json);
            List<MEnterpriseImportVo> beans = pr.readBeans(MEnterpriseImportVo.class);

            if (pr.isDataValid()) {
                pr.closeAll();

                if (beans.size() == 0) {
                    isPagesService.updateImportProcessingFalse(pagesVo);
                    throw new BusinessException("导入失败,导入文件无数据");
                }
                // 读取没有错误，开始插入
                List<MEnterpriseImportVo> mEnterpriseVos = service.importData(beans);

                isPagesService.updateImportProcessingFalse(pagesVo);

                sLogImportVo.setType(SystemConstants.LOG_FLG.OK);
                isLogImportService.insert(sLogImportVo);
                return ResponseEntity.ok().body(ResultUtil.OK(beans));
            } else {
                // 读取失败，需要返回错误
                File rtnFile = pr.getValidateResultsInFile(pr.getFileName());
                MEnterpriseImportVo errorInfo = super.uploadFile(rtnFile.getAbsolutePath(), MEnterpriseImportVo.class);
                pr.closeAll();

                isPagesService.updateImportProcessingFalse(pagesVo);

                sLogImportVo.setType(SystemConstants.LOG_FLG.NG);
                sLogImportVo.setError_url(errorInfo.getUrl());
                isLogImportService.insert(sLogImportVo);
                return ResponseEntity.ok().body(ResultUtil.OK(errorInfo, ResultEnum.IMPORT_DATA_ERROR));
            }
        } catch (Exception e) {
            throw e;
        } finally{
            isPagesService.updateImportProcessingFalse(pagesVo);
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MEnterpriseVo>> print(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        MEnterpriseVo mEnterpriseVo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(mEnterpriseVo));
    }
    
    @SysLogAnnotion("获取企业下拉列表数据（交易对手、供应商）")
    @PostMapping("/cp/supplier/select_grid")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> selectCounterpartySupplierGridData(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.selectCounterpartySupplierGridData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取企业下拉列表数据（交易对手、买方、客户、经销商）")
    @PostMapping("/cp/customer/select_grid")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> selectCounterpartyCustomerGridData(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.selectCounterpartyCustomerGridData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取企业下拉列表数据（主体企业、系统企业、供应商）")
    @PostMapping("/se/supplier/select_grid")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> selectSystemEnterpriseSupplierGridData(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.selectSystemEnterpriseSupplierGridData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取企业下拉列表数据（主体企业、系统企业、买方、经销商）")
    @PostMapping("/se/customer/select_grid")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> selectSystemEnterpriseCustomerGridData(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.selectSystemEnterpriseCustomerGridData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取企业列表（交易对手、供应商）")
    @PostMapping("/cp/supplier/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> getCounterpartySupplierList(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.getCounterpartySupplierList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取企业列表（主体企业、系统企业、买方、经销商）")
    @PostMapping("/se/customer/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MEnterpriseVo>>> getSystemEnterpriseCustomerList(@RequestBody(required = false) MEnterpriseVo searchCondition) {
        IPage<MEnterpriseVo> list = service.getSystemEnterpriseCustomerList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
