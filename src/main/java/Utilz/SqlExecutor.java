package Utilz;


import Reports.AbstratReports.AbstractReport;
import Reports.AbstratReports.Report;
import Reports.ReportFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;


public class SqlExecutor extends Thread{
    private static PriorityBlockingQueue<SqlProperties> waitingQueue;
    ExecutorService service = Executors.newFixedThreadPool(BaseConstants.MAX_COUNT_THREADS);
    //private Map<SqlProperties,Boolean> workingPool=new LinkedHashMap<>();

    public SqlExecutor(PriorityBlockingQueue<SqlProperties> queue) {
        this.waitingQueue=queue;
      /*  for (SqlProperties prop:waitingQueue) {
  //       workingPool.put(prop,false);
       *//*  prop.addProperty("timeStampLastExecution", getCurrentDateTime());
         prop.addProperty("asleeptime", String.valueOf(prop.calcSleepingTime()));*//*
        }*/
    }

    @Override
    public void run() {
        while (true){
            if (waitingQueue.size()>0) {
                SqlProperties sqlProp = waitingQueue.peek();
                long period = sqlProp.calcSleepingTime();
                //sqlProp.addProperty("asleeptime", String.valueOf(period));
                if (period == 0 && !sqlProp.isRunning()) {
                    AbstractReport r =(AbstractReport) ReportFactory.create(sqlProp, this);
                    service.submit(r);
                    waitingQueue.remove(sqlProp);
                    sqlProp.setRunning(true);
                    waitingQueue.put(sqlProp);
                }
            }
        }
    }

    public void addQueue(AbstractReport abstractReport){
        abstractReport.addProperty("timeStampLastExecution", getCurrentDateTime());
        //abstractReport.addProperty("asleeptime", String.valueOf(abstractReport.getProps().calcSleepingTime()));
        waitingQueue.remove(abstractReport.getProps());
        abstractReport.getProps().setRunning(false);
        waitingQueue.add(abstractReport.getProps());
        //workingPool.put(abstractReport.getProps(),false);

        long period = abstractReport.getSleepingTime();
        Printer.printRowToMonitor("Thread "+abstractReport.getProperty("description")+" sleep for " +
                        String.valueOf(period / 1000 / 60 / 60 / 24) + " days "
                        + String.valueOf(period / 1000 / 60 / 60 % 24) + " hours "
                        + String.valueOf(period / 1000 / 60 % 60) + " min "
                        + String.valueOf(period / 1000   % 60) + " sec "
                        + String.valueOf(period   %1000) + " milisec.");
  }

    private String getCurrentDateTime (){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return date.format(formatter);
    }
    public static PriorityBlockingQueue<SqlProperties> getWaitingQueue() {
        return waitingQueue;
    }

    /*public Map<SqlProperties, Boolean> getWorkingPool() {
        return workingPool;
    }*/


}
