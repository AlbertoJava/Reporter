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

public class AutoRegistration extends AbstractExcelReport {
    private String currentDate;
    private String startDate;
    private String finishDate;

    public AutoRegistration(SqlProperties sqlprop, SqlExecutor sqlExecutor) {
        super(sqlprop, sqlExecutor);
        Calendar c = new GregorianCalendar();
        currentDate = c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.YEAR);
    }

    @Override
    public boolean createReport(ResultSet result) {
        boolean flag = false;
        List<String> headerXLS = new ArrayList();
        headerXLS.add("Код ТО");
        headerXLS.add("ЭК10 ГОД Регистрация");
        headerXLS.add("ЭК10 ГОД АвтоРегистрация");
        headerXLS.add("ЭК10 ГОД Доля, %");

        headerXLS.add("ЭК10 Неделя Регистрация");
        headerXLS.add("ЭК10 Неделя АвтоРегистрация");
        headerXLS.add("ЭК10 Неделя Доля, %");

        headerXLS.add("ИМ40,78 ГОД Регистрация");
        headerXLS.add("ИМ40,78 ГОД АвтоРегистрация");
        headerXLS.add("ИМ40,78 ГОД Доля, %");

        headerXLS.add("ИМ40,78 Неделя Регистрация");
        headerXLS.add("ИМ40,78 Неделя АвтоРегистрация");
        headerXLS.add("ИМ40,78 Неделя Доля, %");

        headerXLS.add("ЭК10 ГОД Выпуск");
        headerXLS.add("ЭК10 ГОД АвтоВыпуск");
        headerXLS.add("ЭК10 ГОД Доля автовыпуск, %");

        headerXLS.add("ЭК10 Неделя Выпуск");
        headerXLS.add("ЭК10 Неделя АвтоВыпуск");
        headerXLS.add("ЭК10 Неделя Доля автовыпуск, %");

        headerXLS.add("ИМ40 ГОД выпуск");
        headerXLS.add("ИМ40 ГОД Автовыпуск");
        headerXLS.add("ИМ40 ГОД Доля автовыпуска, %");

        headerXLS.add("ИМ40 Неделя выпуск");
        headerXLS.add("ИМ40 Неделя Автовыпуск");
        headerXLS.add("ИМ40 Неделя Доля автовыпуск, %");

        headerXLS.add("Все таможенные процедуры за год");
        headerXLS.add("ТУВ за год");
        headerXLS.add("Доля ТУВ за год, %");

        headerXLS.add("Все таможенные процедуры за неделю");
        headerXLS.add("ТУВ за неделю");
        headerXLS.add("Доля ТУВ за неделю, %");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet worksheet = workbook.createSheet("0006-Р");

        for (int i = 0; i < 31; i++) {
            worksheet.setColumnWidth(i, 10 * 256);
        }

        Row preRow = worksheet.createRow(0);
        Cell cell_header = preRow.createCell(0, CellType.STRING);
        worksheet.addMergedRegion(new CellRangeAddress(
                0,
                0,
                0,
                3
        ));
        cell_header.setCellValue("Отчетный период: с " + startDate + " по " + finishDate + ".");
        Row row = worksheet.createRow(1);
        /*Creating of header*/
        // Создаем стиль ячейки для заголовка таблицы
        HSSFCellStyle style = workbook.createCellStyle();

        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontName("Times New Roman");
        fontHeader.setFontHeightInPoints((short) 12);
        fontHeader.setBold(true);
        style.setFont(fontHeader);
        style.setRotation((short) 90);

        createHeader(headerXLS, worksheet, style, 1);
        insertData(result, worksheet, 2);
        flag = createFile(workbook);
        return flag;
    }


}
