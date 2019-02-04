package Reports;

import Reports.AbstratReports.AbstractExcelReport;
import Utilz.Printer;
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
import java.util.List;

public class OktsOilReport extends AbstractExcelReport {

    public OktsOilReport(SqlProperties props, SqlExecutor sqlExecutor) {
        super(props, sqlExecutor);
    }
    @Override
    public boolean createReport(ResultSet result) {
        boolean flag=false;
        List<String> headerXLS = new ArrayList();
        headerXLS.add("№ п/п");
        headerXLS.add("Код ТНВЭД");
        headerXLS.add("ДТ№");
        headerXLS.add("№ товара");
        headerXLS.add("ИТС");
        headerXLS.add("Вес нетто");
        HSSFWorkbook workbook =new HSSFWorkbook();
        HSSFSheet worksheet =workbook.createSheet("OilReport");

        worksheet.setColumnWidth(0,12*256);
        worksheet.setColumnWidth(1,30*256);
        worksheet.setColumnWidth(2,60*256);
        worksheet.setColumnWidth(3,20*256);
        worksheet.setColumnWidth(4,20*256);
        worksheet.setColumnWidth(5,30*256);


        Row row=worksheet.createRow(0);

         /*Creating of header*/
        // Создаем стиль ячейки для заголовка таблицы
        HSSFCellStyle style = workbook.createCellStyle();

        style.setWrapText(true);
        style.setAlignment        (HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontName("Times New Roman");
        fontHeader.setFontHeightInPoints((short)12);
        fontHeader.setBold (true);
        style.setFont(fontHeader);

        for (int i=0;i<headerXLS.size();i++){
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(headerXLS.get(i));
        }
        createHeader(headerXLS,worksheet,style,0);
        insertData(result,worksheet,1);
        flag=createFile(workbook);
        return flag;
    }


    @Override
    protected void insertData (ResultSet result, HSSFSheet worksheet, int rowNum){
       int num =createRows(result,worksheet,rowNum,"2710198200" );
        createRows(result,worksheet,num,"2710198400" );

    }
    private int createRows(ResultSet result, HSSFSheet worksheet, int rowNum, String KodEAEC){
        int rownum=rowNum;
        Double summ=0.0;
        int numLine=1;
        boolean flag = false;
        //Записи  и итоговая строчка для 2710198400
        try {
            while (result.next()) {
                Row dataRow = worksheet.createRow(rownum);

                for (int i = 0; i <=result.getMetaData().getColumnCount(); i++) {
                    if (result.getString(1).equals(KodEAEC)) {
                        flag=true;
                        if (i==0){
                            Cell cell1 = dataRow.createCell(i, CellType.STRING);
                            cell1.setCellValue(numLine);
                            numLine++;
                        }
                        else {
                            Cell cell1 = dataRow.createCell(i, CellType.STRING);
                            cell1.setCellValue(result.getString(i));
                        }
                    }
                    else {
                        break;
                    }
                }
               if (flag) {
                   summ+=Double.parseDouble(result.getString(5));
                    rownum++;
                    flag=false;
               }

            }

            //Считаем сумму для строчки итого если были записи
            if (rownum!=rowNum) {
                Row conclusionRow = worksheet.createRow(rownum);
                Cell conclusionCell = conclusionRow.createCell(0, CellType.STRING);
                worksheet.addMergedRegion(new CellRangeAddress(
                        rownum,
                        rownum,
                        0,
                        4
                ));
                conclusionCell.setCellValue("Итого вес нетто:");
                Cell summCell = conclusionRow.createCell(5, CellType.STRING);
                summCell.setCellValue(summ);
            }
            result.beforeFirst();
        } catch (SQLException e) {
            Printer.printLog(e);
        }

        return rownum;
    }
}
