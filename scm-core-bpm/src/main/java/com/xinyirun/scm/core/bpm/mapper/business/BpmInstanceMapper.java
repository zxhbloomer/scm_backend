package com.xinyirun.scm.core.bpm.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCommentVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceApproveVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import com.xinyirun.scm.bean.system.vo.master.user.MPositionInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.xinyirun.scm.bean.bpm.vo.BpmInstanceVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Repository
public interface BpmInstanceMapper extends BaseMapper<BpmInstanceEntity> {

    @Select("                             "
        + "     SELECT                                                                              "
        + "     	t1.*,                                                                           "
        + "     	t2.process_definition_business_name ,                                            "
        + "     	t2.summary                                                                      "
        + "     FROM                                                                                "
        + "     	bpm_instance t1                                                                 "
        + "     	inner join bpm_instance_summary t2 on t2.process_code = t1.process_code         "
        + "     WHERE true                                                                          "
        + "     and	t1.process_instance_id = #{p1}                                                  "
        + "     and t1.process_definition_id = #{p2}                                                "
    )
    BpmInstanceVo getInstanceAndSummary(@Param("p1") String processInstanceId,@Param("p2") String processDefinitionId);


    @Select("select * from bpm_instance where process_instance_id = #{p1} and process_definition_id = #{p2}")
    BpmInstanceEntity selectByInstanceIdAndDefId(@Param("p1") String processInstanceId,@Param("p2") String processDefinitionId);

    @Select("select process_instance_id from bpm_instance where process_code = #{processCode}")
    String selectProcessInstanceIdByCode(@Param("processCode") String processCode);


    /**
     * 查看我发起的实例（流程）
     */
    @Select(" <script>                                                                                                 "
            + " SELECT                                                                                                 "
            + " t1.*,                                                                                                  "
            + " t2.assignee_name,                                                                                      "
            + " t3.label AS approve_type_name,                                                                         "
            + " t4.label AS status_name,                                                                               "
            + " t5.user_name AS owner_name                                                                             "
            + " FROM bpm_instance t1                                                                                   "
            + " LEFT JOIN bpm_todo t2 ON t1.current_task_id = t2.task_id                                               "
            + " LEFT JOIN s_dict_data t3 ON t3.CODE = '"+ DictConstant.DICT_SYS_CODE_BPM_APPROVE_TYPE +"'              "
            + " AND t2.approve_type = t3.dict_value                                                                    "
            + " LEFT JOIN s_dict_data t4 ON t4.CODE = '"+ DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS +"'           "
            + " AND t1.status = t4.dict_value                                                                          "
            + " LEFT JOIN bpm_users t5 ON t5.user_code = t1.owner_code                                                 "
            + " WHERE TRUE AND t1.owner_code = #{p1.owner_code}                                                        "
            + " </script>                                                                                              "
    )
    IPage<BBpmInstanceVo> selectPageList(Page<BpmInstanceEntity> pageCondition,@Param("p1") BBpmInstanceVo param);


    /**
     * 查看当前审批的前一个节点审批信息
     */
    @Select("                                                                                               "
            + "       	 select *                                                                            "
            + "         from bpm_instance_approve t1                                                          "
            + "        where exists (                                                                         "
            + "       	select subt2.node_id                                                                   "
            + "         from bpm_instance_process subt1                                                        "
            + "       	INNER JOIN bpm_instance_process subt2                                                  "
            + "       	on subt1.node_id = subt2.is_next                                                      "
            + "        and subt1.process_code = subt2.process_code                                            "
            + "        where subt1.result = 'running'                                                         "
            + "          and subt1.process_code = #{p1}                                                       "
            + "          and t1.node_id = subt2.node_id                                                      "
            + "       	 and t1.process_code = subt1.process_code                                            "
            + "        )                                                                            "
    )
    BBpmInstanceApproveVo getPreviousApproveInfo(@Param("p1") String processInstanceCode);


    /**
     * 获取全部岗位
     * @return
     */
    @Select("                                                                                                                                                           "
            + "	SELECT                                                                                                  "
            + "		GROUP_CONCAT(subt2.NAME SEPARATOR '、')  as position_names                                          "
            + "	FROM                                                                                                    "
            + "		m_staff_org subt1                                                                                   "
            + "		INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position'                                     "
            + "		AND subt1.serial_id = subt2.id                                                                      "
            + "    inner JOIN m_staff subt3                                                                             "
            + "    on (subt3.code =#{p1} or #{p1} is null)                                                              "
            + "    and (subt1.staff_id = subt3.id)                                                                      "
            + "           "
            + " ")
    String getPositionNames(@Param("p1") String code);

    /**
     * 获取全部岗位
     * @return
     */
    @Select("                                                                                                                                                           "
            + "	                                                                                       "
            + "	  SELECT                                                                               "
            + "	  	t4.*                                                                               "
            + "	  FROM                                                                                 "
            + "	  	bpm_instance t1                                                                    "
            + "	  	INNER JOIN bpm_instance_process t2 ON t1.process_code = t2.process_code            "
            + "	  	INNER JOIN bpm_instance_approve t3 ON t3.node_id = t2.node_id                      "
            + "	  	AND t3.process_code = t1.process_code                                              "
            + "	  	INNER JOIN bpm_comment t4 ON t4.process_code = t1.process_code                     "
            + "	  	AND t4.node_id = t3.node_id                                                         "
            + "	  WHERE                                                                                "
            + "	  	t1.process_code = #{p1}                                                            "
            + "           "
            + " ")
    List<BBpmCommentVo> getProcessComments(@Param("p1") String code);
}
