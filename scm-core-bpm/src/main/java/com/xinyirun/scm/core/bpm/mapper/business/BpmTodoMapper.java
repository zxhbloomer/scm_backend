package com.xinyirun.scm.core.bpm.mapper.business;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.master.user.AppMUserVo;
import com.xinyirun.scm.bean.entity.bpm.BpmTodoEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.*;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.bpm.mybatis.handler.JsonObjectTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

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
public interface BpmTodoMapper extends BaseMapper<BpmTodoEntity> {

    @Select("select * from bpm_todo where task_id = #{p1}")
    BpmTodoEntity selectByTaskId(@Param("p1") String id);

    /**
     * 查看我的待办
     */
    @Select( "    "
            +"  SELECT t1.*,                                                                                                                                          "
            +"        t2.process_code as bpm_instance_code,                                                                                                           "
            +"        t2.process_definition_name,                                                                                                                     "
            +"        t2.process_definition_version,                                                                                                                  "
            +"        t3.label as approve_type_name,                                                                                                                  "
            +"        t4.user_name as owner_name ,                                                                                                                     "
            +"        t5.process_definition_business_name ,                                                                                                           "
            +"        t5.summary as json_summary                                                                                                                      "
            +"        FROM bpm_todo t1                                                                                                                                 "
            +"  LEFT JOIN bpm_instance t2 on t1.process_code = t2.process_code                                                                                         "
            +"  LEFT JOIN s_dict_data t3 ON t3.CODE = '"+ DictConstant.DICT_SYS_CODE_BPM_APPROVE_TYPE +"'                                                              "
            +"  AND t1.approve_type = t3.dict_value                                                                                                                    "
            +"  LEFT JOIN bpm_users t4  ON t2.owner_code = t4.user_code                                                                                                "
            +"  LEFT JOIN bpm_instance_summary t5  ON t2.process_code = t5.process_code                                                                                "
            +"  WHERE TRUE                                                                                                                                             "
            +"    AND t1.assignee_code = #{p1.user_code}                                                                                                               "
            +"    AND t1.status = #{p1.status}                                                                                                                         "
            +"     AND (t1.process_code like CONCAT ('%',#{p1.process_code},'%') or #{p1.process_code} is null)                                                         "
            +"     AND (t2.process_definition_name like CONCAT ('%',#{p1.process_definition_name},'%') or #{p1.process_definition_name} is null)                        "
            +"                           ")
    IPage<BBpmTodoVo> selectPageList(Page<BBpmTodoVo> pageCondition, @Param("p1") BBpmTodoVo param);


    /**
     * 查看我的待办
     */
    @Select( "    "
            +"  select count(1)   as count                                                                                                "
            +"   from (                                                                                                         "
            +"          SELECT t1.*,                                                                                             "
            +"                t2.process_code as bpm_instance_code,                                                              "
            +"                t2.process_definition_name,                                                                        "
            +"                t2.process_definition_version,                                                                     "
            +"                t3.label as approve_type_name,                                                                     "
            +"                t4.user_name as owner_name ,                                                                        "
            +"                t5.summary as json_summary                                                                         "
            +"                FROM bpm_todo t1                                                                                    "
            +"          LEFT JOIN bpm_instance t2 on t1.process_code = t2.process_code                                            "
            +"          LEFT JOIN s_dict_data t3 ON t3.CODE = '"+ DictConstant.DICT_SYS_CODE_BPM_APPROVE_TYPE +"'                 "
            +"          AND t1.approve_type = t3.dict_value                                                                       "
            +"          LEFT JOIN bpm_users t4  ON t2.owner_code = t4.user_code                                                   "
            +"          LEFT JOIN bpm_instance_summary t5  ON t2.process_code = t5.process_code                                   "
            +"          WHERE TRUE AND t1.assignee_code = #{p1.user_code} AND t1.status = #{p1.status}                            "
            +"    )  tab                                                                                                             "
            +"                                                                                                                   ")
    Integer selectTodoCount(@Param("p1") BBpmTodoVo param);


    /**
     * 查看我的待办，我的已办
     */
    @Select( "    "
            +"  SELECT t1.*,                                                                                             "
            +"        t2.process_code as bpm_instance_code,                                                              "
            +"        t2.process_definition_name,                                                                        "
            +"        t2.process_definition_version,                                                                     "
            +"        t3.label as approve_type_name,                                                                     "
            +"        t4.user_name as owner_name ,                                                                        "
            +"        t5.process_definition_business_name ,                                                              "
            +"        t5.summary as json_summary ,                                                                        "
            +"        #{p1.avatar} as avatar                                                                             "
            +"        FROM bpm_todo t1                                                                                    "
            +"  LEFT JOIN bpm_instance t2 on t1.process_code = t2.process_code                                            "
            +"  LEFT JOIN s_dict_data t3 ON t3.CODE = '"+ DictConstant.DICT_SYS_CODE_BPM_APPROVE_TYPE +"'                 "
            +"  AND t1.approve_type = t3.dict_value                                                                       "
            +"  LEFT JOIN bpm_users t4  ON t2.owner_code = t4.user_code                                                   "
            +"  LEFT JOIN bpm_instance_summary t5  ON t2.process_code = t5.process_code                                   "
            +"  WHERE TRUE AND t1.assignee_code = #{p1.user_code} AND t1.status = #{p1.status}                            "
            +"  order by t1.u_time desc                                                                                                 "
            +"  limit 10                           ")
    List<BBpmTodoVo> getListTen(@Param("p1") BBpmTodoVo param);

