package com.xinyirun.scm.ai.mapper.worker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.worker.WorkerNodeEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 工作节点表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface BaseWorkerNodeMapper extends BaseMapper<WorkerNodeEntity> {

    /**
     * 批量插入工作节点记录
     */
    @Insert("<script>" +
            "INSERT INTO worker_node (id, host_name, port, type, launch_date, modified, created) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.hostName}, #{item.port}, #{item.type}, #{item.launchDate}, #{item.modified}, #{item.created})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<WorkerNodeEntity> list);

    /**
     * 根据主机名和端口查询工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "WHERE host_name = #{hostName} AND port = #{port} " +
            "LIMIT 1")
    WorkerNodeEntity selectByHostNameAndPort(@Param("hostName") String hostName, @Param("port") String port);

    /**
     * 根据节点类型查询工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "WHERE type = #{type} " +
            "ORDER BY created DESC")
    List<WorkerNodeEntity> selectByType(@Param("type") Integer type);

    /**
     * 查询所有活跃的工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "WHERE launch_date IS NOT NULL " +
            "ORDER BY launch_date DESC")
    List<WorkerNodeEntity> selectActiveNodes();

    /**
     * 根据启动时间范围查询工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "WHERE launch_date >= #{startTime} AND launch_date <= #{endTime} " +
            "ORDER BY launch_date DESC")
    List<WorkerNodeEntity> selectByLaunchDateRange(@Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime);

    /**
     * 更新工作节点修改时间
     */
    @Update("UPDATE worker_node " +
            "SET modified = #{modified} " +
            "WHERE id = #{id}")
    int updateModified(@Param("id") Long id, @Param("modified") Long modified);

    /**
     * 更新工作节点启动时间
     */
    @Update("UPDATE worker_node " +
            "SET launch_date = #{launchDate}, modified = #{modified} " +
            "WHERE id = #{id}")
    int updateLaunchDate(@Param("id") Long id,
                        @Param("launchDate") Long launchDate,
                        @Param("modified") Long modified);

    /**
     * 统计工作节点数量
     */
    @Select("SELECT COUNT(*) FROM worker_node")
    long countNodes();

    /**
     * 统计指定类型的工作节点数量
     */
    @Select("SELECT COUNT(*) FROM worker_node " +
            "WHERE type = #{type}")
    long countByType(@Param("type") Integer type);

    /**
     * 查询最新的工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "ORDER BY created DESC " +
            "LIMIT 1")
    WorkerNodeEntity selectLatestNode();

    /**
     * 根据主机名查询工作节点
     */
    @Select("SELECT id, host_name, port, type, launch_date, modified, created " +
            "FROM worker_node " +
            "WHERE host_name = #{hostName} " +
            "ORDER BY created DESC")
    List<WorkerNodeEntity> selectByHostName(@Param("hostName") String hostName);

    /**
     * 删除指定时间之前的工作节点记录
     */
    @Delete("DELETE FROM worker_node " +
            "WHERE created < #{beforeTime}")
    int deleteByCreatedBefore(@Param("beforeTime") Long beforeTime);

    /**
     * 清理旧的工作节点记录
     */
    @Delete("DELETE FROM worker_node " +
            "WHERE launch_date IS NULL " +
            "AND created < #{beforeTime}")
    int cleanupOldNodes(@Param("beforeTime") Long beforeTime);
}