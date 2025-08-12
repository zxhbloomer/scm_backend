package com.xinyirun.scm.excel.merge;

import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 业务合并策略抽象基类
 * 
 * 提供基于业务逻辑的动态单元格合并功能，支持根据数据内容变化
 * 动态决定合并区域，无需预计算整个数据集的合并规则。
 * 
 * 技术特点：
 * - 继承FastExcel的AbstractMergeStrategy，集成到EasyExcel工作流
 * - 模板方法模式，子类只需实现具体的业务合并逻辑
 * - 动态合并：逐行处理，根据数据变化触发合并
 * - 通用工具方法：单元格值获取、行数据访问、错误处理
 * 
 * 使用模式：
 * 1. 子类继承此基类
 * 2. 实现isGroupFieldChanged()判断分组字段是否变化
 * 3. 实现getMergeColumnIndexes()定义需要合并的列
 * 4. 注册到EasyExcel写入处理器
 * 
 * @author SCM系统
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public abstract class AbstractBusinessMergeStrategy extends AbstractMergeStrategy {
    
    /**
     * 当前分组的起始行号
     */
    protected int currentGroupStartRow = -1;
    
    /**
     * 上一个分组字段的值，用于检测分组变化
     */
    protected String previousGroupValue = null;
    
    
    /**
     * 是否启用调试日志
     */
    protected boolean debugEnabled = false;
    
    /**
     * 构造函数
     * 
     * @param debugEnabled 是否启用调试日志
     */
    public AbstractBusinessMergeStrategy(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    
    /**
     * 默认构造函数，关闭调试日志
     */
    public AbstractBusinessMergeStrategy() {
        this(false);
    }
    
    @Override
    protected final void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        try {
            if (debugEnabled) {
                log.info("=== 业务合并策略处理 ===");
                log.info("相对行号: {}, 绝对行号: {}, 列号: {}", 
                        relativeRowIndex, cell.getRowIndex(), cell.getColumnIndex());
            }
            
            // 获取当前行数据
            Row currentRow = cell.getRow();
            
            // 调试输出当前行数据
            if (debugEnabled) {
                logRowData(currentRow);
            }
            
            // 只在项目编号列进行分组检测，避免重复检测
            // 从ProjectMergeStrategy中获取PROJECT_CODE_COLUMN常量
            if (this instanceof ProjectMergeStrategy && cell.getColumnIndex() == 1) {
                // 获取当前分组字段值
                String currentGroupValue = extractGroupFieldValue(currentRow);
                
                if (debugEnabled) {
                    log.info("【项目编号列分组检测】当前分组字段值: {}, 上一个分组字段值: {}", currentGroupValue, previousGroupValue);
                }
                
                // 检测分组字段是否发生变化
                if (isGroupFieldChanged(currentGroupValue, previousGroupValue)) {
                    // 分组发生变化，合并上一个分组
                    if (previousGroupValue != null && currentGroupStartRow != -1) {
                        performMerge(sheet, currentGroupStartRow, cell.getRowIndex() - 1);
                    }
                    
                    // 记录新分组的开始
                    currentGroupStartRow = cell.getRowIndex();
                    previousGroupValue = currentGroupValue;
                    
                    if (debugEnabled) {
                        log.info("【分组变化】detected: {} -> {}, 新分组开始行: {}", 
                                getPreviousGroupValue(), currentGroupValue, currentGroupStartRow);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("业务合并策略处理异常", e);
        }
    }
    
    /**
     * 执行合并操作并设置居中对齐样式
     * 
     * @param sheet     工作表
     * @param startRow  开始行
     * @param endRow    结束行
     */
    protected void performMerge(Sheet sheet, int startRow, int endRow) {
        if (startRow >= endRow) {
            return; // 只有一行，无需合并
        }
        
        int[] columnIndexes = getMergeColumnIndexes();
        
        // 获取工作簿并创建居中对齐样式
        Workbook workbook = sheet.getWorkbook();
        CellStyle centerStyle = createCenterAlignStyle(workbook);
        
        for (int columnIndex : columnIndexes) {
            try {
                // 1. 合并单元格
                CellRangeAddress cellRangeAddress = new CellRangeAddress(
                        startRow, endRow, columnIndex, columnIndex);
                sheet.addMergedRegionUnsafe(cellRangeAddress);
                
                // 2. 设置合并区域的居中对齐样式
                applyCenterStyleToMergedRegion(sheet, cellRangeAddress, centerStyle);
                
                if (debugEnabled) {
                    log.info("合并单元格并设置居中对齐: 行{}-{}, 列{}", startRow, endRow, columnIndex);
                }
            } catch (Exception e) {
                log.error("合并单元格失败: startRow={}, endRow={}, column={}", 
                        startRow, endRow, columnIndex, e);
            }
        }
    }
    
    /**
     * 创建居中对齐的单元格样式
     * 
     * @param workbook 工作簿
     * @return 居中对齐样式
     */
    protected CellStyle createCenterAlignStyle(Workbook workbook) {
        CellStyle centerStyle = workbook.createCellStyle();
        
        // 设置水平居中对齐
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置垂直居中对齐
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置边框（保持与原有样式一致）
        centerStyle.setBorderTop(BorderStyle.THIN);
        centerStyle.setBorderBottom(BorderStyle.THIN);
        centerStyle.setBorderLeft(BorderStyle.THIN);
        centerStyle.setBorderRight(BorderStyle.THIN);
        
        // 设置自动换行
        centerStyle.setWrapText(true);
        
        return centerStyle;
    }
    
    /**
     * 将居中对齐样式应用到合并区域的所有单元格
     * 
     * @param sheet           工作表
     * @param mergedRegion    合并区域
     * @param centerStyle     居中对齐样式
     */
    protected void applyCenterStyleToMergedRegion(Sheet sheet, CellRangeAddress mergedRegion, CellStyle centerStyle) {
        for (int rowIndex = mergedRegion.getFirstRow(); rowIndex <= mergedRegion.getLastRow(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            
            for (int colIndex = mergedRegion.getFirstColumn(); colIndex <= mergedRegion.getLastColumn(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    cell = row.createCell(colIndex);
                }
                cell.setCellStyle(centerStyle);
            }
        }
    }
    
    /**
     * 完成写入后的清理工作
     * 处理最后一个分组的合并
     * 
     * @param sheet 工作表
     */
    public final void finalizeMerge(Sheet sheet) {
        if (previousGroupValue != null && currentGroupStartRow != -1) {
            // 合并最后一个分组
            int lastRow = sheet.getLastRowNum();
            performMerge(sheet, currentGroupStartRow, lastRow);
            
            if (debugEnabled) {
                log.info("完成最后分组的合并: {}, 行{}-{}", previousGroupValue, currentGroupStartRow, lastRow);
            }
        }
        
        // 清理状态
        cleanup();
    }
    
    /**
     * 清理内部状态
     */
    protected void cleanup() {
        currentGroupStartRow = -1;
        previousGroupValue = null;
    }
    
    /**
     * 获取单元格值的字符串形式
     * 
     * @param cell 单元格
     * @return 字符串值
     */
    protected String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return "";
            }
        } catch (Exception e) {
            log.warn("获取单元格值异常: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * 记录当前行的所有数据（用于调试）
     * 
     * @param row 当前行
     */
    protected void logRowData(Row row) {
        if (row == null) {
            log.info("当前行为空");
            return;
        }
        
        StringBuilder rowData = new StringBuilder("当前行数据: ");
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String value = getCellValueAsString(cell);
            rowData.append(String.format("列%d=%s, ", i, value));
        }
        log.info(rowData.toString());
    }
    
    /**
     * 获取上一个分组值（用于日志输出）
     * 
     * @return 上一个分组值
     */
    protected String getPreviousGroupValue() {
        return previousGroupValue;
    }
    
    // ==================== 抽象方法，子类必须实现 ====================
    
    /**
     * 从当前行提取分组字段的值
     * 
     * @param currentRow 当前行
     * @return 分组字段值
     */
    protected abstract String extractGroupFieldValue(Row currentRow);
    
    /**
     * 判断分组字段是否发生变化
     * 
     * @param currentValue  当前分组字段值
     * @param previousValue 上一个分组字段值
     * @return 是否发生变化
     */
    protected abstract boolean isGroupFieldChanged(String currentValue, String previousValue);
    
    /**
     * 获取需要合并的列索引数组
     * 
     * @return 列索引数组
     */
    protected abstract int[] getMergeColumnIndexes();
}