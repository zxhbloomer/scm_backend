package com.xinyirun.scm.excel.merge;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

/**
 * 采购合同导出专用合并策略
 * 
 * 基于合同编号(contract_code)进行动态合并，当同一个合同包含多个商品明细时，
 * 自动合并合同层面的信息列，保持商品明细列独立显示。
 * 
 * 合并逻辑：
 * - 分组字段：合同编号（第2列）
 * - 合并列范围：
 *   * 合同基本信息：0-26列（序号、项目编号、合同编号、类型、状态、供应商等）
 *   * 系统审计信息：34-37列（创建人、创建时间、更新人、更新时间）
 * - 不合并列：27-33列（商品编码、名称、规格、产地、数量、单价、税率）
 * 
 * 数据结构示例：
 * | 序号 | 项目编号 | 合同编号 | ... | 商品编码 | 商品名称 | 数量 | ... | 创建人 |
 * |------|----------|----------|-----|----------|----------|------|-----|--------|
 * |  1   | PROJ001  | C001     | ... |  G001    |   钢材   | 100  | ... |  张三  |
 * |      |          |          | ... |  G002    |   水泥   | 200  | ... |        |
 * |      |          |          | ... |  G003    |   木材   | 300  | ... |        |
 * 
 * 使用场景：
 * - 采购合同导出，一个合同对应多个商品时的Excel展示
 * - 确保合同信息在垂直方向上整洁合并，商品信息逐行展示
 * - 配合数据去重逻辑，确保Excel合计计算的准确性
 * 
 * @author SCM系统
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class PoContractMergeStrategy extends AbstractBusinessMergeStrategy {
    
    /**
     * 合同编号列索引（分组依据）
     */
    private static final int CONTRACT_CODE_COLUMN = 2;
    
    /**
     * 需要合并的列索引数组
     * 包含所有合同层面的字段，排除商品明细字段
     */
    private static final int[] MERGE_COLUMNS = {
        // 基础信息列 (0-29)
        0,  // No（序号）
        1,  // 项目编号
        2,  // 合同编号（分组字段）
        3,  // 类型
        4,  // 订单笔数
        5,  // 状态
        6,  // 审批情况
        7,  // 供应商
        8,  // 采购方（主体企业）
        9,  // 执行进度
        10, // 签约日期
        11, // 到期日期
        12, // 交货日期
        13, // 运输方式
        14, // 结算方式
        15, // 结算单据类型
        16, // 付款方式
        17, // 交货地点
        18, // 自动生成订单
        19, // 备注
        20, // 合同总金额
        21, // 总采购数量（吨）
        22, // 税额
        23, // 已结算数量（吨）
        24, // 结算金额
        25, // 预付款金额
        26, // 累计实付
        27, // 未付
        28, // 预付款可退金额
        29, // 已开票金额
        
        // 商品相关列 (30-36) 不合并，因为每个商品的信息不同
        // 30, // 商品编码 - 不合并
        // 31, // 商品名称 - 不合并
        // 32, // 规格 - 不合并
        // 33, // 产地 - 不合并
        // 34, // 数量 - 不合并
        // 35, // 单价 - 不合并
        // 36, // 税率 - 不合并
        
        // 系统审计信息列 (37-40)
        37, // 创建人
        38, // 创建时间
        39, // 更新人
        40  // 更新时间
    };
    
    /**
     * 构造函数
     * 
     * @param debugEnabled 是否启用调试日志
     */
    public PoContractMergeStrategy(boolean debugEnabled) {
        super(debugEnabled);
        if (debugEnabled) {
            log.info("采购合同合并策略初始化: 分组列={}, 合并列数量={}", CONTRACT_CODE_COLUMN, MERGE_COLUMNS.length);
        }
    }
    
    /**
     * 默认构造函数，关闭调试日志
     */
    public PoContractMergeStrategy() {
        this(false);
    }
    
    @Override
    protected String extractGroupFieldValue(Row currentRow) {
        if (currentRow == null) {
            return null;
        }
        
        // 获取合同编号（第2列）作为分组字段
        return getCellValueAsString(currentRow.getCell(CONTRACT_CODE_COLUMN));
    }
    
    @Override
    protected boolean isGroupFieldChanged(String currentValue, String previousValue) {
        // 第一次处理或合同编号发生变化
        if (previousValue == null) {
            return currentValue != null; // null -> 有值 认为是变化
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
        return String.format("PoContractMergeStrategy[分组列=%d, 合并列数量=%d, 调试模式=%s]", 
                CONTRACT_CODE_COLUMN, MERGE_COLUMNS.length, debugEnabled);
    }
}