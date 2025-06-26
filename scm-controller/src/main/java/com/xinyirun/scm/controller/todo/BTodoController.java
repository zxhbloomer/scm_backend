package com.xinyirun.scm.controller.todo;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.todo.BAlreadyDoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.BTodoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.TodoCountVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.todo.IBAlreadyDoService;
import com.xinyirun.scm.core.system.service.business.todo.IBTodoService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 查询待办数量
 * </p>
 *
 */
@Slf4j
// @Api(tags = "库存")
@RestController
@RequestMapping(value = "/api/v1/todo")
public class BTodoController extends SystemBaseController {

    @Autowired
    private IBTodoService service;

    @Autowired
    private IBAlreadyDoService ibAlreadyDoService;

    @SysLogAnnotion("根据查询条件，获取信息")
    // @ApiOperation(value = "根据参数获取信息")
    @PostMapping("/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<TodoCountVo>> count(@RequestBody(required = false) TodoCountVo searchCondition) {
        TodoCountVo count = service.selectTodoCount(searchCondition.getSerial_type());
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

    @SysLogAnnotion("根据查询条件，获取待办列表")
    // @ApiOperation(value = "根据参数获取信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BTodoVo>>> list(@RequestBody(required = false) BTodoVo searchCondition) {
        IPage<BTodoVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取已办列表")
    // @ApiOperation(value = "根据参数获取信息")
    @PostMapping("/done_list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BAlreadyDoVo>>> alreadyDolist(@RequestBody(required = false) BAlreadyDoVo searchCondition) {
        IPage<BAlreadyDoVo> list = ibAlreadyDoService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
    
}
