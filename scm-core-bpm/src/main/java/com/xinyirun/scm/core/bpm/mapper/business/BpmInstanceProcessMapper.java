package com.xinyirun.scm.core.bpm.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceProcessEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Repository
public interface BpmInstanceProcessMapper extends BaseMapper<BpmInstanceProcessEntity> {

    /**
     * 获取未开始的流程节点
     */
    @Select("select * from bpm_instance_process where node_id = #{p1} and process_code = #{p2} and start_time is null")
    BpmInstanceProcessEntity selectBpmInstanceAndStartTimeIsNull(@Param("p1") String taskDefinitionKey,@Param("p2") String processCode);

    /**
     * 获取流程节点
     */
    @Select("select * from bpm_instance_process where node_id = #{p1} and process_code = #{p2}")
    BpmInstanceProcessEntity selectBpmInstance(@Param("p1") String taskDefinitionKey,@Param("p2") String processCode);
}
