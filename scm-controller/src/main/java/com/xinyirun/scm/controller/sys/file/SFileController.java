package com.xinyirun.scm.controller.sys.file;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.file.ISFileInfoService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 附件信息 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "附件信息")
@RestController
@RequestMapping(value = "/api/v1/file")
public class SFileController extends SystemBaseController {

    @Autowired
    private ISFileService service;

    @Autowired
    private ISFileInfoService isFileInfoService;

    @SysLogAnnotion("根据查询条件，获取附件信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFileVo>>> list(@RequestBody(required = false) SFileVo searchCondition) {
        List<SFileVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("附件信息保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SFileVo>> insert(@RequestBody(required = false) SFileVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据逻辑物理删除，部分数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<SFileVo> searchConditionList) {
        service.realDeleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

//    @SysLogAnnotion("根据查询条件，备份附件信息")
//    @GetMapping("/backup")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> backup() {
//        isFileInfoService.backup();
//
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
//    }

    @SysLogAnnotion("根据查询条件，备份附件信息")
    @GetMapping("/backup/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> backup(Integer backup_now_count) {
        isFileInfoService.backup(backup_now_count);

        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
