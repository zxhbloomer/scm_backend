package com.xinyirun.scm.bean.system.vo.sys.pages;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000068Vo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000128Vo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000158Vo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
// @ApiModel(value = "页面表vo", description = "页面表vo")
@EqualsAndHashCode(callSuper=false)
public class SPagesVo implements Serializable {

    private static final long serialVersionUID = 4833527967811065817L;

    private Long id;

    /**
     * 配置vue export default  name时所使用的type：constants_program.P_VUE_SETTING
     */
    private String code;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 模块地址：@/views/10_system/vuesetting/vue
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 页面的名称
     */
    private String meta_title;

    /**
     * 菜单中显示的icon
     */
    private String meta_icon;

    /**
     * 说明
     */
    private String descr;

    /**
     * 是否正在导入数据
     */
    private Boolean import_processing;

    /**
     * 是否正在导出数据
     */
    private Boolean export_processing;

    /**
     * 模板文件
     */
    private String template_url;

    /**
     * 数据导入-json
     */
    private String import_json;

    /**
     * 是否自动导入
     */
    private Boolean auto_audit;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 每日加工报表, 配置
     */
    private P00000128Vo p00000128Vo;

    /**
     * 监管任务 导出 配置
     */
    private P00000068Vo p00000068Vo;

    /**
     * 监管任务直销直采 导出 配置
     */
    private P00000158Vo p00000158Vo;

    /**
     * 日生产报表 执行状态
     */
    private String product_daily_processing;

    /**
     * id 集
     */
    private Integer[] ids;

    /**
     * 报表系统的编号
     */
    @TableField("print_code")
    private String print_code;
}
