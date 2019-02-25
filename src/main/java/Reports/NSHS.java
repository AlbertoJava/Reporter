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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NSHS extends AbstractExcelReport {
    private String currentDate;
    private String startDate;
    private String finishDate;

    public NSHS(SqlProperties sqlprop, SqlExecutor sqlExecutor) {
        super(sqlprop,sqlExecutor);
        Calendar c =new GregorianCalendar();
        currentDate=c.get(Calendar.DAY_OF_MONTH)+"_"+c.get(Calendar.MONTH)+"_"+c.get(Calendar.YEAR);
    }

    @Override
    public boolean createReport(ResultSet result) {
        List<String> headerXLS = new ArrayList();
        headerXLS.add("№ п/п");
        headerXLS.add("Код ТО");
        headerXLS.add("Дата");
        headerXLS.add("Номер");
        headerXLS.add("Время регистрации ДТ");
        headerXLS.add("Дата выпуска товаров");
        headerXLS.add("Время выпуска товаров");
        headerXLS.add("Время на выпуск товаров");
        headerXLS.add("Обоснование превышения срока, установленного пп. 1, 3 ст. 119 ТК ЕАЭС");

        HSSFWorkbook workbook =new HSSFWorkbook();
        HSSFSheet worksheet =workbook.createSheet("0007-р");
        for (int i=0;i<31;i++){
            worksheet.setColumnWidth(i,10*256);
        }

        try {
            result.last();
            result.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Row preRow=worksheet.createRow(0);
        Cell cell_header = preRow.createCell(0,CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                0,
                0,
                0,
                3
        ));
        cell_header.setCellValue("Отчетный период: с " + getProperty("date1") + " по " + getProperty("date2")+".");

        Row row=worksheet.createRow(1);
         /*Creating of header*/
        // Создаем стиль ячейки для заголовка таблицы
        HSSFCellStyle style = workbook.createCellStyle();

        style.setWrapText(true);
        style.setAlignment (HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontName("Times New Roman");
        fontHeader.setFontHeightInPoints((short)12);
        fontHeader.setBold (true);
        style.setFont(fontHeader);
        style.setRotation((short) 90);
        createHeader(headerXLS,worksheet,style,1);
        insertData(result,worksheet,3);
        createFile(workbook);
        return true;
    }


}
