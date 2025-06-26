package com.xinyirun.scm.api.controller.master.customer;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiOwnerVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.customer.ApiOwnerService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 货主下拉
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Slf4j
// @Api(tags = "货主下拉")
@RestController
@RequestMapping(value = "/api/service/v1/owner")
public class ApiOwnerInfoController extends SystemBaseController {
    @Autowired
    private ApiOwnerService service;

    @SysLogApiAnnotion("1、查询所有货主数据")
    // @ApiOperation(value = "1、查询所有货主数据")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiOwnerVo>>> getOwner(@RequestBody ApiOwnerVo vo) {
        List<ApiOwnerVo> list = service.getOwner(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(list));
    }

}
