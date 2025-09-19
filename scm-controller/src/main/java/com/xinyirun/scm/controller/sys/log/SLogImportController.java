//package com.xinyirun.scm.controller.sys.log;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
//import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
//import com.xinyirun.scm.common.annotations.SysLogAnnotion;
//import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
//import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * @author wwl
// */
//@RestController
//@RequestMapping(value = "/api/v1/log/import")
//@Slf4j
//public class SLogImportController extends SystemBaseController {
//
//    @Autowired
//    private ISLogImportService service;
//
//
//    @SysLogAnnotion("根据查询条件，获取app日志数据表信息")
//    @PostMapping("/list")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<IPage<SLogImportVo>>> list(@RequestBody(required = false) SLogImportVo searchCondition)  {
//        IPage<SLogImportVo> list = service.selectPage(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//}
