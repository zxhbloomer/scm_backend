package com.xinyirun.scm.bean.system.vo.business.releaseorder;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 放货指令/借货指令附件表
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BReleaseFilesVo implements Serializable {

    
    private static final long serialVersionUID = 2487961073042307607L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 放货指令id
     */
    private String release_order_id;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 附件名称
     */
    private String file_name;

    /**
     * 附件链接
     */
    private String url;

    /**
     * 上传时间
     */
    private LocalDateTime timestamp;


}
