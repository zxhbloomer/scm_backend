package com.xinyirun.scm.controller.sys.table;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @SysLogAnnotion("重置表格列配置")
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<STableColumnConfigVo>>> reset(@RequestBody @Valid ResetColumnConfigRequest request) {
        List<STableColumnConfigVo> resultList = service.resetTableColumnsAndReturn(request.getConfigs(), request.getPageCode());
        return ResponseEntity.ok().body(ResultUtil.OK(resultList));
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

    /**
     * 重置列配置请求包装类
     */
    @Data
    public static class ResetColumnConfigRequest {
        @NotNull(message = "列配置不能为空")
        @Size(min = 1, message = "至少需要一个列配置")
        private List<STableColumnConfigVo> configs;
        
        @NotBlank(message = "页面代码不能为空")
        private String pageCode;
    }
}
