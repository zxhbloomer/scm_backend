package com.xinyirun.scm.excel.merge;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

/**
 * 销售合同导出专用合并策略
 * 
 * 基于合同编号(contract_code)进行动态合并，当同一个销售合同包含多个商品明细时，
 * 自动合并合同层面的信息列，保持商品明细列独立显示。
 * 
 * 合并逻辑：
 * - 分组字段：合同编号（第2列）
 * - 合并列范围：
 *   * 基础合同信息：0-26列（序号、项目编号、合同编号、类型、状态、客户等）
 *   * 审计信息：34-37列（创建人、创建时间、更新人、更新时间）
 * - 不合并列：27-33列（商品编码、名称、规格、产地、数量、单价、税率）
 * 
 * 使用场景：
 * - SO销售合同导出，一个合同对应多个商品时的Excel展示
 * - 确保合同信息在垂直方向上整洁合并，商品信息逐行展示
 * 
 * @author SCM系统
 * @version 2.0 - 修正版本，与BSoContractExportVo字段索引完全匹配
 * @since 2025-01-22
 */
@Slf4j
public class SoContractMergeStrategy extends AbstractBusinessMergeStrategy {
    
    /**
     * 合同编号列索引（分组依据）
     */
    private static final int CONTRACT_CODE_COLUMN = 2;
    
    /**
     * 需要合并的列索引数组
     * 包含所有合同层面的字段，排除商品明细字段
     */
    private static final int[] MERGE_COLUMNS = {
        // 基础合同信息 (0-8)
        0,  // No（序号）
        1,  // 项目编号
        2,  // 合同编号
        3,  // 类型
        4,  // 订单笔数
        5,  // 状态
        6,  // 审批情况
        7,  // 客户
        8,  // 销售方（主体企业）
        
        // 业务执行信息 (9-16)
        9,  // 执行进度
        10, // 签约日期
        11, // 到期日期
        12, // 交货日期
        13, // 运输方式
        14, // 结算方式
        15, // 结算单据类型
        16, // 付款方式
        
        // 财务汇总信息 (17-26)
        17, // 合同总金额
        18, // 总销售数量（吨）
        19, // 税额
        20, // 已结算数量（吨）
        21, // 结算金额
        22, // 预收款金额
        23, // 累计实收
        24, // 未收
        25, // 预收款可退金额
        26, // 可开票金额
        
        // 商品信息 (27-33) 不合并，因为每个商品的信息不同
        // 27, // 商品编码 - 不合并
        // 28, // 商品名称 - 不合并
        // 29, // 规格 - 不合并
        // 30, // 商品产地 - 不合并
        // 31, // 商品数量 - 不合并
        // 32, // 商品单价 - 不合并
        // 33, // 商品税率 - 不合并
        
        // 审计信息 (34-37)
        34, // 创建人
        35, // 创建时间
        36, // 更新人
        37  // 更新时间
    };
    
    /**
     * 构造函数
     * 
     * @param debugEnabled 是否启用调试日志
     */
    public SoContractMergeStrategy(boolean debugEnabled) {
        super(debugEnabled);
        if (debugEnabled) {
            log.info("销售合同合并策略初始化: 分组列={}, 合并列数量={}", CONTRACT_CODE_COLUMN, MERGE_COLUMNS.length);
        }
    }
    
    /**
     * 默认构造函数，关闭调试日志
     */
    public SoContractMergeStrategy() {
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
        return String.format("SoContractMergeStrategy[分组列=%d, 合并列数量=%d, 调试模式=%s]", 
                CONTRACT_CODE_COLUMN, MERGE_COLUMNS.length, debugEnabled);
    }
    
    /**
     * 验证合并策略配置
     * @return 验证结果
     */
    public boolean validateStrategy() {
        boolean valid = true;
        
        // 验证列索引合理性（导出VO有38个字段，索引0-37）
        for (int mergeCol : MERGE_COLUMNS) {
            if (mergeCol < 0 || mergeCol > 37) {
                log.error("合并列索引超出范围: {}, 有效范围: 0-37", mergeCol);
                valid = false;
            }
        }
        
        log.info("SoContractMergeStrategy 验证完成，结果: {}", valid ? "通过" : "失败");
        return valid;
    }
}