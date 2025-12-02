package com.xinyirun.scm.ai.bean.vo.rag;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI知识库文档项VO类
 *
 * <p>与AiKnowledgeBaseItemEntity一一对应</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class AiKnowledgeBaseItemVo {

    /**
     * 主键ID（自增）
     */
    private Integer id;

    /**
     * 文档UUID（业务主键）
     */
    private String itemUuid;

    /**
     * 所属知识库UUID
     */
    private String kbUuid;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 简介（文档摘要）
     */
    private String brief;

    /**
     * 备注（文档完整内容）
     */
    private String remark;

    /**
     * 源文件名称
     */
    private String sourceFileName;

    /**
     * 向量化状态(1-待处理,2-处理中,3-已完成,4-失败)
     */
    private Integer embeddingStatus;

    /**
     * 向量化状态变更时间
     */
    private LocalDateTime embeddingStatusChangeTime;

    /**
     * 图谱化状态(1-待处理,2-处理中,3-已完成,4-失败)
     */
    private Integer graphicalStatus;

    /**
     * 图谱化状态变更时间
     */
    private LocalDateTime graphicalStatusChangeTime;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 附件文件ID（关联s_file表）
     * 注意：此字段不对应数据库字段，仅用于VO传输
     */
    @TableField(exist = false)
    private Integer doc_att_file;

    /**
     * 附件文件列表
     * 用于接收前端上传的文件数组，以及查询时返回文件列表
     * 注意：此字段不对应数据库字段，仅用于VO传输
     */
    @TableField(exist = false)
    private List<SFileInfoVo> doc_att_files;
}
