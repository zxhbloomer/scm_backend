package com.xinyirun.scm.controller.business.po.cargo_right_transfer;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBCargoRightTransferService;
import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 货权转移表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@RestController
@RequestMapping("/api/v1/cargo_right_transfer")
public class BCargoRightTransferController extends SystemBaseController {

    @Autowired
    private IBCargoRightTransferService service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISLogImportService isLogImportService;

    /**
     * 货权转移 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("货权转移 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> insert(@RequestBody BCargoRightTransferVo searchCondition) {
        InsertResultAo<BCargoRightTransferVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("货权转移校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("根据查询条件，获取货权转移集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BCargoRightTransferVo>>> list(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        IPage<BCargoRightTransferVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按货权转移合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> querySum(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        BCargoRightTransferVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取货权转移信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> get(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        BCargoRightTransferVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("货权转移更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> save(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，货权转移逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> delete(@RequestBody(required = false) List<BCargoRightTransferVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> print(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        BCargoRightTransferVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    @SysLogAnnotion("货权转移，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> cancel(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("货权转移，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCargoRightTransferVo>> finish(@RequestBody(required = false) BCargoRightTransferVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BCargoRightTransferVo param, HttpServletResponse response) throws IOException {

    }

}