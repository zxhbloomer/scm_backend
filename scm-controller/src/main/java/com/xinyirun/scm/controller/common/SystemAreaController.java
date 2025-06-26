package com.xinyirun.scm.controller.common;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaCitiesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaProvincesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasCascaderTreeVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import com.xinyirun.scm.core.system.service.sys.areas.ICommonAreasService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/common/areas")
@Slf4j
// @Api(tags = "省市区级联相关")
public class SystemAreaController extends SystemBaseController {

    @Autowired
    private ICommonAreasService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取省信息")
    // @ApiOperation(value = "根据参数id，获取省信息")
    @PostMapping("/province/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SAreaProvincesVo>>> provinceList(@RequestBody(required = false) SAreaProvincesVo searchCondition) {
        List<SAreaProvincesVo> vo = service.getProvinces(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取市信息")
    // @ApiOperation(value = "根据参数id，获取市信息")
    @PostMapping("/city/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SAreaCitiesVo>>> cityList(@RequestBody(required = false) SAreaCitiesVo searchCondition) {
        List<SAreaCitiesVo> vo = service.getCities(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取区信息")
    // @ApiOperation(value = "根据参数id，获取区信息")
    @PostMapping("/area/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SAreasVo>>> cityArea(@RequestBody(required = false) SAreasVo searchCondition) {
        List<SAreasVo> vo = service.getAreas(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取区信息")
    // @ApiOperation(value = "根据参数id，获取区信息")
    @PostMapping("/area/casca")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SAreasCascaderTreeVo>>> getAreasCascader() {
        List<SAreasCascaderTreeVo> vo = service.getAreasCascaderTreeVo();
        return ResponseEntity.ok().body(ResultUtil.OK(vo, JsonResultTypeConstants.STRING_EMPTY_BOOLEAN_FALSE));
    }


}
