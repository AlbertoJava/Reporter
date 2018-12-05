package Reports.AbstratReports;

import Utilz.Printer;
import Utilz.SqlExecutor;
import Utilz.SqlProperties;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbstractExcelReport extends AbstractReport {
    private Set<String> paths = new HashSet<>();
    private final String reservePath = "c:\\regular_report\\";
    private ResultSet resultSet=null;

    public AbstractExcelReport(SqlProperties props, SqlExecutor sqlExecutor) {
        super(props,sqlExecutor);
        paths.add(reservePath);
        if (getProperty("path")!=null){
        paths.add(getProperty("path"));
        }
        Printer.printRowToMonitor("Конструктор AbstractMonitor: cтартуем поток " + getProperty("description") + ", сервер подключения: " + getProperty("server"));
    }
    public abstract boolean createReport(ResultSet result);

    @Override
    public boolean createReport() {
        resultSet=executeSqlClause(getProperty("sql"));
        return createReport(resultSet);
    }

    protected boolean createFile(HSSFWorkbook workbook){
        boolean flag=false;
        for (String s:paths) {
            File path =new File (s);
            path.mkdir();
            File file = new File (getUniqFileName(s));
            try (FileOutputStream fos = new FileOutputStream(file)){
                workbook.write(fos);
                Printer.printRowToMonitor("File created " + s);

            } catch (IOException e) {
                e.printStackTrace();
                Printer.printRowToMonitor("Error when writing " + s+  " file!");

            }
        }
        closeConnection(resultSet);
        flag= getProps().updatePeriodinFile();
        return flag;
    }
    /*Добавляет путь для выгрузки*/
    protected void addUnloadPath(String outputFile){
        paths.add(outputFile);
    }
    /*
     * Метод возвращают текущую дату в виде строки с разделителем delimiter
     * */
    protected String getCurrentDate (String delimiter){
        Calendar c= new GregorianCalendar();
        String sDate= String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String sMonth = String.valueOf ((c.get(Calendar.MONTH)+1));
        String sYear = String.valueOf (c.get(Calendar.YEAR));
        String currentDate= alignString (sDate,2,"0") +delimiter
                +alignString (sMonth,2,"0")+delimiter
                +alignString (sYear,4,"20");
        return currentDate;
    }
    /*
     * Метод выравнивает длину строки знаками sign до длины строки quantitySigns
     * */
    protected String alignString (String object, int quantitySigns, String sign){
        if (object.length()>=quantitySigns) return object;
        StringBuilder result = new StringBuilder(object);
        while (result.length()<quantitySigns) {
            result.insert(0,sign);
        }
        return result.toString();
    }

    private String  getUniqFileName(String outputFile) {
        for (int i = 0; ; i++) {
            if (!Files.exists(Paths.get(outputFile + "\\" + "ОТОиТК 3_"+getCurrentDate(" ") + "_" + String.valueOf(i) + ".xls"))) {
                outputFile = outputFile + "\\" + "ОТОиТК 3_"+ getCurrentDate(" ") + "_" + String.valueOf(i) + ".xls";
                break;
            }
        }
        return outputFile;
    }

    private File createFileName(String outputFile) {
        File file;
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd_MM_yyyy");
        StringBuilder fileName = new StringBuilder();
        fileName.append(outputFile + "_"+formatForDateNow.format(new Date()));
        for (int i=0;;i++){
            file = new File(fileName.toString() +"_"+ i + ".xls");
            file.getParentFile().mkdir();
            if (!file.exists()){
                return  file;
            }
        }
    }
    protected void insertData (ResultSet result,HSSFSheet worksheet, int rowNum){
    insertData(result,worksheet,rowNum,0);
    }

    protected void insertData (ResultSet result,HSSFSheet worksheet, int rowNum, int colNum)
    {
        int rownum=rowNum;
        try {
            while (result.next()) {
                Row dataRow = worksheet.createRow(rownum);
                for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                    Cell cell1 = dataRow.createCell(i+colNum, CellType.STRING);
                    cell1.setCellValue(result.getString(i + 1));
                }
                rownum++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void insertData (String[][] matrix,HSSFSheet sheet, int rowNum, int colNum)
    {
        int rownum=rowNum;
        for (int i=0;i<matrix.length;i++){
            Row row = (sheet.getRow(i+rowNum)==null)?sheet.createRow(i+rowNum):sheet.getRow(i+rowNum);
            for (int j=0;j<matrix[0].length;j++){
                Cell cell = row.createCell(i+colNum,CellType.STRING);
                cell.setCellValue(matrix[i][j]);
            }
        }
    }

    protected void createHeader (List<String> headerXLS, HSSFSheet worksheet, HSSFCellStyle style){
        createHeader(headerXLS,worksheet,style,0,0);
    }

    protected void createHeader (List<String> headerXLS, HSSFSheet worksheet, HSSFCellStyle style, int startRowNum ){
        createHeader(headerXLS,worksheet,style,startRowNum,0);
    }
    protected void createHeader (List<String> headerXLS, HSSFSheet worksheet, HSSFCellStyle style, int startRowNum, int column ){
        Row row=worksheet.createRow(startRowNum);
        createHeader(headerXLS,style,row,column);
    }
    /*
    * @Param column - номер столбца с которого начнется в ставка
    * */
    protected void createHeader (List<String> headerXLS, HSSFCellStyle style,Row row, int column ){
        for (int i=0;i<headerXLS.size();i++){
            Cell cell = row.createCell(i+column, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(headerXLS.get(i));
            cell.getStringCellValue();
        }
    }

    protected void createVerticalHeader (List<String> headerXLS, HSSFCellStyle style,HSSFSheet sheet, int rowNum,int colNum ){
        for (int i=0;i<headerXLS.size();i++){
            /*если Row не создан, то создаем*/
            Row row = (sheet.getRow(i+rowNum)==null)?sheet.createRow(i+rowNum):sheet.getRow(i+rowNum);
            Cell cell = row.createCell(i+colNum, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(headerXLS.get(i));
        }
    }
}
