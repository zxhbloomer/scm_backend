package com.xinyirun.scm.excel.export;

import com.xinyirun.scm.common.annotations.ExcelAnnotion;
import com.xinyirun.scm.common.annotations.ExcelsAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.CommonUtil;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleCol;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Excel相关处理
 * 
 * @author zxh
 */
@Slf4j
public class ExcelUtil<T> {

    /**
     * Excel sheet最大行数，默认65536
     */
    public static final int sheetSize = 65536;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 导入导出数据列表
     */
    private List<T> list;

    /**
     * 注解列表
     */
    private List<Object[]> fields;

    /**
     * 实体对象
     */
    public Class<T> clazz;

    public ExcelUtil() {
    }

    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void init(List<T> list, String sheetName, String fileName) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.fileName = fileName;
        createExcelField();
        createWorkbook();
    }

//    /**
//     * 下载文件
//     *
//     * @param serverFile 服务器端文件路径
//     * @param downLoadFile 下载文件的文件名
//     * @throws IOException
//     */
//    public static void download(String serverFile, String downLoadFile, HttpServletResponse response)
//        throws IOException {
//        /** 获取下载文件的路径 */
//        File file = new File(serverFile);
//        InputStream fis = new FileInputStream(serverFile);
//        /** 获取输出流 */
//        OutputStream out = response.getOutputStream();
//
//        /** 设置导出文件名称 */
//        downLoadFile = downLoadFile + "_" + DateTimeUtil.dateTimeNow() + SystemConstants.XLSX_SUFFIX;
//        downLoadFile = URLEncoder.encode(downLoadFile, "utf-8");
//        /** 弹出下载的框filename:提示用户下载的文件名 */
//        response.addHeader("content-disposition", "attachment;filename=" + downLoadFile);
//        response.setContentType("application/octet-stream;charset=UTF-8");
//        response.setHeader("Content-Length", String.valueOf(file.length()));
//        response.setHeader("wms-filename", downLoadFile);
//        log.debug("获取responseContentType：" + response.getContentType());
//        /** 循环读取 */
//        byte[] buff = new byte[1024];
//        int len = 0;
//        while((len=fis.read(buff))!=-1){
//            out.write(buff,0,len);
//        }
//        out.flush();
//
//        // 关闭流资源
//        fis.close();
//        out.close();
//        // 删除服务器端生成的下载文件,减轻服务器的压力
//        if (file.exists()) {
//            if(!file.delete()) {
//                throw new BusinessException("文件删除失败");
//            }
//        }
//    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param exportFileName
     * @param sheetName
     * @param et
     * @param response
     */
    public void exportExcelHead(String exportFileName, String sheetName, ExcelTemplate et, HttpServletResponse response)
        throws IOException {
        this.sheetName = sheetName;
        this.fileName = exportFileName;
        createWorkbook();
        // 调用excel文件生成逻辑，并返回文件路径
        String fileNamePath = doExport(et);
        /** 下载Excel文件 */
        CommonUtil.download(fileNamePath, this.fileName, response);
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     * 
     * @param exportFileName
     * @param sheetName
     * @param dataList
     * @param response
     */
    public void exportExcel(String exportFileName, String sheetName, List<T> dataList, HttpServletResponse response)
        throws IOException {
        this.init(dataList, sheetName, exportFileName);
        exportExcel(response);
    }

    /**
     * jxls2 导出数据
     */
    public void exportJxls2Excel(String exportFileName, List<T> dataList, String template, HttpServletResponse response)
            throws IOException {
        /** 弹出下载的框filename:提示用户下载的文件名 */
        response.addHeader("content-disposition", "attachment;filename=" + exportFileName);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("wms-filename", exportFileName);
        OutputStream os = response.getOutputStream();
        InputStream is = this.getClass().getResourceAsStream(template);
        log.debug("获取responseContentType：" + response.getContentType());


        Context ctx = new Context();
        ctx.putVar("list", dataList);

        //模版处理
        JxlsHelper.getInstance().processTemplate(is, os, ctx);
    }

    private void exportExcel(HttpServletResponse response) throws IOException {
        // 调用excel文件生成逻辑，并返回文件路径
        String fileNamePath = doExport();
        /** 下载Excel文件 */
        CommonUtil.download(fileNamePath, this.fileName, response);
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     * 
     * @return 结果
     */
    public String doExport() {
        OutputStream out = null;
        try {
            // 取出一共有多少个sheet.
            double sheetNo = Math.ceil(list.size() / sheetSize);
            for (int index = 0; index <= sheetNo; index++) {
                createSheet(sheetNo, index);

                // 产生一行
                Row row = sheet.createRow(0);
                int column = 0;
                // 写入各个字段的列头名称
                for (Object[] os : fields) {
                    ExcelAnnotion excel = (ExcelAnnotion)os[1];
                    this.createRowHeadCell(excel, row, column++);
                }
                fillExcelData(index, row);
            }
            String tmpFilename = getAbsoluteFile(this.fileName);
            out = new FileOutputStream(tmpFilename);
            wb.write(out);
            return tmpFilename;
        } catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    log.error("doExport error", e1);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    log.error("doExport error", e1);
                }
            }
        }
    }

    /**
     * 根据ExcelTemplate导出excel的导入模板
     *
     * @return 结果
     */
    public String doExport(ExcelTemplate et) {
        OutputStream out = null;
        try {
            double sheetNo = 0;
            int index = 0;
            createSheet(sheetNo, index);
            // 产生一行
            Row row = sheet.createRow(0);

            int column = 0;
            // 写入各个字段的列头名称
            for(TitleCol col : et.getTitleRows().get(0).getCols()) {
                this.createRowHeadCell(col.getTitle(), row, column++);
            }

            String tmpFilename = getAbsoluteFile(this.fileName);
            out = new FileOutputStream(tmpFilename);
            wb.write(out);
            return tmpFilename;
        } catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    log.error("doExport error", e1);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    log.error("doExport error", e1);
                }
            }
        }
    }

    /**
     * 填充excel数据
     * 
     * @param index 序号
     * @param row 单元格行
     */
    public void fillExcelData(int index, Row row) {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        // 写入各条记录,每条记录对应excel表中的一行
        // CellStyle cs = wb.createCellStyle();
        // cs.setAlignment(HorizontalAlignment.CENTER);
        // cs.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle cs = createDetailStyle();
        for (int i = startNo; i < endNo; i++) {
            row = sheet.createRow(i + 1 - startNo);
            // 得到导出对象.
            T vo = (T)list.get(i);
            int column = 0;
            for (Object[] os : fields) {
                Field field = (Field)os[0];
                ExcelAnnotion excel = (ExcelAnnotion)os[1];
                // 设置实体类私有属性可访问
                field.setAccessible(true);
                this.addCell(excel, row, vo, field, column++, cs);
            }
        }
    }

    /**
     * 创建head单元格
     */
    public Cell createRowHeadCell(ExcelAnnotion attr, Row row, int column) {
        // 创建列
        Cell cell = row.createCell(column);
        // 设置列中写入内容为String类型
        cell.setCellType(CellType.STRING);
        // 写入列名
        cell.setCellValue(attr.name());
        CellStyle cellStyle = createHeadStyle(attr, row, column);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 创建head单元格
     */
    public Cell createRowHeadCell(String name, Row row, int column) {
        // 创建列
        Cell cell = row.createCell(column);
        // 设置列中写入内容为String类型
        cell.setCellType(CellType.STRING);
        // 写入列名
        cell.setCellValue(name);
        CellStyle cellStyle = createHeadStyle(name, row, column);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 创建head表格样式
     */
    public CellStyle createHeadStyle(ExcelAnnotion attr, Row row, int column) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        // 粗体显示
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        // 选择需要用到的字体格式
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
        // 设置列宽
        sheet.setColumnWidth(column, (int)((attr.width() + 0.72) * 256));
        row.setHeight((short)(attr.height() * 20));

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setWrapText(true);
        // 如果设置了提示信息则鼠标放上去提示.
        if (StringUtils.isNotEmpty(attr.prompt())) {
            // 这里默认设了2-101列提示.
            setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, column, column);
        }
        return cellStyle;
    }

    /**
     * 创建head表格样式
     */
    public CellStyle createHeadStyle(String attr, Row row, int column) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        // 粗体显示
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        // 选择需要用到的字体格式
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
        // 设置列宽
        sheet.setColumnWidth(column, (int)((16 + 0.72) * 256));
