package com.xinyirun.scm.excel.export;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.handler.WorkbookWriteHandler;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.excel.config.EasyExcelCustomCellWriteHandler;
import com.xinyirun.scm.excel.merge.AbstractBusinessMergeStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

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
     * 实体对象类型
     */
    private final Class<T> clazz;

    /**
     * 业务合并策略
     */
    private AbstractBusinessMergeStrategy mergeStrategy;

    public EasyExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    /**
     * 设置业务合并策略
     * 
     * @param mergeStrategy 合并策略实例
     * @return this，支持链式调用
     */
    public EasyExcelUtil<T> withMergeStrategy(AbstractBusinessMergeStrategy mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
        return this;
    }
    
    /**
     * 工作簿写入完成处理器，用于触发最后的合并
     * 
     * 由于FastExcel 1.2.0版本的SheetWriteHandler没有afterSheetDispose方法，
     * 我们使用WorkbookWriteHandler的afterWorkbookDispose来实现最终合并。
     */
    public static class FinalMergeHandler implements WorkbookWriteHandler {
        private final AbstractBusinessMergeStrategy mergeStrategy;
        
        public FinalMergeHandler(AbstractBusinessMergeStrategy mergeStrategy) {
            this.mergeStrategy = mergeStrategy;
        }
        
        @Override
        public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
            // 在工作簿写入完成后，触发最后的合并
            if (mergeStrategy != null && writeWorkbookHolder.getHasBeenInitializedSheetIndexMap() != null) {
                // 遍历所有Sheet并执行最终合并
                writeWorkbookHolder.getHasBeenInitializedSheetIndexMap().values().forEach(writeSheetHolder -> {
                    if (writeSheetHolder.getSheet() != null) {
                        mergeStrategy.finalizeMerge(writeSheetHolder.getSheet());
                        log.info("Sheet[{}]写入完成，执行最终合并", writeSheetHolder.getSheetName());
                    }
                });
            }
        }
    }

    /**
     * 处理导出文件名，添加后缀并进行URL编码
     * 
     * @param exportFileName 原始文件名
     * @return 处理后的文件名
     */
    private static String processFileName(String exportFileName) {
        String fullFileName = exportFileName + SystemConstants.XLSX_SUFFIX;
        try {
            return URLEncoder.encode(fullFileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
            return fullFileName;
        }
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param exportFileName 导出文件名
     * @param sheetName      Sheet名称
     * @param dataList       导出数据列表
     * @param response       HTTP响应
     */
    public void exportExcel(String exportFileName, String sheetName, List<T> dataList, HttpServletResponse response)
            throws IOException {
        // 处理空数据和文件名
        List<T> actualDataList = dataList != null ? dataList : new ArrayList<>();
        String processedFileName = processFileName(exportFileName);
        
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename="+new String(processedFileName.getBytes(), StandardCharsets.UTF_8));
        response.setHeader("wms-filename", processedFileName);
        
        // 构建EasyExcel写入器
        var writerBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                .registerWriteHandler(getStyleStrategy());
        
        // 如果设置了合并策略，注册合并处理器
        if (mergeStrategy != null) {
            writerBuilder.registerWriteHandler(mergeStrategy)
                        .registerWriteHandler(new FinalMergeHandler(mergeStrategy));
            log.info("已注册业务合并策略: {}", mergeStrategy.getClass().getSimpleName());
        }
        
        writerBuilder.sheet(sheetName).doWrite(actualDataList);
    }


    /**
     * 使用自定义WriteSheet导出Excel
     * 
     * @param exportFileName 导出文件名
     * @param dataList       导出数据列表
     * @param response       HTTP响应
     * @param writeSheet     自定义WriteSheet
     */
    public void exportExcel(String exportFileName, List<T> dataList, HttpServletResponse response, WriteSheet writeSheet)
            throws IOException {
        // 处理空数据和文件名
        List<T> actualDataList = dataList != null ? dataList : new ArrayList<>();
        String processedFileName = processFileName(exportFileName);
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename="+new String(processedFileName.getBytes(), StandardCharsets.UTF_8));
        response.setHeader("wms-filename", processedFileName);
        
        // 构建EasyExcel写入器
        var writerBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                .registerWriteHandler(getStyleStrategy());
        
        // 如果设置了合并策略，注册合并处理器
        if (mergeStrategy != null) {
            writerBuilder.registerWriteHandler(mergeStrategy)
                        .registerWriteHandler(new FinalMergeHandler(mergeStrategy));
            log.info("已注册业务合并策略: {}", mergeStrategy.getClass().getSimpleName());
        }
        
        ExcelWriter excelWriter = writerBuilder.build();
        excelWriter.write(actualDataList, writeSheet);
        excelWriter.finish();
    }
    
    /**
     * 带合并策略的简化导出方法
     * 
     * @param exportFileName   导出文件名
     * @param sheetName       Sheet名称
     * @param dataList        导出数据列表
     * @param response        HTTP响应
     * @param mergeStrategy   业务合并策略
     */
    public void exportExcelWithMergeStrategy(String exportFileName, String sheetName, List<T> dataList, 
                                           HttpServletResponse response, AbstractBusinessMergeStrategy mergeStrategy) 
            throws IOException {
        this.withMergeStrategy(mergeStrategy);
        this.exportExcel(exportFileName, sheetName, dataList, response);
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