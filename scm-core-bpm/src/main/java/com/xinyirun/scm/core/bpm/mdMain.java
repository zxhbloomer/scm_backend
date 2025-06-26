package com.xinyirun.scm.core.bpm;


import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmNoticeVo;
import com.xinyirun.scm.core.bpm.utils.bpm.MarkDownNotice;

import java.time.LocalDateTime;

public class mdMain {
    public static void main(String[] args) {
//        getMd(getTestData());
//        runTest();
        Document doc = MarkDownNotice.createAgreeNotice(getTestData());
        MutableDataSet options = new MutableDataSet();
        String str = Formatter.builder(options).build().render(doc);
        System.out.println(str);
    }

    private static BBpmNoticeVo getTestData(){
        BBpmNoticeVo noticeVo = new BBpmNoticeVo();
        //        1、审批流名称
        noticeVo.setProcess_definition_name("新增企业审批");
        noticeVo.setTitle("【审批任务通知-" + noticeVo.getProcess_definition_name()  + "】");
        // 2、申请人 提交时间
        noticeVo.setOwner_code("testcode");
        noticeVo.setOwner_name("张三");
        noticeVo.setStart_time(LocalDateTime.now());
        /**
         * 3、当前进度：上一个审批人 own_code和own_name
         * 搜索方法：
         * 1、根据
         *         bpm_instance_process.process_code = bpmInstanceVo.getProcess_code()
         *    and bpm_instance_process.result = running
         *    获取到符合条件的node_id，继续查询
         *     bpm_instance_process.is_next = 上面获取到的node_id
         *     获取到符合条件的node_id
         *     使得该该node_id=bpm_instance_approve.node_id
         *     获取到bpm_instance_approve.assignee_code、bpm_instance_approve.assignee_name
         */
//        根据usercode获取岗位
//        获取摘要
        noticeVo.setSummary("{\"类型：\": \"客户\", \"企业名称：\": \"上海天臣投资控股集团有限公司团有限公司\", \"社会信用号：\": \"913100006929312780\"}"        );
//        获取审批流程：如果为空说明还在第一个节点
//        noticeVo.setComment(bpmCommentVos);

        // 编辑跳转链接
        noticeVo.setSerial_type("test");
        noticeVo.setSerial_id(123);
        noticeVo.setC_time(LocalDateTime.now());
        noticeVo.setDeadLine(LocalDateTime.now());
        noticeVo.setApprovalUrl("http://www.baidu.com");

        return noticeVo;
    }

}
