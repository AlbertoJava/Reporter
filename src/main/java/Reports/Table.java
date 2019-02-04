package Reports;

import Utilz.Printer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static Utilz.Printer.printLineToMonitor;
import static Utilz.Printer.printRowToMonitor;

public class Table {
    private String [][] matrix ;

/*
* Конструктор копирует результаты запроса в двумерный массив
* */
    public Table(ResultSet resultSet) {
        if (resultSet == null) return;
        int q=0;
        try {

            while(resultSet.next()){
                q++;
            }
            matrix=new String[q][resultSet.getMetaData().getColumnCount()];
            resultSet.beforeFirst();
            int rownum=0;
            while (resultSet.next()) {
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    matrix[rownum][i]=resultSet.getString(i+1);
                }
                rownum++;
               }
            resultSet.beforeFirst();
        }catch (SQLException e) {
            Printer.printLog(e);
    }
    }
    public void transposeTable (){
        String [][]rotatedMatrix = new String [matrix[0].length][matrix.length];

        for (int i=0;i<matrix.length;i++){
            for (int j=0;j<matrix[0].length;j++){
                rotatedMatrix[j][i]=matrix[i][j];
            }
        }
        matrix =rotatedMatrix;
        }

    public void printMatrix (){
        if (matrix==null) return;
        for (int i=0;i<matrix.length;i++){
            for (int j=0;j<matrix[0].length;j++){
                printLineToMonitor(matrix[i][j] + "; ");
            }
            printRowToMonitor("");
        }
    }

    public String[][] getMatrix() {
        return matrix;
    }
}