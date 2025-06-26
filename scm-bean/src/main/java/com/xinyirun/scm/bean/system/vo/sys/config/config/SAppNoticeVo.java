package com.xinyirun.scm.bean.system.vo.sys.config.config;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SAppNoticeVo implements Serializable {

    private static final long serialVersionUID = -1826056003491291760L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 版本号
     */
    private String version_code;

    /**
     * 版本名称
     */
    private String version_name;

    /**
     * 通知类型 1-强制
     */
    private String type;

    /**
     * 通知类型 1-强制
     */
    private String type_name;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * app下载地址
     */
    private String url;

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
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 创建人id
     */
    private String c_name;

    /**
     * 修改人id
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
