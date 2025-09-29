package com.xinyirun.scm.ai.mapper.contract;

import com.xinyirun.scm.ai.mapper.statistics.AiTokenViewsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AiTokenViewsMapper契约测试
 *
 * 验证SQL格式化前后AiTokenViewsMapper接口的行为一致性
 * 基于契约文件: specs/002-d-2025-project/contracts/AiTokenViewsMapper-contract.json
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@DisplayName("AiTokenViewsMapper接口契约测试")
class AiTokenViewsMapperContractTest extends MapperContractTestFramework {

    @Autowired
    private AiTokenViewsMapper aiTokenViewsMapper;

    @Test
    @DisplayName("selectUserTokenSummary - 查询用户Token使用汇总")
    void testSelectUserTokenSummary() {
        System.out.println("\n=== 测试selectUserTokenSummary方法 ===");

        // 验证方法签名
        verifyMethodSignature(AiTokenViewsMapper.class, "selectUserTokenSummary",
                new Class[]{String.class, Integer.class}, List.class);

        try {
            // 测试用例1: 正常查询特定用户
            List<Map<String, Object>> result1 = aiTokenViewsMapper.selectUserTokenSummary("user123", 10);
            verifyParameterBinding(result1, "正常查询特定用户");
            verifyUtf8Encoding(result1, "用户Token汇总查询");

            // 测试用例2: 查询所有用户
            List<Map<String, Object>> result2 = aiTokenViewsMapper.selectUserTokenSummary(null, 50);
            verifyParameterBinding(result2, "查询所有用户");

            // 测试用例3: 无限制查询
            List<Map<String, Object>> result3 = aiTokenViewsMapper.selectUserTokenSummary("user123", null);
            verifyParameterBinding(result3, "无限制查询");

            // 验证动态SQL标签功能
            verifyDynamicSqlTags("selectUserTokenSummary", new Object[]{"user123", 10}, result1);

            recordContractTestResult("AiTokenViewsMapper", "selectUserTokenSummary", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "selectUserTokenSummary", false);
            throw new AssertionError("selectUserTokenSummary契约测试失败", e);
        }
    }

    @Test
    @DisplayName("selectModelUsageStats - 查询模型使用统计")
    void testSelectModelUsageStats() {
        System.out.println("\n=== 测试selectModelUsageStats方法 ===");

        // 验证方法签名
        verifyMethodSignature(AiTokenViewsMapper.class, "selectModelUsageStats",
                new Class[]{String.class, Integer.class}, List.class);

        try {
            // 测试用例1: 查询特定提供商
            List<Map<String, Object>> result1 = aiTokenViewsMapper.selectModelUsageStats("OpenAI", 20);
            verifyParameterBinding(result1, "查询特定提供商");
            verifyUtf8Encoding(result1, "模型使用统计");

            // 测试用例2: 查询所有提供商
            List<Map<String, Object>> result2 = aiTokenViewsMapper.selectModelUsageStats(null, 50);
            verifyParameterBinding(result2, "查询所有提供商");

            // 验证动态SQL标签功能
            verifyDynamicSqlTags("selectModelUsageStats", new Object[]{"OpenAI", 20}, result1);

            recordContractTestResult("AiTokenViewsMapper", "selectModelUsageStats", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "selectModelUsageStats", false);
            throw new AssertionError("selectModelUsageStats契约测试失败", e);
        }
    }

