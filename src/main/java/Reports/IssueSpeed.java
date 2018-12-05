package Reports;

import Reports.AbstratReports.AbstractExcelReport;
import Utilz.SqlExecutor;
import Utilz.SqlProperties;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class IssueSpeed extends AbstractExcelReport {
    private String startDate="";
    private String finishDate="";
    private String currentDate="";

    public IssueSpeed(SqlProperties prop, SqlExecutor sqlExecutor) {
        super(prop,sqlExecutor);
        Calendar c =new GregorianCalendar();
        currentDate=c.get(Calendar.DAY_OF_MONTH)+"_"+c.get(Calendar.MONTH)+"_"+c.get(Calendar.YEAR);
    }

    @Override
    public boolean createReport(ResultSet result) {
        boolean flag=false;
        List<String> headerXLS = new ArrayList();
        headerXLS.add("");
        headerXLS.add("");
        headerXLS.add("За год");
        headerXLS.add("За неделю");
        headerXLS.add("Количество за год");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Количество за неделю");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Количество за год");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Количество за неделю");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Количество за год");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Количество за неделю");
        headerXLS.add("Доля в общем количестве");
        headerXLS.add("Без учета ДТ, выпущенных с применением ТУВ");
        headerXLS.add("ДТ, выпущенные с применением ТУВ");

        HSSFWorkbook workbook =new HSSFWorkbook();
        HSSFSheet worksheet =workbook.createSheet("0008-Р");
        Row [] headerRows=new Row[4];
        for (int i=0;i<4;i++){
            headerRows[i]= worksheet.createRow(i);
        }

        for (int i=0;i<headerXLS.size();i++){
            worksheet.setColumnWidth(i,10*256);
        }

        HSSFCellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment        (HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontName("Times New Roman");
        fontHeader.setFontHeightInPoints((short)12);
        fontHeader.setBold (true);
        style.setFont(fontHeader);
        style.setRotation((short) 90);
        headerRows[3].setHeight((short) (10*256));
        createHeader(headerXLS,style,headerRows[3],0);

        Cell cell_header = headerRows[2].createCell(2, CellType.STRING);
        headerRows[2].setHeight((short) (9*256));
        worksheet.addMergedRegion(new CellRangeAddress(
                2,
                2,
                2,
                3
        ));
        cell_header.setCellValue("Общее количество ДТ, по которым принято решение об выпуске/отказе в выпуске товаров.");
        HSSFCellStyle style2 =  workbook.createCellStyle();
        style2.cloneStyleFrom(style);
        style2.setRotation((short) 0);
        cell_header.setCellStyle(style2);

        Cell cell_header2 = headerRows[2].createCell(4, CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                2,
                2,
                4,
                7
        ));
        cell_header2.setCellStyle(style2);
        cell_header2.setCellValue("ДТ, по которым решение о выпуске/отказе в выпуске товаров принято в сроки, регламентированные п. 1 ст. 119 ТК ЕАЭС (4 часа)");

        Cell cell_header3 = headerRows[2].createCell(8, CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                2,
                2,
                8,
                11
        ));
        cell_header3.setCellStyle(style2);
        cell_header3.setCellValue("ДТ, по которым решение о выпуске/отказе в выпуске товаров принято в сроки, регламентированные п. 3 ст. 119 ТК ЕАЭС (день, следующий за днем регистрации ДТ)");

        Cell cell_header4 = headerRows[2].createCell(12, CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                2,
                2,
                12,
                15
        ));
        cell_header4.setCellStyle(style2);
        cell_header4.setCellValue("ДТ, по которым решение о выпуске/отказе в выпуске товаров принято в сроки, превыщающие день, следующий за днем регистрации ДТ");


        Cell cell_header5 = headerRows[2].createCell(16, CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                2,
                2,
                16,
                17
        ));
        cell_header5.setCellStyle(style2);
        cell_header5.setCellValue("Среднее время выпуска товаров");

        Cell cell_header6 = headerRows[1].createCell(0, CellType.STRING);
        headerRows[1].setHeight((short) (3*256));
        worksheet.addMergedRegion(new CellRangeAddress(
                1,
                1,
                0,
                17
        ));
        cell_header6.setCellValue("Отчет о сроках принятия решений о выпуске/отказе в выпуске товаров с " +startDate+" по " + finishDate+".");
        style2.cloneStyleFrom(style);
        style2.setRotation((short) 0);
        cell_header6.setCellStyle(style2);

        insertData(result,worksheet,4);
        flag= createFile(workbook);
        return flag;
    }

}
