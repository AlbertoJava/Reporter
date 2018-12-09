package Utilz;

import Frames.ProccessesPanelTab;
import Reports.AbstratReports.AbstractReport;
import Reports.ReportFactory;
import Frames.ProccessesPanel;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;


public class SqlExecutor extends Thread{
    private static PriorityBlockingQueue<SqlProperties> waitingQueue;
    private Map<SqlProperties,Boolean> workingPool=new LinkedHashMap<>();

    public SqlExecutor(PriorityBlockingQueue<SqlProperties> queue) {
        this.waitingQueue=queue;
        for (SqlProperties prop:waitingQueue) {
         workingPool.put(prop,false);
         prop.addProperty("timeStampLastExecution", getCurrentDateTime());
         prop.addProperty("asleeptime", String.valueOf(prop.calcSleepingTime()));
        }
    }

    @Override
    public void run() {
        while (true){
            if (waitingQueue.size()>0) {
                SqlProperties sqlProp = waitingQueue.peek();
                long period = sqlProp.calcSleepingTime();
                sqlProp.addProperty("asleeptime", String.valueOf(period));
                if (period == 0) {
                    waitingQueue.remove(sqlProp);
                    ReportFactory.create(sqlProp, this);
                    workingPool.put(sqlProp,true);
                }
            }
        }
    }
    public void addQueue(AbstractReport abstractReport){
        abstractReport.addProperty("timeStampLastExecution", getCurrentDateTime());
        abstractReport.addProperty("asleeptime", String.valueOf(abstractReport.getProps().calcSleepingTime()));
        waitingQueue.add(abstractReport.getProps());
        workingPool.put(abstractReport.getProps(),false);
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long period = abstractReport.getSleepingTime();
        Printer.printRowToMonitor("Поток "+abstractReport.getProperty("description")+" заснул на " +
                        String.valueOf(period / 1000 / 60 / 60 / 24) + " дней "
                        + String.valueOf(period / 1000 / 60 / 60 % 24) + " часов "
                        + String.valueOf(period / 1000 / 60 % 60) + " минут "
                        + String.valueOf(period / 1000   % 60) + " секунд "
                        + String.valueOf(period   %1000) + " милисекунд.");

    }

    private String getCurrentDateTime (){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return date.format(formatter);
    }
    public static PriorityBlockingQueue<SqlProperties> getWaitingQueue() {
        return waitingQueue;
    }

    public Map<SqlProperties, Boolean> getWorkingPool() {
        return workingPool;
    }


}
