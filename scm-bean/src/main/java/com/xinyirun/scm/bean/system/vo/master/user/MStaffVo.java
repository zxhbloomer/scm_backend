package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 员工
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工主表", description = "员工主表")
@EqualsAndHashCode(callSuper=false)
public class MStaffVo extends BaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -6747881290631807125L;


    private Long id;

    /**
     * 编号
     */
    private String code;

    /**
     * 姓名
     */
    private String name;

    /**
     * 登录名
     */
    private String login_name;

    /**
     * 全称拼音
     */
    private String name_py;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 简称拼音
     */
    private String simple_name_py;

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
     * 说明
     */
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

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
     * 用户主表
     */
    private MUserVo user;

    /**
     * 如果需要获取该对象，使用这个发方法
     */
    private MUserVo realUser;

//    public MUserVo getRealUser(){
//        return user;
//    }

    public List<MPositionInfoVo> positions;

    private String position_name;

    /**
     * 身份证正面照片id
     */
    private Integer one_file;

    /**
     * 身份证正面照片附件对象
     */
    private SFileInfoVo one_fileVo;

    /**
     * 身份证背面附件id
     */
    private Integer two_file;

    /**
     * 身份证背面附件对象
     */
    private SFileInfoVo two_fileVo;

    // 权限数信息
    private TreeDataVo permissionTreeData;

    /**
     * 最后一次登录登出时间
     */
    private LocalDateTime last_login_date
            , last_logout_date;

    /**
     * 是否开启登录
     */
    private Boolean is_enable;

}
