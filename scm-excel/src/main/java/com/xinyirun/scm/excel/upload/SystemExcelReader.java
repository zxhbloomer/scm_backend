package com.xinyirun.scm.excel.upload;

import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataCol;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataRow;
import com.xinyirun.scm.excel.bean.importconfig.template.title.DummyTitleCol;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleCol;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleRow;
import com.xinyirun.scm.excel.conf.convertor.ConvertorUtil;
import com.xinyirun.scm.excel.conf.validator.ColValidateResult;
import com.xinyirun.scm.excel.conf.validator.RowValidateResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel导入类
 * 
 * @author zxh
 */
@Slf4j
public class SystemExcelReader extends SystemExcelBase {

    private InputStream is;
    private List<RowValidateResult> rowValidateResults = new ArrayList<RowValidateResult>();

    @Getter
    @Setter
    private String fileName;

    /**
     * true:xlsx,false:xls
     */
    private boolean xlsOrXlsx;

    /**
     * 临时文件夹
     */
    private Path tempPath;
    private File errorFile;
    /**
     * Office 2003 ，xls:HSSFWorkbook
     * Office 2007 ，xls:XSSFWorkbook
     */
    Workbook wb ;

    /**
     * 读取文件
     * 
     * @param excelFile
     * @throws FileNotFoundException
     */
    public SystemExcelReader(File excelFile, ExcelTemplate et, String fileName) throws Exception {
        this(new FileInputStream(excelFile), et, fileName);
    }

    /**
     * 读取流
     * 
     * @param is
     */
    public SystemExcelReader(InputStream is, ExcelTemplate et, String fileName) {
        this.is = is;
        super.setExcelTemplate(et);
        this.fileName = fileName;
    }

    /**
     * 关闭对象
     */
    public void closeAll(){
        try {
            wb.close();
        } catch (IOException e) {
        }
        try {
            is.close();
        } catch (IOException e) {
        }
        try {
            if(errorFile != null && errorFile.exists()){
                if(!errorFile.delete()) {
                    throw new BusinessException("文件删除失败");
                }
            }
        } catch (Exception e) {
        }
        try {
            Files.delete(tempPath);
        } catch (Exception e) {
        }
    }

    /**
     * 数据是否有异常
     * 
     * @return
     */
    public boolean isDataValid() {
        return rowValidateResults.size() == 0;
    }

    /**
     * 返回异常数据
     * 
     * @return
     */
    public List<RowValidateResult> getRowValidateResults() {
        return rowValidateResults;
    }

    /**
     * 获取包含错误的excel
     * @return
     * @throws IOException
     */
    public File getValidateResultsInFile(String fileName) throws IOException {
        //生成UUID唯一标识，以防止文件覆盖
        OutputStream fos = null;
        try {
            tempPath = Files.createTempDirectory("ExcelError");
            String fname = fileName;
            int pos = fileName.lastIndexOf('.');
            if (pos > -1) {
                fname = fname.substring(0, pos);
            }
            if(xlsOrXlsx){
                fname = fname + "_" + "错误导出" + SystemConstants.XLSX_SUFFIX;
            } else {
                fname = fname + "_" + "错误导出" + SystemConstants.XLS_SUFFIX;
            }
            errorFile = new File(tempPath.toString(), fname);
            fos = new FileOutputStream(errorFile);
            // ws => outputstream
//            if(xlsOrXlsx){
                wb.write(fos);
//            }
        } catch (IOException e) {
            throw new SystemExcelException(e);
        } catch (Exception e) {
            throw new SystemExcelException(e);
        } finally {
            if(fos != null){
                fos.close();
            }
        }

        return errorFile;
    }

    /**
     * 读取后，泛型返回
     * 
     * @param clasz
     * @param <T>
     * @return
     */
    public <T> List<T> readBeans(final Class<T> clasz) throws IOException {
        return read(new ReadPolicy<T>() {
            @Override
            protected T newRowData() {
                try {
                    return clasz.newInstance();
                } catch (InstantiationException e) {
                    throw new SystemExcelException(e);
                } catch (IllegalAccessException e) {
                    throw new SystemExcelException(e);
                }
            }

            @Override
            protected void setColData(T rowData, DataCol dataCol, Object colDataVal) {
                try {
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(rowData, dataCol.getName());
                    if (pd != null && pd.getPropertyType().equals(String.class)) {
                        BeanUtils.setProperty(rowData, dataCol.getName(), colDataVal);
                    }
                    if (pd != null && pd.getPropertyType().equals(BigDecimal.class)) {
                        if (colDataVal == null || (colDataVal instanceof String && ((String) colDataVal).trim().isEmpty())) {
                            colDataVal = null;
                        } else {
                            BeanUtils.setProperty(rowData, dataCol.getName(), colDataVal);
                        }
                    }

                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new SystemExcelException(e);
                }
            }


        });
    }

