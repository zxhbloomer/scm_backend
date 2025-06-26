package com.xinyirun.scm.controller.common;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.common.component.DictConditionVo;
import com.xinyirun.scm.bean.system.vo.common.component.DictGroupVo;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.common.ICommonComponentService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/common/component")
@Slf4j
// @Api(tags = "共通模块数据下载，下拉选项")
public class CommonComponentController extends SystemBaseController {

    @Autowired
    private ICommonComponentService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("共通模块数据下载，下拉选项：删除类型下拉选项")
    // @ApiOperation(value = "共通模块数据下载，下拉选项：删除类型下拉选项：/deleteType/list")
    @PostMapping("/select/deletetypenormal/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<NameAndValueVo>>> deleteTypeListNormal(HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectComponentDeleteMapNormal()));
    }

    @SysLogAnnotion("共通模块数据下载，下拉选项，按传入参数来获取下拉选项")
    // @ApiOperation(value = "共通模块数据下载，下拉选项，按传入参数来获取下拉选项")
    @PostMapping("/select/dict/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<NameAndValueVo>>> getDictList(HttpServletResponse response , @RequestBody
                DictConditionVo condition) {
        List<NameAndValueVo> listRtn = null;
        if(condition.getFilter_para() != null && condition.getFilter_para().length > 0){
            listRtn = service.selectComponentFilter(condition);
        } else {
            listRtn = service.selectComponent(condition);
        }
        return ResponseEntity.ok().body(ResultUtil.OK(listRtn));
    }

    @SysLogAnnotion("共通模块数据下载，下拉选项，按传入参数来获取下拉选项，按组")
    // @ApiOperation(value = "共通模块数据下载，下拉选项，按传入参数来获取下拉选项，按组")
    @PostMapping("/select/dict/group_list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<DictGroupVo>>> getDictGroupList(HttpServletResponse response , @RequestBody
        DictConditionVo condition) {
        List<DictGroupVo> listRtn = service.selectGroupComponent(condition);
        return ResponseEntity.ok().body(ResultUtil.OK(listRtn));
    }

}
