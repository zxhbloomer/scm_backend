package com.xinyirun.scm.bean.bpm.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 审批流实例-摘要
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BpmInstanceSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 667215928549917853L;

    private Integer id;

    /**
     * 审批编号
     */
    private String processCode;

    /**
     * 摘要数据
     */
    private String summary;

}
