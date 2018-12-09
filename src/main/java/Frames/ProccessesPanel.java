package Frames;

import Utilz.Printer;
import Utilz.SqlProperties;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.sleep;


public class ProccessesPanel extends JScrollPane {
    private EventListenerList listenerList = new EventListenerList();
    private static PriorityBlockingQueue<SqlProperties> waitingQueue;
    private static BlockingQueue<SqlProperties> runningQueue ;



    private static Map<SqlProperties,Boolean> statusMap;
    private static List<SqlProperties> processList;
    private List<List<JComponent>> elements = new ArrayList<>();
    private GridBagConstraints gc;
    private JPanel jPanel;
    private GridBagLayout layout ;
    private GridBagConstraints gbc;
    private MyFrame myFrame;
    private JLabel greenLabel=null;
    private List <JComponent> greenLine=null;
    private Color greenColor = new Color (10,200,10);
    private Color grayColor = null;
    private List<JLabel> header= new ArrayList<>();




    public void setStatusMap(Map<SqlProperties, Boolean> statusMap) {
        if (statusMap==null){
            throw new NullPointerException("ProccessPanel.setStatusMap get null parameter");
        }
        myFrame = (MyFrame) getParent().getParent().getParent().getParent();
        ProccessesPanel.statusMap = statusMap;
//        layout = new GridLayout(/*ProccessesPanel.statusMap.size()*/1, 4, 5, 1);
        layout = new GridBagLayout();
        jPanel = new JPanel();
        jPanel.setLayout(layout);
        jPanel.setBorder(BorderFactory.createTitledBorder("Task list jpanel"));


        gbc = new GridBagConstraints(0,0,1,1,1,1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(2,0,2,0),1,1);

        Border bottomBorder = BorderFactory.createCompoundBorder();
        bottomBorder=BorderFactory.createCompoundBorder(bottomBorder,BorderFactory.createMatteBorder(0,0,2,0,Color.gray));

        JLabel jLabel1 =new JLabel("DESCRIPTION");
        jLabel1.setBorder(bottomBorder);
        jPanel.add(jLabel1,gbc);
        gbc.gridx=1;
        JLabel jLabel2 =new JLabel("CURRENT STATUS");
        jLabel2.setBorder(bottomBorder);
        jPanel.add(jLabel2,gbc);
        gbc.gridx=2;
        JLabel jLabel3 =new JLabel("LAST EXECUTION TIME");
        jLabel3.setBorder(bottomBorder);
        jPanel.add(jLabel3,gbc);
        gbc.gridx=3;
        JLabel jLabel4 =new JLabel("WAIT TIME");
        jLabel4.setBorder(bottomBorder);
        jPanel.add(jLabel4,gbc);

        //updateJComponentsFromQueue();
        setViewportView(jPanel);

        setBorder(BorderFactory.createTitledBorder("This is ScrollPanel"));
        setPreferredSize(new Dimension(600,100));
        updateJComponentsFromQueue();
    }

    public ProccessesPanel (){
       // this.myFrame = myFrame;
    }

    private void updateJComponentsFromQueue() {
        if (statusMap==null)return;
        Point pos = getViewport().getViewPosition();
        /*if (layout==null) {
            layout = new GridLayout(ProccessesPanel.statusMap.size(), 4, 5, 1);
            jPanel.setLayout(layout);
        }*/
        /*зачищаем панель от элементов*/
        for (int i =0;i<elements.size();i++){
            jPanel.remove(elements.get(i).get(0));
            jPanel.remove(elements.get(i).get(1));
            jPanel.remove(elements.get(i).get(2));
            jPanel.remove(elements.get(i).get(3));
        }
        elements.clear();
        int x=0;
        int y=1;

        for (Map.Entry<SqlProperties,Boolean> pair:
             statusMap.entrySet()) {
            String threadStatus = pair.getValue()?"running":"asleep";

            List<JComponent> componentRow = Arrays.asList(
                    new JLabel(pair.getKey().getProperty("description")),
                    new JLabel(threadStatus),
                    new JLabel(pair.getKey().getProperty("timeStampLastExecution")),
                    new JLabel (ms_totime (Long.valueOf(pair.getKey().getProperty("asleeptime"))))
            );

            componentRow.get(0).addMouseListener(new GetDetails());
            if (greenLine!=null && ((JLabel)componentRow.get(0)).getText().equals(((JLabel)greenLine.get(0)).getText())){
                setLineColor(componentRow, greenColor);
            }
            gbc.gridy=y++;
            gbc.gridx=x++;
            jPanel.add(componentRow.get(0),gbc);
            gbc.gridx=x++;
            jPanel.add(componentRow.get(1),gbc);
            gbc.gridx=x++;
            jPanel.add(componentRow.get(2),gbc);
            gbc.gridx=x++;
            jPanel.add(componentRow.get(3),gbc);
            gbc.gridx=0;
            x=0;
            elements.add(componentRow);

            //addJComponent();

            jPanel.revalidate();
            getViewport().setViewPosition(pos);
            //getVerticalScrollBar().setValue(pos);
            //jPanel.repaint();
        }

    }

    private void setLineColor(List<JComponent> componentRow, Color color) {
        for (JComponent jLabel:componentRow) {
            jLabel.setOpaque(true);
            ((JLabel)jLabel).setBackground(color);
        }
        if (greenLine!=null) {
            for (JComponent jLabel : greenLine) {
                ((JLabel) jLabel).setBackground(null);
            }
        }
        greenLine=componentRow;
    }
    private void setLineColor(JComponent component, Color color) {
        for(List<JComponent> list:elements){
            for (JComponent comp:list){
                if (comp==component){
                  setLineColor(list,color);
                }
            }
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
            System.out.println(formatted_time);
            return formatted_time.toString();
    }

    private void addJComponent() {
        for (int i=0;i<elements.size();i++) {
            for (int j=0;j<elements.get(i).size();j++){
                jPanel.add(elements.get(i).get(j));
            }
        }
    }
    private void clearPanel(){
        for (int i=0;i<elements.size();i++) {
            for (int j=0;j<elements.get(i).size();j++){
                jPanel.remove (elements.get(i).get(j));
            }
        }
    }

    private void createProxyProccessList(int countRows) {
        elements.clear();
        for (int i=0;i<countRows;i++){
                List<JComponent> componentList = Arrays.asList(new JLabel("description " +i), new JLabel("Time till start " + i), new JLabel("Time of last execution " + i));
                elements.add(componentList);
            }
    }

    synchronized public void updateProccessList (){
        updateJComponentsFromQueue();
}
    private class GetDetails implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
                JLabel source = (JLabel)e.getSource();
                String description= source.getText();
                List<StringBuilder> res =  Printer.getResults(description);
                //////////////////////////////
                if (greenLine!=null){
                    setLineColor(greenLine,greenColor);
                }
                setLineColor(source,greenColor);
           if (res==null) {
                    myFrame.getTextArea().setText("no results");
                    return;
                }
                myFrame.getTextArea().setText("Last run time " + res.get(1).toString()+ "\r\n");
                myFrame.getTextArea().append(res.get(0).toString());

            }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
