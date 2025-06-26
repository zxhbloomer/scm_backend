package com.xinyirun.scm.excel.export;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.excel.config.EasyExcelCustomCellWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel相关处理 easyExcel
 * 
 * @author wwl
 */
@Slf4j
public class EasyExcelUtil<T> {

    /**
     * 实体对象
     */
    public Class<T> clazz;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * 导入导出数据列表
     */
    private List<T> list;

    public EasyExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void init(List<T> list, String sheetName, String fileName) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.fileName = fileName + SystemConstants.XLSX_SUFFIX;;

        /** 设置导出文件名称 */
        try {
            this.fileName = URLEncoder.encode(this.fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("init error", e);
        }
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
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename="+new String(fileName.getBytes(), StandardCharsets.UTF_8));
        response.setHeader("wms-filename", fileName);
        EasyExcel.write(response.getOutputStream(), clazz).registerWriteHandler(new EasyExcelCustomCellWriteHandler()).registerWriteHandler(getStyleStrategy()).sheet(sheetName).doWrite(list);
    }

    public void exportExcel(String exportFileName, List<T> dataList, HttpServletResponse response, WriteSheet writeSheet)
            throws IOException {
        this.init(dataList, sheetName, exportFileName);
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename="+new String(fileName.getBytes(), StandardCharsets.UTF_8));
        response.setHeader("wms-filename", fileName);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), clazz).registerWriteHandler(new EasyExcelCustomCellWriteHandler()).registerWriteHandler(getStyleStrategy()).build();
        excelWriter.write(dataList, writeSheet);
        excelWriter.finish();
    }

    public static HorizontalCellStyleStrategy getStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置对齐
        //headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        WriteFont font = new WriteFont();
        font.setFontName("微软雅黑");
        // 粗体显示
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        headWriteCellStyle.setWriteFont(font);
        // 背景色, 设置为蓝色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
        headWriteCellStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
        // 字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setColor(IndexedColors.WHITE.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        // contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());

        // 字体策略
        WriteFont contentWriteFont = new WriteFont();
        //contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //设置 自动换行
        contentWriteCellStyle.setWrapped(true);
        //设置 垂直居中
//        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置 水平居中
//        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);

        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }


}