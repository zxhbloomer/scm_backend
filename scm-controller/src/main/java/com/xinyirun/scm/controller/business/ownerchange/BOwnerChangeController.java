package com.xinyirun.scm.controller.business.ownerchange;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.ownerchange.IBOwnerChangeDetailService;
import com.xinyirun.scm.core.system.service.business.ownerchange.IBOwnerChangeService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 货权转移 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/ownerchange")
public class BOwnerChangeController extends SystemBaseController {

    @Autowired
    private IBOwnerChangeService service;

    @Autowired
    private IBOwnerChangeDetailService allocateDetailService;

    @SysLogAnnotion("根据查询条件，获取货权转移分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOwnerChangeVo>>> list(@RequestBody(required = false) BOwnerChangeVo searchCondition) {
        IPage<BOwnerChangeVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取货权转移信息")
    // @ApiOperation(value = "根据参数获取货权转移信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOwnerChangeVo>> get(@RequestBody(required = false) BOwnerChangeVo vo) {
        BOwnerChangeVo allocateVo = service.get(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(allocateVo));
    }
    
    @SysLogAnnotion("新增货权转移单")
    // @ApiOperation(value = "新增货权转移单")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOwnerChangeVo>> insert(@RequestBody(required = false) BOwnerChangeVo vo) {
        if(allocateDetailService.insert(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(vo.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BOwnerChangeVo> searchConditionList) {
        service.audit(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据删除，部分数据")
    // @ApiOperation(value = "根据参数id，删除数据")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<BOwnerChangeVo> searchConditionList) {
        service.delete(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
