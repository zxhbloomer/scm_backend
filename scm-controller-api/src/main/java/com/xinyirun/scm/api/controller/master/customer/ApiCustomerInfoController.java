package com.xinyirun.scm.api.controller.master.customer;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.core.api.service.master.v1.customer.ApiCustomerService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 同步客户企业
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Slf4j
// @Api(tags = "同步客户企业")
@RestController
@RequestMapping(value = "/api/service/v1/customer")
public class ApiCustomerInfoController extends SystemBaseController {

    @Autowired
    private ApiCustomerService service;

    @SysLogApiAnnotion("13、首次同步所有客户企业")
    // @ApiOperation(value = "13、首次同步所有客户企业")
    @PostMapping("/sync/all")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiCustomerVo> vo, HttpServletRequest request) {
        try {
            service.syncAll(vo);
            return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
        } catch (Exception e) {
            log.error("首次同步所有客户企业 /sync/all:", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
    }


    @SysLogApiAnnotion("13、新增同步客户企业")
    // @ApiOperation(value = "13、新增同步客户企业")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncNewOnly(@RequestBody List<ApiCustomerVo> vo, HttpServletRequest request){
        try {
            service.syncNewOnly(vo);
            return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
        } catch (Exception e) {
            log.error("新增同步客户企业 /sync/new:", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
    }

    @SysLogApiAnnotion("13、修改同步客户企业")
    // @ApiOperation(value = "13、修改同步客户企业")
    @PostMapping("/sync/update")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncUpdateOnly(@RequestBody List<ApiCustomerVo> vo, HttpServletRequest request){
        try {
            service.syncUpdateOnly(vo);
            return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
        } catch (Exception e) {
            log.error("修改同步客户企业 /sync/update:", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
    }

}
