package edgarAnalytics;

import java.util.*;



/**
 * Class that provides statistics for a user's session
 */
public class Session {

    private Calendar start, end;
    private int firstentry;
    private int count = 0;
    
    
    
    /**
     * Constructor that creates the class instance from LogEntry
     * @param entry user event data
     */
    public Session(LogEntry entry)
    {
        start = entry.getTime();
        end   = entry.getTime();
        
        firstentry = LogEntry.getEntryNumber();
        count = 1;
    }
    
    
    
    /**
     * Method that handles the new webpage request made by user
     * @param entry user event data
     */
    public void addVisitedWebpage(LogEntry entry)
    {
        count += 1;
        end    = entry.getTime();
    }
    
    
    
    /**
     * Returns start time of the session
     * @return start time
     */
    public Calendar getStartTime()
    { return (Calendar) start.clone(); }
    
    
    
    /**
     * Returns end time of the session
     * @return end time
     */
    public Calendar getEndTime()
    { return (Calendar) end.clone(); }
    
    
    
    /**
     * Returns total webpage count during the session
     * @return webpage count
     */
    public int getWebpageCount()
    { return count; }
    
    
    
    public int getEntryNumber()
    { return firstentry; }
    
//    
//    
//    /**
//     * Returns unique session identifier that consists of timestamp of user's last activity
//     * and entry number that started the session
//     * @return (last timestamp, first number) tuple
//     */
//    public Tuple<Calendar, Integer> getUniqueTupleStart()
//    { return new Tuple<>(getStartTime(), firstentry); }
//    
//    
//    
//    /**
//     * Returns unique session identifier that consists of timestamp of user's last activity
//     * and entry number that started the session
//     * @return (last timestamp, first number) tuple
//     */
//    public Tuple<Calendar, Integer> getUniqueTupleEnd()
//    { return new Tuple<>(getEndTime(), firstentry); }
//    
}