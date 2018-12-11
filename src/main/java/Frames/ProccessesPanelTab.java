package Frames;

import Utilz.Printer;
import Utilz.SqlProperties;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.sleep;


public class ProccessesPanelTab extends JScrollPane implements Runnable{
    private static LinkedHashMap<SqlProperties,Boolean> statusMap;
    private MyFrame myFrame;
    private ProccessesPanelTab.ProcessTableModel ptm;
    private JTable proccessesTable;



    public void init (Map<SqlProperties, Boolean> statusMap) {
        if (statusMap==null){
            throw new NullPointerException("ProccessPanel.setStatusMap get null parameter");
        }
        this.statusMap = (LinkedHashMap<SqlProperties, Boolean>) statusMap;
        //this.initialMap = statusMap;
        try {
        myFrame = (MyFrame) getParent().getParent().getParent().getParent();}
        catch(Exception e) {
            }
        ptm = new ProcessTableModel();
        proccessesTable = new JTable(ptm);
        DefaultListSelectionModel ssm = new DefaultListSelectionModel();
        ssm.setSelectionMode(0);
        ssm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row=e.getFirstIndex();
                if (row==-1) return;
                String desc = (String)proccessesTable.getModel().getValueAt(row,0);
                List<StringBuilder> res =  Printer.getResults(desc);
                myFrame.getTextArea().setText("Last run time " + res.get(1).toString()+ "\r\n");
                myFrame.getTextArea().append(res.get(0).toString());
            }
        });
        proccessesTable.setSelectionModel(ssm);
        for (int i=0; i<proccessesTable.getColumnCount();i++){
            proccessesTable.getColumnModel().getColumn(i).setCellRenderer(new Renderer());
        }
        setViewportView(proccessesTable);
        setBorder(BorderFactory.createTitledBorder("This is ScrollPanel"));
        setPreferredSize(new Dimension(600,100));
        updateJComponentsFromQueue();
        (new Thread(this)).start();
    }

    public ProccessesPanelTab(){

    }

    private void updateJComponentsFromQueue() {

        if (ptm !=null){

            //ptm.upDate();
            ptm.fireTableDataChanged();
            proccessesTable.repaint();
            proccessesTable.revalidate();
        }

    }

     private String ms_totime(long ms) {
            Integer [] time = new Integer[4];
            time[3] = (int)ms/86400000;
            ms=ms%86400000;
            time[2] = (int)ms/3600000;
            ms=ms%3600000;
            time[1] = (int)ms/60000;
            ms=ms%60000;
            time[0] = (int)ms/1000;
            ms=ms%1000;
            StringBuilder formatted_time = new StringBuilder();
            for (int i=time.length-1;i>=0;i--){
                if (time[i].toString().length()<2) {
                    formatted_time.append('0');
                }
                formatted_time.append(time[i]);
                formatted_time.append(":");
            }
            formatted_time.deleteCharAt(formatted_time.length()-1);
           // System.out.println(formatted_time);
            return formatted_time.toString();
    }



    @Override
    public void run() {
        while (true) {
            try {
                /*
                for (int i = 0; i < ptm.getRowCount(); i++) {
                    String key = (String) ptm.getValueAt(i, 0);
                    SqlProperties keyObject = statusMap.keySet()
                            .stream()
                            .filter(s -> s.getProperty("description").equals(key))
                            .findFirst()
                            .get();

                    String status= statusMap.get(key)?"running":"asleep";
                    if (!ptm.getValueAt(i, 1).equals(status)) {
                        ptm.setValueAt(status, i, 1);
                    }

                    if (!ptm.getValueAt(i, 2).equals(keyObject.getProperty("timeStampLastExecution"))) {
                        ptm.setValueAt(keyObject.getProperty("timeStampLastExecution"), i, 2);
                    }
                    if (!ptm.getValueAt(i, 3).equals(ms_totime (Long.valueOf(keyObject.calcSleepingTime())))) {
                        ptm.setValueAt(ms_totime (Long.valueOf(keyObject.calcSleepingTime())), i, 3);
                    }
                    ((AbstractTableModel)proccessesTable.getModel()).fireTableRowsUpdated(i,i);
                }*/
                int row = proccessesTable.getSelectedRow();
                ptm.fireTableDataChanged();
                if (row!=-1)proccessesTable.setRowSelectionInterval(row,row);
               // proccessesTable.updateUI();


                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private class ProcessTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return statusMap.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public synchronized Object getValueAt(int rowIndex, int columnIndex) {
            Iterator<Map.Entry<SqlProperties,Boolean>> itr = statusMap.entrySet().iterator();
            int i=0;
            Map.Entry<SqlProperties,Boolean> entry=null;
            while (itr.hasNext() )            {
                if (i==rowIndex){
                    entry = itr.next();
                    break;
                }
                i++;
                itr.next();

           }

           String result=null;
           switch (columnIndex){
               case 0: result=entry.getKey().getProperty("description");break;
               case 1: result=entry.getValue()?"running":"asleep";break;
               case 2: result=entry.getKey().getProperty("timeStampLastExecution");break;
              // case 3: result=ms_totime (Long.valueOf(entry.getKey().getProperty("asleeptime")));break;
               case 3: result=ms_totime (Long.valueOf(entry.getKey().calcSleepingTime()));break;
           }
            return result;
        }

        @Override
        public String getColumnName(int columnIndex){
            switch(columnIndex){
                case 0 : return "Description";
                case 1 : return "Current status";
                case 2 : return "Last execution time";
                case 3 : return "Wait time";

            }
            return "";
        }

    }
}
