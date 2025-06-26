package com.xinyirun.scm.app.controller.sys.config.dict;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppNutuiNameAndValue;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppSDictDataVo;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.app.service.sys.config.dict.AppISDictDataService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  app字典
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@RestController
@RequestMapping(value = "/api/app/v1/dictdata")
@Slf4j
// @Api(tags = "app字典数据表相关")
public class AppSDictDataController extends SystemBaseController {

    @Autowired
    private AppISDictDataService service;


    @SysLogAppAnnotion("根据查询条件，获取字典数据表信息")
    // @ApiOperation(value = "根据查询条件，获取字典数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<List<AppSDictDataVo>>> list(@RequestBody(required = false) AppSDictDataVo searchCondition)  {
        List<AppSDictDataVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(entity));
    }

    @SysLogAppAnnotion("根据查询条件，获取字典数据表信息")
    // @ApiOperation(value = "根据查询条件，获取字典数据表信息")
    @PostMapping("/nutui/list")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<List<AppNutuiNameAndValue>>> selectListNutuiNameAndValue(@RequestBody(required = false) AppNutuiNameAndValue searchCondition)  {
        List<AppNutuiNameAndValue> vo = service.selectNutuiNameAndValue(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(vo));
    }
}
