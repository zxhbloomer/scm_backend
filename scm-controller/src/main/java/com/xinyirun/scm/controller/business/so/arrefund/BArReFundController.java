package com.xinyirun.scm.controller.business.so.arrefund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.so.arrefund.IBArReFundService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 应收退款管理表（Accounts Receivable） 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ar/refund")
public class BArReFundController {

    @Autowired
    private IBArReFundService service;

    @SysLogAnnotion("应收退款管理,获取业务类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BArReFundVo>>> getType(@RequestBody(required = false) BArReFundVo searchCondition) {
        List<BArReFundVo> list = service.getType();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 应收账款管理表  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("应收退款管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArReFundVo>> insert(@RequestBody BArReFundVo searchCondition) {
        InsertResultAo<BArReFundVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("应收退款管理 更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArReFundVo>> save(@RequestBody(required = false) BArReFundVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应收退款管理，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BArReFundVo>>> selectPagelist(@RequestBody(required = false) BArReFundVo searchCondition) {
        IPage<BArReFundVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("应收退款管理，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BArReFundVo>> get(@RequestBody(required = false) BArReFundVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("应收退款管理，校验")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BArReFundVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("应收退款管理，获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReFundVo>> print(@RequestBody(required = false) BArReFundVo searchCondition) {
        BArReFundVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }


    @SysLogAnnotion("应收退款管理，逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BArReFundVo>> delete(@RequestBody(required = false) List<BArReFundVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("应收退款管理，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReFundVo>> cancel(@RequestBody(required = false) BArReFundVo vo) {
        if(service.cancel(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应收退款管理，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReFundVo>> querySum(@RequestBody(required = false) BArReFundVo vo) {
        BArReFundVo result = service.querySum(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("应收退款管理 导出")
    public void export(@RequestBody(required = false) BArReFundVo param, HttpServletResponse response) throws IOException {

    }

}
