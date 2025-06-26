package com.xinyirun.scm.bean.api.vo.business.file;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiFileVo implements Serializable {

    
    private static final long serialVersionUID = 9067567522027899504L;

    /**
     * 放货指令id
     */
    private String release_order_id;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 文件url（完整url）
     */
    private String url;

    /**
     * 文件名称
     */
    private String file_name;

    /**
     * 上传时间
     */
    private LocalDateTime timestamp;
}
