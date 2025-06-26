package com.xinyirun.scm.api.controller.report.master.enterprise;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.enterprise.ApiEnterpriseService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 添加入库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/enterpriser")
public class ApiEnterpriseController extends SystemBaseController {

    @Autowired
    private ApiEnterpriseService service;

    @SysLogApiAnnotion("企业单据打印")
    @GetMapping("/print")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<MEnterpriseVo>> print(@ModelAttribute MEnterpriseVo vo){
        MEnterpriseVo rtn = service.getDetail(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(rtn));
    }

    @SysLogAnnotion("获取营业执照")
    @GetMapping("/print/license")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFileInfoVo>>> printEnterpriseLicense(@ModelAttribute MEnterpriseVo searchCondition) {
        List<SFileInfoVo> sFileInfoVoList = service.getprintEnterpriseLicense(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(sFileInfoVoList));
    }
}
