package com.xinyirun.scm.bean.entity.business.track;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_track_gsh56")
public class BTrackGsh56Entity implements Serializable {

    private static final long serialVersionUID = -8059864797528918504L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户唯一标识
     */
    @TableField("open_id")
    private String open_id;

    /**
     * 密钥
     */
    @TableField("secret_key")
    private String secret_key;

}
