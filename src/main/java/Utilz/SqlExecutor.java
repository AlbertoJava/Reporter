package Utilz;

import Reports.AbstratReports.AbstractReport;
import Reports.ReportFactory;
import Frames.ProccessesPanel;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;


public class SqlExecutor extends Thread{
    private String filename =null;
    private final static Object lock = new Object();//������ ������������� �� ������ ������
    private BaseConstants constants=null;
    private boolean startQuery=true;
    private static PriorityBlockingQueue<SqlProperties> waitingQueue;

    public Map<SqlProperties, Boolean> getWorkingPool() {
        return workingPool;
    }

    //private static BlockingQueue<SqlProperties> runningQueue = new LinkedBlockingDeque<>();
    private Map<SqlProperties,Boolean> workingPool=new HashMap();
    private static ProccessesPanel proccessesPanel;

public SqlExecutor(PriorityBlockingQueue<SqlProperties> queue, ProccessesPanel proccessesPanel) {
        this.waitingQueue=queue;
        for (SqlProperties prop:
         waitingQueue) {
        workingPool.put(prop,false);
         prop.addProperty("timeStampLastExecution", getCurrentDateTime());
         prop.addProperty("asleeptime", String.valueOf(prop.calcSleepingTime()));
        }
        this.proccessesPanel=proccessesPanel;
    }

    @Override
    public void run() {
        while (true){
            if (waitingQueue.size()>0) {
                //Printer.printRowToMonitor("������ ������� " + queue.size());
                SqlProperties sqlProp = waitingQueue.peek();
                long period = sqlProp.calcSleepingTime();
                sqlProp.addProperty("asleeptime", String.valueOf(period));
                if (period == 0) {
                    waitingQueue.remove(sqlProp);
                    ReportFactory.create(sqlProp, this);
                    workingPool.put(sqlProp,true);
                    while (proccessesPanel==null){
                        System.out.println("proccessesPanel is null");
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    proccessesPanel.updateProccessList();
                }
            }
        }
    }
    public void addQueue(AbstractReport abstractReport){
        abstractReport.addProperty("timeStampLastExecution", getCurrentDateTime());
        abstractReport.addProperty("asleeptime", String.valueOf(abstractReport.getProps().calcSleepingTime()));
        waitingQueue.add(abstractReport.getProps());
        workingPool.put(abstractReport.getProps(),false);
        proccessesPanel.updateProccessList();
        System.out.println("������� ��������");
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long period = abstractReport.getSleepingTime();
        Printer.printRowToMonitor("����� "+abstractReport.getProperty("description")+" ������ �� " +
                        String.valueOf(period / 1000 / 60 / 60 / 24) + " ���� "
                        + String.valueOf(period / 1000 / 60 / 60 % 24) + " ����� "
                        + String.valueOf(period / 1000 / 60 % 60) + " ����� "
                        + String.valueOf(period / 1000   % 60) + " ������ "
                        + String.valueOf(period   %1000) + " ����������.");

    }
    private String getCurrentDateTime (){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return date.format(formatter);
    }
    public static PriorityBlockingQueue<SqlProperties> getWaitingQueue() {
        return waitingQueue;
    }


    public void setPanel(ProccessesPanel proccessesPanel) {
    this.proccessesPanel=proccessesPanel;
    }
}
