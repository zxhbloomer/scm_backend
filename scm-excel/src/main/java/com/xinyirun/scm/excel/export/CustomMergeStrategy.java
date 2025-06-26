package com.xinyirun.scm.excel.export;

import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/6/14 14:53
 */


public class CustomMergeStrategy extends AbstractMergeStrategy {

    /**
     * 分组，每几行合并一次
     */
    private List<Integer> exportFieldGroupCountList;

    private List<BWoExportUtilVo> exportFiledGroupCodeList;

    /**
     * 目标合并列index
     */
    private Integer targetColumnIndex;

    // 需要开始合并单元格的首行index
    private Integer rowIndex;


    // exportDataList为待合并目标列的值
    public CustomMergeStrategy(List<String> exportDataList, Integer targetColumnIndex) {
        this.exportFieldGroupCountList = getGroupCountList(exportDataList);
        this.targetColumnIndex = targetColumnIndex;
    }

    // exportDataList为待合并目标列的值
    public CustomMergeStrategy(List<BWoExportUtilVo> exportDataList, Integer targetColumnIndex, String a) {
        this.exportFieldGroupCountList = getCodeGroupCountList(exportDataList);
        this.targetColumnIndex = targetColumnIndex;
    }


    private int getLastColumn(Sheet sheet, int row, int startColumn) {
        int lastColumn = startColumn;
        Row currentRow = sheet.getRow(row);
        if (currentRow == null) {
            return lastColumn;
        }
        int lastCellNum = currentRow.getLastCellNum();
        for (int i = startColumn + 1; i < lastCellNum; i++) {
            Cell currentCell = currentRow.getCell(i);
            if (currentCell != null && currentCell.getStringCellValue().equals(getPreviousCellValue(sheet, row, i))) {
                lastColumn = i;
            } else {
                break;
            }
        }
        return lastColumn;
    }

    private String getPreviousCellValue(Sheet sheet, int row, int column) {
        Row previousRow = sheet.getRow(row - 1);
        if (previousRow == null) {
            return "";
        }
        Cell previousCell = previousRow.getCell(column);
        if (previousCell == null) {
            return "";
        }
        return previousCell.getStringCellValue();
    }

    /**
     * merge
     *
     * @param sheet
     * @param cell
     * @param head
     * @param relativeRowIndex
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (null == rowIndex) {
            rowIndex = cell.getRowIndex();
        }
        // 仅从首行以及目标列的单元格开始合并，忽略其他
        if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == targetColumnIndex) {
            mergeGroupColumn(sheet);
        }
    }

    private void mergeGroupColumn(Sheet sheet) {
        int rowCount = rowIndex;
        for (Integer count : exportFieldGroupCountList) {
            if(count == 1) {
                rowCount += count;
                continue ;
            }
            // 合并单元格
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCount, rowCount + count - 1, targetColumnIndex, targetColumnIndex);
            sheet.addMergedRegionUnsafe(cellRangeAddress);
            rowCount += count;
        }
    }

    // 该方法将目标列根据值是否相同连续可合并，存储可合并的行数
    private List<Integer> getGroupCountList(List<String> exportDataList){
        if (CollectionUtils.isEmpty(exportDataList)) {
            return new ArrayList<>();
        }

        List<Integer> groupCountList = new ArrayList<>();
        int count = 1;

        for (int i = 1; i < exportDataList.size(); i++) {
            if (exportDataList.get(i).equals(exportDataList.get(i - 1))) {
                count++;
            } else {
                groupCountList.add(count);
                count = 1;
            }
        }
        // 处理完最后一条后
        groupCountList.add(count);
        return groupCountList;
    }

    private List<Integer> getCodeGroupCountList(List<BWoExportUtilVo> exportDataList) {
        if (CollectionUtils.isEmpty(exportDataList)) {
            return new ArrayList<>();
        }
        List<Integer> groupCountList = new ArrayList<>();
        int count = 1;

        for (int i = 1; i < exportDataList.size(); i++) {
            if (exportDataList.get(i).getCode().equals(exportDataList.get(i - 1).getCode())
                    && exportDataList.get(i).getKey().equals(exportDataList.get(i - 1).getKey())) {
                count++;
            } else {
                groupCountList.add(count);
                count = 1;
            }
        }
        // 处理完最后一条后
        groupCountList.add(count);
        return groupCountList;
    }

}
