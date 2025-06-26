package com.xinyirun.scm.controller.schedule;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.job.TaskException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.quartz.ISJobService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 调度任务信息操作处理
 * 
 */
@RestController
@RequestMapping(value = "/api/v1/job")
@Slf4j
public class SysJobController extends SystemBaseController {

    @Autowired
    private ISJobService service;

    /**
     * 查询定时任务列表
     */
    @SysLogAnnotion("根据查询条件，获取调度任务信息信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SJobVo>>> list(@RequestBody(required = false) SJobVo searchCondition) {
        IPage<SJobVo> entity = service.selectJobList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("调度任务信息更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SJobVo>> save(@RequestBody(required = false) SJobVo bean) throws SchedulerException, TaskException {
        if(service.update(bean).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectJobById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("调度任务信息新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SJobVo>> insert(@RequestBody(required = false) SJobVo bean) throws SchedulerException, TaskException {
        SJobVo rtn = service.insert(bean).getData();

        return ResponseEntity.ok().body(ResultUtil.OK(rtn,"插入成功"));
    }

    @SysLogAnnotion("调度任务信息 删除")
    @PostMapping("/delete")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) SJobVo bean) {
        service.delete(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }

}
