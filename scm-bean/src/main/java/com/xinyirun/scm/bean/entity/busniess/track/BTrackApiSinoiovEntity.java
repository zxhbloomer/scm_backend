package com.xinyirun.scm.bean.entity.busniess.track;

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
@TableName("b_track_api_sinoiov")
public class BTrackApiSinoiovEntity implements Serializable {

    private static final long serialVersionUID = 7942960375522682535L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 测试url
     */
    @TableField("test_url")
    private String test_url;

    /**
     * 生成url
     */
    @TableField("prod_url")
    private String prod_url;


}
