package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

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
@EqualsAndHashCode(callSuper=false)
public class MStaffExportVo implements Serializable {

    private static final long serialVersionUID = 4733748930234972849L;


    @ExcelAnnotion(name = "NO")
    private Integer no;

    @ExcelAnnotion(name = "用户名")
    private String login_name;

    @ExcelAnnotion(name = "员工姓名")
    private String name;



    @ExcelAnnotion(name = "性别")
    private String sex_text;

    @ExcelAnnotion(name = "生日")
    private LocalDate birthday;

    @ExcelAnnotion(name = "身份证号码")
    private String id_card;

   /* @ExcelAnnotion(name = "护照号码")
    private String passport;

    @ExcelAnnotion(name = "是否在职")
    private String service_text;

    @ExcelAnnotion(name = "是否已婚")
    private String is_wed_text;

    @ExcelAnnotion(name = "民族")
    private String nation;

    @ExcelAnnotion(name = "学历")
    private String degree_text;*/

    @ExcelAnnotion(name = "邮箱地址")
    private String email;

   /* @ExcelAnnotion(name = "家庭电话")
    private String home_phone;


    @ExcelAnnotion(name = "办公室电话")
    private String office_phone;

    @ExcelAnnotion(name = "手机号码")
    private String mobile_phone;

    @ExcelAnnotion(name = "备用手机号码")
    private String mobile_phone_backup;

    @ExcelAnnotion(name = "备用电子邮件")
    private String email_backup;


    private Long company_id;*/

    @ExcelAnnotion(name = "所属公司")
    private String company_name;

    @ExcelAnnotion(name = "默认部门")
    private String dept_name;

    @ExcelAnnotion(name = "岗位信息")
    private String positions;

    @ExcelAnnotion(name = "仓库组")
    private String warehouse_group_list;

    @ExcelAnnotion(name = "排除权限", width=15)
    private String exclude_permission_count_text;

    @ExcelAnnotion(name = "排除权限信息", width=30)
    private String exclude_permissions_text;

    @ExcelAnnotion(name = "最后登录时间", dateFormat="yyyy-MM-dd HH:mm:ss", width=25)
    private Date last_login_date;

    @ExcelAnnotion(name = "最后主动登出时间", dateFormat="yyyy-MM-dd HH:mm:ss", width=25)
    private Date last_logout_date;

    @ExcelAnnotion(name = "是否删除")
    private String is_del_name;

/*    @ExcelAnnotion(name = "身份证正面照片")
    private String one_file_url;

    @ExcelAnnotion(name = "身份证背面照片")
    private String two_file_url;*/



    @ExcelAnnotion(name = "创建人")
    private String c_name;
    @ExcelAnnotion(name = "创建时间", dateFormat="yyyy-MM-dd HH:mm:ss", width=25)
    private Date c_time;

    @ExcelAnnotion(name = "更新人")
    private String u_name;

    @ExcelAnnotion(name = "更新时间", dateFormat="yyyy-MM-dd HH:mm:ss", width=25)
    private Date u_time;
}
