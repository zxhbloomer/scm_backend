package com.xinyirun.scm.controller.business.alarm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmRulesVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesGroupService;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 预警规则清单 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@RestController
@RequestMapping("/api/v1/alarm/settings")
public class BAlarmRulesController {

    @Autowired
    private IBAlarmRulesService service;

    @Autowired
    private IBAlarmRulesGroupService rulesGroupService;

    @PostMapping("page_list")
    @SysLogAnnotion("查询 预警 规则")
    public ResponseEntity<JsonResultAo<IPage<BAlarmRulesVo>>> selectPageList(@RequestBody(required = false) BAlarmRulesVo vo) {
        IPage<BAlarmRulesVo> page = service.selectPageList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @PostMapping("insert")
    @SysLogAnnotion("新增 预警 规则")
    public ResponseEntity<JsonResultAo<BAlarmRulesVo>> insert(@RequestBody(required = false) BAlarmRulesVo vo) {
        InsertResultAo<BAlarmRulesVo> rtn = service.insert(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData()));
    }

    @PostMapping("update")
    @SysLogAnnotion("新增 预警 规则")
    public ResponseEntity<JsonResultAo<BAlarmRulesVo>> update(@RequestBody(required = false) BAlarmRulesVo vo) {
        UpdateResultAo<BAlarmRulesVo> rtn = service.edit(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData()));
    }

    @PostMapping("enable")
    @SysLogAnnotion("预警 启用, 禁用")
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<BAlarmRulesVo> vo) {
        service.enable(vo);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("预警设置 所有员工的数据，为穿梭框服务")
    @PostMapping("/staff/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAlarmStaffGroupTransferVo>> getStaffTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = rulesGroupService.getStaffTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("预警设置 保存所有员工的数据，为穿梭框服务")
    @PostMapping("/staff/transfer/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> setStaffTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        rulesGroupService.setStaffTransfer(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("预警设置 所有员工的数据，为穿梭框服务")
    @PostMapping("/group/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAlarmStaffGroupTransferVo>> getGroupTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = rulesGroupService.getGroupTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("预警设置 保存所有员工的数据，为穿梭框服务")
    @PostMapping("/group/transfer/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> setGroupTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        rulesGroupService.setGroupTransfer(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
