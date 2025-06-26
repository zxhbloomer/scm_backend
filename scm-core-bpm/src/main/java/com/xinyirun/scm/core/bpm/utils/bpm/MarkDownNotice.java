package com.xinyirun.scm.core.bpm.utils.bpm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Document;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCommentVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmNoticeVo;
import com.xinyirun.scm.common.utils.string.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MarkDownNotice {

    /**
     * 审批通过后的待办通知：
     * 建立markdown通知
     * 样例：
     *  【审批任务通知-新增企业审批】
     *   提交人：张三  提交日期：2025-02-23 18:00
     *   当前进度：已通过+岗位+姓名审批 → 待您审批
     *   流程摘要：新企业登记审批
     *           企业名称：xxxx
     *           uscc：aaaaaaaa
     *           法人名称：aaaaaaaa
     *   审批流程：
     *           张三 审批意见： 同意   审批事件： 2025-02-23 18:00
     *           李四 审批意见： 同意   审批事件： 2025-02-23 18:00
     *   点击链接跳转我的待办进行审批 [审批链接]
     *
     */
    public static Document createTodoAgreeNextApprovePersonNotice(BBpmNoticeVo noticeVo) {

        Document document = new Document(null, null);

        // 1. 标题（H3级加粗）
        appendSection(document, "### " + noticeVo.getTitle()  + "\n\n");

        // 2. 副标题（动态时间）
        String subtitle = "**`[您的待办]`** 请您尽快完成审批，消息时间：" + noticeVo.getC_time().format(
                DateTimeFormatter.ofPattern("yyyy 年MM月dd日 HH:mm:ss")
        )   + "\n\n";
        appendSection(document, subtitle);

        // 3. 分割线
        appendSection(document, "---\n\n");

        // 4. 审批表格生成
        StringBuilder md = new StringBuilder();
        appendSection(document,  "\n#### 审批信息  \n");
        // 表头
        md.append(" | 节点 | 审批人名称 | 审批意见 | 时间      |\n");
        md.append(" |---|---|---|---|\n");
        // 提交人信息（首行固定）
        md.append(String.format(" | 提交人   | %s | -        | %s |\n",
                noticeVo.getOwner_name(),
                noticeVo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
        ));
        
        if(noticeVo.getComment() != null) {
            // 循环审批人信息
            for (BBpmCommentVo vo : noticeVo.getComment()) {
                md.append(String.format(" | %s   | %s | %s | %s |\n",
                        vo.getAssignee_name(),
                        vo.getAssignee_name(),
                        vo.getText(),
                        vo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                ));
            }
        }
        appendSection(document, md.toString());

        // 5、審批摘要信息
        String summary = noticeVo.getSummary();
        if(StringUtils.isNotEmpty(summary)) {
            appendSection(document, "---\n\n");
            appendSection(document, parseSummaryJson(summary) + "\n\n");
        }
        appendSection(document, "---\n\n");
//        6、快捷审批入口
        appendSection(document,  "\n#### 审批办理  \n");
// 6. 审批链接（带截止时间）
        if (StringUtils.isNotEmpty(noticeVo.getApprovalUrl()))  {
            String deadlineStr = noticeVo.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
            String link = String.format(
                    "[立即审批](%s)（截止时间：%s）",
                    noticeVo.getApprovalUrl(),
                    deadlineStr
            );
            appendSection(document, "\n" + link + "\n");
        }

        /**
         *
         * | 审批流程            |
         * |-|
         * | 提交人：  张三    2025-02-23 18:00    |
         * | 审批人：  张三    2025-02-23 18:00    |
         *
         */

        return document;
    }

    /**
     * 审批通过后的待办通知：
     * 建立markdown通知
     * 样例：
     *  【审批任务通知-新增企业审批-审批通过】
     *   提交人：张三  提交日期：2025-02-23 18:00
     *   当前进度：已通过+岗位+姓名审批 → 待您审批
     *   流程摘要：新企业登记审批
     *           企业名称：xxxx
     *           uscc：aaaaaaaa
     *           法人名称：aaaaaaaa
     *   审批流程：
     *           张三 审批意见： 同意   审批事件： 2025-02-23 18:00
     *           李四 审批意见： 同意   审批事件： 2025-02-23 18:00
     *
     */
    public static Document createPassNotice(BBpmNoticeVo noticeVo) {

        Document document = new Document(null, null);

        // 1. 标题（H3级加粗）
        appendSection(document, "### " + noticeVo.getTitle()  + "\n\n");

        // 2. 副标题（动态时间）
        String subtitle = "消息时间：" + noticeVo.getC_time().format(
                DateTimeFormatter.ofPattern("yyyy 年MM月dd日 HH:mm:ss")
        )   + "\n\n";
        appendSection(document, subtitle);

        // 3. 分割线
        appendSection(document, "---\n\n");

        // 4. 审批表格生成
        StringBuilder md = new StringBuilder();
        appendSection(document,  "\n#### 审批信息  \n");
        // 表头
        md.append(" | 节点 | 审批人名称 | 审批意见 | 时间      |\n");
        md.append(" |---|---|---|---|\n");
        // 提交人信息（首行固定）
        md.append(String.format(" | 提交人   | %s | -        | %s |\n",
                noticeVo.getOwner_name(),
                noticeVo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
        ));

        if(noticeVo.getComment() != null) {
            // 循环审批人信息
            for (BBpmCommentVo vo : noticeVo.getComment()) {
                md.append(String.format(" | %s   | %s | %s | %s |\n",
                        vo.getAssignee_name(),
                        vo.getAssignee_name(),
                        vo.getText(),
                        vo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                ));
            }
        }
        appendSection(document, md.toString());

        // 5、審批摘要信息
        String summary = noticeVo.getSummary();
        if(StringUtils.isNotEmpty(summary)) {
            appendSection(document, "---\n\n");
            appendSection(document, parseSummaryJson(summary) + "\n\n");
        }
//        appendSection(document, "---\n\n");
////        6、快捷审批入口
//        appendSection(document,  "\n#### 审批办理  \n");
//// 6. 审批链接（带截止时间）
//        if (StringUtils.isNotEmpty(noticeVo.getApprovalUrl()))  {
//            String deadlineStr = noticeVo.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
//            String link = String.format(
//                    "[立即审批](%s)（截止时间：%s）",
//                    noticeVo.getApprovalUrl(),
//                    deadlineStr
//            );
//            appendSection(document, "\n" + link + "\n");
//        }

        return document;
    }

    /**
     * 审批通过后的待办通知：
     * 建立markdown通知
     * 样例：
     *  【审批任务通知-新增企业审批】
     *   提交人：张三  提交日期：2025-02-23 18:00
     *   当前进度：已通过+岗位+姓名审批 → 待您审批
     *   流程摘要：新企业登记审批
     *           企业名称：xxxx
     *           uscc：aaaaaaaa
     *           法人名称：aaaaaaaa
     *   审批流程：
     *           张三 审批意见： 同意   审批事件： 2025-02-23 18:00
     *           李四 审批意见： 同意   审批事件： 2025-02-23 18:00
     *
     */
    public static Document createAgreeNotice(BBpmNoticeVo noticeVo) {

        Document document = new Document(null, null);

        // 1. 标题（H3级加粗）
        appendSection(document, "### " + noticeVo.getTitle()  + "\n\n");

        // 2. 副标题（动态时间）
        String subtitle = "消息时间：" + noticeVo.getC_time().format(
                DateTimeFormatter.ofPattern("yyyy 年MM月dd日 HH:mm:ss")
        )   + "\n\n";
        appendSection(document, subtitle);

        // 3. 分割线
        appendSection(document, "---\n\n");

        // 4. 审批表格生成
        StringBuilder md = new StringBuilder();
        appendSection(document,  "\n#### 审批信息  \n");
        // 表头
        md.append(" | 节点 | 审批人名称 | 审批意见 | 时间      |\n");
        md.append(" |---|---|---|---|\n");
        // 提交人信息（首行固定）
        md.append(String.format(" | 提交人   | %s | -        | %s |\n",
                noticeVo.getOwner_name(),
                noticeVo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
        ));

        if(noticeVo.getComment() != null) {
            // 循环审批人信息
            for (BBpmCommentVo vo : noticeVo.getComment()) {
                md.append(String.format(" | %s   | %s | %s | %s |\n",
                        vo.getAssignee_name(),
                        vo.getAssignee_name(),
                        vo.getText(),
                        vo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                ));
            }
        }
        appendSection(document, md.toString());

        // 5、審批摘要信息
        String summary = noticeVo.getSummary();
        if(StringUtils.isNotEmpty(summary)) {
            appendSection(document, "---\n\n");
            appendSection(document, parseSummaryJson(summary) + "\n\n");
        }
//        appendSection(document, "---\n\n");
////        6、快捷审批入口
//        appendSection(document,  "\n#### 审批办理  \n");
//// 6. 审批链接（带截止时间）
//        if (StringUtils.isNotEmpty(noticeVo.getApprovalUrl()))  {
//            String deadlineStr = noticeVo.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
//            String link = String.format(
//                    "[立即审批](%s)（截止时间：%s）",
//                    noticeVo.getApprovalUrl(),
//                    deadlineStr
//            );
//            appendSection(document, "\n" + link + "\n");
//        }

        return document;
    }

    /**
     * 审批拒绝后的待办通知
     * 建立markdown通知
     * 样例：
     *  【审批拒绝通知-新增企业审批】
     *   提交人：张三  提交日期：2025-02-23 18:00
     *   当前进度：已通过+岗位+姓名审批 → 待您审批
     *   流程摘要：新企业登记审批
     *           企业名称：xxxx
     *           uscc：aaaaaaaa
     *           法人名称：aaaaaaaa
     *   审批流程：
     *           张三 审批意见： 同意   审批事件： 2025-02-23 18:00
     *           李四 审批意见： 同意   审批事件： 2025-02-23 18:00
     *
     */
    public static Document createBpmRefuseNotice(BBpmNoticeVo noticeVo) {

        Document document = new Document(null, null);

        // 1. 标题（H3级加粗）
        appendSection(document, "### " + noticeVo.getTitle()  + "\n\n");

        // 2. 副标题（动态时间）
        String subtitle = "消息时间：" + noticeVo.getC_time().format(
                DateTimeFormatter.ofPattern("yyyy 年MM月dd日 HH:mm:ss")
        )   + "\n\n";
        appendSection(document, subtitle);

        // 3. 分割线
        appendSection(document, "---\n\n");

        // 4. 审批表格生成
        StringBuilder md = new StringBuilder();
        appendSection(document,  "\n#### 审批信息  \n");
        // 表头
        md.append(" | 节点 | 审批人名称 | 审批意见 | 时间      |\n");
        md.append(" |---|---|---|---|\n");
        // 提交人信息（首行固定）
        md.append(String.format(" | 提交人   | %s | -        | %s |\n",
                noticeVo.getOwner_name(),
                noticeVo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
        ));

        if(noticeVo.getComment() != null) {
            // 循环审批人信息
            for (BBpmCommentVo vo : noticeVo.getComment()) {
                md.append(String.format(" | %s   | %s | %s | %s |\n",
                        vo.getAssignee_name(),
                        vo.getAssignee_name(),
                        vo.getText(),
                        vo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                ));
            }
        }
        appendSection(document, md.toString());

        // 5、審批摘要信息
        String summary = noticeVo.getSummary();
        if(StringUtils.isNotEmpty(summary)) {
            appendSection(document, "---\n\n");
            appendSection(document, parseSummaryJson(summary) + "\n\n");
        }
//        appendSection(document, "---\n\n");
//        6、快捷审批入口
//        appendSection(document,  "\n#### 审批办理  \n");
// 6. 审批链接（带截止时间）
//        if (StringUtils.isNotEmpty(noticeVo.getApprovalUrl()))  {
//            String deadlineStr = noticeVo.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
//            String link = String.format(
//                    "[跳转页面](%s)",
//                    noticeVo.getApprovalUrl()
//            );
//            appendSection(document, "\n" + link + "\n");
//        }

        return document;
    }

    /**
     * 审批拒绝后的待办通知
     * 建立markdown通知
     * 样例：
     *  【审批拒绝通知-新增企业审批】
     *   提交人：张三  提交日期：2025-02-23 18:00
     *   当前进度：已通过+岗位+姓名审批 → 待您审批
     *   流程摘要：新企业登记审批
     *           企业名称：xxxx
     *           uscc：aaaaaaaa
     *           法人名称：aaaaaaaa
     *   审批流程：
     *           张三 审批意见： 同意   审批事件： 2025-02-23 18:00
     *           李四 审批意见： 同意   审批事件： 2025-02-23 18:00
     *
     */
    public static Document createBpmCanceNotice(BBpmNoticeVo noticeVo) {

        Document document = new Document(null, null);

        // 1. 标题（H3级加粗）
        appendSection(document, "### " + noticeVo.getTitle()  + "\n\n");

        // 2. 副标题（动态时间）
        String subtitle = "消息时间：" + noticeVo.getC_time().format(
                DateTimeFormatter.ofPattern("yyyy 年MM月dd日 HH:mm:ss")
        )   + "\n\n";
        appendSection(document, subtitle);

        // 3. 分割线
        appendSection(document, "---\n\n");

        // 4. 审批表格生成
        StringBuilder md = new StringBuilder();
        appendSection(document,  "\n#### 审批信息  \n");
        // 表头
        md.append(" | 节点 | 审批人名称 | 审批意见 | 时间      |\n");
        md.append(" |---|---|---|---|\n");
        // 提交人信息（首行固定）
        md.append(String.format(" | 提交人   | %s | -        | %s |\n",
                noticeVo.getOwner_name(),
                noticeVo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
        ));

        if(noticeVo.getComment() != null) {
            // 循环审批人信息
            for (BBpmCommentVo vo : noticeVo.getComment()) {
                md.append(String.format(" | %s   | %s | %s | %s |\n",
                        vo.getAssignee_name(),
                        vo.getAssignee_name(),
                        vo.getText(),
                        vo.getC_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                ));
            }
        }
        appendSection(document, md.toString());

        // 5、審批摘要信息
        String summary = noticeVo.getSummary();
        if(StringUtils.isNotEmpty(summary)) {
            appendSection(document, "---\n\n");
            appendSection(document, parseSummaryJson(summary) + "\n\n");
        }
//        appendSection(document, "---\n\n");
//        6、快捷审批入口
//        appendSection(document,  "\n#### 审批办理  \n");
// 6. 审批链接（带截止时间）
//        if (StringUtils.isNotEmpty(noticeVo.getApprovalUrl()))  {
//            String deadlineStr = noticeVo.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
//            String link = String.format(
//                    "[跳转页面](%s)",
//                    noticeVo.getApprovalUrl()
//            );
//            appendSection(document, "\n" + link + "\n");
//        }

        return document;
    }

    /**
     * 通用内容追加方法
     */
    private static void appendSection(Document doc, String content) {
        Paragraph paragraph = new Paragraph();
        paragraph.appendChild(new Text(content));
        doc.appendChild(paragraph);
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    private static String parseSummaryJson(String jsonStr) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(jsonStr,  new TypeReference<Map<String, String>>() {});

            StringBuilder md = new StringBuilder("\n#### 审批摘要信息 \n");
            map.forEach((k,  v) -> {
                String cleanKey = k.replaceAll("[: ：\\s]", ""); // 去除键中的冒号/空格
                md.append(String.format("-  **%s**：%s\n", cleanKey, v));
            });
            return md.toString();

        } catch (JsonProcessingException e) {
            return "\n#### 审批摘要信息 \n（数据格式异常）";
        }
    }
}
