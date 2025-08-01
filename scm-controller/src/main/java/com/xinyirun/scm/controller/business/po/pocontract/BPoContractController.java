package com.xinyirun.scm.controller.business.po.pocontract;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractExportVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.po.pocontract.IBPoContractService;
import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购合同表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@RestController
@RequestMapping("/api/v1/pocontract")
public class BPoContractController extends SystemBaseController {

    @Autowired
    private IBPoContractService service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISLogImportService isLogImportService;

    /**
     * 采购合同  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("采购合同 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPoContractVo>> insert(@RequestBody BPoContractVo searchCondition) {
        InsertResultAo<BPoContractVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("采购合同校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BPoContractVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("根据查询条件，获取采购合同集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BPoContractVo>>> list(@RequestBody(required = false) BPoContractVo searchCondition) {
        IPage<BPoContractVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按采购合同合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoContractVo>> querySum(@RequestBody(required = false) BPoContractVo searchCondition) {
        BPoContractVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取采购合同信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BPoContractVo>> get(@RequestBody(required = false) BPoContractVo searchCondition) {
        BPoContractVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("采购合同更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPoContractVo>> save(@RequestBody(required = false) BPoContractVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，采购合同逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BPoContractVo>> delete(@RequestBody(required = false) List<BPoContractVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoContractVo>> print(@RequestBody(required = false) BPoContractVo searchCondition) {
        BPoContractVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    @SysLogAnnotion("采购合同，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoContractVo>> cancel(@RequestBody(required = false) BPoContractVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("采购合同，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoContractVo>> finish(@RequestBody(required = false) BPoContractVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BPoContractVo param, HttpServletResponse response) throws IOException {
        List<BPoContractVo> result = service.selectExportList(param);
        // 创建导出的数据列表
        List<BPoContractExportVo> exportDataList = new ArrayList<>();

        for (BPoContractVo BPoContractVo : result) {
            List<BPoContractDetailVo> productList =JSON.parseArray(BPoContractVo.getDetailListData().toString(), BPoContractDetailVo.class);

            for (int i = 0; i < productList.size(); i++) {
                BPoContractDetailVo BPoContractDetailVo = productList.get(i);
                BPoContractExportVo BPoContractExportVo = new BPoContractExportVo();
                BeanUtils.copyProperties(BPoContractVo, BPoContractExportVo);
                BeanUtils.copyProperties(BPoContractDetailVo, BPoContractExportVo);
                exportDataList.add(BPoContractExportVo);
            }
        }

        List<String> strategy_1 = exportDataList.stream().map(BPoContractExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getSku_code())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("采购合同").head(BPoContractExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 7))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 8))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 9))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 10))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 11))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 12))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 13))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 14))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 15))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 16))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 17))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 18))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 19))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 20))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 21))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 22))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 23))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 24))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 25))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 26))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 27,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 28,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 29,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 30,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 31,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 32,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 33,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 34))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 35))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 36))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 37))
                .build();
        new EasyExcelUtil<>(BPoContractExportVo.class).exportExcel("采购合同" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);
    }

    @SysLogAnnotion("采购合同数据导入")
    @PostMapping("/import")
    public ResponseEntity<JsonResultAo<Object>> importData(@RequestBody(required = false) BPoContractImportVo vo, HttpServletResponse response) throws Exception {
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
            List<BPoContractImportVo> beans = pr.readBeans(BPoContractImportVo.class);

            if (pr.isDataValid()) {
                pr.closeAll();

                if (beans.size() == 0) {
                    isPagesService.updateImportProcessingFalse(pagesVo);
                    throw new BusinessException("导入失败,导入文件无数据");
                }
                // 读取没有错误，开始插入
                List<BPoContractImportVo> mEnterpriseVos = service.importData(beans);

                isPagesService.updateImportProcessingFalse(pagesVo);

                sLogImportVo.setType(SystemConstants.LOG_FLG.OK);
                isLogImportService.insert(sLogImportVo);
                return ResponseEntity.ok().body(ResultUtil.OK(beans));
            } else {
                // 读取失败，需要返回错误
                File rtnFile = pr.getValidateResultsInFile(pr.getFileName());
                BPoContractImportVo errorInfo = super.uploadFile(rtnFile.getAbsolutePath(), BPoContractImportVo.class);
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

}
