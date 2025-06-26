package com.xinyirun.scm.core.bpm.mapper.business;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.bpm.mybatis.handler.JsonObjectTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;

import java.util.List;

/**
 * <p>
 * process_templates Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Repository
public interface BpmProcessTemplatesMapper extends BaseMapper<BpmProcessTemplatesEntity> {


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("                                                                         "
            + "       SELECT                                                            "
            + "       	t1.id,                                                         "
            + "       	t1.version,                                                    "
            + "       	t1.template_id,                                                "
            + "       	t1.deployment_id,                                              "
            + "       	t1.`code`,                                                     "
            + "       	t1.`name`,                                                     "
            + "       	t1.settings,                                                   "
            + "       	t1.form_items,                                                 "
            + "       	t1.process,                                                    "
            + "       	t1.icon,                                                       "
            + "       	t1.background,                                                 "
            + "       	t1.notify,                                                     "
            + "       	t1.who_commit,                                                 "
            + "       	t1.who_edit,                                                   "
            + "       	t1.who_export,                                                 "
            + "       	t1.remark,                                                     "
            + "       	t1.group_id,                                                   "
            + "       	t1.is_stop,                                                    "
            + "       	t1.c_time,                                                     "
            + "       	t1.u_time,                                                     "
            + "       	t1.c_id,                                                       "
            + "       	t1.u_id,                                                       "
            + "       	t1.dbversion,                                                  "
            + "       	t2.name as c_name,                                             "
            + "       	t3.name as u_name                                              "
            + "       FROM                                                              "
            + "       	bpm_process_templates AS t1                                    "
            + "       	LEFT JOIN m_staff t2 ON t1.c_id = t2.id                         "
            + "       	LEFT JOIN m_staff t3 ON t1.u_id = t3.id                         "
            + "       WHERE                                                             "
            + "       	t1.is_stop = 0                                                 "
            + "        and (t1.name  like CONCAT('%', #{p1.name}, '%')  or #{p1.name} is null )                     "
            + "        and (t1.page_code  like CONCAT('%', #{p1.page_code}, '%')  or #{p1.page_code} is null ) or #{p1.page_code} = ''              "
    )
    IPage<BBpmProcessVo> selectPage(Page page, @Param("p1") BBpmProcessVo searchCondition);

//    @Select( " SELECT * FROM bpm_process_templates t1 WHERE                                                 "
//            +"	t1.CODE =                                                                                   "
//            +"	( SELECT t2.extra1 FROM s_dict_data t2                                                      "
//            +"	   WHERE t2.CODE = '"+ DictConstant.B_BPM_PROCESS_TYPE +"'                                  "
//            +"	    AND t2.dict_value = #{p1} )                                                             ")
//    BBpmProcessVo generateEngineFlow(@Param("p1") String entityName);

//    /**
//     * id查询审批流程模板
//     */
//    @Select("select * from bpm_process_templates where template_id = #{p1}")
//    BpmProcessTemplatesEntity selectByTemplates(@Param("p1")String replace);

    /**
     * code查询审批流程模板
     */
    @Select("select * from bpm_process_templates where code = #{p1} and is_stop = 0 ")
    BpmProcessTemplatesEntity selectByCode(@Param("p1")String code);

    /**
     * 根据类型获取到审批流程模板，只有1条
     */
    @Select("                                            "
            + "   SELECT                                                                "
            + "   	*,                                                                "
            + "   IF                                                                    "
            + "   	(                                                                 "
            + "   		process,                                                       "
            + "   		JSON_ARRAY(),                                                  "
            + "   	JSON_EXTRACT( process, '$.props.assignedUser' )) AS orgUserVoList  "
            + "   FROM                                                                "
            + "   	bpm_process_templates                                             "
            + "   WHERE true                                                          "
            + "   	and type = #{p1}                                                  "
            + "     and is_stop = 0                                                   "
            + "                                                                        "
    )
    @Results({
            @Result(property = "orgUserVoList", column = "orgUserVoList", javaType = List.class ,typeHandler = JsonObjectTypeHandler.class),
    })
    BBpmProcessVo getBpmFLowByType(@Param("p1")String type);


    /**
     * 获取审批节点使用的数据
     * @param id
     * @return
     */
    @Select("                                                        "
            + "   SELECT                                             "
            + "       t1.id,                                         "
            + "       t1.`name`,                                     "
            + "       t1.code,                                     "
            + "       t2.avatar,                                     "
            + "       null as position,                              "
            + "       'user' as type                                 "
            + "   FROM                                               "
            + "       m_staff t1                                     "
            + "       LEFT JOIN m_user t2 on t2.id = t1.user_id      "
            + "   WHERE                                              "
            + "       t1.id =  #{p1}                                  "
            + "                                                      ")
    AppStaffUserBpmInfoVo getBpmDataByStaffid(@Param("p1") Long id);

    /**
     * 获取userVo
     * @param id
     * @return
     */
    @Select("                                                                                                                    "
            + "      SELECT                                                                                                          "
            + "              t1.* ,                                                                                                  "
            + "            	 t2.label as type_text                                                                                   "
            + "        FROM                                                                                                          "
            + "              m_user t1                                                                                               "
            + "   left join  v_dict_info t2 on t2.code = 'usr_login_type' and t1.type = t2.dict_value                                "
            + "       where  true                                                                                                    "
            + "         and  (t1.id = #{p1})                                                                                         "
//        + "         and (t1.tenant_id  = #{p2} or #{p2} is null)                                                                "
            + "                                                                                                                      ")
    MUserVo selectUserById(@Param("p1") Long id );

    /**
     * 获取staffvo
     * @return
     */
    @Select("    "
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
            + "             LEFT JOIN v_dict_info AS t8 ON t8.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t8.dict_value = cast(t1.is_del as char(1)) "
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
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)      "
            + "      ")
    MStaffVo selectMstaffVoByid(@Param("p1") MStaffVo searchCondition);

    /**
     * 流程管理-获取流程模型数据
     * @param searchCondition
     * @return
     */
    @Select("                                                                         "
            + "       SELECT                                                            "
            + "       	t1.id,                                                         "
            + "       	t1.version,                                                    "
            + "       	t1.template_id,                                                "
            + "       	t1.deployment_id,                                              "
            + "       	t1.`code`,                                                     "
            + "       	t1.`name`,                                                     "
            + "       	t1.settings,                                                   "
            + "       	t1.form_items,                                                 "
            + "       	t1.process,                                                    "
            + "       	t1.icon,                                                       "
            + "       	t1.background,                                                 "
            + "       	t1.notify,                                                     "
            + "       	t1.who_commit,                                                 "
            + "       	t1.who_edit,                                                   "
            + "       	t1.who_export,                                                 "
            + "       	t1.remark,                                                     "
            + "       	t1.group_id,                                                   "
            + "       	t1.is_stop,                                                    "
            + "       	t1.c_time,                                                     "
            + "       	t1.u_time,                                                     "
            + "       	t1.c_id,                                                       "
            + "       	t1.u_id,                                                       "
            + "       	t1.dbversion,                                                  "
            + "       	t2.name as c_name,                                             "
            + "       	t3.name as u_name                                              "
            + "       FROM                                                              "
            + "       	bpm_process_templates AS t1                                    "
            + "       	LEFT JOIN m_staff t2 ON t1.c_id = t2.id                         "
            + "       	LEFT JOIN m_staff t3 ON t1.u_id = t3.id                         "
            + "       WHERE                                                             "
            + "       	t1.is_stop = 0                                                 "
            + "       and t1.page_code = #{p1.page_code}                               "
    )
    List<BBpmProcessVo> getBpmDataByPageCode( @Param("p1") BBpmProcessVo searchCondition);
}
