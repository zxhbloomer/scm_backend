package com.xinyirun.scm.core.bpm.mapper.business;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceApproveEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
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
public interface BpmInstanceApproveMapper extends BaseMapper<BpmInstanceApproveEntity> {


    /**
     * 查询审批流用户节点信息
     */
    @Select("select * from bpm_instance_approve where node_id = #{p1} and process_code = #{p2} and assignee_code = #{p3}")
    BpmInstanceApproveEntity selByNodeIdAndAssigneeCode(@Param("p1") String taskDefinitionKey,@Param("p2") String process_code,@Param("p3") String assignee);

    /**
     * 查询审批流节点已完成审批信息
     */
    @Select(  " select * from bpm_instance_approve tab1 left join bpm_instance_process tab2 on tab1.node_id = tab2.node_id and tab1.process_code = tab2.process_code "
            + " where tab1.node_id = #{p1} and tab1.process_code = #{p2} and tab1.assignee_code != #{p3} and tab2.approval_mode = #{p4} and tab1.approve_type !='1'     ")
    List<BpmInstanceApproveEntity> selByNodeIdNotAssigneeCode(@Param("p1")String taskDefinitionKey,@Param("p2") String processCode,@Param("p3") String assignee,@Param("p4") String mode);

    /**
     * 查询审批流节点所有信息
     */
    @Select("select * from bpm_instance_approve where node_id = #{p1} and process_instance_id = #{p2} and type = #{p3} ")
    List<BpmInstanceApproveEntity> selByProcessInstanceIdAndNodeIdAndAssigneeCode(@Param("p1") String node_id,@Param("p2") String process_instance_id,@Param("p3")String type);

    @Select("select * from bpm_instance_approve where task_id = #{p1}")
    BpmInstanceApproveEntity selectByTaskId(@Param("p1")String id);

    /**
     * 查看流程代办的任务
     */
    @Select("select * from bpm_instance_approve where process_code = #{p1} and (task_id != #{p2} or task_id is null) and approve_type = #{p3}")
    List<BpmInstanceApproveEntity> getProcessCodeNotByTaskIdByStatus(@Param("p1") String processCode,@Param("p2") String taskId,@Param("p3") String bBpmTodoStatusZero);



    /**
     * 查询节点未审批的任务
     */
    @Select("SELECT tab1.* FROM bpm_instance_approve tab1 where tab1.type = 1 and tab1.status = 0 and tab1.process_code = #{p1}")
    List<BpmInstanceApproveEntity> selectRunIngNodeIdByTask(@Param("p1")String processCode);


    /**
     * 查询节点下的最新的评论
     */
    @Select(" 	WITH recursive tab1 AS (                                                                                  "
            +"	SELECT                                                                                                    "
            +"		*                                                                                                     "
            +"	FROM                                                                                                      "
            +"		bpm_instance_approve                                                                                  "
            +"	WHERE                                                                                                     "
            +"		node_id = #{p2} UNION ALL                                                                             "
            +"	SELECT                                                                                                    "
            +"		t2.*                                                                                                  "
            +"	FROM                                                                                                      "
            +"		bpm_instance_approve t2,                                                                              "
            +"		tab1                                                                                                  "
            +"	WHERE                                                                                                     "
            +"		tab1.is_next = t2.node_id )                                                                           "
            +"select * FROM tab1 where process_code = #{p1}  and type = 3 ORDER BY c_time DESC LIMIT 1                    ")
    BpmInstanceApproveEntity selectNodeIdByNewComment(@Param("p1")String processCode,@Param("p2")String nodeId);


    /**
     * 查询节点下的其他任务
     */
    @Select("SELECT * FROM bpm_instance_approve WHERE process_code = #{p1} AND node_id = #{p2} AND status = #{p3}")
    List<BpmInstanceApproveEntity> getProcessCodeAndNodeIdByStatusAll(@Param("p1") String processCode,@Param("p2") String nodeId,@Param("p3") String bBpmTodoStatusZero);

    /**
     * 查询节点下所有的任务
     */
    @Select(
          "                                                                            "
        + "   WITH recursive tb2 AS (                                                  "
        + "   	SELECT                                                                 "
        + "   		*                                                                  "
        + "   	FROM                                                                   "
        + "   		bpm_instance_process                                               "
        + "   	WHERE                                                                  "
        + "   		node_id = #{p2} UNION ALL                                          "
        + "   	SELECT                                                                 "
        + "   		t2.*                                                               "
        + "   	FROM                                                                   "
        + "   		bpm_instance_process t2,                                           "
        + "   		tb2                                                                "
        + "   	WHERE                                                                  "
        + "   		tb2.is_next = t2.node_id                                           "
        + "   		AND tb2.process_code = #{p1} 		                               "
        + "   	) 		                                                               "
        + "  SELECT 		                                                          "
        + "   	GROUP_CONCAT( tb3.assignee_code ) AS next_approve_code ,	          "
        + "   	GROUP_CONCAT( tb3.assignee_name ) AS next_approve_name 		           "
        + "   FROM		                                                               "
        + "   	bpm_instance_approve tb3 		                                       "
        + "   WHERE		                                                               "
        + "   	tb3.process_code = #{p1} 		                                       "
        + "   	AND tb3.node_id = (		                                               "
        + "   	SELECT		                                                           "
        + "   		tb2.node_Id 		                                               "
        + "   	FROM		                                                           "
        + "   		tb2 		                                                       "
        + "   	WHERE		                                                           "
        + "   		tb2.process_code = #{p1} 		                                   "
        + "   		AND tb2.node_type = 'APPROVAL' 		                               "
        + "   	GROUP BY		                                                      "
        + "   		tb2.node_id 		                                            "
        + "   	LIMIT 1 		                                                   "
        + "   	)		                                                                   "
        + "   		                                                                   "
    )
    BBpmInstanceVo selNextApproveName(@Param("p1")String processCode, @Param("p2") String node_id);
}
