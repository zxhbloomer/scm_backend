package com.xinyirun.scm.controller.sys.table;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/api/v1/table/column")
public class STableColumnConfigController {

    @Autowired
    private ISTableColumnConfigService service;

    @SysLogAnnotion("根据查询条件，获取列表配置信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<STableColumnConfigVo>>> list(@RequestBody(required = false) STableColumnConfigVo vo) {
        List<STableColumnConfigVo> list = service.list(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取原始配置数据用于重置预览")
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<STableColumnConfigVo>>> reset(@RequestBody(required = false) STableColumnConfigVo vo) {
        List<STableColumnConfigVo> originalData = service.getOriginalDataForReset(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(originalData));
    }

    @SysLogAnnotion("check用户数据是否与original数据一致")
    @PostMapping("/check")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Boolean>> check(@RequestBody(required = false) STableColumnConfigVo vo) {
        Boolean flag = service.check(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(flag));
    }

    @SysLogAnnotion("根据查询条件，获取列表配置信息")
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> list(@RequestBody(required = false) List<STableColumnConfigVo> list) {
        service.saveList(list);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

}