    /**
     * 读取后，以数组方式来返回
     * 
     * @return
     */
    public List<String[]> readArrays() throws IOException {
        return read(new ReadPolicy<String[]>() {
            @Override
            protected String[] newRowData() {
                return new String[excelTemplate.getColSize()];
            }

            @Override
            protected void setColData(String[] rowData, DataCol dataCol, Object colDataVal) {
                rowData[dataCol.getIndex()] = StringUtils.toString(colDataVal);
            }
        });
    }

    /**
     * 读取后，以List<Map>方式来返回
     * 
     * @return
     */
    public List<Map<String, Object>> readMaps() throws IOException {
        return read(new ReadPolicy<Map<String, Object>>() {
            @Override
            protected Map<String, Object> newRowData() {
                return new HashMap<String, Object>();
            }

            @Override
            protected void setColData(Map<String, Object> rowData, DataCol dataCol, Object colDataVal) {
                rowData.put(dataCol.getName(), colDataVal);
            }
        });
    }

    /**
     * 以 读取策略方式来进行读取，这个是的核心
     * 
     * @param readPolicy
     * @param <T>
     * @return
     */
    private <T> List<T> read(ReadPolicy<T> readPolicy) throws IOException {
        checkTemplate();
        // 文件分析，判断是否是excel文档
        if (FileMagic.valueOf(is) == FileMagic.OLE2) {
            // office 2003
            xlsOrXlsx = false;
            wb = new HSSFWorkbook(is);
        } else {
            // office 2007
            xlsOrXlsx = true;
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet =  wb.getSheetAt(0);
        readPolicy.checkTemplateTitles(sheet);
        return readPolicy.readDatasFromSheet(sheet);
    }

    /**
     * 判断是否诗xlsx or xls
     * 
     * @return
     */
    private boolean getXlsOrXlsx() throws IOException {
        // 文件分析，判断是否是excel文档
        if (FileMagic.valueOf(is) == FileMagic.OLE2) {
            // Office 2003 ，xls
            return true;
        } else {
            // Office 2007 +，xlsx
            return false;
        }
    }

    /**
     * 获取读取excel的策略，并执行策略（check）
     * 
     * @param <T>
     */
    abstract class ReadPolicy<T> {
        /**
         * 设置列数据
         * 
         * @param rowData
         * @param dataCol
         * @param colDataVal
         */
        protected abstract void setColData(T rowData, DataCol dataCol, Object colDataVal);

        /**
         * 新的一行数据
         * 
         * @return
         */
        protected abstract T newRowData();

        /**
         * 读取数据
         * 
         * @param sheet
         * @return
         */
        List<T> readDatasFromSheet(Sheet sheet) {
            ArrayList<T> datas = new ArrayList<T>();
//
//            // 删除空行
//            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                // 删除空行
//                if (row != null && isRowEmpty(row)) {
//                    int lastRowNum = sheet.getLastRowNum();
//                    if (i >= 0 && i < lastRowNum) {
//                        sheet.shiftRows(i + 1, lastRowNum, -1);// 将行号为i+1一直到行号为lastRowNum的单元格全部上移一行，以便删除i行
//                    }
//                    if (i == lastRowNum) {
//                        if (row != null) {
//                            sheet.removeRow(row);
//                        }
//                    }
//                    i--;
//                }
//            }

            for (int row = excelTemplate.getDataRowIndex(); row <= sheet.getLastRowNum(); row++) {

                List<DataCol> dataCols = excelTemplate.getDataCols();
                T rowData = newRowData();

                boolean isRowDataValid = true;
                RowValidateResult rowValidateResult = new RowValidateResult();

                // check row 是否错误
                if (sheet.getRow(row) == null || isRowEmpty(sheet.getRow(row))) {
                    log.error("excel文档中此行为null！行号：" + row);
                    ColValidateResult colValidateResult = new ColValidateResult();
                    colValidateResult.setSuccess(false);
                    colValidateResult.setErrorMsg("该行是非法数据，请删除此行！");
                    rowValidateResult.addColValidateResult(colValidateResult);
                    isRowDataValid = false;
                }
                if (isRowDataValid) {
                    for (int col = 0; col < dataCols.size(); col++) {
                        String value = getCellValue(row, col, sheet).trim();
                        DataCol dataCol = dataCols.get(col);
                        if (dataCol.hasValidator()) {
                            ColValidateResult colValidateResult = dataCol.validate(value, rowData);
                            rowValidateResult.setRowIndex(row);
                            boolean isColDataValid = colValidateResult.isSuccess();
                            isRowDataValid = isRowDataValid && isColDataValid;
                            if (!isColDataValid) {
                                rowValidateResult.addColValidateResult(colValidateResult);
                            }
                        }
                        if (isRowDataValid) {
                            String convertor = dataCol.getConvertor();
                            Object colDataVal = null;
                            if (StringUtils.isNotEmpty(convertor)) {
                                colDataVal = ConvertorUtil.convertToType(value, convertor);
                            } else {
                                colDataVal = value;
                            }
                            setColData(rowData, dataCol, colDataVal);
                        }
                    }
                }
                // 进行行级check
                if (isRowDataValid) {
                    DataRow dataRow = excelTemplate.getDataRows();
                    if (dataRow.hasRowValidator()) {
                        ColValidateResult colValidateResult = dataRow.validate(rowData, datas);
                        rowValidateResult.setRowIndex(row);
                        boolean isColDataValid = colValidateResult.isSuccess();
                        isRowDataValid = isRowDataValid && isColDataValid;
                        if (!isColDataValid) {
                            rowValidateResult.addColValidateResult(colValidateResult);
                        }
                    }
                }

                if (isRowDataValid) {
                    datas.add(rowData);
                } else {
                    rowValidateResults.add(rowValidateResult);
                    // 添加错误数据
                    setErrorCellValue(row, dataCols.size() + 1, sheet, rowValidateResult.getErrors(excelTemplate.getTitleRows()));
                }
            }
            return datas;
        }

        /**
         * 检查模板和excel是否匹配
         * 
         * @param sheet
         */
        void checkTemplateTitles(Sheet sheet) {
            if (sheet.getRow(0).getPhysicalNumberOfCells() != excelTemplate.getColSize()) {
                throw new SystemExcelException(String.format("读取的excel与模板不匹配：期望%s列，实际为%s列", excelTemplate.getColSize(),
                    sheet.getRow(0).getPhysicalNumberOfCells()));
            }
            List<TitleRow> titleRows = excelTemplate.getTitleRows();
            StringBuffer errorMsg = new StringBuffer();
            for (int row = 0; row < titleRows.size(); row++) {
                TitleRow titleRow = titleRows.get(row);
                for (int col = 0; col < titleRow.colSize(); col++) {
                    TitleCol titleCol = titleRow.getCol(col);
                    if (titleCol instanceof DummyTitleCol) {
                        continue;
                    }
                    // String value = sheet.getCell(col, row).getContents().trim();
                    String value = getCellValue(row, col, sheet).trim();
                    if (!value.equals(titleCol.getTitle())) {
                        errorMsg.append(
                            String.format("第%s行第%s列期望[%s]，实际为[%s]", row + 1, col + 1, titleCol.getTitle(), value));
                    }
                }
            }
            if (errorMsg.length() > 0) {
                errorMsg.deleteCharAt(errorMsg.length() - 1);
                throw new SystemExcelException("读取的excel与模板不匹配：" + errorMsg);
            }
        }

        /**
         * 设置单元格中的值
         * 
         * @return
         */
        private void setErrorCellValue(int rowId, int col, Sheet sheet, String error) {
            Font font = wb.createFont();
            font.setFontName("微软雅黑");
            font.setColor(Font.COLOR_RED);
            font.setBold(true);
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFont(font);

            Row row = sheet.getRow(rowId);
            // 如果行不存在，为了报错，就新建个行来报错
            if (row == null) {
                row = sheet.createRow(rowId);
            }
            Row rowHead = sheet.getRow(0);
            // 创建头部head
            Cell cellHead = rowHead.createCell(col);
            cellHead.setCellStyle(cellStyle);
            cellHead.setCellValue("导入错误信息");
            // 创建错误列
            Cell cell = row.createCell(col);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(error);
        }

        /**
         * 获取单元格中的值
         *
         * @return
         */
        String getCellValue(int rowId, int col, Sheet sheet) {
            Row row = sheet.getRow(rowId);
            Cell cell = row.getCell(col);

            // 返回值
            String rtn = "";
            // 如果cell中没有值
            if (cell == null) {
                log.debug("cell的value: rowid=" + rowId + "；col=" + col + "；cellvalue:" + rtn);
                return rtn;
            }

            if (cell.getCellType() == CellType.NUMERIC) {
                // 判断是否为日期格式
                if(DateUtil.isCellDateFormatted(cell)) {
                    // 日期格式
                    rtn = DateTimeUtil.parseDateToStr(DateTimeUtil.YYYY_MM_DD_HH_MM_SS, cell.getDateCellValue());
                } else {
                    // 非日期格式
                    rtn = String.valueOf(cell.getNumericCellValue());
                }
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                rtn = String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                rtn = String.valueOf(cell.getStringCellValue());
            } else if (cell.getCellType() == CellType.BLANK) {
                rtn = "";
            } else if (cell.getCellType() == CellType.FORMULA) {
                switch (cell.getCachedFormulaResultType()) {
                    case BOOLEAN:
                        rtn = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case NUMERIC:
                        // 判断是否为日期格式
                        if(DateUtil.isCellDateFormatted(cell)) {
                            // 日期格式
                            rtn = DateTimeUtil.parseDateToStr(DateTimeUtil.YYYY_MM_DD_HH_MM_SS, cell.getDateCellValue());
                        } else {
                            // 非日期格式
                            rtn = String.valueOf(cell.getNumericCellValue());
                        }
                        break;
                    case STRING:
                        rtn = String.valueOf(cell.getRichStringCellValue());
                        break;
                    case BLANK:
                        rtn = "";
                        break;
                }
            } else {
                cell.setCellType(CellType.STRING);
                rtn = cell.getStringCellValue();
            }
            log.debug("cell的value: rowid=" + rowId + "；col=" + col + "；cellvalue:" + rtn);
            return rtn;
        }
    }

    Boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

}
