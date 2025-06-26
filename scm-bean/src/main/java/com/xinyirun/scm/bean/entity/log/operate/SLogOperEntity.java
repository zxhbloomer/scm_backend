package com.xinyirun.scm.bean.entity.log.operate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author zxh
 * @since 2019-12-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_log_oper")
public class SLogOperEntity implements Serializable {

    private static final long serialVersionUID = 8497364323105437596L;

    /**
     * 操作日志记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作业务名
     */
    @TableField("name")
    private String name;

    /**
     * 业务类型（其它、新增、修改、逻辑删除、物理删除）
     */
    @TableField("type")
    private String type;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @TableField("platform")
    private Integer platform;

    /**
     * 操作人id
     */
    @TableField("oper_id")
    private Long oper_id;

    /**
     * 操作人姓名
     */
    @TableField("oper_name")
    private String oper_name;

    /**
     * 操作时间
     */
    @TableField("oper_time")
    private LocalDateTime oper_time;


}
