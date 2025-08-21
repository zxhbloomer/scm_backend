package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 员工页签
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工页签", description = "员工页签")
@EqualsAndHashCode(callSuper=false)
public class MStaffTabDataVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3018802866904602055L;

    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 全称拼音
     */
    private String name_py;


    /**
     * 登录用户id，关联id
     */
    private Long user_id;

    /**
     * 男=1,女=2
     */
    private String sex;
    private String sex_text;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 审核状态:未审核=0,已审核=1
     */
    private Boolean state;

    /**
     * 家庭电话
     */
    private String home_phone;

    /**
     * 办公室电话
     */
    private String office_phone;

    /**
     * 手机号码
     */
    private String mobile_phone;

    /**
     * 备用手机号码
     */
    private String mobile_phone_backup;

    /**
     * 备用电子邮件
     */
    private String email_backup;

    /**
     * 身份证号码
     */
    private String id_card;

    /**
     * 护照号码
     */
    private String passport;

    /**
     * 是否在职：在职=1,不在职=0,离职=2,离退休=3,返聘=4
     */
    private String service;
    private String service_text;

    /**
     * 婚否
     */
    private String is_wed;
    private String is_wed_text;

    /**
     * 名族
     */
    private String nation;

    /**
     * 学历
     */
    private String degree;
    private String degree_text;

    /**
     * 所属公司
     */
    private Long company_id;
    private String company_name;
    private String company_simple_name;

    /**
     * 默认部门
     */
    private Long dept_id;
    private String dept_name;
    private String dept_simple_name;

    /**
     * 是否删除
     */
    private Boolean is_del;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 编号，00010001..
     */
    private String code;
    private int active_tabs_index;

    /**
     * 用户名
     */
    private String login_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 用户主表
     */
    private MUserVo user;
    public MUserVo getUser(){
        return new MUserVo();
    }

    /**
     * 如果需要获取该对象，使用这个发方法
     * @return
     */
    public MUserVo getRealUser(){
        return user;
    }

    /**
     * 员工的岗位信息集合
     */
    public List<MStaffPositionsVo> positions;
}
