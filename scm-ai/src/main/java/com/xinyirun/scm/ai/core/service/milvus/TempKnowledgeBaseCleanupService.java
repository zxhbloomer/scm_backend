package com.xinyirun.scm.ai.core.service.milvus;

import com.xinyirun.scm.ai.core.service.KnowledgeBaseService;
import com.xinyirun.scm.bean.system.vo.business.ai.TempKbCleanupParamVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 临时知识库自动清理服务
 *
 * 功能说明：
 * 1. 本服务由Quartz定时任务调用，在临时知识库创建2小时后自动执行
 * 2. 删除指定的临时知识库及其所有关联数据（items、segments、Milvus向量、Neo4j图谱）
 * 3. 每个临时知识库创建时会生成一个专属的SimpleTrigger任务，执行后任务自动失效
 *
 * 设计理念：
 * - 不使用LambdaQueryWrapper查询（符合SCM规范17、24）
 * - 直接根据参数中的kbUuid调用delete()方法
 * - 复用现有KnowledgeBaseService.delete()的级联删除逻辑
 *
 * @author zzxxhh
 * @date 2025-12-03
 */
@Slf4j
@Service
public class TempKnowledgeBaseCleanupService {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 清理临时知识库（Quartz任务入口方法）
     *
     * 调用链路：
     * Quartz Scheduler → AbstractQuartzJob.executeInternal() → 本方法
     *
     * 数据源切换：
     * AbstractQuartzJob已自动根据tenant_code切换到租户数据源，本方法内部再次确保
     *
     * @param paramVo 任务参数（包含kbUuid和tenantCode）
     */
    public void cleanupTempKnowledgeBase(TempKbCleanupParamVo paramVo) {
        log.info("定时任务开始执行临时知识库清理: kbUuid={}, tenantCode={}",
                paramVo.getKbUuid(), paramVo.getTenantCode());

        try {
            // 确保使用租户数据源
            DataSourceHelper.use(paramVo.getTenantCode());

            // 直接调用delete方法，复用现有的级联删除逻辑
            // 该方法会自动处理：
            // 1. 删除 ai_knowledge_base 主记录
            // 2. 级联删除 ai_knowledge_base_item 子记录
            // 3. 级联删除 ai_knowledge_base_graph_segment 子记录
            // 4. 调用 MilvusVectorIndexingService 删除向量数据
            // 5. 调用 Neo4jGraphIndexingService 删除图谱数据（如果有）
            knowledgeBaseService.delete(paramVo.getKbUuid());

            log.info("定时任务执行完成，临时知识库已清理: kbUuid={}", paramVo.getKbUuid());
        } catch (Exception e) {
            log.error("定时任务执行失败，临时知识库清理异常: kbUuid={}, error={}",
                    paramVo.getKbUuid(), e.getMessage(), e);
            // 抛出异常让Quartz标记任务失败
            throw new RuntimeException("临时知识库清理失败: " + e.getMessage(), e);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }
}
