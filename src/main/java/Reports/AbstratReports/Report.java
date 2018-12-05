package Reports.AbstratReports;

public interface Report {
    boolean createReport();
    //void setIntervalDate(String date1,String date2);
    //void setPeriod(Long millisecond);
    Long getSleepingTime();
    void addProperty(String s1, String s2);

}
