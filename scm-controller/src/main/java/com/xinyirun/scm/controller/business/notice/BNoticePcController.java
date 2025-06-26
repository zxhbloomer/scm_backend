package com.xinyirun.scm.controller.business.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.service.business.notice.IBNoticeService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知管理 客户端
 *
 * @author wwl
 * @since 2021-03-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/pc/notice/")
public class BNoticePcController extends SystemBaseController {

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

    @PostMapping("pagelist")
    @SysLogAnnotion("列表通知查询")
    public ResponseEntity<JsonResultAo<IPage<BNoticeVo>>> selectPageList(@RequestBody BNoticeVo param)  {
        param.setStatus(DictConstant.DICT_B_NOTICE_STATUS_1);
        param.setStaff_id(getUserSessionStaffId());
        IPage<BNoticeVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("unread/ten")
    @SysLogAnnotion("列表通知查询")
    public ResponseEntity<JsonResultAo<List<BNoticeVo>>> getNoticeUnreadTen(@RequestBody BNoticeVo param)  {
        param.setStatus(DictConstant.DICT_B_NOTICE_STATUS_1);
        param.setStaff_id(getUserSessionStaffId());
        List<BNoticeVo> result = service.getNoticeUnreadTen(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("get")
    @SysLogAnnotion("列表通知 详情")
    public ResponseEntity<JsonResultAo<BNoticeVo>> getDetail(@RequestBody BNoticeVo param)  {
        param.setStatus(DictConstant.DICT_B_NOTICE_STATUS_1);
        param.setStaff_id(getUserSessionStaffId());
        BNoticeVo result = service.getPCDetail(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}
