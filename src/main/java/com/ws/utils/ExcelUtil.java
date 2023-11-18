package com.ws.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.ws.excepion.IException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExcelUtil {

    public static void writeOneSheetExcel(
            List<Map<String, Object>> dataList,
            List<String> dataKeyList,
            List<String> columnList,
            List<WriteHandler> writeHandlerList,
            List<Converter<?>> converterList,
            String fileName,
            HttpServletResponse response
    ) throws IOException {
        writeOneSheetExcel(
                dataList,
                dataKeyList,
                columnList,
                fileName.replaceAll(".xlsx", "").replaceAll(".xls", ""),
                writeHandlerList,
                converterList,
                fileName,
                response
        );
    }

    public static void writeOneSheetExcel(
            List<Map<String, Object>> dataList,
            List<String> dataKeyList,
            List<String> columnList,
            String sheet,
            List<WriteHandler> writeHandlerList,
            List<Converter<?>> converterList,
            String fileName,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        writeOneSheetExcel(
                dataList,
                dataKeyList,
                columnList,
                sheet,
                writeHandlerList,
                converterList,
                fileName,
                response.getOutputStream()
        );
    }

    public static void writeOneSheetExcel(
            List<Map<String, Object>> dataList,
            List<String> dataKeyList,
            List<String> columnList,
            String sheet,
            List<WriteHandler> writeHandlerList,
            List<Converter<?>> converterList,
            String fileName,
            OutputStream outputStream
    ) {
        List<List<Object>> newData = new ArrayList<>();
        List<List<String>> newColumn = new ArrayList<>();
        dataList.forEach(item -> {
            List<Object> list = new ArrayList<>();
            dataKeyList.forEach(key -> list.add(item.get(key)));
            newData.add(list);
        });
        columnList.forEach(item -> newColumn.add(List.of(item)));
        writeOneSheetExcel(
                newData,
                newColumn,
                sheet,
                writeHandlerList,
                converterList,
                fileName,
                outputStream
        );
    }

    public static void writeOneSheetExcel(List<List<Object>> dataList,
                                          List<List<String>> columnsList,
                                          String sheet,
                                          List<WriteHandler> writeHandlerList,
                                          List<Converter<?>> converterList,
                                          String fileName,
                                          OutputStream outputStream) {
        writeExcel(
                List.of(dataList),
                List.of(columnsList),
                List.of(URLDecoder.decode(sheet, StandardCharsets.UTF_8)),
                writeHandlerList,
                converterList,
                fileName,
                outputStream
        );
    }

    public static void writeExcel(
            List<List<List<Object>>> dataList,
            List<List<List<String>>> columnList,
            List<String> sheetList,
            List<WriteHandler> writeHandlerList,
            List<Converter<?>> converterList,
            String fileName,
            OutputStream outputStream
    ) {
        ExcelWriter excelWriter = null;
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream).autoCloseStream(Boolean.FALSE).excelType(ExcelTypeEnum.XLSX);
            if (Objects.isNull(writeHandlerList)) {
                writeHandlerList = new ArrayList<>();
            }
            if (Objects.isNull(converterList)) {
                converterList = new ArrayList<>();
            }
            converterList.add(new TimestampConverter());
            converterList.forEach(excelWriterBuilder::registerConverter);
            excelWriter = excelWriterBuilder.build();
            for (int index = 0; index < dataList.size(); index++) {
                if (Objects.nonNull(dataList.get(index)) && Objects.nonNull(columnList.get(index)) && Objects.nonNull(sheetList.get(index))) {
                    WriteSheet writeSheet = EasyExcel.writerSheet(index, sheetList.get(index)).head(columnList.get(index)).build();
                    excelWriter.write(dataList.get(index), writeSheet);
                }
            }
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    public static List<List<Object>> processExportDataWithThread(List<Map<String, Object>> data, List<String> columns, Function<Object, Object> consumer) {
        if (data != null) {
            List<List<Object>> result = new ArrayList<>();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int threadSize = (int) Math.round(1.0 * data.size() / 5);
            ExecutorService threadPool = Executors.newFixedThreadPool(5);
            List<FutureTask<List<List<Object>>>> futureTasks = new ArrayList<>(5);
            for (int i = 0; i < 5; i++) {
                List<Map<String, Object>> item = data.subList(i * threadSize, (i + 1) * threadSize);
                FutureTask<List<List<Object>>> task = new FutureTask<>(new IProcessDataCallable<List<List<Object>>>("处理导出数据thread" + (i + 1), item, () -> {
                    List<List<Object>> splitDataResult = new ArrayList<>();
                    item.forEach(mapItem -> {
                        List<Object> temp = new ArrayList<>();
                        if (consumer != null) {
                            columns.forEach(column -> temp.add(consumer.apply(mapItem.get(column))));
                        } else {
                            columns.forEach(column -> temp.add(mapItem.get(column)));
                        }
                        splitDataResult.add(temp);
                    });
                    return splitDataResult;
                }));
                threadPool.submit(task);
                futureTasks.add(task);
            }
            for (FutureTask<List<List<Object>>> futureTask : futureTasks) {
                try {
                    result.addAll(futureTask.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new IException("导出Excel失败", e);
                }
            }
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
            }
            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeSeconds());
            return result;
        }
        return null;
    }

    public static WriteCellStyle getDefaultCellStyle() {
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        WriteFont cellFont = new WriteFont();
        cellFont.setFontHeightInPoints((short) 14);
        contentWriteCellStyle.setWriteFont(cellFont);
        return contentWriteCellStyle;
    }

    public static WriteCellStyle getDefaultHeaderStyle() {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        writeCellStyle.setBorderBottom(BorderStyle.THIN);
        writeCellStyle.setBorderTop(BorderStyle.THIN);
        writeCellStyle.setBorderLeft(BorderStyle.THIN);
        writeCellStyle.setBorderRight(BorderStyle.THIN);
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        WriteFont headerFont = new WriteFont();
        headerFont.setFontHeightInPoints((short) 16);
        headerFont.setBold(true);
        writeCellStyle.setWriteFont(headerFont);
        return writeCellStyle;
    }

    public static HorizontalCellStyleStrategy getDefaultExcelStyle() {
        return new HorizontalCellStyleStrategy(getDefaultHeaderStyle(), getDefaultCellStyle());
    }

}

class IProcessDataCallable<R> implements Callable<R> {

    private final Supplier<R> supplier;

    public IProcessDataCallable(String name, List<Map<String, Object>> item, Supplier<R> supplier) {
        this.supplier = supplier;
    }

    @Override
    public R call() throws Exception {
        return supplier.get();
    }

}
