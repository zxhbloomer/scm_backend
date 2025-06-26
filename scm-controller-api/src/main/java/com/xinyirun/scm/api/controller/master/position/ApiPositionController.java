package com.xinyirun.scm.api.controller.master.position;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.position.ApiPositionVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.position.ApiPositionService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 岗位信息
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 * 参考：https://blog.csdn.net/jiangyu1013/article/details/83107255
 */
@Slf4j
// @Api(tags = "仓库下拉")
@RestController
@RequestMapping(value = "/api/service/v1/position")
public class ApiPositionController extends SystemBaseController {

    @Autowired
    private ApiPositionService service;

    @SysLogApiAnnotion("查询所有岗位数据")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiPositionVo>>> getWarehouse(@RequestBody ApiPositionVo vo) {
        List<ApiPositionVo> list = service.list(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(list));
    }


}
