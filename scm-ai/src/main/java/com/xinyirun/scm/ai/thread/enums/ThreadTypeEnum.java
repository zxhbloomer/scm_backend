/**
 * 会话类型枚举
 */
package com.xinyirun.scm.ai.thread.enums;

/**
 * 会话类型
 */
public enum ThreadTypeEnum {
    AGENT(0), // 一对一客服，不支持机器人接待
    WORKGROUP(1), // 技能组客服，支持机器人接待，支持转人工
    ROBOT(2), // 机器人客服，不支持转人工
    // 
    MEMBER(3), // 组织成员对话
    GROUP(4), // 群组对话
    FEEDBACK(6), // 意见反馈
    ASSISTANT(7), // 助理，包括文件助理、剪贴板助理
    CHANNEL(8), // 渠道对话，包括系统通知、订阅号、服务号，NoticeAccountTypeEnum
    LOCAL(9), // 本地对话
    FRIEND(10), // 好友对话
    TICKET(11), // 工单对话
    // 
    KBASE(12), // 机器人-知识库对话，后台模拟测试
    KBDOC(13), // 机器人-知识库某一个文档对话，后台模拟测试
    // 
    LLM(14), // 机器人-直接调用大模型
    UNIFIED(15), // 统一客服入口
    HISTORY(16), // 历史对话，用于管理后台查看历史对话
    WORKFLOW(17), // 工作流对话
    TEXT(18), // 文本对话
    // AI_TOOL(19), // AI工具对话
    ;

    private final int value;

    // 枚举构造器，每个枚举常量都有一个与之关联的整型值
    ThreadTypeEnum(int value) {
        this.value = value;
    }

    // 获取枚举常量的整型值
    public int getValue() {
        return value;
    }

    // 根据整型值查找对应的枚举常量
    public static ThreadTypeEnum fromValue(int value) {
        for (ThreadTypeEnum type : ThreadTypeEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
    
    /**
     * 获取枚举类型对应的中文名称
     * @return 对应的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case AGENT:
                return "一对一客服";
            case WORKGROUP:
                return "技能组客服";
            case ROBOT:
                return "机器人客服";
            case MEMBER:
                return "组织成员对话";
            case GROUP:
                return "群组对话";
            case FEEDBACK:
                return "意见反馈";
            case ASSISTANT:
                return "助理";
            case CHANNEL:
                return "渠道对话";
            case LOCAL:
                return "本地对话";
            case FRIEND:
                return "好友对话";
            case TICKET:
                return "工单对话";
            case KBASE:
                return "知识库对话";
            case KBDOC:
                return "知识库文档对话";
            case LLM:
                return "大模型对话";
            case UNIFIED:
                return "统一客服入口";
            case HISTORY:
                return "历史对话";
            case WORKFLOW:
                return "工作流对话";
            case TEXT:
                return "文本对话";
            default:
                return this.name();
        }
    }
    
    /**
     * 根据枚举名称获取对应的中文名称
     * @param name 枚举名称
     * @return 对应的中文名称，如果找不到匹配的枚举则返回原始名称
     */
    public static String getChineseNameByString(String name) {
        try {
            ThreadTypeEnum type = ThreadTypeEnum.valueOf(name);
            return type.getChineseName();
        } catch (IllegalArgumentException e) {
            return name;
        }
    }
}