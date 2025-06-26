package com.xinyirun.scm.app.controller.master.contact_list;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.master.contact_list.AppContractListVo;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.app.service.master.contact_list.AppIContactListService;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通讯录
 *
 * @author zxh
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/app/v1/contact")
public class AppContactListController extends AppBaseController {
    @Autowired
    private AppIContactListService service;

    @SysLogAppAnnotion("根据查询条件，获取通讯录列表")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<List<AppContractListVo>>> pageList(@RequestBody(required = false) AppContractListVo searchCondition) {
        List<AppContractListVo> list = service.list(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(list));
    }

    @SysLogAppAnnotion("根据查询条件，获取通讯录详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppContractListVo>> getDetail(@RequestBody(required = false) AppContractListVo searchCondition) {
        AppContractListVo vo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(AppResultUtil.OK(vo));
    }

}
