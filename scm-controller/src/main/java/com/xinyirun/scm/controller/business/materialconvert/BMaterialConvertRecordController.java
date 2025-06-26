package com.xinyirun.scm.controller.business.materialconvert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BConvertRecordVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertRecordService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wang Qianfeng
 * @Description 物料转换记录
 * @date 2022/11/23 16:00
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/convert_log")
public class BMaterialConvertRecordController extends SystemBaseController {

    @Autowired
    private IBMaterialConvertRecordService service;

    @PostMapping("/list")
    @SysLogAnnotion("物料转换记录列表查询")
    public ResponseEntity<JsonResultAo<IPage<BConvertRecordVo>>> list(@RequestBody(required = false) BConvertRecordVo searchCondition) {
        IPage<BConvertRecordVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
