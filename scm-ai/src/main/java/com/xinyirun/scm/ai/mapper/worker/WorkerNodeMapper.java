package com.xinyirun.scm.ai.mapper.worker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.worker.WorkerNodeEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 工作节点表 Mapper接口
 *
 * 提供工作节点的数据访问操作，使用注解SQL实现
 * 支持分布式工作节点的管理和查询
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Repository
@Mapper
public interface WorkerNodeMapper extends BaseMapper<WorkerNodeEntity> {

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
    WorkerNodeEntity selectByHostAndPort(@Param("hostName") String hostName, @Param("port") String port);

    /**
     * 查询指定类型的所有工作节点
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
        ORDER BY launch_date DESC
        """)
    List<WorkerNodeEntity> selectByType(@Param("type") Integer type);

    /**
     * 查询活跃的工作节点（最近修改时间在指定分钟内）
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
        WHERE u_time >= DATE_SUB(NOW(), INTERVAL #{minutes} MINUTE)
        ORDER BY u_time DESC
        """)
    List<WorkerNodeEntity> selectActiveNodes(@Param("minutes") Integer minutes);

    /**
     * 更新工作节点心跳时间
     */
    @Update("""
        UPDATE worker_node
        SET
            u_time = NOW(),
            u_id = #{userId},
            dbversion = dbversion + 1
        WHERE id = #{id}
        """)
    int updateHeartbeat(@Param("id") Integer id, @Param("userId") Long userId);

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
        ORDER BY launch_date DESC
        """)
    List<WorkerNodeEntity> selectByTenant(@Param("tenant") String tenant);

    /**
     * 获取下一个可用的工作节点ID
     */
    @Select("""
        SELECT IFNULL(MAX(id), 0) + 1
        FROM worker_node
        """)
    Long getNextWorkerId();

    /**
     * 批量插入工作节点
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
}