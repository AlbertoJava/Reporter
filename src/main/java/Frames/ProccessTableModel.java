package Frames;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProccessTableModel extends AbstractTableModel {
    private int columnCount=5;
    private ArrayList<String[]> dataArrayList;


    public ProccessTableModel() {
        dataArrayList=new ArrayList<String[]>();
        for (int i=0;i<dataArrayList.size();i++){
            dataArrayList.add(new String [getColumnCount()]);
        }
    }

    @Override
    public int getRowCount() {
        return dataArrayList.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] row = dataArrayList.get(rowIndex);
        return row[columnIndex];
    }
    @Override
    public String getColumnName(int columnIndex){
        switch(columnIndex){
            case 0 : return "#id";
            case 1 : return "title";
            case 2 : return "isbn";
            case 3 : return "description";

        }
        return "";
    }

    public void addData (String[]row){
        String [] rowTable = new String[getColumnCount()];
        rowTable=row;
        dataArrayList.add(rowTable);
    }


}
