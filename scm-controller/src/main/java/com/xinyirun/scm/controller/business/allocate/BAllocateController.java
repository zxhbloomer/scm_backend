package com.xinyirun.scm.controller.business.allocate;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.allocate.IBAllocateDetailService;
import com.xinyirun.scm.core.system.service.business.allocate.IBAllocateService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.ApiOperation;
// import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 库存调整 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Slf4j
// @Api(tags = "调拨单")
@RestController
@RequestMapping(value = "/api/v1/allocate")
public class BAllocateController extends SystemBaseController {

    @Autowired
    private IBAllocateService service;

    @Autowired
    private IBAllocateDetailService allocateDetailService;

    @SysLogAnnotion("根据查询条件，获取调整单分页集合信息")
    // @ApiOperation(value = "根据参数获取调整单分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BAllocateVo>>> list(@RequestBody(required = false) BAllocateVo searchCondition) {
        IPage<BAllocateVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取调整单信息")
    // @ApiOperation(value = "根据参数获取调整单信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAllocateVo>> get(@RequestBody(required = false) BAllocateVo vo) {
        BAllocateVo allocateVo = service.get(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(allocateVo));
    }
    
    @SysLogAnnotion("新增调拨单")
    // @ApiOperation(value = "新增调拨单")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BAllocateVo>> insert(@RequestBody(required = false) BAllocateVo vo) {
        if(allocateDetailService.insert(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(vo.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

//    @SysLogAnnotion("根据选择的数据审核，部分数据")
//    // @ApiOperation(value = "根据参数id，审核数据")
//    @PostMapping("/audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BAllocateVo> searchConditionList) {
//        service.audit(searchConditionList);
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }

    @SysLogAnnotion("根据选择的数据删除，部分数据")
    // @ApiOperation(value = "根据参数id，删除数据")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<BAllocateVo> searchConditionList) {
        service.delete(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
