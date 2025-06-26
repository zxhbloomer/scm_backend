package com.xinyirun.scm.controller.business.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.message.BMessageVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * websocket 消息通知表 前端控制器
 *
 * @author xinyirun
 * @since 2023-03-22
 */
@RestController
@RequestMapping("/api/v1/message")
public class BMessageController {

    @Autowired
    private IBMessageService service;

    @PostMapping("/header/page_list")
    @SysLogAnnotion("根据查询条件，预警错误信息列表")
    public ResponseEntity<JsonResultAo<IPage<BMessageVo>>> getHeaderPageList(@RequestBody(required = false)BMessageVo param)  {
        // 查询预警消息
        IPage<BMessageVo> page = service.selectHeaderPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @PostMapping("/header/count")
    @SysLogAnnotion("根据查询条件，获取同步日志数据表信息")
    public ResponseEntity<JsonResultAo<BMessageVo>> getHeaderCount(@RequestBody(required = false)BMessageVo param)  {
        // 查询预警数量
        BMessageVo result = service.getHeaderCount(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}
