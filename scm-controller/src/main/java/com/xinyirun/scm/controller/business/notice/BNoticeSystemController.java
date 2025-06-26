package com.xinyirun.scm.controller.business.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.notice.IBNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知管理 管理层
 *
 * @since 2021-03-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/system/notice/")
public class BNoticeSystemController {

   /* @Autowired
    private IBSyncStatusErrorService syncService;

    @GetMapping("count")
    @SysLogAnnotion("根据查询条件，获取同步日志数据表信息")
    public ResponseEntity<JsonResultAo<Map<String, Long>>> getCount()  {
        Map<String, Long> result = new HashMap<>();
        // 查询消息中心总数量, 不止有同步错误的, 后续在加
        Long count = syncService.selectCount();
        result.put("syncCount", count);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }*/

    @Autowired
    private IBNoticeService service;

    @PostMapping("/pagelist")
    @SysLogAnnotion("通知管理查询")
    public ResponseEntity<JsonResultAo<IPage<BNoticeVo>>> selectPageList(@RequestBody BNoticeVo param)  {
        IPage<BNoticeVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/insert")
    @SysLogAnnotion("通知新增")
    public ResponseEntity<JsonResultAo<BNoticeVo>> insert(@RequestBody BNoticeVo param)  {
        InsertResultAo<BNoticeVo> result = service.insert(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData(), "新增成功"));
    }

    @PostMapping("/get")
    @SysLogAnnotion("通知 详情")
    public ResponseEntity<JsonResultAo<BNoticeVo>> get(@RequestBody BNoticeVo param)  {
        BNoticeVo result = service.selectById(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(result, "新增成功"));
    }

    @PostMapping("/update")
    @SysLogAnnotion("通知 更新")
    public ResponseEntity<JsonResultAo<BNoticeVo>> update(@RequestBody BNoticeVo param)  {
        UpdateResultAo<BNoticeVo> result = service.updateParamById(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData(), "更新成功"));
    }

}
