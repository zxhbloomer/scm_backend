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
    @Insert("""
    <script>
        INSERT INTO worker_node (
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        )
        VALUES
        <foreach collection='list' item='item' separator=','>
            (
                #{item.host_name},
                #{item.port},
                #{item.type},
                #{item.launch_date},
                #{item.tenant},
                #{item.c_time},
                #{item.u_time},
                #{item.c_id},
                #{item.u_id},
                #{item.dbversion}
            )
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<WorkerNodeEntity> list);

    /**
     * 根据主机名和端口查询工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE host_name = #{hostName}
            AND port = #{port}
        LIMIT 1
        """)
    WorkerNodeEntity selectByHostNameAndPort(@Param("hostName") String hostName, @Param("port") String port);

    /**
     * 根据节点类型查询工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE type = #{type}
        ORDER BY c_time DESC
        """)
    List<WorkerNodeEntity> selectByType(@Param("type") Integer type);

    /**
     * 查询所有活跃的工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE launch_date IS NOT NULL
        ORDER BY launch_date DESC
        """)
    List<WorkerNodeEntity> selectActiveNodes();

    /**
     * 根据启动时间范围查询工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE launch_date >= #{startTime}
            AND launch_date <= #{endTime}
        ORDER BY launch_date DESC
        """)
    List<WorkerNodeEntity> selectByLaunchDateRange(@Param("startTime") java.time.LocalDateTime startTime,
                                                  @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 更新工作节点修改时间
     */
    @Update("""
        UPDATE worker_node
        SET
            u_time = #{uTime},
            u_id = #{uId},
            dbversion = dbversion + 1
        WHERE id = #{id}
        """)
    int updateModified(@Param("id") Integer id,
                      @Param("uTime") java.time.LocalDateTime uTime,
                      @Param("uId") Long uId);

    /**
     * 更新工作节点启动时间
     */
    @Update("""
        UPDATE worker_node
        SET
            launch_date = #{launchDate},
            u_time = #{uTime},
            u_id = #{uId},
            dbversion = dbversion + 1
        WHERE id = #{id}
        """)
    int updateLaunchDate(@Param("id") Integer id,
                        @Param("launchDate") java.time.LocalDateTime launchDate,
                        @Param("uTime") java.time.LocalDateTime uTime,
                        @Param("uId") Long uId);

    /**
     * 统计工作节点数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM worker_node
        """)
    long countNodes();

    /**
     * 统计指定类型的工作节点数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM worker_node
        WHERE type = #{type}
        """)
    long countByType(@Param("type") Integer type);

    /**
     * 查询最新的工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        ORDER BY c_time DESC
        LIMIT 1
        """)
    WorkerNodeEntity selectLatestNode();

    /**
     * 根据主机名查询工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE host_name = #{hostName}
        ORDER BY c_time DESC
        """)
    List<WorkerNodeEntity> selectByHostName(@Param("hostName") String hostName);

    /**
     * 删除指定时间之前的工作节点记录
     */
    @Delete("""
        DELETE FROM worker_node
        WHERE c_time < #{beforeTime}
        """)
    int deleteByCreatedBefore(@Param("beforeTime") java.time.LocalDateTime beforeTime);

    /**
     * 清理旧的工作节点记录
     */
    @Delete("""
        DELETE FROM worker_node
        WHERE launch_date IS NULL
            AND c_time < #{beforeTime}
        """)
    int cleanupOldNodes(@Param("beforeTime") java.time.LocalDateTime beforeTime);

    /**
     * 根据租户查询工作节点
     */
    @Select("""
        SELECT
            id,
            host_name,
            port,
            type,
            launch_date,
            tenant,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM worker_node
        WHERE tenant = #{tenant}
        ORDER BY c_time DESC
        """)
    List<WorkerNodeEntity> selectByTenant(@Param("tenant") String tenant);

    /**
     * 统计租户工作节点数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM worker_node
        WHERE tenant = #{tenant}
        """)
    long countByTenant(@Param("tenant") String tenant);
}