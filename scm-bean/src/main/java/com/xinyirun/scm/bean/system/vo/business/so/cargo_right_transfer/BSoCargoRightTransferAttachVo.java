package com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 销售货权转移附件表VO类
 * 
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoCargoRightTransferAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3010889729226018121L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 货权转移主表ID
     */
    private Integer cargo_right_transfer_id;

    /**
     * 附件文件ID
     */
    private Integer one_file;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 更新人ID
     */
    private Long u_id;

    /**
     * 数据版本号
     */
    private Integer dbversion;

    // ========== 扩展字段 ==========

    /**
     * 文件信息
     */
    private SFileVo fileInfo;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;
}