package com.xinyirun.scm.excel.merge;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

/**
 * 项目管理导出专用合并策略
 * 
 * 基于项目编号(code)进行动态合并，当同一个项目包含多个商品明细时，
 * 自动合并项目层面的信息列，保持商品明细列独立显示。
 * 
 * 合并逻辑：
 * - 分组字段：项目编号（第0列）
 * - 合并列范围：
 *   * 项目基本信息：0-6列（项目编号、名称、状态、审批情况、类型、供应商、客户）
 *   * 项目业务信息：14-25列（付款方式、账期、融资额度、周期、费率等）
 * - 不合并列：7-13列（商品编码、名称、规格、产地、数量、单价、税率）
 * 
 * 使用场景：
 * - PO项目管理导出，一个项目对应多个商品时的Excel展示
 * - 确保项目信息在垂直方向上整洁合并，商品信息逐行展示
 * 
 * @author SCM系统
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class ProjectMergeStrategy extends AbstractBusinessMergeStrategy {
    
    /**
     * 项目编号列索引（分组依据）
     */
    private static final int PROJECT_CODE_COLUMN = 1;
    
    /**
     * 需要合并的列索引数组
     * 包含所有项目层面的字段，排除商品明细字段
     */
    private static final int[] MERGE_COLUMNS = {
        // 序号和项目基本信息列 (0-7)
        0,  // No（序号）
        1,  // 项目编号
        2,  // 项目名称  
        3,  // 状态
        4,  // 审批情况
        5,  // 类型
        6,  // 上游供应商
        7,  // 下游客户（主体企业）
        
        // 商品相关列 (8-14) 不合并，因为每个商品的信息不同
        // 8,  // 商品编码 - 不合并
        // 9,  // 商品名称 - 不合并
        // 10, // 规格 - 不合并
        // 11, // 产地 - 不合并
        // 12, // 数量 - 不合并
        // 13, // 单价 - 不合并
        // 14, // 税率 - 不合并
        
        // 项目业务信息列 (15-26)
        15, // 付款方式
        16, // 是否有账期/天数
        17, // 融资额度
        18, // 项目周期
        19, // 费率
        20, // 交货地点
        21, // 运输方式
        22, // 备注
        23, // 创建人
        24, // 创建时间
        25, // 更新人
        26  // 更新时间
    };
    
    /**
     * 构造函数
     * 
     * @param debugEnabled 是否启用调试日志
     */
    public ProjectMergeStrategy(boolean debugEnabled) {
        super(debugEnabled);
        if (debugEnabled) {
            log.info("项目合并策略初始化: 分组列={}, 合并列数量={}", PROJECT_CODE_COLUMN, MERGE_COLUMNS.length);
        }
    }
    
    /**
     * 默认构造函数，关闭调试日志
     */
    public ProjectMergeStrategy() {
        this(false);
    }
    
    @Override
    protected String extractGroupFieldValue(Row currentRow) {
        if (currentRow == null) {
            return null;
        }
        
        // 获取项目编号（第1列）作为分组字段
        return getCellValueAsString(currentRow.getCell(PROJECT_CODE_COLUMN));
    }
    
    @Override
    protected boolean isGroupFieldChanged(String currentValue, String previousValue) {
        // 第一次处理或项目编号发生变化
        if (previousValue == null) {
            return currentValue != null;
        }
        
        // 使用Objects.equals处理null值比较
        return !Objects.equals(currentValue, previousValue);
    }
    
    @Override
    protected int[] getMergeColumnIndexes() {
        return MERGE_COLUMNS;
    }
    
    /**
     * 获取策略描述信息
     * 
     * @return 策略描述
     */
    public String getStrategyInfo() {
        return String.format("ProjectMergeStrategy[分组列=%d, 合并列数量=%d, 调试模式=%s]", 
                PROJECT_CODE_COLUMN, MERGE_COLUMNS.length, debugEnabled);
    }
}