package com.xinyirun.scm.excel.merge;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.NumberFormat;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目合并策略测试类
 * 
 * 验证新的简化合并方案的技术可行性和实际效果：
 * 1. 测试AbstractBusinessMergeStrategy抽象基类的功能
 * 2. 测试ProjectMergeStrategy项目合并策略
 * 3. 验证EasyExcelUtil的合并策略注册机制
 * 4. 生成实际Excel文件进行人工验证
 * 
 * @author SCM系统
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class ProjectMergeStrategyTest {
    
    /**
     * 简化的项目测试数据实体
     * 模拟BProjectExportVo的核心字段
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectTestData {
        @ExcelProperty(value = "项目编号", index = 0)
        private String code;
        
        @ExcelProperty(value = "项目名称", index = 1)
        private String name;
        
        @ExcelProperty(value = "状态", index = 2)
        private String status;
        
        @ExcelProperty(value = "审批情况", index = 3)
        private String approval;
        
        @ExcelProperty(value = "类型", index = 4)
        private String type;
        
        @ExcelProperty(value = "上游供应商", index = 5)
        private String supplier;
        
        @ExcelProperty(value = "下游客户", index = 6)
        private String customer;
        
        // 商品相关字段 - 不合并
        @ExcelProperty(value = "商品编码", index = 7)
        private String skuCode;
        
        @ExcelProperty(value = "商品名称", index = 8)
        private String goodsName;
        
        @ExcelProperty(value = "规格", index = 9)
        private String skuName;
        
        @ExcelProperty(value = "数量", index = 10)
        @NumberFormat("#,##0.0000")
        private BigDecimal qty;
        
        @ExcelProperty(value = "单价", index = 11)
        @NumberFormat("¥#,##0.00")
        private BigDecimal price;
        
        // 项目业务信息 - 合并
        @ExcelProperty(value = "付款方式", index = 12)
        private String paymentMethod;
        
        @ExcelProperty(value = "融资额度", index = 13)
        @NumberFormat("¥#,##0.00")
        private BigDecimal amount;
        
        @ExcelProperty(value = "备注", index = 14)
        private String remark;
    }
    
    /**
     * 测试项目合并策略的基本功能
     */
    public static void testBasicProjectMerge() {
        log.info("=== 开始测试项目合并策略 ===");
        
        String fileName = System.getProperty("user.dir") + File.separator + 
                         "project_merge_test_" + System.currentTimeMillis() + ".xlsx";
        
        try {
            // 创建测试数据
            List<ProjectTestData> testData = createProjectTestData();
            log.info("创建测试数据: {} 条记录", testData.size());
            
            // 创建项目合并策略（启用调试模式）
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(true);
            log.info("创建项目合并策略: {}", mergeStrategy.getStrategyInfo());
            
            // 创建EasyExcelUtil实例
            EasyExcelUtil<ProjectTestData> excelUtil = new EasyExcelUtil<>(ProjectTestData.class);
            
            // 使用带合并策略的导出方法（文件输出）
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                excelUtil.withMergeStrategy(mergeStrategy);
                
                // 构建EasyExcel写入器并导出
                var writerBuilder = cn.idev.excel.EasyExcel.write(fileOutputStream, ProjectTestData.class)
                        .registerWriteHandler(mergeStrategy)
                        .registerWriteHandler(EasyExcelUtil.getStyleStrategy())
                        .registerWriteHandler(new EasyExcelUtil.FinalMergeHandler(mergeStrategy));
                
                writerBuilder.sheet("项目合并测试").doWrite(testData);
                
                log.info("Excel文件已生成: {}", fileName);
            }
            
            // 验证文件生成
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                log.info("文件验证成功: 大小 {} bytes", file.length());
                log.info("请打开文件查看合并效果: {}", file.getAbsolutePath());
            } else {
                log.error("文件生成失败或文件为空");
            }
            
        } catch (Exception e) {
            log.error("项目合并策略测试失败", e);
        }
        
        log.info("=== 项目合并策略测试完成 ===");
    }
    
    /**
     * 性能测试 - 大数据量场景
     */
    public static void testPerformanceWithLargeData() {
        log.info("=== 开始性能测试 ===");
        
        String fileName = System.getProperty("user.dir") + File.separator + 
                         "project_performance_test_" + System.currentTimeMillis() + ".xlsx";
        
        try {
            // 创建大量测试数据
            List<ProjectTestData> largeData = createLargeProjectTestData(500); // 500条记录
            log.info("创建大数据测试集: {} 条记录", largeData.size());
            
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(false); // 关闭调试日志
            
            long startTime = System.currentTimeMillis();
            
            // 导出测试
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                var writerBuilder = cn.idev.excel.EasyExcel.write(fileOutputStream, ProjectTestData.class)
                        .registerWriteHandler(mergeStrategy)
                        .registerWriteHandler(EasyExcelUtil.getStyleStrategy())
                        .registerWriteHandler(new EasyExcelUtil.FinalMergeHandler(mergeStrategy));
                
                writerBuilder.sheet("性能测试").doWrite(largeData);
                
                long endTime = System.currentTimeMillis();
                log.info("性能测试完成: 处理 {} 条记录，耗时 {} ms", largeData.size(), endTime - startTime);
                log.info("平均处理速度: {:.2f} 记录/秒", largeData.size() * 1000.0 / (endTime - startTime));
                
                // 验证文件生成
                File file = new File(fileName);
                if (file.exists()) {
                    log.info("性能测试文件生成成功: {} bytes", file.length());
                }
            }
            
        } catch (Exception e) {
            log.error("性能测试失败", e);
        }
        
        log.info("=== 性能测试完成 ===");
    }
    
    /**
     * 创建项目测试数据
     */
    private static List<ProjectTestData> createProjectTestData() {
        List<ProjectTestData> data = new ArrayList<>();
        
        // 项目 PRJ001 - 3个商品
        data.add(new ProjectTestData("PRJ001", "电商系统开发", "执行中", "已审批", "开发项目", "阿里巴巴", "京东商城", 
                "SKU001", "服务器", "高配置", new BigDecimal("10"), new BigDecimal("5000"), 
                "银行转账", new BigDecimal("150000"), "需求完整"));
        data.add(new ProjectTestData("PRJ001", "电商系统开发", "执行中", "已审批", "开发项目", "阿里巴巴", "京东商城",
                "SKU002", "网络设备", "交换机", new BigDecimal("5"), new BigDecimal("2000"),
                "银行转账", new BigDecimal("150000"), "需求完整"));
        data.add(new ProjectTestData("PRJ001", "电商系统开发", "执行中", "已审批", "开发项目", "阿里巴巴", "京东商城",
                "SKU003", "软件许可", "操作系统", new BigDecimal("20"), new BigDecimal("500"),
                "银行转账", new BigDecimal("150000"), "需求完整"));
        
        // 项目 PRJ002 - 2个商品
        data.add(new ProjectTestData("PRJ002", "库存管理系统", "计划中", "待审批", "管理系统", "华为技术", "小米集团",
                "SKU004", "存储设备", "硬盘", new BigDecimal("100"), new BigDecimal("800"),
                "支票支付", new BigDecimal("200000"), "技术可行"));
        data.add(new ProjectTestData("PRJ002", "库存管理系统", "计划中", "待审批", "管理系统", "华为技术", "小米集团",
                "SKU005", "监控设备", "摄像头", new BigDecimal("50"), new BigDecimal("300"),
                "支票支付", new BigDecimal("200000"), "技术可行"));
        
        // 项目 PRJ003 - 4个商品
        data.add(new ProjectTestData("PRJ003", "供应链管理", "启动", "审批中", "供应链", "腾讯科技", "美团",
                "SKU006", "传感器", "温度传感器", new BigDecimal("200"), new BigDecimal("50"),
                "现金支付", new BigDecimal("300000"), "市场需求大"));
        data.add(new ProjectTestData("PRJ003", "供应链管理", "启动", "审批中", "供应链", "腾讯科技", "美团",
                "SKU007", "控制器", "PLC控制器", new BigDecimal("30"), new BigDecimal("1500"),
                "现金支付", new BigDecimal("300000"), "市场需求大"));
        data.add(new ProjectTestData("PRJ003", "供应链管理", "启动", "审批中", "供应链", "腾讯科技", "美团",
                "SKU008", "执行器", "电机", new BigDecimal("60"), new BigDecimal("800"),
                "现金支付", new BigDecimal("300000"), "市场需求大"));
        data.add(new ProjectTestData("PRJ003", "供应链管理", "启动", "审批中", "供应链", "腾讯科技", "美团",
                "SKU009", "线缆", "数据线", new BigDecimal("500"), new BigDecimal("20"),
                "现金支付", new BigDecimal("300000"), "市场需求大"));
        
        // 项目 PRJ004 - 1个商品
        data.add(new ProjectTestData("PRJ004", "报表系统", "已完成", "已审批", "报表系统", "百度", "滴滴出行",
                "SKU010", "数据库软件", "MySQL", new BigDecimal("1"), new BigDecimal("10000"),
                "信用支付", new BigDecimal("50000"), "已验收"));
        
        return data;
    }
    
    /**
     * 创建大量项目测试数据（用于性能测试）
     */
    private static List<ProjectTestData> createLargeProjectTestData(int totalRecords) {
        List<ProjectTestData> data = new ArrayList<>();
        int projectsCount = 20; // 20个项目
        int recordsPerProject = totalRecords / projectsCount;
        
        for (int p = 1; p <= projectsCount; p++) {
            String projectCode = String.format("PRJ%03d", p);
            String projectName = String.format("项目%d", p);
            String status = p % 3 == 0 ? "已完成" : (p % 2 == 0 ? "执行中" : "计划中");
            String approval = p % 4 == 0 ? "已审批" : "待审批";
            
            for (int r = 1; r <= recordsPerProject; r++) {
                data.add(new ProjectTestData(
                        projectCode, projectName, status, approval, "测试项目",
                        String.format("供应商%d", p), String.format("客户%d", p),
                        String.format("SKU%03d", r), String.format("商品%d", r), String.format("规格%d", r),
                        new BigDecimal(Math.random() * 1000), new BigDecimal(Math.random() * 10000),
                        "银行转账", new BigDecimal(Math.random() * 500000), "测试备注"
                ));
            }
        }
        
        return data;
    }
    
    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        log.info("========== 项目合并策略测试开始 ==========");
        
        // 基本功能测试
        testBasicProjectMerge();
        
        // 等待一秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 性能测试
        testPerformanceWithLargeData();
        
        log.info("========== 项目合并策略测试结束 ==========");
    }
}