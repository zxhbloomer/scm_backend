package com.xinyirun.scm.controller.business.so.cargo_right_transfer;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer.IBSoCargoRightTransferService;
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
 * 销售货权转移表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@RestController
@RequestMapping("/api/v1/so_cargo_right_transfer")
public class BSoCargoRightTransferController extends SystemBaseController {

    @Autowired
    private IBSoCargoRightTransferService service;

    @Autowired
    private ISPagesService isPagesService;

    /**
     * 销售货权转移 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("销售货权转移 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> insert(@RequestBody BSoCargoRightTransferVo searchCondition) {
        InsertResultAo<BSoCargoRightTransferVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("销售货权转移校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("根据查询条件，获取销售货权转移集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BSoCargoRightTransferVo>>> list(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        IPage<BSoCargoRightTransferVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按销售货权转移合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> querySum(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        BSoCargoRightTransferVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取销售货权转移信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> get(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        BSoCargoRightTransferVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("销售货权转移更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> save(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，销售货权转移逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> delete(@RequestBody(required = false) List<BSoCargoRightTransferVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> print(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        BSoCargoRightTransferVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    @SysLogAnnotion("销售货权转移，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> cancel(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("销售货权转移，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoCargoRightTransferVo>> finish(@RequestBody(required = false) BSoCargoRightTransferVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BSoCargoRightTransferVo param, HttpServletResponse response) throws IOException {

    }

}