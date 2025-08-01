package com.xinyirun.scm.core.whapp.mapper.master.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.whapp.vo.master.user.WhAppStaffUserBpmInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 员工 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Repository
public interface WhAppMStaffMapper extends BaseMapper<MStaffEntity> {

    String common_select1 = "       "
            + "      SELECT                                                                                                         "
            + "            	t1.* ,                                                                                                  "
            + "            	t10.login_name,                                                                                         "
            + "            	t10.is_enable,                                                                                          "
            + "            	t2.label as sex_text,                                                                                   "
            + "            	t3.label as service_text,                                                                               "
            + "            	t4.label as degree_text,                                                                                "
            + "            	t5.label as is_wed_text,                                                                                "
            + "            	t6.name as company_name,                                                                                "
            + "            	t6.simple_name as company_simple_name,                                                                  "
            + "            	t7.name as dept_name,                                                                                   "
            + "            	t7.simple_name as dept_simple_name,                                                                     "
            + "             c_staff.name as c_name,                                                                                 "
            + "             u_staff.name as u_name,                                                                                 "
            + "             t10.last_login_date,                                                                                    "
            + "             t10.last_logout_date,                                                                                   "
            + "             t8.label as is_del_name,                                                                                "
            + "             t11.group_list as warehouse_group_list                                                                  "
            + "        FROM                                                                                                         "
            + "            	m_staff t1                                                                                              "
            + "            	LEFT JOIN v_dict_info AS t2 ON t2.code = '" + DictConstant.DICT_SYS_SEX_TYPE + "' and t2.dict_value = t1.sex                      "
            + "            	LEFT JOIN v_dict_info AS t3 ON t3.code = '" + DictConstant.DICT_USR_SERVICE_TYPE + "' and t3.dict_value = t1.service              "
            + "            	LEFT JOIN v_dict_info AS t4 ON t4.code = '" + DictConstant.DICT_USR_DEGREE_TYPE + "' and t4.dict_value = t1.degree                "
            + "             LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_USR_WED_TYPE + "' and t5.dict_value = t1.is_wed                   "
            + "             LEFT JOIN m_company AS t6 ON t6.id = t1.company_id                                                      "
            + "             LEFT JOIN m_dept AS t7 ON t7.id = t1.dept_id                                                            "
            + "             LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       "
            + "             LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       "
            + "             LEFT JOIN v_dict_info AS t8 ON t8.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t8.dict_value = CONCAT('', t1.is_del) "
            + "             LEFT JOIN m_user t10 ON t10.staff_id = t1.id                                                            "
            // WMS-444 用户管理，岗位信息后一列增加给用户分配的仓库组，该仓库组列不需穿透，导出也需包含该仓库组列
            + " LEFT JOIN (                                                                                             "
            + "   SELECT                                                                                                "
            + "     t1.staff_id,                                                                                        "
            + "     JSON_ARRAYAGG(JSON_OBJECT('name', t2.name, 'id', t2.id)) as group_list                              "
            + "   FROM                                                                                                  "
            + "      b_warehouse_relation t1                                                                            "
            + " 	INNER JOIN b_warehouse_group t2 ON t2.id = t1.serial_id AND t1.serial_type = 'b_warehouse_group'    "
            + " GROUP BY t1.staff_id) t11 ON t11.staff_id = t1.id                                                       "

            + "       where true                                                                                        "
            + "          and (t1.code != 'SYSTEMADMIN' or t1.code is null)                                              "
            + "                    ";

    /**
     * 页面查询列表
     * @return
     */
    @Select("    "
            + common_select1
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)      "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)      "
            + "      ")
    MStaffVo selectByid(@Param("p1") MStaffVo searchCondition);


    /**
     * 获取审批节点使用的数据
     * @param id
     * @return
     */
    @Select("                                                        "
            + "   SELECT                                             "
            + "       t1.id,                                         "
            + "       t1.`name`,                                     "
            + "       t2.avatar,                                     "
            + "       null as position,                              "
            + "       'user' as type                                 "
            + "   FROM                                               "
            + "       m_staff t1                                     "
            + "       LEFT JOIN m_user t2 on t2.id = t1.user_id      "
            + "   WHERE                                              "
            + "       t1.id =  #{p1}                                  "
            + "                                                      ")
    WhAppStaffUserBpmInfoVo getBpmDataByStaffid(@Param("p1") Long id);
}