//        row.setHeight((short)(attr.height() * 20));

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    /**
     * 创建head表格样式
     */
    public CellStyle createDetailStyle() {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        // 粗体显示
        // font.setBold(true);
        // font.setColor(IndexedColors.WHITE.index);
        // 选择需要用到的字体格式
        cellStyle.setFont(font);

        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    /**
     * 添加单元格
     */
    public Cell addCell(ExcelAnnotion attr, Row row, T vo, Field field, int column, CellStyle cs) {
        Cell cell = null;
        try {
            // 设置行高
            row.setHeight((short)(attr.height() * 20));
            // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
            if (attr.isExport()) {
                // 创建cell
                cell = row.createCell(column);
                cell.setCellStyle(cs);

                // 用于读取对象中的属性
                Object value = getTargetValue(vo, field);
                String dateFormat = attr.dateFormat();
                if (StringUtils.isNotEmpty(dateFormat) && StringUtils.isNotNull(value)) {
                    cell.setCellValue(DateTimeUtil.parseDateToStr(dateFormat, (Date)value));
                } else {
                    cell.setCellType(CellType.STRING);
                    // 如果数据存在就填入,不存在填入空格.
                    cell.setCellValue(StringUtils.isNull(value) ? attr.defaultValue() : value + attr.suffix());
                }
            }
        } catch (Exception e) {
            log.error("导出Excel失败{}", e);
        }
        return cell;
    }

    /**
     * 获取bean中的属性值
     *
     * @param vo 实体对象
     * @param field 字段
     * @return 最终的属性值
     * @throws Exception
     */
    private Object getTargetValue(T vo, Field field) throws Exception {
        Object o = field.get(vo);
        return o;
    }

    /**
     * 设置 POI XSSFSheet 单元格提示
     * 
     * @param sheet 表单
     * @param promptTitle 提示标题
     * @param promptContent 提示内容
     * @param firstRow 开始行
     * @param endRow 结束行
     * @param firstCol 开始列
     * @param endCol 结束列
     */
    public void setXSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow, int endRow,
        int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createPromptBox(promptTitle, promptContent);
        dataValidation.setShowPromptBox(true);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 获取下载路径
     * 
     * @param filename 文件名称
     */
    public String getAbsoluteFile(String filename) throws IOException {
        // 生成UUID唯一标识，以防止文件覆盖
        UUID uuid = UUID.randomUUID();
        File tempFile = TempFile.createTempFile(uuid.toString() + filename, SystemConstants.XLSX_SUFFIX);
        tempFile.deleteOnExit();
        log.debug("生成临时文件，路径为:" + tempFile.getAbsolutePath());

        // String downloadPath = "" + filename;
        // File desc = new File(downloadPath);
        // if (!desc.getParentFile().exists()) {
        // desc.getParentFile().mkdirs();
        // }
        return tempFile.getAbsolutePath();
    }

    /**
     * 以类的属性的get方法方法形式获取值
     * 
     * @param o
     * @param name
     * @return value
     * @throws Exception
     */
    private Object getValue(Object o, String name) throws Exception {
        if (StringUtils.isNotEmpty(name)) {
            Class<?> clazz = o.getClass();
            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method method = clazz.getMethod(methodName);
            o = method.invoke(o);
        }
        return o;
    }

    /**
     * 得到所有定义字段
     */
    private void createExcelField() {
        this.fields = new ArrayList<Object[]>();
        List<Field> tempFields = new ArrayList<>();
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        for (Field field : tempFields) {
            // 单注解
            if (field.isAnnotationPresent(ExcelAnnotion.class)) {
                putToField(field, field.getAnnotation(ExcelAnnotion.class));
            }

            // 多注解
            if (field.isAnnotationPresent(ExcelsAnnotion.class)) {
                ExcelsAnnotion attrs = field.getAnnotation(ExcelsAnnotion.class);
                ExcelAnnotion[] excels = attrs.value();
                for (ExcelAnnotion excel : excels) {
                    putToField(field, excel);
                }
            }
        }
    }

    /**
     * 放到字段集合中
     */
    private void putToField(Field field, ExcelAnnotion attr) {
        if (attr != null) {
            this.fields.add(new Object[] {field, attr});
        }
    }

    /**
     * 创建一个工作簿
     */
    public void createWorkbook() {
        this.wb = new SXSSFWorkbook(500);
    }

    /**
     * 创建工作表
     * 
     * @param sheetNo，指定Sheet名称
     * @param sheetNo sheet数量
     * @param index 序号
     */
    public void createSheet(double sheetNo, int index) {
        this.sheet = wb.createSheet();
        // 设置工作表的名称.
        if (sheetNo == 0) {
            wb.setSheetName(index, sheetName);
        } else {
            wb.setSheetName(index, sheetName + index);
        }
    }

    /**
     * 获取单元格值
     * 
     * @param row 获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column) {
        if (row == null) {
            return row;
        }
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val = DateUtil.getJavaDate((Double)val); // POI Excel 日期格式转换
                    } else {
                        if ((Double)val % 1 > 0) {
                            val = new DecimalFormat("0.00").format(val);
                        } else {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                } else if (cell.getCellType() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }

            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }
}