package Utilz;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            e.printStackTrace();
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
                System.out.print(matrix[i][j] + "; ");
            }
            System.out.println("");
        }
    }

    public String[][] getMatrix() {
        return matrix;
    }
}