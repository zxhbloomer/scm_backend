package com.xinyirun.scm.controller.master.carrier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.carrier.MCarrierVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.carrier.IMCarrierService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  承运商
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Slf4j
// @Api(tags = "承运商")
@RestController
@RequestMapping(value = "/api/v1/carrier")
public class MCarrierController extends SystemBaseController {
    @Autowired
    private IMCarrierService service;

    @SysLogAnnotion("根据查询条件，获取承运商分页列表")
    // @ApiOperation(value = "根据参数获取承运商分页列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MCarrierVo>>> pageList(@RequestBody(required = false) MCarrierVo searchCondition) {
        IPage<MCarrierVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取承运商详情")
    // @ApiOperation(value = "根据参数获取承运商详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MCarrierVo>> getDetail(@RequestBody(required = false) MCarrierVo searchCondition) {
        MCarrierVo list = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("承运商数据新增")
    // @ApiOperation(value = "承运商数据新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCarrierVo>> insert(@RequestBody(required = false) MCarrierVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }

    @SysLogAnnotion("承运商数据更新保存")
    // @ApiOperation(value = "承运商数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCarrierVo>> save(@RequestBody(required = false) MCarrierVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("删除承运商")
    // @ApiOperation(value = "删除承运商")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MCarrierVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
