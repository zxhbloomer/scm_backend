package com.xinyirun.scm.app.controller.config.notice;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.sys.config.AppNoticeVo;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.app.service.sys.config.AppISAppNoticeService;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  app版本通知信息
 * </p>
 *
 * @author wwl
 * @since 2022-02-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/app/v1/notice")
public class AppSNoticeController extends AppBaseController {

    @Autowired
    private AppISAppNoticeService service;

    @SysLogAppAnnotion("获取app配置新消息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppNoticeVo>> get(@RequestBody(required = false)AppNoticeVo searchCondition) {
        AppNoticeVo result = service.get(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(result));
    }

}
