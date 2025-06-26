package com.xinyirun.scm.controller.master.goods;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecPropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Wqf
 * @Description: 商品属性
 * @CreateTime : 2023/4/20 11:18
 */


@RestController
@RequestMapping("/api/v1/goods/prop")
public class MGoodsSpecPropController {

    @Autowired
    private IMGoodsSpecPropService service;

    @SysLogAnnotion("启用的属性列表下拉框")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MGoodsSpecPropVo>>> list(@RequestBody(required = false) MGoodsSpecPropVo searchCondition) {
        List<MGoodsSpecPropVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
