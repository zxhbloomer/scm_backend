package com.xinyirun.scm.controller.business.alarm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 预警组
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@RestController
@RequestMapping("/api/v1/alarm/group")
public class BAlarmGroupController extends SystemBaseController {

    @Autowired
    private IBAlarmGroupService service;

    @PostMapping("/page_list")
    @SysLogAnnotion("预警组列表")
    public ResponseEntity<JsonResultAo<IPage<BAlarmGroupVo>>> selectPageList(@RequestBody BAlarmGroupVo vo) {
        IPage<BAlarmGroupVo> page = service.selectPageList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @PostMapping("/insert")
    @SysLogAnnotion("预警组 新增")
    public ResponseEntity<JsonResultAo<BAlarmGroupVo>> insert(@RequestBody BAlarmGroupVo vo) {
        InsertResultAo<BAlarmGroupVo> rtn = service.insert(vo);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @PostMapping("/update")
    @SysLogAnnotion("预警组 更新")
    public ResponseEntity<JsonResultAo<BAlarmGroupVo>> update(@RequestBody BAlarmGroupVo vo) {
        UpdateResultAo<BAlarmGroupVo> rtn = service.updateAlarm(vo);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @SysLogAnnotion("预警组 添加员工")
    @PostMapping("/staff/transfer/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAlarmStaffGroupTransferVo>> setStaffTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setStaffTransfer(bean)));
    }

    @SysLogAnnotion("预警组获取所有员工的数据，为穿梭框服务")
    @PostMapping("/staff/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAlarmStaffGroupTransferVo>> getStaffTransferList(@RequestBody(required = false) BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = service.getStaffTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

}
