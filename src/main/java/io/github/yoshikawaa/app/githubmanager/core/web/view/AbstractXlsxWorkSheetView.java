package io.github.yoshikawaa.app.githubmanager.core.web.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

public abstract class AbstractXlsxWorkSheetView extends AbstractXlsxView {

    private static final String EXTENSION = ".xlsx";

    protected Row row(Sheet sheet, AtomicInteger row, AtomicInteger cell) {
        cell.set(0);
        return sheet.createRow(row.getAndIncrement());
    }

    protected Cell cell(Row currentRow, AtomicInteger cell, CellStyle style) {
        Cell currentCell = currentRow.createCell(cell.getAndIncrement());
        currentCell.setCellStyle(style);
        return currentCell;
    }

    protected CellStyle defaultStyle(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    protected CellStyle headerStyle(CellStyle style, Font font) {
        style = defaultStyle(style);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        style.setFont(font);
        return style;
    }

    protected CellStyle dataRowStyle(CellStyle style, int index) {
        style = defaultStyle(style);
        style.setFillForegroundColor(
                (Math.floorMod(index, 2) == 0) ? IndexedColors.LIGHT_TURQUOISE.index : IndexedColors.WHITE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    protected void autoSizeAndFilter(Sheet sheet, int columns, int rows) {
        IntStream.rangeClosed(0, columns).forEach(i -> sheet.autoSizeColumn(i, true));
        sheet.setAutoFilter(new CellRangeAddress(0, rows, 0, columns - 1));
    }

    protected void setContentDisposition(HttpServletResponse response, String filename, Charset charset)
            throws UnsupportedEncodingException {
        String encodedFilename = URLEncoder.encode(filename + EXTENSION, charset.name());
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
    }
}
