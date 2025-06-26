package com.xinyirun.scm.controller.sys.table;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigOriginalVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigOriginalService;
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
@RequestMapping("/api/v1/table/column/original")
public class STableColumnConfigOriginalController {

    @Autowired
    private ISTableColumnConfigOriginalService service;

    @SysLogAnnotion("根据查询条件，获取列表配置信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<STableColumnConfigOriginalVo>>> list(@RequestBody(required = false) STableColumnConfigOriginalVo vo) {
        List<STableColumnConfigOriginalVo> list = service.list(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("列配置分页列表")
    @PostMapping("page_list")
    public ResponseEntity<JsonResultAo<IPage<STableColumnConfigOriginalVo>>> selectPageList(@RequestBody(required = false) STableColumnConfigOriginalVo param) {
        IPage<STableColumnConfigOriginalVo> list = service.selectPageList(param);
        return  ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("添加列配置")
    @PostMapping("insert")
    public ResponseEntity<JsonResultAo<String>> insert(@RequestBody(required = false) STableColumnConfigOriginalVo param) {
        service.insert(param);
        return  ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("更新列配置")
    @PostMapping("update")
    public ResponseEntity<JsonResultAo<String>> update(@RequestBody(required = false) STableColumnConfigOriginalVo param) {
        STableColumnConfigOriginalEntity entity = (STableColumnConfigOriginalEntity) BeanUtilsSupport.copyProperties(param, STableColumnConfigOriginalEntity.class);
        service.updateById(entity);
        return  ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("排序")
    @PostMapping("sort")
    public ResponseEntity<JsonResultAo<String>> sort(@RequestBody(required = false) STableColumnConfigOriginalVo param) {
        service.sort(param);
        return  ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("排序")
    @PostMapping("get")
    public ResponseEntity<JsonResultAo<String>> get(@RequestBody(required = false) STableColumnConfigOriginalVo param) {
        service.sort(param);
        return  ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

}