    @Test
    @DisplayName("selectTokenUsageTrend - 查询Token使用趋势")
    void testSelectTokenUsageTrend() {
        System.out.println("\n=== 测试selectTokenUsageTrend方法 ===");

        // 验证方法签名
        verifyMethodSignature(AiTokenViewsMapper.class, "selectTokenUsageTrend",
                new Class[]{LocalDate.class, LocalDate.class}, List.class);

        try {
            // 测试用例1: 正常日期范围查询
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 9, 29);
            List<Map<String, Object>> result1 = aiTokenViewsMapper.selectTokenUsageTrend(startDate, endDate);
            verifyParameterBinding(result1, "正常日期范围查询");

            // 测试用例2: 单日查询
            LocalDate singleDate = LocalDate.of(2025, 9, 29);
            List<Map<String, Object>> result2 = aiTokenViewsMapper.selectTokenUsageTrend(singleDate, singleDate);
            verifyParameterBinding(result2, "单日查询");

            recordContractTestResult("AiTokenViewsMapper", "selectTokenUsageTrend", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "selectTokenUsageTrend", false);
            throw new AssertionError("selectTokenUsageTrend契约测试失败", e);
        }
    }

    @Test
    @DisplayName("selectUserRanking - 查询用户排行榜")
    void testSelectUserRanking() {
        System.out.println("\n=== 测试selectUserRanking方法 ===");

        // 验证方法签名
        verifyMethodSignature(AiTokenViewsMapper.class, "selectUserRanking",
                new Class[]{LocalDate.class, LocalDate.class, Integer.class}, List.class);

        try {
            // 测试用例1: 有日期范围和排名限制
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 9, 29);
            List<Map<String, Object>> result1 = aiTokenViewsMapper.selectUserRanking(startDate, endDate, 10);
            verifyParameterBinding(result1, "有日期范围和排名限制");

            // 测试用例2: 无日期限制
            List<Map<String, Object>> result2 = aiTokenViewsMapper.selectUserRanking(null, null, 20);
            verifyParameterBinding(result2, "无日期限制");

            // 验证特殊字符转义(&gt;, &lt;)
            verifySpecialCharacterEscaping("包含 &gt;= 和 &lt;= 条件");

            // 验证动态SQL标签功能
            verifyDynamicSqlTags("selectUserRanking", new Object[]{startDate, endDate, 10}, result1);

            recordContractTestResult("AiTokenViewsMapper", "selectUserRanking", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "selectUserRanking", false);
            throw new AssertionError("selectUserRanking契约测试失败", e);
        }
    }

    @Test
    @DisplayName("selectDailyTokenSummary - 查询日期统计汇总")
    void testSelectDailyTokenSummary() {
        System.out.println("\n=== 测试selectDailyTokenSummary方法 ===");

        // 验证方法签名
        verifyMethodSignature(AiTokenViewsMapper.class, "selectDailyTokenSummary",
                new Class[]{LocalDate.class, LocalDate.class, Integer.class}, List.class);

        try {
            // 测试用例1: 完整参数查询
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 9, 29);
            List<Map<String, Object>> result1 = aiTokenViewsMapper.selectDailyTokenSummary(startDate, endDate, 30);
            verifyParameterBinding(result1, "完整参数查询");

            // 测试用例2: 仅日期范围查询
            List<Map<String, Object>> result2 = aiTokenViewsMapper.selectDailyTokenSummary(
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 29), null);
            verifyParameterBinding(result2, "仅日期范围查询");

            // 验证动态SQL标签功能
            verifyDynamicSqlTags("selectDailyTokenSummary", new Object[]{startDate, endDate, 30}, result1);

            // 验证特殊字符转义
            verifySpecialCharacterEscaping("包含 &gt;= 和 &lt;= 条件");

            recordContractTestResult("AiTokenViewsMapper", "selectDailyTokenSummary", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "selectDailyTokenSummary", false);
            throw new AssertionError("selectDailyTokenSummary契约测试失败", e);
        }
    }

    @Test
    @DisplayName("性能基准测试 - 所有查询方法")
    void testPerformanceBenchmark() {
        System.out.println("\n=== 性能基准测试 ===");

        try {
            // 测试selectUserTokenSummary性能
            long startTime = System.currentTimeMillis();
            aiTokenViewsMapper.selectUserTokenSummary("user123", 10);
            long executionTime = System.currentTimeMillis() - startTime;
            verifyPerformanceBenchmark("selectUserTokenSummary", executionTime);

            // 测试selectTokenUsageTrend性能
            startTime = System.currentTimeMillis();
            aiTokenViewsMapper.selectTokenUsageTrend(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 9, 29));
            executionTime = System.currentTimeMillis() - startTime;
            verifyPerformanceBenchmark("selectTokenUsageTrend", executionTime);

            recordContractTestResult("AiTokenViewsMapper", "PerformanceBenchmark", true);

        } catch (Exception e) {
            recordContractTestResult("AiTokenViewsMapper", "PerformanceBenchmark", false);
            throw new AssertionError("性能基准测试失败", e);
        }
    }
}