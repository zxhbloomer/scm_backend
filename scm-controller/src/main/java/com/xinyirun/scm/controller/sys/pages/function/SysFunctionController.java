package com.xinyirun.scm.controller.sys.pages.function;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.pages.function.SFunctionVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.pages.function.ISFunctionService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/sys/funs")
@Slf4j
// @Api(tags = "按钮维护相关")
public class SysFunctionController extends SystemBaseController {

    @Autowired
    private ISFunctionService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取按钮信息")
    // @ApiOperation(value = "根据参数id，获取按钮信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFunctionVo>>> list(@RequestBody(required = false) SFunctionVo searchCondition) {
        List<SFunctionVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("按钮数据更新保存")
    // @ApiOperation(value = "根据参数id，获取按钮信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SFunctionVo>> save(@RequestBody(required = false) SFunctionVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("按钮数据新增保存")
    // @ApiOperation(value = "根据参数id，获取按钮信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SFunctionVo>> insert(@RequestBody(required = false)
        SFunctionVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("按钮表数据物理删除，部分数据")
    // @ApiOperation(value = "根据参数id，物理删除数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false)
        List<SFunctionVo> searchConditionList) {
        service.realDeleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("按钮表排序后保存")
    // @ApiOperation(value = "list数据的保存")
    @PostMapping("/save_sort")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFunctionVo>>> saveSort(@RequestBody(required = false) List<SFunctionVo> beanList) {
        UpdateResultAo<List<SFunctionVo>> result = service.saveSort(beanList);
        if(result.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(result.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }
}
