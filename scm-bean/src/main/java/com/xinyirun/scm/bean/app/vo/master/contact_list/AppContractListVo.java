package com.xinyirun.scm.bean.app.vo.master.contact_list;

import com.xinyirun.scm.bean.app.config.base.AppBaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 通讯录
 *
 * @author zxh
 * @since 2024-12-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AppContractListVo extends AppBaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4823280296903226217L;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 姓名
     */
    private String staff_name;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 登录用户id，关联id
     */
    private Long user_id;

    /**
     * 手机号码
     */
    private String mobile_phone;

    /**
     * 所属公司名称
     */
    private String company_name;


    /**
     * 员工的岗位信息集合
     */
    public List<AppMStaffPositionsVo> positions;

    /**
     * 员工的岗位信息——按分隔符
     */
    private String positions_name;

    /**
     * 页面查询的关键字
     */
    private String search_str;

}
