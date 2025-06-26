package com.xinyirun.scm.controller.business.alarm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmStaffService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  预警人员
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@RestController
@RequestMapping("/api/v1/alarm/staff")
public class BAlarmStaffController extends SystemBaseController {

    @Autowired
    private IBAlarmStaffService service;

    @PostMapping("/insert")
    @SysLogAnnotion("预警人员 新增")
    public ResponseEntity<JsonResultAo<BAlarmStaffVo>> insert(@RequestBody BAlarmStaffVo vo) {
        InsertResultAo<BAlarmStaffVo> rtn = service.insert(vo);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @PostMapping("/page_list")
    @SysLogAnnotion("预警人员 查询列表")
    public ResponseEntity<JsonResultAo<IPage<BAlarmStaffVo>>> selectPageList(@RequestBody BAlarmStaffVo vo) {
        IPage<BAlarmStaffVo> page = service.selectPageList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @PostMapping("/update")
    @SysLogAnnotion("预警人员 修改")
    public ResponseEntity<JsonResultAo<BAlarmStaffVo>> update(@RequestBody BAlarmStaffVo vo) {
        UpdateResultAo<BAlarmStaffVo> rtn = service.updateStaff(vo);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败。");
        }
    }
}
