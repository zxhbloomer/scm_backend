package com.xinyirun.scm.controller.sys.columns;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.columns.SColumnSizeVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.columns.ISColumnSizeService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/columns")
@Slf4j
// @Api(tags = "vue页面上表格列宽度相关")
public class ColumnsController extends SystemBaseController {

    @Autowired
    private ISColumnSizeService service;

    @SysLogAnnotion("根据查询条件，vue页面上表格列宽度信息")
    // @ApiOperation(value = "根据参数id，vue页面上表格列宽度信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SColumnSizeVo>>> list(@RequestBody(required = false) SColumnSizeVo searchCondition) {
        /** 获取员工id */
        searchCondition.setStaff_id(getUserSessionStaffId());
        /** 拼接缓存的key */
        String cache_key = searchCondition.getPage_code()
            +":"
            + (searchCondition.getType() ==null ? "" : searchCondition.getType())
            +":"
            + searchCondition.getStaff_id();
        searchCondition.setCache_key(cache_key);
        List<SColumnSizeVo> entity = service.getData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("保存自定义列宽度")
    // @ApiOperation(value = "更新保存")
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveColumnsSize(@RequestBody(required = false) SColumnSizeVo bean) {
        /** 获取员工id */
        bean.setStaff_id(getUserSessionStaffId());
        /** 拼接缓存的key */
        String cache_key = bean.getPage_code()
            +":"
            + (bean.getType() ==null ? "" : bean.getType())
            +":"
            + bean.getStaff_id();
        bean.setCache_key(cache_key);
        if(service.saveColumnsSize(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK("更新成功","更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("删除自定义列宽度")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> deleteColumnsSize(@RequestBody(required = false) SColumnSizeVo bean) {
        /** 获取员工id */
        bean.setStaff_id(getUserSessionStaffId());
        /** 拼接缓存的key */
        String cache_key = bean.getPage_code()
                +":"
                + (bean.getType() ==null ? "" : bean.getType())
                +":"
                + bean.getStaff_id();
        bean.setCache_key(cache_key);
        service.deleteColumnsSize(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功","删除成功"));
    }
}
