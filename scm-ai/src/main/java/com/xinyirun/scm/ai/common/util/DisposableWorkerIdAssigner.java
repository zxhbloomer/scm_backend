package com.xinyirun.scm.ai.common.util;

import com.xinyirun.scm.ai.bean.entity.worker.WorkerNodeEntity;
import com.xinyirun.scm.ai.core.mapper.worker.WorkerNodeMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Represents an implementation of {@link WorkerIdAssigner},
 * the worker id will be discarded after assigned to the UidGenerator
 */
@Service
@Slf4j
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
    @Resource
    private WorkerNodeMapper workerNodeMapper;

    /**
     * Assign worker id base on database.<p>
     * If there is host name & port in the environment, we considered that the node runs in Docker container<br>
     * Otherwise, the node runs on an actual machine.
     *
     * @return assigned worker id
     */
    public long assignWorkerId() {
        // build worker node entity
        try {
            WorkerNodeEntity workerNode = buildWorkerNode();

            // add worker node for new (ignore the same IP + PORT)
            workerNodeMapper.insert(workerNode);
            log.info("Add worker node:" + workerNode);

            return workerNode.getId();
        } catch (Exception e) {
            log.error("Assign worker id exception. ", e);
            return 1;
        }
    }

    /**
     * Build worker node entity by IP and PORT
     */
    private WorkerNodeEntity buildWorkerNode() {
        WorkerNodeEntity workerNode = new WorkerNodeEntity();
        if (DockerUtils.isDocker()) {
            workerNode.setType(WorkerNodeType.CONTAINER.value());
            workerNode.setHost_name(DockerUtils.getDockerHost());
            workerNode.setPort(DockerUtils.getDockerPort());

        } else {
            workerNode.setType(WorkerNodeType.ACTUAL.value());
            workerNode.setHost_name(NetUtils.getLocalAddress());
            workerNode.setPort(System.currentTimeMillis() + "-" + RandomUtils.nextInt());
        }
        // 使用新的字段名
        Long currentTime = System.currentTimeMillis();
        workerNode.setLaunch_date(currentTime);
        // c_time和u_time会由MyBatis Plus自动填充
        return workerNode;
    }

}