    /**
     * 通过流程实例id查看详情
     */
    @Select(
            // 设置查询字段显示长度，解决数据显示截断问题
            //"SET SESSION group_concat_max_len = 10240;                                                                 "
            // 查询节点信息
            " WITH recursive tb2 AS (                                                                                        "
            +"		SELECT                                                                                                   "
            +"			*                                                                                                    "
            +"		FROM                                                                                                     "
            +"			bpm_instance_approve                                                                                 "
            +"		WHERE                                                                                                    "
            +"			node_id = 'root' UNION ALL                                                                           "
            +"		SELECT                                                                                                   "
            +"			t2.*                                                                                                 "
            +"		FROM                                                                                                     "
            +"			bpm_instance_approve t2,tb2                                                                          "
            +"			WHERE  tb2.is_next = t2.node_id  AND tb2.process_code = #{p1.process_code}                          "
            +"		)                                                                                                        "
            // 查询数据
            +" SELECT                                                                                                        "
            +"	tab1.process_code,                                                                                           "
            +"	tab1.serial_type,                                                                                           "
            +"	tab1.form_items,                                                                                             "
            +"	tab1.process_instance_id,                                                                                    "
            +"	tab1.process_definition_name,                                                                                "
            +"	tab1.form_data,                                                                                              "
            +"	tab3.label as status_name,                                                                                   "
            +"	tab3.extra1 as result,																						 "
            +"		JSON_ARRAYAGG(                                                                                           "
            +"			JSON_OBJECT(                                                                                         "
            +"				'no',tab2.no,                                                                                    "
            +"				'node_id',tab2.node_id,                                                                          "
            +"				'task_id',tab2.task_id,                                                                          "
            +"				'approval_mode', tab2.approval_mode,                                                             "
            +"				'node_type', tab2.node_type,                                                                     "
            +"				'name', tab2.name,                                                                               "
            +"				'user', tab2.user,                                                                               "
            +"				'owner_name', tab2.owner_name,                                                                   "
            +"				'result',  tab2.result,                                                                          "
            +"				'start_time', tab2.start_time,                                                                   "
            +"				'finish_time', tab2.finish_time,                                                                 "
            +"				'comment',tab2.comment                                                                           "
            +"			)) AS progress,                                                                                      "
            +"		tab1.start_time,                                                                                         "
            +"		tab1.end_time as finish_time,                                                                            "
            +"	    JSON_OBJECT(                                                                                             "
            +"		  'id',tab4.id,                                                                                          "
            +"		  'code',tab4.user_code,                                                                                 "
            +"		  'name',tab4.user_name,                                                                                 "
            +"		  'avatar',tab4.avatar,                                                                                  "
            +"		  'type','user') AS owner_user                                                                           "
            +" FROM                                                                                                          "
            +"	bpm_instance tab1                                                                                            "
            +"	LEFT JOIN (                                                                                                  "
            +"	SELECT                                                                                                       "
            +"	 @row_num:= @row_num+ 1 as no,                                                                               "
            +"	tb1.process_code,                                                                                            "
            +"	tb1.node_id,                                                                                                 "
            +"	tb2.task_id,                                                                                                 "
            +"	tb1.approval_mode,                                                                                           "
            +"	tb1.node_type,                                                                                               "
            +"	tb1.name,                                                                                                    "
            +"	JSON_OBJECT(                                                                                                 "
            +"		'id',tb3.id,                                                                                             "
            +"		'code',tb3.user_code,                                                                                    "
            +"		'name',tb3.user_name,                                                                                    "
            +"		'avatar',tb3.avatar,                                                                                     "
            +"		'type',IF( tb3.id, 'user', NULL )) user,                                                                 "
            +"	tb1.owner_name,                                                                                              "
            +"	tb2.result,                                                                                                  "
            +"	tb1.start_time,                                                                                              "
            +"	tb2.approve_time AS finish_time,                                                                             "
            +"  IF(JSON_LENGTH(tb4.comment)>0,tb4.comment,JSON_ARRAY()) as  comment                                          "
            +"FROM                                                                                                           "
            +"  tb2                                                                                                          "
            +"	LEFT JOIN  bpm_instance_process tb1 ON tb1.process_code = tb2.process_code                                   "
            +"	AND tb1.node_id = tb2.node_id                                                                                "
            +"	LEFT JOIN bpm_users tb3 ON tb2.assignee_code = tb3.user_code                                                 "
            +"	LEFT JOIN ( SELECT tb1.node_id,tb1.task_id,                                                                  "
            +"	JSON_ARRAYAGG(                                                                                               "
            +"		JSON_OBJECT('text',tb1.text,'id',tb1.id,'task_id',tb1.task_id,'c_time',tb1.c_time,'user',                "
            +"			JSON_OBJECT( 'id', tb2.id, 'code', tb2.user_code, 'name', tb2.user_name, 'avatar', tb2.avatar ),     "
            +"			 'annex_files',IF(tb3.annex_files,JSON_ARRAY(),tb3.annex_files)                                      "
            +"		)) AS COMMENT                                                                                            "
            +"FROM                                                                                                           "
            +"	bpm_comment tb1                                                                                              "
            +"	LEFT JOIN bpm_users tb2 ON tb1.assignee_code = tb2.user_code                                                 "
            +"	LEFT JOIN (select f_id,JSON_ARRAYAGG(                                                                        "
            +"                  JSON_OBJECT('id',id,'f_id',f_id,'url',url,'timestamp',timestamp,'file_name',file_name))      "
            +"	 as annex_files from s_file_info where f_Id is not null GROUP BY f_id)                                       "
            +"	 tb3 on tb1.files_id = tb3.f_id                                                                              "
            +"GROUP BY                                                                                                       "
            +"	tb1.process_code,                                                                                            "
            +"	tb1.node_id)                                                                                                 "
            +"as tb4 on tb4.node_id = tb2.node_id  AND tb4.task_id = tb2.task_id,(select @row_num:=0) tb5                    "
            +"	group by tb2.id	                                                                                             "
            +"	) AS tab2 ON tab1.process_code = tab2.process_code                                                           "
            +"	LEFT JOIN s_dict_data tab3 ON tab3.CODE =  'bpm_instance_status'                                             "
            +"	AND tab1.STATUS = tab3.dict_value                                                                            "
            +"	LEFT JOIN bpm_users tab4 ON tab1.owner_code = tab4.user_code                                                 "
            +" WHERE                                                                                                         "
            +" TRUE                                                                                                          "
            +"	AND tab1.process_code = #{p1.process_code}  ORDER BY JSON_EXTRACT(progress, '$[*].no') ASC;                  "
//            +" GROUP BY                                                                                                    "
//            +"	tab1.process_code                                                                                        "
    )
    @Results({
            @Result(property = "progress", column = "progress", javaType = List.class ,typeHandler = JsonObjectTypeHandler.class),
            @Result(property = "user", column = "user", javaType = OrgUserVo.class ,typeHandler = JsonObjectTypeHandler.class),
            @Result(property = "owner_user", column = "owner_user", javaType = OrgUserVo.class ,typeHandler = JsonObjectTypeHandler.class),
            @Result(property = "comment", column = "comment", javaType = List.class ,typeHandler = JsonObjectTypeHandler.class),
            @Result(property = "annex_files", column = "annex_files", javaType = List.class ,typeHandler = JsonObjectTypeHandler.class),
    })
    BBpmInstanceProgressVo getInstanceProgress(@Param("p1")BBpmInstanceProgressVo param);

