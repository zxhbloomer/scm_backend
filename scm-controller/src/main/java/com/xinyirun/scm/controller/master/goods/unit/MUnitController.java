package com.xinyirun.scm.controller.master.goods.unit;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitSelectVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 单位 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "单位")
@RestController
@RequestMapping(value = "/api/v1/unit")
public class MUnitController extends SystemBaseController {
    @Autowired
    private IMUnitService service;

    @SysLogAnnotion("根据查询条件，获取单位信息")
    // @ApiOperation(value = "根据参数获取单位信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MUnitVo>>> pagelist(@RequestBody(required = false) MUnitVo searchCondition) {
        IPage<MUnitVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取单位信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MUnitSelectVo>> list(@RequestBody(required = false) MUnitVo searchCondition) {
        MUnitSelectVo unitSelectData = service.getUnitSelectData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(unitSelectData));
    }
}
