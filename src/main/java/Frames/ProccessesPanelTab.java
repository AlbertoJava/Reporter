package Frames;

import Utilz.BaseConstants;
import Utilz.Printer;
import Utilz.SqlProperties;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;


public class ProccessesPanelTab extends JScrollPane implements Runnable {
    private static HashMap<SqlProperties, Boolean> statusMap;
    private MyFrame myFrame;
    private ProccessesPanelTab.ProcessTableModel ptm;
    private JTable proccessesTable;


    public ProccessesPanelTab() {

    }

    public void init(Map<SqlProperties, Boolean> statusMap) {
        if (statusMap == null) {
            throw new NullPointerException("ProccessPanel.setStatusMap get null parameter");
        }
        this.statusMap = (HashMap<SqlProperties, Boolean>) statusMap;
        try {
            myFrame = (MyFrame) getParent().getParent().getParent().getParent();
        } catch (Exception e) {
        }
        ptm = new ProcessTableModel();
        proccessesTable = new JTable(ptm);
        DefaultListSelectionModel ssm = new DefaultListSelectionModel();
        ssm.setSelectionMode(0);
        ssm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = e.getFirstIndex();
                if (row == -1) return;
                String desc = (String) proccessesTable.getModel().getValueAt(row, 0);
                List<StringBuilder> res = Printer.getResults(desc);
                myFrame.getTextArea().setText("Last run time " + res.get(1).toString() + "\r\n");
                myFrame.getTextArea().append(res.get(0).toString());
            }
        });
        proccessesTable.setSelectionModel(ssm);
        for (int i = 0; i < proccessesTable.getColumnCount(); i++) {
            proccessesTable.getColumnModel().getColumn(i).setCellRenderer(new Renderer());
        }
        setViewportView(proccessesTable);
        setBorder(BorderFactory.createTitledBorder("This is ScrollPanel"));
        setPreferredSize(new Dimension(600, 100));
        updateJComponentsFromQueue();
        (new Thread(this)).start();
    }

    private void updateJComponentsFromQueue() {

        if (ptm != null) {

            //ptm.upDate();
            ptm.fireTableDataChanged();
            proccessesTable.repaint();
            proccessesTable.revalidate();
        }

    }


    @Override
    public void run() {
        while (true) {
            try {
                int row = proccessesTable.getSelectedRow();
                ptm.fireTableDataChanged();
                if (row != -1) proccessesTable.setRowSelectionInterval(row, row);
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
            Iterator<Map.Entry<SqlProperties, Boolean>> itr = statusMap.entrySet().iterator();
            int i = 0;
            Map.Entry<SqlProperties, Boolean> entry = null;
            while (itr.hasNext()) {
                if (i == rowIndex) {
                    entry = itr.next();
                    break;
                }
                i++;
                itr.next();

            }

            String result = null;
            switch (columnIndex) {
                case 0:
                    result = entry.getKey().getProperty("description");
                    break;
                case 1:
                    result = entry.getValue() ? "running" : "asleep";
                    break;
                case 2:
                    result = entry.getKey().getProperty("timeStampLastExecution");
                    break;
                case 3:
                    result = BaseConstants.ms_totime(Long.valueOf(entry.getKey().calcSleepingTime()));
                    break;
            }
            return result;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Description";
                case 1:
                    return "Current status";
                case 2:
                    return "Last execution time";
                case 3:
                    return "Wait time";
            }
            return "";
        }
    }
}
