package com.xinyirun.scm.controller.sys.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppNoticeVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppNoticeService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/notice")
@Slf4j
public class SAppNoticeController extends SystemBaseController {

    @Autowired
    private ISAppNoticeService service;

    @SysLogAnnotion("根据查询条件，获取APP通知分页列表")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SAppNoticeVo>>> pageList(@RequestBody(required = false) SAppNoticeVo searchCondition) {
        IPage<SAppNoticeVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("APP通知数据新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> insert(@RequestBody(required = false) SAppNoticeVo vo) {
        if(service.insert(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK("ok"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }

    @SysLogAnnotion("APP通知数据修改")
    @PostMapping("/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> update(@RequestBody(required = false) SAppNoticeVo vo) {
        if(service.update(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK("ok"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }
}
