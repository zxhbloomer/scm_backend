package com.xinyirun.scm.ai.workflow.node.faqextractor;

/**
 * 工作流FAQ提取节点提示词
 * 参考 aideepin: com.moyz.adi.common.workflow.node.faqextractor.FaqExtractorPrompt
 *
 * 功能：
 * - 生成FAQ提取的提示词
 * - 指导LLM提取常见问题及答案
 * - 输出格式化的Q&A文本
 *
 * @author SCM AI Team
 * @since 2025-10-27
 */
public class FaqExtractorPrompt {

    /**
     * 生成FAQ提取提示词
     * 参考 aideepin FaqExtractorPrompt.getPrompt() 完整实现
     *
     * @param topN 提取前N组FAQ
     * @param text 用户输入的文本内容
     * @return 格式化的提示词
     */
    public static String getPrompt(int topN, String text) {
        return """
                你是一个文本解析引擎，用于分析文本数据并提取常见问题（FAQ）。
                ### 任务
                分析用户的输入内容，提取前%d组常见问题（FAQ）及其对应的答案；
                ### 要求
                1. 将输出格式化为纯文本，
                2. 输出的每个问题前加上'Q:'，做为一行内容输出；
                3. 输出的每个答案前加上'A:'，做为一行内容输出；
                4. 如果未提取到问题和答案，则输出'无结果'；
                5. 确保输出清晰、简洁。
                ### 示例（用户输入）
                要重置密码，请转到登录页面并点击'忘记密码'。
                输入您的电子邮件地址，您将收到一个重置密码的链接。
                要删除账户，请访问设置页面并选择'删除账户'。确认您的选择以永久删除账户。
                要更新个人资料，请导航到'个人资料设置'部分并进行必要的更改。
                ### 示例（输出）
                Q: 如何重置密码？
                A: 转到登录页面，点击'忘记密码'，输入您的电子邮件地址，并按照发送到您邮箱的链接操作。
                Q: 如何删除账户？
                A: 访问设置页面，选择'删除账户'，并确认您的选择以永久删除账户。
                Q: 如何更新个人资料？
                A: 导航到'个人资料设置'部分并进行必要的更改。
                ### 输出语言
                使用用户提问的语言进行内容输出
                ### 用户的输入
                %s
                """.formatted(topN, text);
    }
}
