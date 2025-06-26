package com.xinyirun.scm.api.controller.master.warehouse;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.warehouse.ApiWarehouseVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.warehouse.ApiWarehouseService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 仓库下拉
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 * 参考：https://blog.csdn.net/jiangyu1013/article/details/83107255
 */
@Slf4j
// @Api(tags = "仓库下拉")
@RestController
@RequestMapping(value = "/api/service/v1/warehouse")
public class ApiWarehouseInfoController extends SystemBaseController {

    @Autowired
    private ApiWarehouseService service;

    @SysLogApiAnnotion("2、查询所有仓库数据")
    // @ApiOperation(value = "2、查询所有仓库数据")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "ResponseResult => ApiJsonResultAo<List<ApiWarehouseVo>>")
//    })
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiWarehouseVo>>> getWarehouse(@RequestBody ApiWarehouseVo vo) {
        List<ApiWarehouseVo> list = service.getWarehouse(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(list));
    }


}
