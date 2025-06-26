package com.xinyirun.scm.bean.entity.sys.pages;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 页面表
 * </p>
 *
 * @author zxh
 * @since 2020-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_pages")
public class SPagesEntity implements Serializable {

    private static final long serialVersionUID = 6325151356623837470L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置vue export default  name时所使用的type：constants_program.P_VUE_SETTING
     */
    @TableField("code")
    private String code;

    /**
     * 页面名称
     */
    @TableField("name")
    private String name;

    /**
     * 模块地址：@/views/10_system/vuesetting/vue
     */
    @TableField("component")
    private String component;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 页面的名称
     */
    @TableField("meta_title")
    private String meta_title;

    /**
     * 菜单中显示的icon
     */
    @TableField("meta_icon")
    private String meta_icon;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 是否正在导入数据
     */
    @TableField("import_processing")
    private Boolean import_processing;

    /**
     * 数据导入-json
     */
    @TableField("import_json")
    private String import_json;

    /**
     * 模板文件url
     */
    @TableField("template_url")
    private String template_url;

    /**
     * 是否自动审核 0-否 1-是
     */
    @TableField("auto_audit")
    private Boolean auto_audit;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    @TableField("page_json")
    private String page_json;

    @TableField("product_daily_processing")
    private String product_daily_processing;

    /**
     * 报表系统的编号
     */
    @TableField("print_code")
    private String print_code;
}
