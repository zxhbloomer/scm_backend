package com.xinyirun.scm.controller.mongobackup.log.excelimport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/log/import/new")
@Slf4j
public class SLogImportMongoController {
//
//    @Autowired
//    private LogImportMongoService service;
//
//
//    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
//    @PostMapping("/list")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<IPage<SLogImportMongoVo>>> list(@RequestBody(required = false) SLogImportMongoVo searchCondition)  {
//        IPage<SLogImportMongoVo> list = service.selectPage(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }

}
