//package com.xinyirun.scm.controller.master.goods.unit;
//
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MUnitConvertUpdateVo;
//import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
//import com.xinyirun.scm.common.annotations.SysLogAnnotion;
//import com.xinyirun.scm.common.exception.system.UpdateErrorException;
////import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitConvertService;
//import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
//// import io.swagger.annotations.Api;
//// import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * <p>
// *  前端控制器
// * </p>
// *
// * @author htt
// * @since 2021-09-24
// */
//@Slf4j
//// @Api(tags = "物料单位换算")
//@RestController
//@RequestMapping(value = "/api/v1/goodsunitconvert")
//public class MGoodsUnitConvertController extends SystemBaseController {
//
//    @Autowired
//    private IMGoodsUnitConvertService service;
//
//    @SysLogAnnotion("根据查询条件，获取单位换算列表")
//    // @ApiOperation(value = "根据参数获取单位换算列表")
//    @PostMapping("/pagelist")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<IPage<MGoodsUnitConvertVo>>> pagelist(@RequestBody(required = false) MGoodsUnitConvertVo searchCondition) {
//        IPage<MGoodsUnitConvertVo> list = service.selectPage(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//    @SysLogAnnotion("根据查询条件，获取单位换算列表")
//    // @ApiOperation(value = "根据参数获取单位换算列表")
//    @PostMapping("/list")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<List<MGoodsUnitConvertVo>>> list(@RequestBody(required = false) MGoodsUnitConvertVo searchCondition) {
//        List<MGoodsUnitConvertVo> list = service.selectList(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//    @SysLogAnnotion("单位换算数据更新保存")
//    // @ApiOperation(value = "单位换算数据更新保存")
//    @PostMapping("/insert")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<List<MGoodsUnitConvertVo>>> insert(@RequestBody(required = false) List<MGoodsUnitConvertVo> list) {
//
//        if(service.insert(list).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(list,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    @SysLogAnnotion("单位换算数据更新保存")
//    // @ApiOperation(value = "单位换算数据更新保存")
//    @PostMapping("/update")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<String>> update(@RequestBody(required = false) MUnitConvertUpdateVo vo) {
//        service.update(vo);
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }
//
//    @SysLogAnnotion("根据选择的数据启用，部分数据")
//    // @ApiOperation(value = "根据参数id，启用数据")
//    @PostMapping("/enabled")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MGoodsUnitConvertVo> searchConditionList) {
//        service.enabledByIdsIn(searchConditionList);
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }
//
//    @SysLogAnnotion("根据选择的数据禁用，部分数据")
//    // @ApiOperation(value = "根据参数id，禁用数据")
//    @PostMapping("/disabled")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MGoodsUnitConvertVo> searchConditionList) {
//        service.disSabledByIdsIn(searchConditionList);
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }
//
//    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
//    // @ApiOperation(value = "根据参数id，启用数据")
//    @PostMapping("/enable")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MGoodsUnitConvertVo> searchConditionList) {
//        service.enableByIdsIn(searchConditionList);
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }
//
//}
