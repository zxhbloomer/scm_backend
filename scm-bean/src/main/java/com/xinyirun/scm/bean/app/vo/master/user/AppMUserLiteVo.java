package com.xinyirun.scm.bean.app.vo.master.user;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 用户表 简单
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "用户表 简单", description = "用户表 简单")
@EqualsAndHashCode(callSuper=false)
public class AppMUserLiteVo implements Serializable {
    private static final long serialVersionUID = 5352629259229227386L;
    /**
     * 主键
     */
    private Long id;

    /**
     * m_user的主键
     */
    private Long user_id;

    /**
     * m_staff的主键
     */
    private Long staff_id;

    /**
     * 登录模式：（10：手机号码；20：邮箱）
     */
    private String login_type;

    /**
     * 登录用户名
     */
    private String login_name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 姓名
     */
    private String name;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 系统用户=10,职员=20,客户=30,供应商=40,其他=50,认证管理员=60,审计管理员=70
     */
    private String type;

    /**
     * 所属公司
     */
    private Long company_id;

    /**
     * 默认部门
     */
    private Long dept_id;

    /**
     * 菜单id，基本为m_permission_menu.permission_id，需要考虑自定义菜单情况
     */
    private Long menu_id;
}