    /**
     * 查看流程待办的任务
     */
    @Select("SELECT * FROM bpm_todo WHERE process_code = #{p1} AND task_id != #{p2} AND status = #{p3}")
    List<BpmTodoEntity> getProcessCodeNotByTaskIdByStatus(@Param("p1") String processCode,@Param("p2") String taskId,@Param("p3")String status);

    /**
     * 查看流程所有待办的任务
     */
    @Select("SELECT * FROM bpm_todo WHERE process_code = #{p1}  AND status = #{p3}")
    List<BpmTodoEntity> getProcessCodeByStatusAll(@Param("p1") String processCode,@Param("p3")String status);

    /**
     * 查看节点下的其他待办的任务
     */
    @Select("SELECT * FROM bpm_todo WHERE process_code = #{p1} AND node_id = #{p2} AND status = #{p3}")
    List<BpmTodoEntity> getProcessCodeAndNodeIdByStatusAll(@Param("p1") String processCode,@Param("p2") String nodeId,@Param("p3")String status);

    /**
     * 查询当前用户的待办任务
     */
    @Select("SELECT tab1.* FROM bpm_todo tab1  where  tab1.status = 0 and tab1.process_code = #{p1} and tab1.assignee_code = #{p2} limit 1")
    BpmTodoEntity selectRunIngNodeIdByTask(@Param("p1")String processCode,@Param("p2")String userCode);


    /**
     * 获取审批节点使用的数据
     * @param id
     * @return
     */
    @Select("                                                        "
            + "   SELECT                                             "
            + "       t1.id,                                         "
            + "       t1.code,                                       "
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
    AppStaffUserBpmInfoVo getBpmDataByStaffid(@Param("p1") Long id);
}
