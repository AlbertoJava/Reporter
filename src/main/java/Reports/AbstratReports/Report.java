package Reports.AbstratReports;

public interface Report {
    boolean createReport();

    Long getSleepingTime();

    void addProperty(String s1, String s2);

}
